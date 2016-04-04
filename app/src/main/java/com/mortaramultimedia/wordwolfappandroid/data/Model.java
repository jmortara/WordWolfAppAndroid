package com.mortaramultimedia.wordwolfappandroid.data;

import android.util.Log;

import com.mortaramultimedia.wordwolf.shared.messages.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;


/**
 * Created by Jason Mortara on 11/26/2014.	//TODO: add client states?
 */
public class Model
{
	private static final String TAG = "Model";

	private static Properties databaseProps = null;
	private static LoginRequest userLogin = null;

	private static CreateNewAccountRequest createNewAccountRequest = null;
	private static String opponentUsername = null;
	private static Object incomingObj = null;
	private static SelectOpponentRequest selectOpponentRequest = null;
	private static GameBoard gameBoard = null;
	private static long gameDurationMS = 10000;

	public static final String HOST = "wordwolfgame.com";	// WARNING - will connect to any site hosted on jasonmortara.com
	public static final int PORT = 4001;

	private static Boolean connected 			= false;
	private static Boolean connectedToDatabase 	= false;

	//	private static Boolean dbTestOK 			= false;
	private static Boolean newAccountCreated	= false;
	private static Boolean loggedIn 			= false;
	private static Integer score 				= 0;

	// moved from old single-player Model
	private static ArrayList<TileData> selectedTiles;
	private static ArrayList<String> validWordsThisGame;
	private static HashSet<GameMove> gameMovesThisGame;
	private static ArrayList<String> clientDictionary;

	// DEBUG SETTINGS
	public static final Boolean DEV_DEBUG_MODE          = false;			// custom debug flag for developer use
	public static final Boolean DEV_DEBUG_USE_LOCAL_IP  = false;			// custom debug flag: use local machine IP?
	public static final String  DEV_DEBUG_LOCAL_IP_ADDR = "10.0.1.5";		// custom debug setting: local machine IP address (may change after reboots) // terminal: to lookup IP use: ipconfig getifaddr en0


	/**
	 * Get the host IP used for connection to WordWolf Server. Could be the remote server, or the local machine IP.
	 * @return Host IP
	 */
	public static String getHostIP()
	{
		String host;
		if(!DEV_DEBUG_USE_LOCAL_IP)
		{
			host = HOST;
			Log.d(TAG, "getHostIP: using host: " + host);
		}
		else
		{
			host = DEV_DEBUG_LOCAL_IP_ADDR;
			Log.w(TAG, "getHostIP: *****using local machine host******: " + host);
		}
		return host;
	}


	////////////////////////
	// GETTERS & SETTERS

	public static Properties getDatabaseProps()
	{
		return databaseProps;
	}

	public static void setDatabaseProps(Properties databaseProps)
	{
		Model.databaseProps = databaseProps;
	}

	public static Boolean getNewAccountCreated()
	{
		return newAccountCreated;
	}

	public static void setNewAccountCreated(Boolean newAccountCreated)
	{
		Model.newAccountCreated = newAccountCreated;
		Log.w(TAG, "setUserLogin: newAccountCreated is now: " + Model.newAccountCreated);
	}

	public static LoginRequest getUserLogin()
	{
		return userLogin;
	}

	public static void setUserLogin(LoginRequest userLogin)
	{
		Model.userLogin = userLogin;
		Log.w(TAG, "setUserLogin: userLogin is now: " + Model.userLogin);
	}

	public static CreateNewAccountRequest getCreateNewAccountRequest()
	{
		return createNewAccountRequest;
	}

	public static void setCreateNewAccountRequest(CreateNewAccountRequest createNewAccountRequest)
	{
		Model.createNewAccountRequest = createNewAccountRequest;
	}

	public static String getOpponentUsername()
	{
		return opponentUsername;
	}

	public static void setOpponentUsername(String opponentUsername)
	{
		/*if(opponentUsername == null)
		{
			throw new Error("WARNING********************* null opponentUserName");
		}*/
		Model.opponentUsername = opponentUsername;
		Log.w(TAG, "setOpponentUsername: opponentUsername is now: " + Model.opponentUsername);
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
		Log.w(TAG, "setLoggedIn: loggedIn is now: " + Model.loggedIn);
	}

	public static Integer getScore()
	{
		return score;
	}

	public static void setScore(Integer score)
	{
		Model.score = score;
	}

	public static ArrayList<String> getClientDictionary()
	{
		return clientDictionary;
	}

	public static void setClientDictionary(ArrayList<String> clientDictionary)
	{
		Model.clientDictionary = clientDictionary;
	}

	public static ArrayList<TileData> getSelectedTiles()
	{
		return selectedTiles;
	}

	public static void setSelectedTiles(ArrayList<TileData> selectedTiles)
	{
		Model.selectedTiles = selectedTiles;
	}

	public static HashSet<GameMove> getGameMovesThisGame()
	{
		return gameMovesThisGame;
	}

	public static void setGameMovesThisGame(HashSet<GameMove> gameMovesThisGame)
	{
		Model.gameMovesThisGame = gameMovesThisGame;
	}

	public static ArrayList<String> getValidWordsThisGame()
	{
		return validWordsThisGame;
	}

	public static void setValidWordsThisGame(ArrayList<String> validWordsThisGame)
	{
		Model.validWordsThisGame = validWordsThisGame;
	}


}
