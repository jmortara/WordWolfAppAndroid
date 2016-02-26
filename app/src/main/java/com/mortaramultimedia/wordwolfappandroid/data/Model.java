package com.mortaramultimedia.wordwolfappandroid.data;

import com.mortaramultimedia.wordwolf.shared.messages.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;


/**
 * Created by Jason Mortara on 11/26/2014.
 */
public class Model
{
	private static final String TAG = "Model";

	private static Properties databaseProps = null;
	private static LoginRequest userLogin = null;
	private static String opponentUsername = null;
	private static Object incomingObj = null;
	private static SelectOpponentRequest selectOpponentRequest = null;
	private static GameBoard gameBoard = null;
	private static long gameDurationMS = 10000;

	public static final String HOST = "wordwolfgame.com";	// WARNING - will connect to any site hosted on jasonmortara.com
	public static final int PORT = 4001;

	private static Boolean connected 	= false;
	private static Boolean connectedToDatabase 	= false;
//	private static Boolean dbTestOK 	= false;
	private static Boolean loggedIn 	= false;
	private static Integer score 		= 0;

	// moved from old single-player Model
	public static ArrayList<TileData> selectedTiles;
	public static ArrayList<String> validWordsThisGame;
	public static HashMap<String, String> clientDictionary;

	public static Boolean DEV_DEBUG_MODE = true;				// custom debug flag for developer use


	////////////////////////
	// Getters & Setters
	public static Properties getDatabaseProps()
	{
		return databaseProps;
	}

	public static void setDatabaseProps(Properties databaseProps)
	{
		Model.databaseProps = databaseProps;
	}

	public static LoginRequest getUserLogin()
	{
		return userLogin;
	}

	public static void setUserLogin(LoginRequest userLogin)
	{
		Model.userLogin = userLogin;
	}

	public static String getOpponentUsername()
	{
		return opponentUsername;
	}

	public static void setOpponentUsername(String opponentUsername)
	{
		Model.opponentUsername = opponentUsername;
	}

	public static Object getIncomingObj()
	{
		return incomingObj;
	}

	public static void setIncomingObj(Object incomingObj)
	{
		Model.incomingObj = incomingObj;
	}

	public static SelectOpponentRequest getSelectOpponentRequest()
	{
		return selectOpponentRequest;
	}

	public static void setSelectOpponentRequest(SelectOpponentRequest selectOpponentRequest)
	{
		Model.selectOpponentRequest = selectOpponentRequest;
	}

	public static GameBoard getGameBoard()
	{
		return gameBoard;
	}

	public static void setGameBoard(GameBoard gameBoard)
	{
		Model.gameBoard = gameBoard;
	}

	public static long getGameDurationMS()
	{
		return gameDurationMS;
	}

	public static void setGameDurationMS(long gameDurationMS)
	{
		Model.gameDurationMS = gameDurationMS;
	}

	public static Boolean getConnected()
	{
		return connected;
	}

	public static void setConnected(Boolean connected)
	{
		Model.connected = connected;
	}

	public static Boolean getConnectedToDatabase()
	{
		return connectedToDatabase;
	}

	public static void setConnectedToDatabase(Boolean connectedToDatabase)
	{
		Model.connectedToDatabase = connectedToDatabase;
	}

	public static Boolean getLoggedIn()
	{
		return loggedIn;
	}

	public static void setLoggedIn(Boolean loggedIn)
	{
		Model.loggedIn = loggedIn;
	}

	public static Integer getScore()
	{
		return score;
	}

	public static void setScore(Integer score)
	{
		Model.score = score;
	}


}
