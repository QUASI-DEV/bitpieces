package com.bitpieces.dev.web_service;

import static spark.Spark.post;
import static spark.SparkBase.setPort;

import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;

import com.bitpieces.shared.DataSources;
import com.bitpieces.shared.actions.Actions;
import com.bitpieces.shared.tools.Tools;
import com.bitpieces.shared.tools.UID;
import com.bitpieces.shared.tools.UnitConverter;
import com.bitpieces.shared.tools.WebCommon;
import com.bitpieces.shared.tools.WebTools;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

// 
// java -cp bitpieces_practice-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.bitpieces.dev.web_service.WebService
public class WebService {




	// Use an expiring map to store the authenticated sessions
	private static Cache<String, UID> SESSION_TO_USER_MAP = CacheBuilder.newBuilder()
			.maximumSize(10000)
			.expireAfterAccess(WebCommon.COOKIE_EXPIRE_SECONDS, TimeUnit.SECONDS) // expire it after its been accessed
			.build();


	public static void main(String[] args) {


		// Load the correct db connection
		Properties prop = Tools.loadProperties(DataSources.DEV_DB_PROP);

		// Load the correct session cache
		SESSION_TO_USER_MAP.putAll(Tools.readObjectFromFile(DataSources.DEV_SESSION_FILE));

		// Set the correct port
		setPort(DataSources.DEV_WEB_PORT);

		// Get an instance of the currency/precision converter
		UnitConverter sf = new UnitConverter();

		// Setup all the common gets
		WebCommon.commonGets(SESSION_TO_USER_MAP, prop, sf, DataSources.DEV_SESSION_FILE);

		// Setup all the common posts
		WebCommon.commonPosts(SESSION_TO_USER_MAP, prop, sf, DataSources.DEV_SESSION_FILE);



		post("/registeruser", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			dbInit(prop);

			// Create the user
			UID uid = Actions.createUserDevFromAjax(req.body());

			dbClose();

			// Its null if it couldn't create the user, usually cause of constraints
			if (uid != null) {
				WebCommon.verifyLoginAndSetCookies(uid, res, SESSION_TO_USER_MAP, DataSources.DEV_SESSION_FILE);

				return "user registered";
			} else {

				res.status(666);
				return "User already exists";
			}

		});

		post("/registercreator", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);
			dbInit(prop);

			// Create the user
			UID uid = Actions.createCreatorDevFromAjax(req.body());

			dbClose();

			// Its null if it couldn't create the user, usually cause of constraints
			if (uid != null) {
				WebCommon.verifyLoginAndSetCookies(uid, res, SESSION_TO_USER_MAP, DataSources.DEV_SESSION_FILE);

				return "creator registered";
			} else {

				res.status(666);
				return "Creator already exists";
			}

		});

		post("/placebid", (req, res) -> {
			String message = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID uid = WebCommon.getUserFromCookie(req, SESSION_TO_USER_MAP);

				WebCommon.verifyUser(uid);

				message = WebTools.placeBid(uid, req.body(), sf);

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});

		post("/placeask", (req, res) -> {
			WebCommon.allowResponseHeaders(req, res);

			dbInit(prop);

			// get the creator id from the token
			UID uid = WebCommon.getUserFromCookie(req, SESSION_TO_USER_MAP);
			String message = null;
			try {
				message = WebTools.placeAsk(uid, req.body(), sf);
			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}

			dbClose();


			return message;

		});

		post("/placebuy", (req, res) -> {
			String message = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID uid = WebCommon.getUserFromCookie(req, SESSION_TO_USER_MAP);
				WebCommon.verifyUser(uid);

				message = WebTools.placeBuy(uid, req.body());

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});

		post("/issue_pieces", (req, res) -> {
			String message = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID uid = WebCommon.getUserFromCookie(req, SESSION_TO_USER_MAP);
				WebCommon.verifyCreator(uid);

				message = WebTools.issuePieces(uid, req.body(), sf);

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});

		post("/new_reward", (req, res) -> {
			String message = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID uid = WebCommon.getUserFromCookie(req, SESSION_TO_USER_MAP);
				WebCommon.verifyCreator(uid);

				message = WebTools.newReward(uid, req.body(), sf);

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});

		post("/raise_funds", (req, res) -> {
			String message = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID uid = WebCommon.getUserFromCookie(req, SESSION_TO_USER_MAP);
				WebCommon.verifyCreator(uid);

				message = WebTools.raiseFunds(uid, req.body(), sf);

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});

		post("/delete_bid_ask", (req, res) -> {
			String message = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID uid = WebCommon.getUserFromCookie(req, SESSION_TO_USER_MAP);
				WebCommon.verifyUser(uid);

				message = WebTools.deleteBidAsk(uid, req.body());

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});

		post("/make_deposit_fake", (req, res) -> {
			String message = null;
			try {
				WebCommon.allowResponseHeaders(req, res);
				dbInit(prop);
				UID uid = WebCommon.getUserFromCookie(req, SESSION_TO_USER_MAP);
				WebCommon.verifyUser(uid);

				message = WebTools.makeDepositFake(uid, req.body(), sf);

				dbClose();

			} catch (NoSuchElementException e) {
				res.status(666);
				return e.getMessage();
			}
			return message;

		});


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

	private static final void dbClose() {
		Base.close();
	}






}