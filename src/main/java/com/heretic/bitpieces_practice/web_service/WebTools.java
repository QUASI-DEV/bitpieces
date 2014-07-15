package com.heretic.bitpieces_practice.web_service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;

import com.heretic.bitpieces_practice.actions.Actions;
import com.heretic.bitpieces_practice.tables.Tables.Creator;
import com.heretic.bitpieces_practice.tables.Tables.Creators_page_fields;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_accum;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_value_accum;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_value_current;
import com.heretic.bitpieces_practice.tables.Tables.Pieces_owned_value_current_by_owner;
import com.heretic.bitpieces_practice.tables.Tables.Prices_for_user;
import com.heretic.bitpieces_practice.tables.Tables.Rewards_earned;
import com.heretic.bitpieces_practice.tables.Tables.Rewards_earned_total_by_user;
import com.heretic.bitpieces_practice.tables.Tables.User;
import com.heretic.bitpieces_practice.tables.Tables.Users_activity;
import com.heretic.bitpieces_practice.tables.Tables.Users_funds_accum;
import com.heretic.bitpieces_practice.tables.Tables.Users_funds_current;
import com.heretic.bitpieces_practice.tables.Tables.Users_reputation;
import com.heretic.bitpieces_practice.tables.Tables.Users_transactions;
import com.heretic.bitpieces_practice.tools.Tools;

public class WebTools {



	public static String saveCreatorPage(String id, String reqBody) {

		Map<String, String> postMap = Tools.createMapFromAjaxPost(reqBody);

		Creators_page_fields page = Creators_page_fields.findFirst("creators_id = ?",  id);
		Creator creator = Creator.findById(id);
		String username = creator.getString("username");

		// The first time filling the page fields
		if (page == null) {
			page = Creators_page_fields.createIt("creators_id", id,
					"main_body", postMap.get("main_body"));
		} else {
			page.set("main_body", postMap.get("main_body")).saveIt();
		}

		// Save the html page
		HTMLTools.saveCreatorHTMLPage(username, page);

		return "Successful";

	}



