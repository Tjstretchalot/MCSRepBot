package me.timothy.mcsrepbot.summons;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import me.timothy.bots.Database;
import me.timothy.bots.FileConfiguration;
import me.timothy.bots.responses.ResponseFormatter;
import me.timothy.bots.responses.ResponseInfo;
import me.timothy.bots.summon.LinkSummon;
import me.timothy.bots.summon.SummonResponse;
import me.timothy.jreddit.info.Link;
import me.timothy.mcsrepbot.MCSRepDatabase;
import me.timothy.mcsrepbot.MCSUtils;
import me.timothy.mcsrepbot.models.MCSUser;
import me.timothy.mcsrepbot.models.Response;

/**
 * Checks a user by linking to there MCSRep account and
 * any additional information that the bot parses from the
 * database
 * 
 * @author Timothy
 */
public class CheckSummon implements LinkSummon {
	private static final DateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
	
	@Override
	public SummonResponse handleLink(Link link, Database database,
			FileConfiguration config) {
		MCSRepDatabase db = (MCSRepDatabase) database;
		ResponseInfo respInfo = new ResponseInfo();
		respInfo.addTemporaryString("author", link.author());
		
		MCSUser user = db.getUserByUsername(respInfo.getObject("author").toString());
		MCSUtils.ensureUpToDate(db, user);
		
		ResponseFormatter formatter = null;
		if(user.repURL != null) {
			respInfo.addTemporaryString("mcsrepurl", user.repURL);
			respInfo.addTemporaryString("last-updated-at", format.format(user.updatedAt));
			Response response = db.getResponseByTitle("check");
			formatter = new ResponseFormatter(response.body, respInfo);
		}else {
			Response response = db.getResponseByTitle("check-no-rep");
			formatter = new ResponseFormatter(response.body, respInfo);
		}
				
		return new SummonResponse(SummonResponse.ResponseType.VALID, formatter.getFormattedResponse(config, database));
	}

}
