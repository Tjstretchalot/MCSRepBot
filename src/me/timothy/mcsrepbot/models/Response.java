package me.timothy.mcsrepbot.models;

import java.sql.Timestamp;

/**
 * Contains the format of the response that the bot uses,
 * acquired from the database for convienent editing.
 * 
 * @author Timothy
 */
public class Response {
	public int id;
	public String title;
	public String body;
	
	public Timestamp createdAt;
	public Timestamp updatedAt;
	
	public Response(int id, String title, String body, Timestamp createdAt, Timestamp updatedAt) {
		this.id = id;
		this.title = title;
		this.body = body;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "Response [id=" + id + ", title=" + title + ", body=" + body
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
}
