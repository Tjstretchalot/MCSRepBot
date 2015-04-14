package me.timothy.mcsrepbot.summons;

import me.timothy.bots.Database;
import me.timothy.bots.FileConfiguration;
import me.timothy.bots.responses.ResponseFormatter;
import me.timothy.bots.responses.ResponseInfo;
import me.timothy.bots.summon.LinkSummon;
import me.timothy.bots.summon.SummonResponse;
import me.timothy.jreddit.info.Link;
import me.timothy.mcsrepbot.MCSRepDatabase;
import me.timothy.mcsrepbot.models.Response;

/**
 * Checks a user by linking to there MCSRep account and
 * any additional information that the bot parses from the
 * database
 * 
 * @author Timothy
 */
public class CheckSummon implements LinkSummon {

	@Override
	public SummonResponse handleLink(Link link, Database database,
			FileConfiguration config) {
		MCSRepDatabase db = (MCSRepDatabase) database;
		Response response = db.getResponseByTitle("check");
		ResponseInfo respInfo = new ResponseInfo();
		respInfo.addTemporaryString("author", link.author());
		
		ResponseFormatter formatter = new ResponseFormatter(response.body, respInfo);
				
		return new SummonResponse(SummonResponse.ResponseType.VALID, formatter.getFormattedResponse(config, database));
	}

}
