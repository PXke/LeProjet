package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bson.types.ObjectId;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import lib.interfaces.Event;
import lib.util.Github;
import models.MarkModel;
import models.ProjectModel;
import models.UserModel;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;

import views.html.*;

public class Dashboard extends Controller {

	private static JsonNodeFactory factory = JsonNodeFactory.instance;

	public static Result dashboard()	{
		if (!session().containsKey("idUser"))
			return redirect("/");
		UserModel me = UserModel.finder.byId(new ObjectId(session("idUser")));
		List<ProjectModel> all = ProjectModel.allFollowedBy(me);
		all.addAll(ProjectModel.projectForUser(me));
		all.addAll(ProjectModel.allContributedBy(me));
		
		ArrayList<Event> flux = new ArrayList<Event>();
		for(ProjectModel pro : all)	{
			try {
				String xml = Github.getFlux(pro.getGithubUrl(), pro.getBranch());
				Github.parseAndCreateCommitFlux(xml, pro);
			} catch (IOException e) {}
			
			flux.addAll(pro.getCommits());
			flux.addAll(pro.getNotes());
		}
		Collections.sort(flux, new Comparator<Event>() {
			@Override
			public int compare(Event o1, Event o2) {
				if (o1.getDate() < o2.getDate())
					return 1;
				else
					return -1;
			}
		});
		
		return ok(dashboard.render(flux, me));
	}
	
	public static Result explore() {
		if (!session().containsKey("idUser"))
			return redirect("/");

		List<ProjectModel> lst = ProjectModel.finder.all();

		Collections.sort(lst, new Comparator<ProjectModel>() {
			@Override
			public int compare(ProjectModel o1, ProjectModel o2) {
				if (o1.averageNote() > o2.averageNote())
					return -1;
				else
					return 1;
			}
		});

		return ok(explore.render("",UserModel.finder.byId(new ObjectId(session("idUser"))), lst));
	}

	public static Result account() {
		return TODO;
	}

	public static Result newproject() {
		if (!session().containsKey("idUser"))
			return redirect("/");
		return ok(newprojectform.render(UserModel.finder.byId(new ObjectId(session("idUser")))));
	}

	public static Result lookForProjectName() {
		DynamicForm form = Form.form().bindFromRequest();
		String projectName = form.field("name").value();

		ObjectNode node = new ObjectNode(factory);
		ProjectModel pro = ProjectModel.findByName(projectName);
		if (pro == null) {
			node.put("found", 0);
		} else {
			node.put("found", 1);
		}
		return ok(node);
	}

	public static Result lookForGithub() {
		DynamicForm form = Form.form().bindFromRequest();
		String github = form.field("url").value();
		String branch = form.field("branch").value();

		ObjectNode node = new ObjectNode(factory);
		String description;
		try {
			description = Github.getDescription(github, branch);
			node.put("desc", description);
		} catch (IOException e) {
			node.put("desc", "");
		}
		return ok(node);
	}
	
	public static Result newProjectForm()	{
		DynamicForm form = Form.form().bindFromRequest();
		String projectName = form.field("projectName").value();
		String inputGithub = form.field("inputGithub").value();
		String branch = form.field("branch").value();
		String descriptionArea = form.field("descriptionArea").value();
		
		UserModel user = UserModel.finder.byId(new ObjectId(session("idUser")));
		
		if(ProjectModel.findByName(projectName) == null)	{	
			ProjectModel project = new ProjectModel();
			project.setGithubUrl(inputGithub);
			project.setName(projectName);
			project.setOwner(user);
			project.setBranch(branch);
			project.setDescription(descriptionArea);
			project.setDate(System.currentTimeMillis());
			project.insert();
			
			try {
				String flux = Github.getFlux(project.getGithubUrl(), project.getBranch());
				Github.parseAndCreateCommitFlux(flux, project);
			} catch (IOException e) {}
			
			return redirect("/"+user.getUsername()+"/"+projectName);
		}else	{
			return ok(newprojectform.render(user));
		}
	}
	
