package controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bson.types.ObjectId;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import lib.util.Github;
import models.ProjectModel;
import models.UserModel;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;

import views.html.*;

public class Dashboard extends Controller {

	private static JsonNodeFactory factory = JsonNodeFactory.instance;

	public static Result dashboard() {
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

		return ok(dashboard.render("",
				UserModel.finder.byId(new ObjectId(session("idUser"))), lst));
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
			return redirect("/"+user.getUsername()+"/"+projectName);
		}else	{
			return ok(newprojectform.render(user));
		}
	}
	
	public static Result projectView(String user, String project)	{
		ProjectModel pro = ProjectModel.findByName(project);
		if(pro.getOwner().getUsername().compareTo(user) != 0)	{
			return redirect("/");
		}
		String flux;
		try {
			flux = Github.getFlux(pro.getGithubUrl(), pro.getBranch());
		} catch (IOException e) {
			flux = "";
		}
		
		return ok(projectDescription.render(pro, flux));
	}
}