	public static String placeBid(String userId, String body) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);

		Actions.createBid(userId, 
				postMap.get("creatorid"), 
				Integer.valueOf(postMap.get("pieces")), 
				Double.valueOf(postMap.get("bid")), 
				postMap.get("validUntil"), 
				true);


		return body;
	}

	public static String placeAsk(String userId, String body) {
		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);

		Actions.createAsk(userId, 
				postMap.get("creatorid"), 
				Integer.valueOf(postMap.get("pieces")), 
				Double.valueOf(postMap.get("ask")), 
				postMap.get("validUntil"), 
				true);


		return body;
	}

	public static String getPiecesOwnedValueAccumSeriesJson(String userId, String body) {
		//		Map<String, String> postMap = Tools.createMapFromAjaxPost(body);

		// First fetch from the table
		List<Model> list = Pieces_owned_value_accum.find("owners_id=?", userId);

		return createHighChartsJSONForMultipleCreators(list, "price_time_", "value_accum", "creators_username");

	}

	public static String getPiecesOwnedAccumSeriesJson(String userId,
			String body) {

		// First fetch from the table
		List<Model> list = Pieces_owned_accum.find("owners_id=?", userId);

		return createHighChartsJSONForMultipleCreators(list, "start_time_", "pieces_accum", "creators_username");
	}



	public static String getPricesForUserSeriesJson(String userId, String body) {

		// First fetch from the table
		List<Model> list = Prices_for_user.find("owners_id=?", userId);

		return createHighChartsJSONForMultipleCreators(list, "time_", "price_per_piece", "creators_username");


	}

	public static String getPiecesOwnedValueCurrentSeriesJson(String userId, String body) {

		// First fetch from the table
		List<Model> list = Pieces_owned_value_current.find("owners_id=?", userId);


		return createHighChartsJSONForCurrent(list, "value_total", "creators_username");

	}
	
	public static String getRewardsEarnedSeriesJson(String userId, String body) {
		// First fetch from the table
		List<Model> list = Rewards_earned.find("owners_id=?", userId);

		return createHighChartsJSONForMultipleCreators(list, "price_time_", "reward_earned", "creators_username");
	}
	
	public static String getUsersFundsAccumSeriesJson(String userId, String body) {
		// First fetch from the table
		List<Model> list = Users_funds_accum.find("users_id=?", userId);

		return createHighChartsJSONForSingleCreator(list, "time_", "funds_accum", "Funds");
	}
	
	public static String getUsersDataJson(String userId, String body) {
		User user  = User.findById(userId);
		
		String json = user.toJson(false, "email", "username");
		System.out.println(json);
		
		return json;
	}
	
	public static String getUsersTransactionsJson(String userId, String body) {
		
		List<Model> list = Users_transactions.find("users_id=?",  userId);
	
		return createTableJSON(list);

	}
	
	public static String getUsersActivityJson(String userId, String body) {
		
		List<Model> list = Users_activity.find("users_id=?",  userId);
	
		return createTableJSON(list);

	}
	
	public static String getUsersFundsCurrentJson(String userId, String body) {
		
		Users_funds_current usersFundsCurrent = Users_funds_current.findFirst("users_id=?",  userId);
	
		String json = usersFundsCurrent.getString("current_funds");
		return json;

	}
	
	public static String getRewardsEarnedTotalByUserJson(String userId, String body) {
		
		Rewards_earned_total_by_user rewardsEarned = Rewards_earned_total_by_user.findFirst("owners_id=?",  userId);
	
		String json = rewardsEarned.getString("reward_earned_total");
		return json;

	}
	
	public static String getPiecesValueCurrentByOwnerJson(String userId, String body) {
		
		Pieces_owned_value_current_by_owner value = Pieces_owned_value_current_by_owner.findFirst("owners_id=?",  userId);
	
		String json = value.getString("value_total");
		return json;

	}
	
	public static String getUsersReputationJson(String userId, String body) {
		
		Users_reputation value = Users_reputation.findFirst("users_id=?",  userId);
	
		String json = value.getString("reputation");
		System.out.println(json);
		return json;

	}
	
	
