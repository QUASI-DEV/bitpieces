package com.heretic.bitpieces_practice.web_service;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.DBException;

import spark.Request;
import spark.Response;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.heretic.bitpieces_practice.actions.Actions;
import com.heretic.bitpieces_practice.tools.UnitConverter;
import com.heretic.bitpieces_practice.tools.Tools;
import com.heretic.bitpieces_practice.tools.Tools.UserType;
import com.heretic.bitpieces_practice.tools.UserTypeAndId;

public class WebService {

	// How long to keep the cookies
	public static final Integer COOKIE_EXPIRE_SECONDS = cookieExpiration(180);
	public static final String SESSION_FILE_LOC = System.getProperty( "user.home" ) + "/session.cache";
	public static final String DB_PROP_LOC = System.getProperty( "user.home" ) + "/db.properties";
	public static final List<String> ALLOW_ACCESS_ADDRESSES = Arrays.asList("http://localhost", "http://68.56.177.238:8080");

	// Use an expiring map to store the authenticated sessions
	public static Cache<String, UserTypeAndId> SESSION_TO_USER_MAP = CacheBuilder.newBuilder()
			.maximumSize(10000)
			.expireAfterAccess(COOKIE_EXPIRE_SECONDS, TimeUnit.SECONDS) // expire it after its been accessed
			.build();

