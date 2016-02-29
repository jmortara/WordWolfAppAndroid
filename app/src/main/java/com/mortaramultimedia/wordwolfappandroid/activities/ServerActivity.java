package com.mortaramultimedia.wordwolfappandroid.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
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
import com.mortaramultimedia.wordwolfappandroid.communications.ServerTask;
import com.mortaramultimedia.wordwolfappandroid.data.Model;
import com.mortaramultimedia.wordwolfappandroid.communications.Comm;
import com.mortaramultimedia.wordwolfappandroid.database.LoginAsyncTask;
import com.mortaramultimedia.wordwolfappandroid.database.DatabaseAsyncTask;
import com.mortaramultimedia.wordwolfappandroid.interfaces.IExtendedAsyncTask;
import com.mortaramultimedia.wordwolf.shared.constants.*;
import com.mortaramultimedia.wordwolf.shared.messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ServerActivity extends Activity implements IExtendedAsyncTask
{
	private static final String TAG = "ServerActivity";

	private ServerTask serverTask;            // inner async task which handles in/out socket streams
	private DatabaseAsyncTask databaseTask;   // external async task
	private LoginAsyncTask loginTask;         // external async task
	private Timer gameTimer;

	// connection, database and login View references
	private Button connectToServerButton;
	private CheckBox connectedToServerCheckBox;
	private Button connectToDatabaseButton;
	private CheckBox connectedToDatabaseCheckBox;
	private Button loginButton;
	private CheckBox loggedInCheckBox;

	// username field references
	private TextView usernameText;
	private TextView opponentUsernameText;

	// input text and related refs
	private TextView inputText;
	private Button hideKeyboardButton;
	private Button clearInputButton;

	// opponent communications buttons refs
	private Button getAllPlayersButton;
	private Button getOpponentsButton;
	private Button selectOpponentButton;
	private Button acceptOpponentButton;
	private Button messageOpponentButton;

	// gameplay-related button refs
	private Button startGameButton;
	private Button sendMoveButton;
	private Button sendScoreButton;
	private Button endGameButton;

	// incoming objects/messages text refs
	private TextView incomingText;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server);

		createUIReferences();
		updateUI();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	/**
	 * Create the needed references to buttons and UI elements.
	 */
	private void createUIReferences()
	{
		connectToServerButton = (Button) findViewById(R.id.connectToServerButton);
		connectedToServerCheckBox = (CheckBox) findViewById(R.id.connectedToServerCheckBox);
		connectToDatabaseButton = (Button) findViewById(R.id.connectToDatabaseButton);
		connectedToDatabaseCheckBox = (CheckBox) findViewById(R.id.connectedToDatabaseCheckBox);
		loginButton = (Button) findViewById(R.id.loginButton);
		loggedInCheckBox = (CheckBox) findViewById(R.id.loggedInCheckBox);

		usernameText = (TextView) findViewById(R.id.usernameText);
		opponentUsernameText = (TextView) findViewById(R.id.opponentUsernameText);

		inputText = (EditText) findViewById(R.id.inputText);
		hideKeyboardButton = (Button) findViewById(R.id.hideKeyboardButton);
		clearInputButton = (Button) findViewById(R.id.clearInputButton);

		getAllPlayersButton = (Button) findViewById(R.id.getAllPlayersButton);
		getOpponentsButton = (Button) findViewById(R.id.getOpponentsButton);
		selectOpponentButton = (Button) findViewById(R.id.selectOpponentButton);
		acceptOpponentButton = (Button) findViewById(R.id.acceptOpponentButton);

		startGameButton = (Button) findViewById(R.id.startGameButton);
		sendMoveButton = (Button) findViewById(R.id.sendMoveButton);
		sendScoreButton = (Button) findViewById(R.id.sendScoreButton);
		endGameButton = (Button) findViewById(R.id.endGameButton);

		incomingText = (TextView) findViewById(R.id.incomingText);
	}

	public void updateUI()
	{
		Log.d(TAG, "updateUI");

		// Server Connection UI
		connectToServerButton.setClickable(!Model.getConnected());
		connectedToServerCheckBox.setChecked(Model.getConnected());
		if (Model.getConnected())
		{
			connectToServerButton.setText("Server\nOK");
		}

		// DB Connection UI
		connectToDatabaseButton.setClickable(!Model.getConnectedToDatabase());
		connectedToDatabaseCheckBox.setChecked(Model.getConnectedToDatabase());
		if (Model.getConnectedToDatabase())
		{
			connectToDatabaseButton.setText("DB\nOK");
		}

		// Login UI
		loginButton.setClickable(!Model.getLoggedIn());
		loggedInCheckBox.setChecked(Model.getLoggedIn());
		if (Model.getLoggedIn())
		{
			loginButton.setText("Logged\nIn");
		}

		// Login info
		if (Model.getUserLogin() != null)
		{
			String username = Model.getUserLogin().getUserName();
			if (username != null)
			{
				usernameText.setText(username);
			}
		}

		// Opponent info
		if (Model.getOpponentUsername() != null)
		{
			opponentUsernameText.setText(Model.getOpponentUsername());
		}

		// Messages
		if(Model.getIncomingObj() != null)
		{
			incomingText.setText(Model.getIncomingObj().toString());
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
		Log.d(TAG, "handleConnectToServerButtonClick");

		if (Model.getConnected())
		{
			Log.d(TAG, "handleConnectToServerButtonClick: already connected!");
			return;
		}
		// Start network tasks separate from the main UI thread
		if (serverTask == null && !Model.getConnected())
		{
			Comm.registerCurrentActivity(this);	// tell Comm to forward published progress updates to this Activity
			serverTask = new ServerTask();
			serverTask.execute();
		}
		else
		{
			Log.d(TAG, "handleConnectToServerButtonClick: not connected");
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
		Context thisContext = ServerActivity.this;
		Intent intent = new Intent(thisContext, LoginActivity.class);
		intent.putExtra("testParam", "testValue");                        //optional params

		// start the activity
		startActivityForResult(intent, 1);      //TODO: note that in order for this class' onActivityResult to be called when the LoginActivity has completed, the requestCode here must be > 0
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
		inputText.setText("");
	}


	/////////////////////////////////////////////////////////////
	// Message Sending, with or without Echo
	/////////////////////////////////////////////////////////////

	public void handleSendMessageButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleSendMessageButtonClick");
		hideSoftKeyboard();
		String msg = inputText.getText().toString();
		SimpleMessage msgObj = new SimpleMessage(msg, false);
		serverTask.sendOutgoingObject(msgObj);
	}

	public void handleEchoMessageButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleEchoMessageButtonClick");
		hideSoftKeyboard();
		String msg = inputText.getText().toString();
		SimpleMessage msgObj = new SimpleMessage(msg, true);
		serverTask.sendOutgoingObject(msgObj);
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
		imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
	}


	/////////////////////////////////////////////////////////////
	// Opponent Communications
	/////////////////////////////////////////////////////////////

	public void handleGetAllPlayersButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleGetAllPlayersButtonClick");
		hideSoftKeyboard();
		GetPlayerListRequest getPlayerListRequest = new GetPlayerListRequest(PlayerListType.ALL_UNMATCHED_PLAYERS);
		serverTask.sendOutgoingObject(getPlayerListRequest);
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
		String msg = inputText.getText().toString();
		SelectOpponentRequest request = new SelectOpponentRequest(Model.getUserLogin().getUserName(), msg);
		serverTask.sendOutgoingObject(request);
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
				serverTask.sendOutgoingObject(response);
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
		String msg = inputText.getText().toString();
		try
		{
			OpponentBoundMessage msgObj = new OpponentBoundMessage(msg, false);   // this constructor assumes the server handles figuring out who is the opponent for the msg destination
			serverTask.sendOutgoingObject(msgObj);
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
		serverTask.sendOutgoingObject(request);
	}

	public void handleSendMoveButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleSendMoveButtonClick: Sumbitting a generated move...");

		// generate a fake, unophisticated GameMove and send it in a request, without client validation.
		GameMove gameMove = getGeneratedGameMove();
		GameMoveRequest request = new GameMoveRequest(Model.getUserLogin().getUserName(), -1, gameMove);
		serverTask.sendOutgoingObject(request);
	}

	public void handleSendScoreButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleSendScoreButtonClick: this button is currently non-functional.");
		hideSoftKeyboard();
		/*String msg = inputText.getText().toString();
		Integer newScore;
		try
		{
			newScore = Integer.parseInt(msg);
		}
		catch (Error e)
		{
			// just substitute an incremented score if the conversion was invalid
			newScore = Model.getScore() + 1;
		}

		Model.setScore(newScore);*/

		//TODO: add new Score update via writeObject()
	}

	private void handleEndGameButtonClick()
	{
		Log.d(TAG, "handleEndGameButtonClick: BEHAVIOR TBD");   //TODO: add behavior
		hideSoftKeyboard();
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

		List<TileData> move = new ArrayList<TileData>();
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
		serverTask.sendOutgoingObject(request);
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

		// next try a login	//TODO get this from input fields ************************************
		LoginMessage newLogin = new LoginMessage(1, "test1", "test1pass", "test1@wordwolfgame.com");    //TODO: HARDCODED
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

	/****************************************************************************************
	 * InnerServerTask - AsyncTask which handles server connections and messaging
	 ****************************************************************************************/
	private class InnerServerTask extends AsyncTask<Void, Integer, Integer>
	{
		private ServerActivity serverActivity;
		private Socket s;
		private ObjectOutputStream s_objOut;
		private ObjectInputStream s_objIn;

		// constructor
		InnerServerTask(ServerActivity sa)
		{
			Log.d(TAG, "ServerTask constructor");
			this.serverActivity = sa;
		}

		@Override
		protected Integer doInBackground(Void... unused)
		{
			// need to force wait for debugger to breakpoint in this thread
			if (Debug.isDebuggerConnected())
			{
				Debug.waitForDebugger();
			}

			s = new Socket();

			try
			{
				Log.d(TAG, "Attempting to connect to " + Model.HOST + " " + Model.PORT);
				try
				{
					s.connect(new InetSocketAddress(Model.HOST, Model.PORT));
				}
				//Host not found
				catch (UnknownHostException e)
				{
					System.err.println("Don't know about host : " + Model.HOST);
//					System.exit(1);	// exit app
					try
					{
						s_objOut.close();
					}
					catch (RuntimeException e2)
					{
						System.err.println("Object output stream may be null or unavailable to close.");
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
					try
					{
						s_objIn.close();
					}
					catch (RuntimeException e3)
					{
						System.err.println("Object input stream may be null or unavailable to close.");
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
					try
					{
						s.close();
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
				catch (SecurityException e)
				{
					Log.d(TAG, "SecurityException: " + e.getMessage());
					e.printStackTrace();
				}
				catch (IOException e)
				{
					Log.d(TAG, "IOException: " + e.getMessage());
					e.printStackTrace();
				}

				// update Model with connection status
				Model.setConnected(s.isConnected());

				if (s.isConnected())
				{
					Log.d(TAG, "Connected to wwss.");

					// create writer for socket
					try
					{
						if (s_objOut == null)
						{
							s_objOut = new ObjectOutputStream(s.getOutputStream());
							Comm.setOut(s_objOut);
							Log.d(TAG, "Created ObjectOutputStream.");
						}
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}

					//Send initial message to server
					SimpleMessage msgObj = new SimpleMessage(Constants.HELLO_SERVER, true);
					sendOutgoingObject(msgObj);

					// Obj reader for socket
					try
					{
						if (s_objIn == null)
						{
							s_objIn = new ObjectInputStream(s.getInputStream());
							Comm.setIn(s_objIn);   // create reference for other classes to use
							Log.d(TAG, "Created ObjectInputStream.");
						}
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}

					// Get obj response from server
					Object responseObj;
					if (s_objIn != null)
					{
						try
						{
							while ((responseObj = s_objIn.readObject()) != null)
							{
								Log.d(TAG, "Server response obj: " + responseObj);
								//Model.setIncomingMessageObj(responseObj);
								//TODO: FILL IN RESPONSE HANDLING
								handleIncomingObject(responseObj);
							}
						}
						catch (IOException | ClassNotFoundException e)
						{
							e.printStackTrace();
						}
					}

				}
				else
				{
					Log.d(TAG, "Not connected.");
				}

			}
			catch (Error e)
			{
				e.printStackTrace();
			}


			// other stuff? close?
			Log.d(TAG, "Continuing...");
			if (s_objOut != null)
			{
				try
				{
					s_objOut.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

			if (s_objIn != null)
			{
				try
				{
					s_objIn.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				s.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			return 1;
		}

		/**
		 * Handle all incoming object types.
		 *
		 * @param obj
		 */
		private void handleIncomingObject(Object obj)
		{
			Log.d(TAG, "handleIncomingObject: " + obj);

			/**
			 * If receiving a SimpleMessage, log the message. If echo was requested, send it back to the client as well.
			 */
			if (obj instanceof SimpleMessage)
			{
				handleSimpleMessage(((SimpleMessage) obj));
			}
			/**
			 * If receiving a ConnectToDatabaseResponse, if it is a successful one, that means we can then attempt a login or create account.
			 */
			if (obj instanceof ConnectToDatabaseResponse)
			{
				handleConnectToDatabaseResponse(((ConnectToDatabaseResponse) obj));
			}
			/**
			 * If receiving a LoginResponse...
			 */
			else if (obj instanceof LoginResponse)
			{
				handleLoginResponse(((LoginResponse) obj));
			}
			/**
			 * If receiving a GetPlayerListResponse
			 */
			else if (obj instanceof GetPlayerListResponse)
			{
				handleGetPlayerListResponse(((GetPlayerListResponse) obj));
			}
			/**
			 * If receiving a SelectOpponentRequest, which is a request from another player to become opponents.
			 */
			else if (obj instanceof SelectOpponentRequest)
			{
				handleRequestToBecomeOpponent(((SelectOpponentRequest) obj));
			}
			/**
			 * If receiving a SelectOpponentResponse, which is a response to a request to become another player's opponent.
			 */
			else if (obj instanceof SelectOpponentResponse)
			{
				handleSelectOpponentResponse(((SelectOpponentResponse) obj));
			}
			/**
			 * If receiving an OpponentBoundMessage, which is a message to this client from this player's opponent.
			 */
			else if (obj instanceof OpponentBoundMessage)
			{
				handleMessageFromOpponent(((OpponentBoundMessage) obj));
			}
			/**
			 * If receiving a CreateNewAccountResponse...
			 */
			/*else if(obj instanceof CreateNewAccountResponse)
			{
				handleCreateNewAccountResponse(((CreateNewAccountResponse) obj), out);
			}*/
			/**
			 * If receiving a CreateGameResponse, create a GameBoard and distribute it to matched players.
			 */
			else if (obj instanceof CreateGameResponse)
			{
				handleCreateGameResponse(((CreateGameResponse) obj));
			}
			/**
			 * If receiving a GameMoveResponse, if the response is invalid, ignore it. If valid, add the score it contains.
			 */
			else if (obj instanceof GameMoveResponse)
			{
				handleGameMoveResponse(((GameMoveResponse) obj));
			}
			/**
			 * If receiving a EndGameResponse, if the response is invalid, ignore it. If valid, add the score it contains.
			 */
			else if (obj instanceof EndGameResponse)
			{
				handleEndGameResponse(((EndGameResponse) obj));
			}

		}

		public void sendOutgoingObject(Object obj)
		{
			Log.d(TAG, "sendOutgoingObject: " + obj);

			if (serverTask == null || !Model.getConnected())
			{
				Log.d(TAG, "sendOutgoingObject: WARNING: not connected. Ignoring.");
				return;
			}

			// send it to the server
			if (s.isConnected() && obj != null)
			{
				if (s_objOut != null)
				{
					try
					{
						s_objOut.writeObject(obj);
						s_objOut.flush();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}

		private void handleSimpleMessage(SimpleMessage msgObj)
		{
			Log.d(TAG, "handleSimpleMessage: " + msgObj.getMsg());
			publishObject(msgObj);
		}

		private void handleConnectToDatabaseResponse(ConnectToDatabaseResponse response)
		{
			Log.d(TAG, "handleConnectToDatabaseResponse: " + response);
			Model.setConnectedToDatabase(response.getSuccess());
			publishObject(response);
		}

		private void handleLoginResponse(LoginResponse response)
		{
			Log.d(TAG, "handleLoginResponse: " + response);
			publishObject(response);
		}

		private void handleGetPlayerListResponse(GetPlayerListResponse response)
		{
			Log.d(TAG, "handleGetPlayerListResponse: " + response);
			publishObject(response);
			Log.d(TAG, "handleGetPlayerListResponse: player list: " + response.getPlayersCopy());
		}

		private void handleRequestToBecomeOpponent(SelectOpponentRequest request)
		{
			Log.d(TAG, "handleRequestToBecomeOpponent: " + request);
			publishObject(request);
			Log.d(TAG, "handleRequestToBecomeOpponent: YOU HAVE BEEN OFFERED TO BECOME AN OPPONENT OF: " + request.getSourceUsername());

			// store the request in the Model until it is accepted or rejected by the user
			Model.setSelectOpponentRequest(request);
		}

		private void handleSelectOpponentResponse(SelectOpponentResponse response)
		{
			Log.d(TAG, "handleSelectOpponentResponse: " + response);
			publishObject(response);
			if (response.getRequestAccepted())
			{
				Log.d(TAG, "handleRequestToBecomeOpponent: REQUEST ACCEPTED! from: " + response.getSourceUserName());
				Model.setOpponentUsername(response.getSourceUserName());
			}
			else
			{
				Log.d(TAG, "handleRequestToBecomeOpponent: REQUEST REJECTED! from: " + response.getSourceUserName());
			}
		}

		private void handleMessageFromOpponent(OpponentBoundMessage msgObj)
		{
			Log.d(TAG, "handleMessageFromOpponent: " + msgObj);
			publishObject(msgObj);
		}

		private void handleCreateGameResponse(CreateGameResponse response)
		{
			Log.d(TAG, "handleCreateGameResponse: " + response);
			GameBoard gameBoard = response.getGameBoard();
			publishObject(gameBoard);
			logGameBoard(gameBoard);
			Model.setGameBoard(gameBoard);
			Model.setGameDurationMS(response.getGameDurationMS());
			Log.d(TAG, "handleCreateGameResponse: game duration is (ms): " + Model.getGameDurationMS());

			// start the game timer
			startGameTimer();

//            launchBoardActivity();
		}

        /**
         * Launch the Board Activity
         */
        private void launchBoardActivity()
        {
            Log.d(TAG, "launchBoardActivity from ServerActivity");

            // create an Intent for launching the Board Activity, with optional additional params
            Context thisContext = ServerActivity.this;
            Intent intent = new Intent(thisContext, BoardActivity.class);
            intent.putExtra("testParam", "testValue");                        //optional params

            // start the activity
            startActivity(intent);
        }



		private void handleGameMoveResponse(GameMoveResponse response)
		{
			Log.d(TAG, "handleGameMoveResponse: " + response);

			// if the response was for an accepted move request, add the score to the player's score
			if(response.getRequestAccepted())
			{
				int movePointsAwarded = response.getPointsAwarded();
				Model.setScore(Model.getScore() + movePointsAwarded);
				Log.d(TAG, "handleGameMoveResponse: move accepted. New score: " + Model.getScore());
			}
			else
			{
				Log.d(TAG, "handleGameMoveResponse: WARNING: submitted move was not accepted by server.");
			}
			publishObject(response);	// this should also update the UI with any score updates
		}

		private void handleEndGameResponse(EndGameResponse response)
		{
			Log.d(TAG, "handleEndGameResponse: " + response);
			publishObject(response);

			//TODO: note that one EndGameRequest send to the server results in one EndGameResponse going to each matched player, so each will receive 2 EndGames.
			Log.d(TAG, "handleEndGameResponse: *****GAME OVER!***** final score according to server: " + response.getFinalScoreFromServer());
		}

		private void logGameBoard(GameBoard gameBoard)
		{
			Log.d(TAG, "logGameBoard: ");
			gameBoard.printBoardData();
		}

		/**
		 * Store the most recent incoming message Object as a String in the Model, and use the AsyncTasks's onProgressUpdate to display it in the test UI.
		 * This effectively displays incoming messages in the UI while the ServerTask thread is running.
		 *
		 * @param obj
		 */
		private void publishObject(Object obj)
		{
			if(obj != null)
			{
				Model.setIncomingObj(obj);
			}
			publishProgress(1);
		}


		@Override
		protected void onProgressUpdate(Integer... progress)
		{
			Log.d(TAG, "onProgressUpdate");
			updateUI();
		}

		@Override
		protected void onPostExecute(Integer result)
		{
			String str = "onPostExecute: " + result;
			Log.d(TAG, str);
			updateUI();
		}

	} // end inner class InnerServerTask

	/**
	 * Handle the result of the activity launched from this one.
	 * TODO - THE REQUEST CODES ARE NOT WORKING CORRECTLY... FAILED LOGINS ON SERVER SIDE RESULT IN REQUESTCODE 1 HERE
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		if (requestCode == 1)   // see the note in the startActivityForResult above
		{
			Log.d(TAG, "onActivityResult: LOGIN SUCCESS returned from LoginActivity. requestCode: " + requestCode);
			Model.setLoggedIn(true);
		}
		else
		{
			Log.d(TAG, "onActivityResult: LOGIN FAILURE returned from LoginActivity. requestCode: " + requestCode);
			Model.setLoggedIn(false);
		}
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
		Log.d(TAG, "handleIncomingObject, behavior TBD... " + obj);
		updateUI();	// this causes wrong-thread exception
	}


}
