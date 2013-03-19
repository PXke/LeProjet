package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Reference;
import com.github.jmkgreen.morphia.query.Query;

import leodagdag.play2morphia.Model;
import lib.interfaces.Event;

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
		return MarkModel.allForProject(this);
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
		List<MarkModel> marks = getNotes();
		for(MarkModel e : marks){
			sum+= e.getMark();
		}
		if(marks.size() == 0)
			return 0;
		else
			return ((float)sum)/((float)marks.size());
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
		List<CommitModel> lst = CommitModel.allForProject(this);
		Collections.sort(lst, new Comparator<Event>() {
			@Override
			public int compare(Event o1, Event o2) {
				if (o1.getDate() < o2.getDate())
					return 1;
				else
					return -1;
			}
		});
		return lst;
	}
	
}