	private static final Gson GSON = new Gson();
	private static Logger log = Logger.getLogger(WebService.class.getName());
	public static void main(String[] args) {

		Properties prop = Tools.loadProperties(DB_PROP_LOC);

		SESSION_TO_USER_MAP.putAll(Tools.readObjectFromFile(SESSION_FILE_LOC));

		// Get an instance of the currency/precision converter
		UnitConverter sf = new UnitConverter();
		
		
		get("/session", (req,res) -> {

			allowResponseHeaders(req, res);

			// Give the session id
			return "derp";
		});

		get("/hello", (req, res) -> {
			allowResponseHeaders(req, res);
			return "hi from the bitpieces web service";
		});
		get("/help", (req, res) -> {

			res.redirect("/hello");
			return null;
		});
		get("/", (req, res) -> {
			res.redirect("/hello");
			return null;
		});

		get("/:auth/testauth", (req, res) -> {
			allowResponseHeaders(req, res);
			return "Heyyy u!";

		});

		get("/:auth/getpiecesownedtotal", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);
				json = Actions.getPiecesOwnedTotal(uid.getId());

				dbClose();

				System.out.println(json);

			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});

		get("/:auth/get_pieces_owned_value_accum", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				// get currency if one exists

				json = WebTools.getPiecesOwnedValueAccumSeriesJson(uid.getId(), req.body());


				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});

		get("/:auth/get_pieces_owned_value_current", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				// get currency if one exists

				json = WebTools.getPiecesOwnedValueCurrentSeriesJson(uid.getId(), req.body());


				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});

		get("/:auth/:creator/get_pieces_owned_value_current", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);
				String creatorName = req.params(":creator");
				// get currency if one exists

				json = WebTools.getPiecesOwnedValueCurrentSeriesJson(uid.getId(), creatorName, req.body());


				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});

		get("/:auth/:creator/get_pieces_owned_current", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);
				String creatorName = req.params(":creator");
				// get currency if one exists

				json = WebTools.getPiecesOwnedCurrentSeriesJson(uid.getId(), creatorName, req.body());


				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});

		get("/:auth/get_prices_for_user", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				// get currency if one exists

				json = WebTools.getPricesForUserSeriesJson(uid.getId(), req.body());

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});

		get("/:auth/get_rewards_earned_accum", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				// get currency if one exists

				json = WebTools.getRewardsEarnedAccumSeriesJson(uid.getId(), req.body());


				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});
		
		get("/:auth/get_rewards_earned_total", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				// get currency if one exists

				json = WebTools.getRewardsEarnedTotalJson(uid.getId(), req.body());

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});

		get("/:auth/get_pieces_owned_accum", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				// get currency if one exists

				json = WebTools.getPiecesOwnedAccumSeriesJson(uid.getId(), req.body());


				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});
		get("/:auth/get_users_funds_accum", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				// get currency if one exists

				json = WebTools.getUsersFundsAccumSeriesJson(uid.getId(), req.body());


				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});

		get("/:auth/get_user_data", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				// get currency if one exists

				json = WebTools.getUsersDataJson(uid.getId(), req.body());


				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});

		get("/:auth/get_users_transactions", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);
				
				// get currency if one exists

				json = WebTools.getUsersTransactionsJson(uid.getId(), req.body(), sf);


				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;


		});

		get("/:auth/get_users_activity", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				// get currency if one exists
				json = WebTools.getUsersActivityJson(uid.getId(), sf);


				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;

		});

		get("/:auth/get_users_funds_current", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				// get currency if one exists
				json = WebTools.getUsersFundsCurrentJson(uid.getId(), sf);

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;

		});

		get("/:auth/get_rewards_earned_total_by_user", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				// get currency if one exists
				json = WebTools.getRewardsEarnedTotalByUserJson(uid.getId(), sf);

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;

		});

		get("/:auth/get_pieces_value_current_by_owner", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				// get currency if one exists
				json = WebTools.getPiecesValueCurrentByOwnerJson(uid.getId(), sf);

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;

		});

		get("/:auth/get_users_reputation", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				// get currency if one exists
				json = WebTools.getUsersReputationJson(uid.getId(), req.body());

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;

		});

		get("/:auth/get_users_bids_asks_current", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				// get currency if one exists
				json = WebTools.getUsersBidsAsksCurrentJson(uid.getId(), sf);

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;

		});


		post("/:auth/placebid", (req, res) -> {
			String message = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				message = WebTools.placeBid(uid.getId(), req.body());

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});

		post("/:auth/placeask", (req, res) -> {
			allowResponseHeaders(req, res);

			dbInit(prop);

			// get the creator id from the token
			UserTypeAndId uid = getUserFromCookie(req);
			String message = null;
			try {
				message = WebTools.placeAsk(uid.getId(), req.body());
			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}

			dbClose();


			return message;

		});

		post("/:auth/placebuy", (req, res) -> {
			String message = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				message = WebTools.placeBuy(uid.getId(), req.body());

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});

		post("/:auth/delete_bid_ask", (req, res) -> {
			String message = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				message = WebTools.deleteBidAsk(uid.getId(), req.body());

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});

		post("/:auth/make_deposit_fake", (req, res) -> {
			String message = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				message = WebTools.makeDepositFake(uid.getId(), req.body());

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});
		
		get("/:auth/get_users_settings", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				// get currency if one exists
				json = WebTools.getUsersSettingsJson(uid.getId());

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;

		});
		
		post("/:auth/save_users_settings", (req, res) -> {
			String json = null;
			try {
				UserTypeAndId uid = standardInit(prop, res, req);
				verifyUser(uid);

				// get currency if one exists
				json = WebTools.saveUsersSettings(uid.getId(), req.body());

				dbClose();

				System.out.println(json);
			} catch (NoSuchElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;

		});

		get("/creators_search/:query", (req, res) -> {
			allowResponseHeaders(req, res);
			dbInit(prop);
			
			String query = req.params(":query");

			String json = WebTools.creatorsSearchJson(query);

			dbClose();

			System.out.println(json);
			return json;


		});
		
		get("/get_categories", (req, res) -> {
			allowResponseHeaders(req, res);
			dbInit(prop);

			String json = WebTools.getCategoriesJson(req.body());

			dbClose();

			System.out.println(json);
			return json;


		});
		
		get("/get_currencies", (req, res) -> {
			allowResponseHeaders(req, res);
			dbInit(prop);

			String json = WebTools.getCurrenciesJson(req.body());

			dbClose();

			System.out.println(json);
			return json;


		});
		
		post("/discover", (req, res) -> {
			allowResponseHeaders(req, res);
			dbInit(prop);

			String json = WebTools.getDiscoverJson(req.body());

			dbClose();

			System.out.println(json);
			return json;


		});

		post("/:auth/user_logout", (req, res) -> {
			allowResponseHeaders(req, res);


			String auth = req.params(":auth");


			// remove the key, and save the map
			SESSION_TO_USER_MAP.invalidate(auth);
			writeCacheToFile();



			return "Logged out";

		});
		
		





		post("/registeruser", (req, res) -> {
			allowResponseHeaders(req, res);
			dbInit(prop);

			// Create the user
			UserTypeAndId uid = Actions.createUserFromAjax(req.body());

			dbClose();

			// Its null if it couldn't create the user, usually cause of constraints
			if (uid != null) {
				verifyLoginAndSetCookies(uid, res);

				return "user registered";
			} else {

				res.status(666);
				return "User already exists";
			}

		});

		post("/registercreator", (req, res) -> {
			allowResponseHeaders(req, res);
			dbInit(prop);

			// Create the user
			UserTypeAndId uid = Actions.createCreatorFromAjax(req.body());

			dbClose();

			// Its null if it couldn't create the user, usually cause of constraints
			if (uid != null) {
				verifyLoginAndSetCookies(uid, res);

				return "creator registered";
			} else {

				res.status(666);
				return "Creator already exists";
			}

		});

		post("/userlogin", (req, res) -> {
			System.out.println(req.headers("Origin"));
			allowResponseHeaders(req, res);

			dbInit(prop);

			// log the user in
			UserTypeAndId uid = Actions.userLogin(req.body());

			dbClose();

			String message = verifyLoginAndSetCookies(uid, res);

			return message;

		});

		post("/creatorlogin", (req, res) -> {
			allowResponseHeaders(req, res);

			dbInit(prop);

			// log the user in
			UserTypeAndId uid = Actions.creatorLogin(req.body());

			dbClose();


			String message = verifyLoginAndSetCookies(uid, res);

			return message;

		});




		post("/:auth/savecreatorpage", (req, res) -> {
			String message = null;
			try {			
				UserTypeAndId cid = standardInit(prop, res, req);
				verifyCreator(cid);



				// get the creator id from the token		
				message = WebTools.saveCreatorPage(cid.getId(), req.body());

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}


			return message;

		});

		get("/:auth/getcreatorpage", (req, res) -> {
			String json = null;
			try {			
				UserTypeAndId cid = standardInit(prop, res, req);
				verifyCreator(cid);

				// get the creator id from the token		
				json = WebTools.getCreatorPageJson(cid.getId(), req.body());

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_pieces_owned_value_current_by_creator", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			try {			

				dbInit(prop);

				// get the creator id from the token		
				json = WebTools.getPiecesOwnedValueCurrentByCreatorJson(creator, req.body());

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_price_per_piece_current", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getPricePerPieceCurrentJson(creator, req.body());

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_rewards_owed", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getRewardsOwedJson(creator, req.body());

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});
		
		get("/:creator/get_pieces_owned_value_current_creator", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getPiecesOwnedValueCurrentCreatorSeriesJson(creator, req.body());

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_backers_current_count", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getBackersCurrentCountJson(creator, req.body());

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_main_body", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getMainBodyJson(creator, req.body());

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_pricing", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getPricesJson(creator, req.body());

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_pricing", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getPricesJson(creator, req.body());

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_worth", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getWorthJson(creator, req.body());

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_bids_asks_current", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			
			UserTypeAndId uid = getUserFromCookie(req);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getBidsAsksCurrentJson(creator, uid.getId(), sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_rewards_pct", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			
			UserTypeAndId uid = getUserFromCookie(req);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getRewardsPctJson(creator, uid.getId(), sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_rewards_owed_to_user", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			UserTypeAndId uid = getUserFromCookie(req);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getRewardsOwedToUserJson(creator,uid.getId(), sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_pieces_issued", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			UserTypeAndId uid = getUserFromCookie(req);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getPiecesIssuedJson(creator, uid.getId(), sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_backers_current", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			
			UserTypeAndId uid = getUserFromCookie(req);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getBackersCurrentJson(creator, uid.getId(), sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_creators_reputation", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getCreatorsReputationJson(creator);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_pieces_available", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getPiecesAvailableJson(creator);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_pieces_owned_total", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getPiecesOwnedTotalJson(creator);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_creators_activity", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			UserTypeAndId uid = getUserFromCookie(req);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getCreatorsActivityJson(creator, uid.getId(), sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_creators_transactions", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			UserTypeAndId uid = getUserFromCookie(req);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getCreatorsTransactionsJson(creator, uid.getId(), sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});
		
		get("/:creator/get_creators_funds_accum", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			UserTypeAndId uid = getUserFromCookie(req);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getCreatorsFundsAccumJson(creator,uid.getId(), sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});

		get("/:creator/get_pieces_issued_most_recent_price", (req, res) -> {
			allowResponseHeaders(req, res);
			String json = null;
			String creator = req.params(":creator");
			UserTypeAndId uid = getUserFromCookie(req);
			try {			

				dbInit(prop);

				// get the creator id from the token	
				json = WebTools.getPiecesIssuedMostRecentPriceJson(creator, uid.getId(), sf);

				dbClose();
			}catch (NoSuchElementException e) {
				e.printStackTrace();
			}

			return json;

		});


	}



	private static void verifyUser(UserTypeAndId uid) throws NoSuchElementException {
		if (uid.getType() != UserType.User) {
			throw new NoSuchElementException("Sorry, not a user");
		}

	}

	private static void verifyCreator(UserTypeAndId uid) throws NoSuchElementException {
		if (uid.getType() != UserType.Creator) {
			throw new NoSuchElementException("Sorry, not a creator");
		}

	}



	private static UserTypeAndId getUserFromCookie(Request req) {
		String authId = req.cookie("authenticated_session_id");

		UserTypeAndId uid = null;
		try {
			uid = SESSION_TO_USER_MAP.getIfPresent(authId);
		} catch(NoSuchElementException e) {
			System.err.println("No such user logged in");
			e.printStackTrace();
		}

		return uid;
	}

	private static String verifyLoginAndSetCookies(UserTypeAndId uid, Response res) {
		if (uid != null) {
			String authenticatedSession = Tools.generateSecureRandom();
			// Put the users ID in the session
			//				req.session().attribute("userId", userId); // put the user id in the session data

			// Store the users Id in a static map, give them a session id
			SESSION_TO_USER_MAP.put(authenticatedSession, uid);
			writeCacheToFile();


			// Set some cookies for that users login
			res.cookie("authenticated_session_id", authenticatedSession, COOKIE_EXPIRE_SECONDS, false);
			res.cookie("username", uid.getUsername(), COOKIE_EXPIRE_SECONDS, false);
			res.cookie("usertype", uid.getType().toString(), COOKIE_EXPIRE_SECONDS, false);

			String json = GSON.toJson(SESSION_TO_USER_MAP);
			System.out.println(json);



			return authenticatedSession;
		} else {
			res.status(666);
			return "Incorrect Username or password";
		}

	}

	private static void writeCacheToFile() {
		Map<String, UserTypeAndId> serializableMap = new HashMap<String, UserTypeAndId>(SESSION_TO_USER_MAP.asMap());
		Tools.writeObjectToFile(serializableMap, SESSION_FILE_LOC);
	}


	private static final void dbInit(Properties prop) {
		try {
			Base.open("com.mysql.jdbc.Driver", 
					prop.getProperty("dburl"), 
					prop.getProperty("dbuser"), 
					prop.getProperty("dbpassword"));
		} catch (DBException e) {
			dbClose();
			dbInit(prop);
		}
	}

	private static final UserTypeAndId standardInit(Properties prop, Response res, Request req) {
		try {
			Base.open("com.mysql.jdbc.Driver", 
					prop.getProperty("dburl"), 
					prop.getProperty("dbuser"), 
					prop.getProperty("dbpassword"));
		} catch (DBException e) {
			dbClose();
			dbInit(prop);
		}

		allowResponseHeaders(req, res);

		UserTypeAndId uid = SESSION_TO_USER_MAP.getIfPresent(req.params(":auth"));

		return uid;
	}



	private static final void dbClose() {
		Base.close();
	}

	public static Integer cookieExpiration(Integer minutes) {
		return minutes*60;
	}

	public static void allowResponseHeaders(Request req, Response res) {
		String origin = req.headers("Origin");
		res.header("Access-Control-Allow-Credentials", "true");
//		System.out.println(origin);
		if (ALLOW_ACCESS_ADDRESSES.contains(req.headers("Origin"))) {
			res.header("Access-Control-Allow-Origin", origin);
		}

	}
}
