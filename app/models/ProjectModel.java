package models;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	private Map<String, Integer> notes; 
	@Reference
	private List<UserModel> contributors;
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
	
	public Map<String, Integer> getNotes()	{
		return notes;
	}
	
	public void addNote(UserModel user, int note)	{
		notes.put(user.getId(), note);
	}

	public List<UserModel> getContributors() {
		return contributors;
	}

	public void addContributor(UserModel contributor) {
		this.contributors.add( contributor );
	}
	
	public void delContributor(UserModel contributor)	{
		int i = 0;
		while(i < contributors.size() && contributors.get(i).getId().compareTo(contributor.getId()) != 0)	{
			i++;
		}
		if(i < contributors.size())	{
			contributors.remove(i);
		}
	}
	
	public float averageNote()	{
		int sum = 0;
		for(Entry<String, Integer> e : notes.entrySet()){
			sum+= e.getValue();
		}
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
	
}
