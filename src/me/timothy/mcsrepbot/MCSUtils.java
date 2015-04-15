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
	
	private static final long UPDATE_EVERY_MS = 1000 * 60 * 60 * 24 * 7;
	/**
	 * <p>Checks if a user has a profile, and if so it has been
	 * updated recently. Otherwise, fetches the new profile.</p>
	 * 
	 * @param database the database to update
	 * @param user the user to update
	 */
	public static void ensureUpToDate(MCSRepDatabase database, MCSUser user) {
		if(user.repURL == null || user.updatedAt.before(new Timestamp(System.currentTimeMillis() - UPDATE_EVERY_MS))) {
			Link link = fetcher.fetchLink(MCSRepBotMain.mcsRepBot.getUser(), user.username);
			if(link != null) {
				user.repURL = link.url();
				user.updatedAt = new Timestamp(System.currentTimeMillis());
				database.updateOrSaveMCSUser(user);
			}
		}
	}
}
