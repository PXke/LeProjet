package controllers;

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
		
		
		return ok(dashboard.render(""));
	}
	
	 public static Result account()	{
	    	return TODO;
	 }
	
}
