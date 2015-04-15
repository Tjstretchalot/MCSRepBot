package me.timothy.mcsrepbot;

import java.sql.Timestamp;

import me.timothy.jreddit.info.Link;
import me.timothy.mcsrepbot.models.MCSUser;

/**
 * A collection of utility functions that may be used
 * in many places.
 * 
 * @author Timothy
 */
public class MCSUtils {
	private static final MCSRepFetcher fetcher = new MCSRepFetcher();
	
	/**
	 * <p>Checks if a user has a profile, and if so it has been
	 * updated recently. Otherwise, fetches the new profile.</p>
	 * 
	 * <p>Does not save the user, but does update updatedAt</p>
	 * 
	 * @param user the user to update
	 */
	public static void ensureUpToDate(MCSUser user) {
		if(user.repURL == null) {
			Link link = fetcher.fetchLink(MCSRepBotMain.mcsRepBot.getUser(), user.username);
			if(link != null) {
				user.repURL = link.url();
				user.updatedAt = new Timestamp(System.currentTimeMillis());
			}
		}
	}
}
