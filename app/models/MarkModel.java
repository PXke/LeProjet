package models;

import java.util.List;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Reference;
import com.github.jmkgreen.morphia.query.Query;

import leodagdag.play2morphia.Model;
import lib.interfaces.Event;

public class MarkModel extends Model implements Event {

	public static int baseMark = 5;
	
	@Id
	private ObjectId id;
	private long date;
	private int mark;
	@Reference
	private UserModel owner;
	@Reference
	private ProjectModel project;
	
	public static Finder<ObjectId, MarkModel> finder = new Finder<ObjectId, MarkModel>(ObjectId.class, MarkModel.class);
	
	public static List<MarkModel> allForProject(ProjectModel pro)	{
		Query<MarkModel> query = finder.getDatastore().createQuery(MarkModel.class);
		return query.field("project").equal(pro).asList();
	}
	
	public static MarkModel forProjectAndUser(ProjectModel pro, UserModel user)	{
		Query<MarkModel> query = finder.getDatastore().createQuery(MarkModel.class);
		query = query.field("project").equal(pro).field("owner").equal(user);
		//play.Logger.debug(query.toString());
		return query.get();
	}
	
	@Override
	public String id() {
		return id.toString();
	}

	public void setDate(long timestamp)	{
		date = timestamp;
	}
	
	@Override
	public long getDate() {
		return date;
	}
	
	public void setMark(int mark)	{
		this.mark = mark;
	}
	public int getMark()	{
		return mark;
	}
	
	public void setOwner(UserModel owner)	{
		this.owner = owner;
	}
	public UserModel getOwner()	{
		return owner;
	}

	@Override
	public String getShortDescription() {
		return owner.getUsername()+" has noted <a href=\""+project.getOwner().getUsername()+"/"+project.getName()+"\">"+project.getName()+"</a> ("+mark+"/"+baseMark+")";
	}

	@Override
	public String getInitiatorUsername() {
		return owner.getUsername();
	}

	public ProjectModel getProject() {
		return project;
	}

	public void setProject(ProjectModel project) {
		this.project = project;
	}

}
