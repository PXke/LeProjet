package models;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.annotations.Reference;

import leodagdag.play2morphia.Model;
import lib.interfaces.Event;

public class MarkModel extends Model implements Event {

	public static int baseMark = 5;
	
	private ObjectId id;
	private long date;
	private int mark;
	@Reference
	private UserModel owner;
	
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
		return owner.getUsername()+" has noted this project ("+mark+"/"+baseMark+")";
	}

	@Override
	public String getInitiatorUsername() {
		return owner.getUsername();
	}

}
