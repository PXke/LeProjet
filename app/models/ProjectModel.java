package models;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Reference;
import com.github.jmkgreen.morphia.query.Query;

import leodagdag.play2morphia.Model;

@Entity
public class ProjectModel extends Model {

	@Id
	private ObjectId id;
	private String githubUrl;
	private String name;
	private String branch;
	private long date;
	@Reference
	private UserModel owner;
	@Reference
	private List<UserModel> contributors;
	@Reference
	private List<UserModel> followers;
	@Reference
	private List<MarkModel> notes;
	
	private String description;
	
	public static Finder<ObjectId, ProjectModel> finder = new Finder<ObjectId, ProjectModel>(ObjectId.class, ProjectModel.class);
	
	public static List<ProjectModel> projectForUser(UserModel user)	{
		Query<ProjectModel> query = finder.getDatastore().createQuery(ProjectModel.class);
		return query.field("owner").equal(user).asList();
	}
	
	public static ProjectModel findByName(String name)	{
		Query<ProjectModel> query = finder.getDatastore().createQuery(ProjectModel.class);
		return query.field("name").equal(name).get();
	}
	
	public static ProjectModel findByUrl(String url)	{
		Query<ProjectModel> query = finder.getDatastore().createQuery(ProjectModel.class);
		return query.field("githubUrl").equal(url).get();
	}
	
	public static List<ProjectModel> allFollowedBy(UserModel user)	{
		Query<ProjectModel> query = finder.getDatastore().createQuery(ProjectModel.class);
		return query.field("followers").hasThisElement(user).asList();
	}
	
	public ProjectModel()	{
		notes = new ArrayList<MarkModel>();
		contributors = new ArrayList<UserModel>();
		followers = new ArrayList<UserModel>();
	}
	
	public String id()	{
		return id.toString();
	}

	public String getGithubUrl() {
		return githubUrl;
	}

	public void setGithubUrl(String githubUrl) {
		this.githubUrl = githubUrl;
	}

	public UserModel getOwner() {
		return owner;
	}

	public void setOwner(UserModel owner) {
		this.owner = owner;
	}
	
	public List<MarkModel> getNotes()	{
		return notes;
	}
	
	public void addNote(MarkModel mark)	{
		notes.add(mark);
	}

	public List<UserModel> getContributors() {
		return contributors;
	}

	public void addContributor(UserModel contributor) {
		this.contributors.add( contributor );
	}
	
	public void delContributor(UserModel contributor)	{
		int i = 0;
		while(i < contributors.size() && contributors.get(i).id().compareTo(contributor.id()) != 0)	{
			i++;
		}
		if(i < contributors.size())	{
			contributors.remove(i);
		}
	}
	
	public float averageNote()	{
		int sum = 0;
		for(MarkModel e : notes){
			sum+= e.getMark();
		}
		if(notes.size() == 0)
			return 0;
		else
			return ((float)sum)/((float)notes.size());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public List<UserModel> getFollowers() {
		return followers;
	}

	public void addFollowersr(UserModel follower) {
		this.followers.add( follower );
	}
	
	public void delFollowers(UserModel follower)	{
		int i = 0;
		while(i < followers.size() && followers.get(i).id().compareTo(follower.id()) != 0)	{
			i++;
		}
		if(i < followers.size())	{
			followers.remove(i);
		}
	}
	
	public List<CommitModel> getCommits()	{
		return CommitModel.allForProject(this);
	}
	
}
