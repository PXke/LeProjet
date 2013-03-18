package controllers;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bson.types.ObjectId;

import models.ProjectModel;
import models.UserModel;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;

import views.html.*;

public class Dashboard extends Controller  {

	public static Result dashboard()	{
		if(!session().containsKey("idUser"))
			return redirect("/");
		
		List<ProjectModel> lst = ProjectModel.finder.all();
		
		Collections.sort( lst, new Comparator< ProjectModel >( ){
			@Override
			public int compare(ProjectModel o1, ProjectModel o2) {
				if(o1.averageNote() > o2.averageNote())
					return -1;
				else
					return 1;
			}
		});
		
		return ok(dashboard.render("", UserModel.finder.byId(new ObjectId(session("idUser"))),lst));
	}
	
	 public static Result account()	{
	    	return TODO;
	 }
	
	 public static Result newproject()	{
		 if(!session().containsKey("idUser"))
				return redirect("/");
		 return ok(newprojectform.render(UserModel.finder.byId(new ObjectId(session("idUser")))));
	 }
}
