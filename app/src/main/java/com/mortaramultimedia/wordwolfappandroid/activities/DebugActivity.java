package com.mortaramultimedia.wordwolfappandroid.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import com.mortaramultimedia.wordwolfappandroid.R;
import com.mortaramultimedia.wordwolfappandroid.communications.ServerIOTask;
import com.mortaramultimedia.wordwolfappandroid.data.Model;
import com.mortaramultimedia.wordwolfappandroid.communications.Comm;
import com.mortaramultimedia.wordwolfappandroid.database.LoginAsyncTask;
import com.mortaramultimedia.wordwolfappandroid.database.DatabaseAsyncTask;
import com.mortaramultimedia.wordwolfappandroid.interfaces.IExtendedAsyncTask;
import com.mortaramultimedia.wordwolf.shared.constants.*;
import com.mortaramultimedia.wordwolf.shared.messages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class DebugActivity extends Activity implements IExtendedAsyncTask
{
	private static final String TAG = "DebugActivity";

	private ServerIOTask serverIOTask;        // external async task which handles in/out socket streams
	private DatabaseAsyncTask databaseTask;   // external async task
	private LoginAsyncTask loginTask;         // external async task
	private Timer gameTimer;

	// connection, database and login View references
	private Button mConnectToServerButton;
	private CheckBox mConnectedToServerCheckBox;
	private Button mConnectToDatabaseButton;
	private CheckBox mConnectedToDatabaseCheckBox;
	private Button mLoginButton;
	private CheckBox mLoggedInCheckBox;

	// username field references
	private TextView mUsernameText;
	private TextView mOpponentUsernameText;

	// input text and related refs
	private TextView mInputText;
	private Button mHideKeyboardButton;
	private Button mClearInputButton;

	// opponent communications buttons refs
	private Button mGetAllPlayersButton;
	private Button mGetOpponentsButton;
	private Button mSelectOpponentButton;
	private Button mAcceptOpponentButton;
	private Button mMessageOpponentButton;

	// gameplay-related button refs
	private Button mStartGameButton;
	private Button mSendMoveButton;
	private Button mSendScoreButton;
	private Button mEndGameButton;

	// incoming objects/messages text refs
	private TextView mIncomingText;

	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);

		createUIReferences();
		updateUI();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	@Override
	protected void onResume()
	{
		Log.d(TAG, "onResume");
		super.onResume();
		Comm.registerCurrentActivity(this);	// tell Comm to forward published progress updates to this Activity
	}

	/**
	 * Create the needed references to buttons and UI elements.
	 */
	private void createUIReferences()
	{
		mConnectToServerButton 			= (Button) 		findViewById(R.id.connectToServerButton);
		mConnectedToServerCheckBox 		= (CheckBox) 	findViewById(R.id.connectedToServerCheckBox);
		mConnectToDatabaseButton 		= (Button) 		findViewById(R.id.connectToDatabaseButton);
		mConnectedToDatabaseCheckBox 	= (CheckBox) 	findViewById(R.id.connectedToDatabaseCheckBox);
		mLoginButton 					= (Button) 		findViewById(R.id.loginButton);
		mLoggedInCheckBox 				= (CheckBox) 	findViewById(R.id.loggedInCheckBox);

		mUsernameText 					= (TextView) 	findViewById(R.id.usernameText);
		mOpponentUsernameText 			= (TextView) 	findViewById(R.id.opponentUsernameText);

		mInputText 						= (EditText) 	findViewById(R.id.inputText);
		mHideKeyboardButton 			= (Button) 		findViewById(R.id.hideKeyboardButton);
		mClearInputButton 				= (Button) 		findViewById(R.id.clearInputButton);

		mGetAllPlayersButton 			= (Button) 		findViewById(R.id.getAllPlayersButton);
		mGetOpponentsButton 			= (Button) 		findViewById(R.id.getOpponentsButton);
		mSelectOpponentButton 			= (Button) 		findViewById(R.id.selectOpponentButton);
		mAcceptOpponentButton 			= (Button) 		findViewById(R.id.acceptOpponentButton);

		mStartGameButton 				= (Button) 		findViewById(R.id.startGameButton);
		mSendMoveButton 				= (Button) 		findViewById(R.id.sendMoveButton);
		mSendScoreButton 				= (Button) 		findViewById(R.id.sendScoreButton);
		mEndGameButton 					= (Button) 		findViewById(R.id.endGameButton);

		mIncomingText 					= (TextView) 	findViewById(R.id.incomingText);
	}

	public void updateUI()
	{
		Log.d(TAG, "updateUI");

		// Server Connection UI
		mConnectToServerButton.setClickable(!Model.getConnected());
		mConnectedToServerCheckBox.setChecked(Model.getConnected());
		if (Model.getConnected())
		{
			mConnectToServerButton.setText("Server\nOK");
		}

		// DB Connection UI
		mConnectToDatabaseButton.setClickable(!Model.getConnectedToDatabase());
		mConnectedToDatabaseCheckBox.setChecked(Model.getConnectedToDatabase());
		if (Model.getConnectedToDatabase())
		{
			mConnectToDatabaseButton.setText("DB\nOK");
		}

		// Login UI
		mLoginButton.setClickable(!Model.getLoggedIn());
		mLoggedInCheckBox.setChecked(Model.getLoggedIn());
		if (Model.getLoggedIn())
		{
			mLoginButton.setText("Logged\nIn");
		}

		// Login info
		if (Model.getUserLogin() != null)
		{
			String username = Model.getUserLogin().getUserName();
			if (username != null)
			{
				mUsernameText.setText(username);
			}
		}

		// Opponent info
		if (Model.getOpponentUsername() != null)
		{
			mOpponentUsernameText.setText(Model.getOpponentUsername());
		}

		// Messages
		if(Model.getIncomingObj() != null)
		{
			mIncomingText.setText(Model.getIncomingObj().toString());
		}
	}

