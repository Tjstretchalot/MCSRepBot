package me.timothy.mcsrepbot;

import me.timothy.bots.Bot;
import me.timothy.bots.BotDriver;
import me.timothy.bots.Database;
import me.timothy.bots.FileConfiguration;
import me.timothy.bots.summon.CommentSummon;
import me.timothy.bots.summon.LinkSummon;
import me.timothy.bots.summon.PMSummon;
import me.timothy.jreddit.info.Thing;

public class MCSRepBotDriver extends BotDriver {

	public MCSRepBotDriver(Database database, FileConfiguration config,
			Bot bot, CommentSummon[] commentSummons, PMSummon[] pmSummons,
			LinkSummon[] submissionSummons) {
		super(database, config, bot, commentSummons, pmSummons, submissionSummons);
	}

	@Override
	protected void handleReply(Thing replyable, String response) {
		super.handleReply(replyable, response);
		sleepFor(1000 * 60 * 10);
	}

}