	public static Result projectView(String user, String project)	{
		UserModel me = null;
		if (session().containsKey("idUser"))
			me = UserModel.finder.byId(new ObjectId(session("idUser")));
		
		ProjectModel pro = ProjectModel.findByName(project);
		if(pro.getOwner().getUsername().compareTo(user) != 0)	{
			return redirect("/");
		}
		String flux;
		try {
			flux = Github.getFlux(pro.getGithubUrl(), pro.getBranch());
			Github.parseAndCreateCommitFlux(flux, pro);
		} catch (IOException e) {}
		
		return ok(projectDescription.render(pro, me));
	}
	
	public static Result takAMark()	{
		UserModel me = UserModel.finder.byId(new ObjectId(session("idUser")));
		DynamicForm form = Form.form().bindFromRequest();
		String inputmark = form.field("mark").value();
		String inputid = form.field("pro").value();
		ProjectModel pro = ProjectModel.finder.byId(new ObjectId(inputid));
		
		ObjectNode node = new ObjectNode(factory);
		
		if(pro != null)	{
			MarkModel mark = MarkModel.forProjectAndUser(pro, me);
			if(mark == null)	{
				play.Logger.debug("null");
				mark = new MarkModel();
				mark.setOwner(me);
				mark.setProject(pro);
				mark.setMark(Integer.parseInt(inputmark));
				mark.setDate(System.currentTimeMillis());
				mark.insert();
			}else	{
				play.Logger.debug("not null");
				mark.setMark(Integer.parseInt(inputmark));
				mark.setDate(System.currentTimeMillis());
				mark.update();
			}
				
			node.put("status", "success");
			node.put("average", pro.averageNote());
		}else	{
			node.put("status", "fail");
		}	
		return ok(node);
	}
	
	public static Result contribute()	{
		UserModel me = UserModel.finder.byId(new ObjectId(session("idUser")));
		DynamicForm form = Form.form().bindFromRequest();
		String inputid = form.field("id").value();
		ProjectModel pro = ProjectModel.finder.byId(new ObjectId(inputid));
		
		ObjectNode node = new ObjectNode(factory);
		if(pro != null)	{
			pro.addContributor(me);
			pro.update();
			node.put("status", "success");
		}else	{
			node.put("status", "fail");
		}
		return ok(node);
	}
	
	public static Result unsubscribe()	{
		UserModel me = UserModel.finder.byId(new ObjectId(session("idUser")));
		DynamicForm form = Form.form().bindFromRequest();
		String inputid = form.field("id").value();
		ProjectModel pro = ProjectModel.finder.byId(new ObjectId(inputid));
		
		ObjectNode node = new ObjectNode(factory);
		if(pro != null)	{
			pro.delContributor(me);
			pro.update();
			node.put("status", "success");
		}else	{
			node.put("status", "fail");
		}
		return ok(node);
	}
	
	public static Result follow()	{
		UserModel me = UserModel.finder.byId(new ObjectId(session("idUser")));
		DynamicForm form = Form.form().bindFromRequest();
		String inputid = form.field("id").value();
		ProjectModel pro = ProjectModel.finder.byId(new ObjectId(inputid));
		
		ObjectNode node = new ObjectNode(factory);
		if(pro != null)	{
			pro.addFollowersr(me);
			pro.update();
			node.put("status", "success");
		}else	{
			node.put("status", "fail");
		}
		return ok(node);
	}
	
	public static Result unfollow()	{
		UserModel me = UserModel.finder.byId(new ObjectId(session("idUser")));
		DynamicForm form = Form.form().bindFromRequest();
		String inputid = form.field("id").value();
		ProjectModel pro = ProjectModel.finder.byId(new ObjectId(inputid));
		
		ObjectNode node = new ObjectNode(factory);
		if(pro != null)	{
			pro.delFollowers(me);
			pro.update();
			node.put("status", "success");
		}else	{
			node.put("status", "fail");
		}
		return ok(node);
	}
}