/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.server, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
*/

	////////////////////////////////////////////////////////
	// Server connection, Database Connection, and Login

	/**
	 * Connect to the WordWolf server.
	 *
	 * @param view
	 * @throws IOException
	 */
	public void handleConnectToServerButtonClick(View view) throws IOException
	{
		//Log.d(TAG, "handleConnectToServerButtonClick");

		if (Model.getConnected())
		{
			Log.d(TAG, "handleConnectToServerButtonClick: already connected! Ignoring.");
			return;
		}
		else
		{
			Log.d(TAG, "handleConnectToServerButtonClick: not connected yet.");

			// Start network tasks separate from the main UI thread
			if (serverIOTask == null)
			{
				Log.d(TAG, "handleConnectToServerButtonClick: starting ServerIOTask and registering this Activity with Comm...");
				Comm.registerCurrentActivity(this);	// tell Comm to forward published progress updates to this Activity
				serverIOTask = new ServerIOTask();
				serverIOTask.execute();
			}
		}
	}

	/**
	 * Connect to the WordWolf database.
	 *
	 * @param view
	 * @throws IOException
	 */
	public void handleConnectToDatabaseButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleConnectToDatabaseButtonClick");
		databaseTask = new DatabaseAsyncTask(this, this);

		// workaround for issues with execute() not working properly on AsyncTasks
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			databaseTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else
		{
			databaseTask.execute();
		}
	}

	/**
	 * Login Button handler - launches LoginActivity
	 *
	 * @param view
	 * @throws IOException
	 */
	public void handleLoginButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleLoginButtonClick");

		// create an Intent, with optional additional params
		Context thisContext = DebugActivity.this;
		Intent intent = new Intent(thisContext, LoginActivity.class);
		intent.putExtra("testParam", "testValue");                        //optional params

		// start the activity
		startActivityForResult(intent, 1);      //note that in order for this class' onActivityResult to be called when the LoginActivity has completed, the requestCode here must be > 0
	}


	/////////////////////////////////////////////////////////////
	// Text Entry, Hide Keyboard, and Clear Input
	/////////////////////////////////////////////////////////////

	public void handleInputTextClick(View view)
	{
		Log.d(TAG, "handleInputTextClick");
		clearInputText();
	}

	public void handleHideSoftKeyboardButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleHideSoftKeyboardButtonClick");
		hideSoftKeyboard();
	}

	private void clearInputText()
	{
		Log.d(TAG, "clearInputText");
		mInputText.setText("");
	}


	/////////////////////////////////////////////////////////////
	// Message Sending, with or without Echo
	/////////////////////////////////////////////////////////////

	public void handleSendMessageButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleSendMessageButtonClick");
		hideSoftKeyboard();
		String msg = mInputText.getText().toString();
		SimpleMessage msgObj = new SimpleMessage(msg, false);
		serverIOTask.sendOutgoingObject(msgObj);
	}

	public void handleEchoMessageButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleEchoMessageButtonClick");
		hideSoftKeyboard();
		String msg = mInputText.getText().toString();
		SimpleMessage msgObj = new SimpleMessage(msg, true);
		serverIOTask.sendOutgoingObject(msgObj);
	}

	public void handleClearInputTextButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleClearInputTextButtonClick");
		clearInputText();
	}

	private void hideSoftKeyboard()
	{
		Log.d(TAG, "hideSoftKeyboard");
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mInputText.getWindowToken(), 0);
	}


	/////////////////////////////////////////////////////////////
	// Opponent Communications
	/////////////////////////////////////////////////////////////

	public void handleGetAllPlayersButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleGetAllPlayersButtonClick");
		hideSoftKeyboard();
		GetPlayerListRequest getPlayerListRequest = new GetPlayerListRequest(PlayerListType.ALL_UNMATCHED_PLAYERS);
		serverIOTask.sendOutgoingObject(getPlayerListRequest);
	}

	public void handleGetOpponentsButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleGetOpponentsButtonClick: this button is not currently functional.");
		hideSoftKeyboard();
	}

	public void handleSelectOpponentButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleSelectOpponentButtonClick");
		hideSoftKeyboard();
		String msg = mInputText.getText().toString();
		SelectOpponentRequest request = new SelectOpponentRequest(Model.getUserLogin().getUserName(), msg);
		serverIOTask.sendOutgoingObject(request);
	}

	public void handleAcceptOpponentButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleAcceptOpponentButtonClick");
		hideSoftKeyboard();

		// ignore if no request exists to respond to
		if (Model.getSelectOpponentRequest() == null)
		{
			Log.d(TAG, "handleAcceptOpponentButtonClick: no SelectOpponentRequest has been received and stored. Ignoring.");
			return;
		}

		Log.d(TAG, "handleRequestToBecomeOpponent: Model.connected: " + Model.getConnected());
		Log.d(TAG, "handleRequestToBecomeOpponent: Model.loggedIn: " + Model.getLoggedIn());
		Log.d(TAG, "handleRequestToBecomeOpponent: Model.userLogin.getUserName(): " + Model.getUserLogin().getUserName());
		Log.d(TAG, "handleRequestToBecomeOpponent: Model.selectOpponentRequest: " + Model.getSelectOpponentRequest());

		SelectOpponentResponse response = null;
		try
		{
			if (Model.getConnected() && Model.getLoggedIn() && Model.getUserLogin().getUserName() != null)
			{
				Log.d(TAG, "handleRequestToBecomeOpponent: accepting opponent request: " + Model.getSelectOpponentRequest());
				response = new SelectOpponentResponse(true, Model.getUserLogin().getUserName(), Model.getSelectOpponentRequest().getSourceUsername());
				serverIOTask.sendOutgoingObject(response);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void handleMessageOpponentButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleMessageOpponentButtonClick");
		hideSoftKeyboard();
		String msg = mInputText.getText().toString();
		try
		{
			OpponentBoundMessage msgObj = new OpponentBoundMessage(msg, false);   // this constructor assumes the server handles figuring out who is the opponent for the msg destination
			serverIOTask.sendOutgoingObject(msgObj);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	/////////////////////////////////////////////////////////////
	// Gameplay Communications
	/////////////////////////////////////////////////////////////

	public void handleStartGameButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleStartGameButtonClick: sending start game request...");
		int rows = 5;   //TODO allow player selection of grid size
		int cols = 5;   //TODO allow player selection of grid size
		CreateGameRequest request = new CreateGameRequest(-1, Model.getUserLogin().getUserName(), "defaultGameType", rows, cols, false, -1, -1, Model.getOpponentUsername(), 9000);
		serverIOTask.sendOutgoingObject(request);
	}

	public void handleSendMoveButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleSendMoveButtonClick: Sumbitting a generated move...");

		// generate a fake, unophisticated GameMove and send it in a request, without client validation.
		GameMove gameMove = getGeneratedGameMove();
		GameMoveRequest request = new GameMoveRequest(Model.getUserLogin().getUserName(), -1, gameMove);
		serverIOTask.sendOutgoingObject(request);
	}

	public void handleSendScoreButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleSendScoreButtonClick: this button is currently non-functional.");
		hideSoftKeyboard();
	}

	public void handleEndGameButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleEndGameButtonClick");
		hideSoftKeyboard();

		EndGameRequest request = new EndGameRequest(Model.getUserLogin().getUserName(), -1);
		serverIOTask.sendOutgoingObject(request);
	}

	/////////////////////////////////////////////////
	// Other methods
	/**
	 * Generates a hardcoded GameMove for testing purposes.
	 * @return
	 */
	private GameMove getGeneratedGameMove()
	{
		Log.d(TAG, "getGeneratedGameMove");

		TileData td;

		// change to custom board
		char a = ("A").charAt(0);
		char d = ("D").charAt(0);
		char e = ("E").charAt(0);
		char i = ("I").charAt(0);
		char t = ("T").charAt(0);
		char o = ("I").charAt(0);
		char n = ("N").charAt(0);

		// build move
		TileData td0 = new TileData(0, 0, a, false);
		TileData td1 = new TileData(0, 1, d, false);
		TileData td2 = new TileData(1, 1, d, false);
		TileData td3 = new TileData(2, 1, e, false);
		TileData td4 = new TileData(2, 2, d, false);

		ArrayList<TileData> move = new ArrayList<TileData>();
		move.add(td0);
		move.add(td1);
		move.add(td2);
		move.add(td3);
		move.add(td4);

		return new GameMove(move);
	}

	private void startGameTimer()
	{
		Log.d(TAG, "startGameTimer");

		if(Model.getGameDurationMS() > 0)
		{
			if(gameTimer != null)
			{
				gameTimer.cancel();
			}
			gameTimer = new Timer();
			gameTimer.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					Log.d(TAG, "startGameTimer: TimerTask running.");
					handleGameTimerCompleted();
				}
			}, Model.getGameDurationMS());
		}
		else Log.w(TAG, "WARNING: gameDurationMS has not been set correctly. Not setting game timer.");
	}

	/**
	 * Handle the completion of the game timer. Ths should trigger a Game Over sequence.
	 */
	private void handleGameTimerCompleted()
	{
		Log.d(TAG, "handleGameTimerCompleted");
		if(gameTimer != null)
		{
			gameTimer.cancel();
		}
		handleGameOver();
	}

	private void handleGameOver()
	{
		Log.d(TAG, "handleGameOver");
		EndGameRequest request = new EndGameRequest(Model.getUserLogin().getUserName(), -1);
		serverIOTask.sendOutgoingObject(request);
	}


	@Override
	public void onStart()
	{
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				  Action.TYPE_VIEW, // TODO: choose an action type.
				  "Server Page", // TODO: Define a title for the content shown.
				  // TODO: If you have web page content that matches this app activity's content,
				  // make sure this auto-generated web page URL is correct.
				  // Otherwise, set the URL to null.
				  Uri.parse("http://host/path"),
				  // TODO: Make sure this auto-generated app deep link URI is correct.
				  Uri.parse("android-app://com.mortaramultimedia.wordwolfappandroid/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop()
	{
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				  Action.TYPE_VIEW, // TODO: choose an action type.
				  "Server Page", // TODO: Define a title for the content shown.
				  // TODO: If you have web page content that matches this app activity's content,
				  // make sure this auto-generated web page URL is correct.
				  // Otherwise, set the URL to null.
				  Uri.parse("http://host/path"),
				  // TODO: Make sure this auto-generated app deep link URI is correct.
				  Uri.parse("android-app://com.mortaramultimedia.wordwolfappandroid/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}


	/////////////////////////////////////////////////////////////
	// Unused button behaviors
	/////////////////////////////////////////////////////////////

	// original login button handler -- attempted login with hardcoded credentials
/*	public void handleLoginButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleTestDatabaseButtonClick");

		// next try a login
		LoginMessage newLogin = new LoginMessage(1, "test1", "test1pass", "test1@wordwolfgame.com");    //HARDCODED
		Model.userLogin = newLogin;

		loginTask = new LoginAsyncTask(this, this);

		// workaround for issues with execute() not working properly on AsyncTasks
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			loginTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else
		{
			loginTask.execute();
		}
	}*/

	/**
	 * Handle the result of the activity launched from this one.
	 * ResultCodes coming back from launched Activity:
	 * RESULT_OK (0), RESULT_CANCELED (-1), RESULT_CREATE_NEW_ACCOUNT_OK (-2), RESULT_CREATE_NEW_ACCOUNT_CANCELED (-3)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		if (resultCode == LoginActivity.RESULT_CREATE_NEW_ACCOUNT_OK)
		{
			Log.d(TAG, "onActivityResult: Create New Account SUCCESS returned from LoginActivity. resultCode: " + resultCode);
			//createNewAccountButton.setVisibility(View.INVISIBLE);
			//showCreateNewAccountResultDialog();
		}
		else if (resultCode == LoginActivity.RESULT_CREATE_NEW_ACCOUNT_CANCELED)
		{
			Log.w(TAG, "onActivityResult: Create New Account FAILURE returned from LoginActivity. resultCode: " + resultCode);
			//showCreateNewAccountResultDialog();
		}
		else if (resultCode == RESULT_OK)   // see the note in the startActivityForResult above
		{
			Log.d(TAG, "onActivityResult: LOGIN SUCCESS returned from LoginActivity. resultCode: " + resultCode);
		}
		else if (resultCode == RESULT_CANCELED)
		{
			Log.w(TAG, "onActivityResult: LOGIN FAILURE returned from LoginActivity. resultCode: " + resultCode);

			// if we've returned from the LoginActivity with no successful login as defined by the Model, nullify the user login credentials so the UI update reflects that
			if(!Model.getLoggedIn())
			{
				Model.setUserLogin(null);
			}
		}
		else Log.w(TAG, "onActivityResult: WARNING: no case for resultCode: " + resultCode);

		updateUI();
	}

	/**
	 * IExtendedAsyncTask overrides
	 * TODO = still called?
	 */
	@Override
	public void onTaskCompleted()
	{
		Log.d(TAG, "onTaskCompleted");
		updateUI();
	}

	@Override
	public void handleIncomingObject(Object obj)
	{
		Log.d(TAG, "handleIncomingObject forwarded from ServerIOTask: " + obj);
		updateUI();
	}


}
