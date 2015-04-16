package me.timothy.mcsrepbot.summons;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;

import me.timothy.bots.Database;
import me.timothy.bots.FileConfiguration;
import me.timothy.bots.responses.ResponseFormatter;
import me.timothy.bots.responses.ResponseInfo;
import me.timothy.bots.summon.LinkSummon;
import me.timothy.bots.summon.SummonResponse;
import me.timothy.jreddit.RedditUtils;
import me.timothy.jreddit.info.Account;
import me.timothy.jreddit.info.Link;
import me.timothy.mcsrepbot.MCSRepBotMain;
import me.timothy.mcsrepbot.MCSRepDatabase;
import me.timothy.mcsrepbot.MCSUtils;
import me.timothy.mcsrepbot.models.MCSUser;
import me.timothy.mcsrepbot.models.Response;

import org.json.simple.parser.ParseException;

/**
 * Checks a user by linking to there MCSRep account and
 * any additional information that the bot parses from the
 * database
 * 
 * @author Timothy
 */
public class CheckSummon implements LinkSummon {
	private static final DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
	@Override
	public SummonResponse handleLink(Link link, Database database,
			FileConfiguration config) {
		MCSRepDatabase db = (MCSRepDatabase) database;
		ResponseInfo respInfo = new ResponseInfo();
		respInfo.addTemporaryString("author", link.author());
		
		MCSUser user = db.getUserByUsername(respInfo.getObject("author").toString());
		MCSUtils.ensureUpToDate(db, user);
		
		ResponseFormatter formatter = null;
		
		try {
			Account account = RedditUtils.getAccountFor(MCSRepBotMain.mcsRepBot.getUser(), respInfo.getObject("author").toString());
			
			long accountAge = System.currentTimeMillis() - (long) (account.createdUTC() * 1000);
			Duration accountAgeDuration = Duration.ofMillis(accountAge);
			respInfo.addTemporaryString("account-age", accountAgeDuration.toDays() + " days");
			respInfo.addTemporaryString("link-karma", Integer.toString(account.linkKarma()));
			respInfo.addTemporaryString("comment-karma", Integer.toString(account.commentKarma()));
			respInfo.addTemporaryString("karma", Integer.toString(account.linkKarma() + account.commentKarma()));
		} catch (IOException | ParseException e) {
			throw new RuntimeException(e);
		}
		
		if(user.repURL != null) {
			respInfo.addTemporaryString("mcsrepurl", user.repURL);
			respInfo.addTemporaryString("last-updated-at", dateFormat.format(user.updatedAt));
			Response response = db.getResponseByTitle("check");
			formatter = new ResponseFormatter(response.body, respInfo);
		}else {
			Response response = db.getResponseByTitle("check-no-rep");
			formatter = new ResponseFormatter(response.body, respInfo);
		}
		
		return new SummonResponse(SummonResponse.ResponseType.VALID, formatter.getFormattedResponse(config, database));
	}

}
