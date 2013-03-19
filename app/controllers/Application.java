package controllers;

import java.security.NoSuchAlgorithmException;

import lib.util.Hash;
import models.UserModel;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {
  
    public static Result index() {
        return ok(index.render(""));
    }
    
    public static Result signup()	{
    	return ok(signup.render(""));
    }
    
    public static Result signupperformed()	{
    	DynamicForm signupForm = Form.form().bindFromRequest();
    	
    	String inputUsername = signupForm.field("inputUsername").value();
		String inputEmail = signupForm.field("inputEmail").value();
		String inputPassword = signupForm.field("inputPassword").value();
		String inputPassword2 = signupForm.field("inputPassword2").value();
		
		if(UserModel.findByUsername(inputUsername) != null)	{
			return ok(signup.render("Username already used"));
		}else if(UserModel.findByMail(inputEmail) != null)	{
			return ok(signup.render("Email already used"));
		}else if(inputPassword.compareTo(inputPassword2) != 0 || inputPassword.length() < 6)	{
			return ok(signup.render("Password are not identical or less than 6"));
		}else	{
			UserModel user = new UserModel();
			user.setEmail(inputEmail);
			try {
				user.setPassword(Hash.encode(inputPassword, "SHA-256"));
			} catch (NoSuchAlgorithmException e) {
				play.Logger.error("no such algorithme SHA-256");
				return ok(signup.render("Internal server error"));
			}
	    	user.setUsername(inputUsername);
	    	user.insert();
	    	return ok();
		}
    }
    
    public static Result signinperformed()	{
    	DynamicForm signinForm = Form.form().bindFromRequest();
    	
    	String emailOrUsername = signinForm.field("email").value();
		String password = signinForm.field("password").value();
		String encodedPassword;
		try {
			encodedPassword = Hash.encode(password, "SHA-256");
			UserModel user = UserModel.findByMailPass(emailOrUsername, encodedPassword);
			if(user == null)	{
				user = UserModel.findByUsernamePass(emailOrUsername, encodedPassword);
				if(user != null)	{
					session().put("idUser", user.id());
					return redirect("/dashboard");
				}else	{
					return ok(index.render("Unable to login with the information"));
				}
			}else	{
				session().put("idUser", user.id());
				return redirect("/dashboard");
			}
		} catch (NoSuchAlgorithmException e) {
			play.Logger.error("no such algorithme SHA-256");
		}
		return ok(index.render("Internal server error"));
    }
    
    public static Result logout()	{
    	if(session().containsKey("idUser"))	{
    		session().remove("idUser");
    	}
    	return redirect("/");
    }
    
    public static Result about()	{
    	return TODO;
    }
    
    public static Result contact()	{
    	return TODO;
    }
  
}
