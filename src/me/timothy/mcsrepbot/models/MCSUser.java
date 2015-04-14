package me.timothy.mcsrepbot.models;

import java.sql.Timestamp;

/**
 * Describes additional information we have on a user, such
 * as their cached MCSRep profile.
 * 
 * @author Timothy
 */
public class MCSUser {
	public int id;
	public String username;
	public String repURL;
	
	public Timestamp createdAt;
	public Timestamp updatedAt;
	
	/**
	 * Initialize the user
	 * @param id the id
	 * @param username the username
	 * @param repURL the MCS Rep URL
	 * @param createdAt when we found out about this user
	 * @param updatedAt when we last updated this users info
	 */
	public MCSUser(int id, String username, String repURL, Timestamp createdAt,
			Timestamp updatedAt) {
		this.id = id;
		this.username = username;
		this.repURL = repURL;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", repURL="
				+ repURL + ", createdAt=" + createdAt + ", updatedAt="
				+ updatedAt + "]";
	}
	
	/**
	 * Checks if this can be saved into the database
	 * without obviously breaking something
	 * 
	 * @return if this is maybe valid
	 */
	public boolean isValid() {
		return (id != 0) && (username != null) && (repURL != null) && (createdAt != null) && (updatedAt != null);
	}
}
