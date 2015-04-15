package me.timothy.mcsrepbot;

import java.io.IOException;
import java.util.stream.Collectors;

import me.timothy.bots.Bot;
import me.timothy.bots.BotDriver;
import me.timothy.bots.FileConfiguration;
import me.timothy.bots.summon.CommentSummon;
import me.timothy.bots.summon.LinkSummon;
import me.timothy.bots.summon.PMSummon;
import me.timothy.jreddit.requests.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Runs the MCSRepBot by loading necessary configurations,
 * connecting to the database, and logging into reddit.
 * 
 * @author Timothy
 */
public class MCSRepBotMain {
	/**
	 * How do I refer to myself?
	 */
	public static final String BOT_NAME = "MCSRepBot";
	
	/**
	 * Who made this?
	 */
	public static final String BOT_AUTHOR = "Tjstretchalot";
	
	/**
	 * The MCSRepBot
	 */
	public static Bot mcsRepBot;
	
	public static void main(String[] args) {
		Logger logger = LogManager.getLogger();
		
		Utils.USER_AGENT = BOT_NAME + " by /u/" + BOT_AUTHOR;
		
		logger.debug("Loading configuration..");
		FileConfiguration config = new FileConfiguration();
		try {
			config.addProperties("database", true, "username", "password", "url");
			config.addList("subreddits", true);
		} catch (NullPointerException | IOException e) {
			logger.throwing(e);
			return;
		}
		
		logger.debug("Connecting to the database..");
		MCSRepDatabase database = new MCSRepDatabase(config.getProperty("database.url"),
						config.getProperty("database.username"), 
						config.getProperty("database.password"));
		
		logger.debug("Connecting to reddit..");
		mcsRepBot = new Bot(config.getList("subreddits").stream().collect(Collectors.joining("+")));
		
		BotDriver botDriver = new BotDriver(database, config, mcsRepBot,
				new CommentSummon[]{}, new PMSummon[]{}, new LinkSummon[]{});
		
		while(true) {
			try {
				botDriver.run();
			}catch(Exception ex) {
				logger.catching(ex);
			}
		}
	}
}
