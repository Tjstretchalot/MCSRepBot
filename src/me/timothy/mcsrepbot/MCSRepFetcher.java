package me.timothy.mcsrepbot;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import me.timothy.jreddit.User;
import me.timothy.jreddit.info.Link;
import me.timothy.jreddit.info.Listing;
import me.timothy.jreddit.requests.Utils;

/**
 * Fetches the MCSRepProfile by searching through
 * /r/MCSRep
 * 
 * @author Timothy
 *
 */
public class MCSRepFetcher {
	private static final String SEARCH_URL = "http://www.reddit.com/r/MCSRep/search.json";
	
	private final Logger logger = LogManager.getLogger();
	/**
	 * Fetches the link for the users profile by searching reddit
	 * 
	 * @param username the user for whom the thread will be for
	 * @return the link if found, or null.
	 */
	public Link fetchLink(User user, String username) {
		try {
			String params = "q=" + URLEncoder.encode(username, "UTF-8") + "&restrict_sr=on&sort=relevance&t=all";
			URL url = new URL(SEARCH_URL);
			
			Object result = Utils.get(params, url, user.getCookie());
			Listing listing = new Listing((JSONObject) result);
			user.setModhash(listing.modhash());
			
			if(listing.numChildren() != 1) {
				logger.info(String.format("Got multiple threads for user %s, choosing most recent", username));
			}
			
			return mostRecentLink(listing);
		} catch (IOException | ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Gets the most recent link from the listing
	 * @param listing the listing to search through
	 * @return the most recent link
	 */
	private Link mostRecentLink(Listing listing) {
		Link best = null;
		double createdAt = Double.MAX_VALUE;
		for(int i = 0; i < listing.numChildren(); i++) {
			Link link = (Link) listing.getChild(i);
			if(link.createdUTC() < createdAt) {
				best = link;
				createdAt = link.createdUTC();
			}
		}
		return best;
	}
}
