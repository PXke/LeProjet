package models;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.query.Query;
import leodagdag.play2morphia.Model;

@Entity
public class UserModel extends Model {

	@Id
	private ObjectId id;
	private String username;
	private String password;
	private String email;
	
	public static Finder<ObjectId, UserModel> finder = new Finder<ObjectId, UserModel>(ObjectId.class, UserModel.class);


	public static UserModel findByMail(String mail)	{
		Query<UserModel> query = finder.getDatastore().createQuery(UserModel.class);
		return query.field("email").equal(mail).get();
	}
	
	public static UserModel findByUsername(String username)	{
		Query<UserModel> query = finder.getDatastore().createQuery(UserModel.class);
		return query.field("username").equal(username).get();
	}
	
	public static UserModel findByMailPass(String mail, String password)	{
		Query<UserModel> query = finder.getDatastore().createQuery(UserModel.class);
		return query.field("email").equal(mail).field("password").equal(password).get();
	}
	
	public static UserModel findByUsernamePass(String username, String password)	{
		Query<UserModel> query = finder.getDatastore().createQuery(UserModel.class);
		return query.field("username").equal(username).field("password").equal(password).get();
	}
	
	public String getId() {
		return id.toString();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	
}
