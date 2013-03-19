package models;

import java.util.List;

import leodagdag.play2morphia.Model;
import lib.interfaces.Event;
import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Reference;
import com.github.jmkgreen.morphia.query.Query;

@Entity
public class CommitModel extends Model implements Event {

	@Id
	private ObjectId id;
	private String idGithub;
	private long date;
	private String commitMessage;
	private String initiatorUsername;
	private String initiatorUri;
	@Reference
	private ProjectModel project;
	
	private String gravatarUrl;
	
	public static Finder<ObjectId, CommitModel> finder = new Finder<ObjectId, CommitModel>(ObjectId.class, CommitModel.class);
	
	public static List<CommitModel> allForProject(ProjectModel pro)	{
		Query<CommitModel> query = finder.getDatastore().createQuery(CommitModel.class);
		return query.field("project").equal(pro).asList();
	}
	
	public static CommitModel getForIdGithub(String idgithub)	{
		Query<CommitModel> query = finder.getDatastore().createQuery(CommitModel.class);
		return query.field("idGithub").equal(idgithub).get();
	}
	
	public String id()	{
		return id.toString();
	}
	
	public void setDate(long timestamp)	{ this.date = timestamp; }
	
	@Override
	public long getDate() {
		return date;
	}

	@Override
	public String getShortDescription() {
		return commitMessage;
	}

	@Override
	public String getInitiatorUsername() {
		return this.initiatorUsername;
	}

	public String getCommitMessage() {
		return commitMessage;
	}

	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}

	public void setInitiatorUsername(String initiatorUsername) {
		this.initiatorUsername = initiatorUsername;
	}

	public ProjectModel getProject() {
		return project;
	}

	public void setProject(ProjectModel project) {
		this.project = project;
	}



	public String getIdGithub() {
		return idGithub;
	}



	public void setIdGithub(String idGithub) {
		this.idGithub = idGithub;
	}

	public String getGravatarUrl() {
		return gravatarUrl;
	}

	public void setGravatarUrl(String gravatarUrl) {
		this.gravatarUrl = gravatarUrl;
	}

	public String getInitiatorUri() {
		return initiatorUri;
	}

	public void setInitiatorUri(String initiatorUri) {
		this.initiatorUri = initiatorUri;
	}

}