public static String creatorsSearchJson(String query) {
	List<Model> list = Creator.find("username like '%" + query + "%'");
	
	String json = createTableJSON(list, "id", "username");
	
	return json;
	
	
}
	
	
	public static String createTableJSON(List<Model> list, String... params) {
		

		
		String json = "[";
		for (int i = 0; i < list.size(); i++) {
			if (params != null) {
				json += list.get(i).toJson(false, params); 
			} else {
				json += list.get(i).toJson(false);
			}
			if (i < list.size()-1) {
				json += ",";
			}
		}
		json += "]";
		
		System.out.println(json);
		
		return json;
	}
	
	public static String createTableJSON(List<Model> list) {
		return createTableJSON(list, null);
	}




	public static String createHighChartsJSONForMultipleCreators(List<Model> list, String dateColName,
			String valueColName, String creatorsIdentifier) {
	
		List<Map<String, String>> listOfMaps = new ArrayList<Map<String, String>>();
	
		List<String[]> oneCreatorsData = new ArrayList<String[]>();
	
		for (int i = 0; i < list.size(); i++) {
			Model p = list.get(i);
	
	
			System.out.println(p);
			String UTCTime = p.getString(dateColName);
			//			String highchartsDate = convertDateStrToHighchartsDateUTCString(UTCTime);
			String millis = convertDateStrToMillis(UTCTime);
			String val = p.getString(valueColName);
	
			String[] pair = {millis, val};
			oneCreatorsData.add(pair);
	
			String cCreatorsId = p.getString(creatorsIdentifier);
	
			// If its the last one, add it to the map
			if (i == list.size() -1) {
				String oneCreatorsDataStr = Tools.GSON.toJson(oneCreatorsData).replaceAll("\"", "");
	
				Map<String, String> map = new LinkedHashMap<String, String>();
				System.out.println(cCreatorsId);
				map.put("name", cCreatorsId);
				map.put("data", oneCreatorsDataStr);
	
				listOfMaps.add(map);
			} else {
				String nextCreatorsId = list.get(i+1).getString(creatorsIdentifier);
	
				if (!cCreatorsId.equals(nextCreatorsId)) {
					String oneCreatorsDataStr = Tools.GSON.toJson(oneCreatorsData).replaceAll("\"", "");
					//					String oneCreatorsDataStr = Arrays.toString(oneCreatorsData.toArray());
					Map<String, String> map = new LinkedHashMap<String, String>();
					System.out.println(cCreatorsId);
					map.put("name", cCreatorsId);
					map.put("data", oneCreatorsDataStr);
	
					listOfMaps.add(map);
					oneCreatorsData.clear();
				}
	
			}
	
		}
	
		String listOfMapsStr = Tools.GSON.toJson(listOfMaps).replaceAll("\"\\[", "[").replaceAll("\\]\"", "]");
	
		System.out.println(listOfMapsStr);
	
	
	
		return listOfMapsStr;
	
	}



	public static String createHighChartsJSONForSingleCreator(List<Model> list, String dateColName,
			String valueColName, String seriesName) {
	
		List<Map<String, String>> listOfMaps = new ArrayList<Map<String, String>>();
	
		List<String[]> oneCreatorsData = new ArrayList<String[]>();
	
		for (int i = 0; i < list.size(); i++) {
			Model p = list.get(i);
	
	
			System.out.println(p);
			String UTCTime = p.getString(dateColName);
			//			String highchartsDate = convertDateStrToHighchartsDateUTCString(UTCTime);
			String millis = convertDateStrToMillis(UTCTime);
			String val = p.getString(valueColName);
	
			String[] pair = {millis, val};
			oneCreatorsData.add(pair);
	
	
			// If its the last one, add it to the map
			if (i == list.size() -1) {
				String oneCreatorsDataStr = Tools.GSON.toJson(oneCreatorsData).replaceAll("\"", "");
	
				Map<String, String> map = new LinkedHashMap<String, String>();
				map.put("name", seriesName);
				map.put("data", oneCreatorsDataStr);
	
				listOfMaps.add(map);
			} 
	
		}
	
		String listOfMapsStr = Tools.GSON.toJson(listOfMaps).replaceAll("\"\\[", "[").replaceAll("\\]\"", "]");
	
		System.out.println(listOfMapsStr);
	
	
	
		return listOfMapsStr;
	
	}



	public static String createHighChartsJSONForCurrent(List<Model> list, 
			String valueColName, String creatorsIdentifier) {
	
		List<String[]> data = new ArrayList<String[]>();
	
		for (int i = 0; i < list.size(); i++) {
			Model p = list.get(i);
			System.out.println(p);
	
			String val = p.getString(valueColName);
			String cCreatorsId = p.getString(creatorsIdentifier);
			String[] pair = {cCreatorsId, val};
			data.add(pair);
		}
	
	
		String arrayStr = Tools.GSON.toJson(data).replaceAll(",\"", ",").replaceAll("\"]","]");
	
		System.out.println(arrayStr);
	
		return arrayStr;
	
	}



	// Sample highcharts data
	// http://jsfiddle.net/gh/get/jquery/1.7.2/highslide-software/highcharts.com/tree/master/samples/highcharts/series/data-array-of-arrays-datetime/
	// [Date.UTC(2010, 5, 1), 71.5], 
	// [Date.UTC(2010, 10, 1), 106.4]
	@Deprecated
	public static String convertDateStrToHighchartsDateUTCString(String UTC) {
		String split[] = UTC.split("-|:|\\s");
		String str = "Date.UTC(" + split[0] + ", " + split[1] + ", " + split[2] + ", " + split[3] 
				+ ", " + split[4] + ", " + split[5] + ")";

		return str;



	}

	public static String convertDateStrToMillis(String UTC) {
		try {
			return String.valueOf(Tools.SDF.get().parse(UTC).getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}













}
