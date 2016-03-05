package com.mortaramultimedia.wordwolfappandroid.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mortaramultimedia.wordwolf.shared.constants.Constants;
import com.mortaramultimedia.wordwolf.shared.messages.*;
import com.mortaramultimedia.wordwolfappandroid.R;
import com.mortaramultimedia.wordwolfappandroid.communications.Comm;
import com.mortaramultimedia.wordwolfappandroid.data.Model;
import com.mortaramultimedia.wordwolfappandroid.interfaces.IExtendedAsyncTask;

import java.io.IOException;


public class ConnectionsActivity extends Activity implements IExtendedAsyncTask
{
	public static final String TAG = "ConnectionsActivity";

//	private ServerTask serverTask;            // async task which handles in/out socket streams
//	private DatabaseAsyncTask databaseTask;   // external async task
//	private LoginAsyncTask loginTask;         // external async task

	// connection, database and login View references
	private CheckBox connectedToServerCheckBox;
	private CheckBox connectedToDatabaseCheckBox;
	private CheckBox loggedInCheckBox;

	// username field references
	private TextView usernameText;
	private TextView opponentUsernameText;

	// login button
	private ImageButton loginButton;

	// input text and related refs
	private TextView inputText;
	private Button hideKeyboardButton;
	private Button clearInputButton;

	// game prep buttons
	private ImageButton chooseOpponentButton;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connections);

		createUIReferences();
		loginButton.setVisibility(View.INVISIBLE);
		chooseOpponentButton.setVisibility(View.INVISIBLE);
		updateUI();

		Comm.registerCurrentActivity(this);	// tell Comm to forward published progress updates to this Activity
		Comm.connectToServer();
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
		connectedToServerCheckBox 		= (CheckBox) 	findViewById(R.id.connectedToServerCheckBox);
		connectedToDatabaseCheckBox 	= (CheckBox) 	findViewById(R.id.connectedToDatabaseCheckBox);
		loggedInCheckBox 				= (CheckBox)	findViewById(R.id.loggedInCheckBox);

		usernameText 					= (TextView) 	findViewById(R.id.usernameText);
		opponentUsernameText 			= (TextView) 	findViewById(R.id.opponentUsernameText);

		loginButton 					= (ImageButton) findViewById(R.id.loginButton);

		inputText 						= (EditText)	findViewById(R.id.inputText);
		hideKeyboardButton 				= (Button)		findViewById(R.id.hideKeyboardButton);
		clearInputButton 				= (Button)		findViewById(R.id.clearInputButton);

		chooseOpponentButton 			= (ImageButton)	findViewById(R.id.chooseOpponentButton);
	}

	/**
	 * Update this activity's UI.
	 */
	private void updateUI()
	{
		Log.d(TAG, "updateUI");

		// Server Connection Indicator
		connectedToServerCheckBox.setChecked(Model.getConnected());

		// DB Connection Indicator
		connectedToDatabaseCheckBox.setChecked(Model.getConnectedToDatabase());

		// Logged In Indicator
		loggedInCheckBox.setChecked(Model.getLoggedIn());

		// Login UI
		loginButton.setClickable(!Model.getLoggedIn());

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
	}

	/*private void connectToServer()	//TODO - implement
	{
		Log.d(TAG, "connectToServer");

		if (Model.getConnected())
		{
			Log.d(TAG, "connectToServer: already connected!");
			return;
		}

		// Start network tasks separate from the main UI thread
		if (serverTask == null && !Model.getConnected())
		{
			serverTask = new ServerTask(this);
			serverTask.execute();
		}
		else
		{
			Log.d(TAG, "connectToServer: not connected and serverTask is null.");
		}
	}*/

	/*private void connectToDB()
	{
		Log.d(TAG, "connectToDB");

		databaseTask = new DatabaseAsyncTask(this, this);	//TODO: note, the second param can be a different Activity if that is more useful

		// workaround for issues with execute() not working properly on AsyncTasks
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			databaseTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else
		{
			databaseTask.execute();
		}
	}*/

	/**
	 * Login Button handler - launches LoginActivity
	 * @param view
	 * @throws IOException
	 */
	public void handleLoginButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleLoginButtonClick");

		// create an Intent for launching the Login Activity, with optional additional params
		Context thisContext = ConnectionsActivity.this;
		Intent intent = new Intent(thisContext, LoginActivity.class);
		intent.putExtra("testParam", "testValue");                        //optional params

		// start the activity
		startActivityForResult(intent, 1);      //TODO: note that in order for this class' onActivityResult to be called when the LoginActivity has completed, the requestCode here must be > 0
	}

	/**
	 * Choose Opponent button handler. Launches a new activity for selecting an opponent.
	 * @param view
	 * @throws IOException
	 */
	public void handleChooseOpponentButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleChooseOpponentButtonClick");

		launchChooseOpponentActivity();
	}

	/**
	 * Launch the Choose Opponent Activity
	 */
	private void launchChooseOpponentActivity()
	{
		Log.d(TAG, "launchChooseOpponentActivity");

		// create an Intent for launching the Choose Opponent Activity, with optional additional params
		Context thisContext = ConnectionsActivity.this;
		Intent intent = new Intent(thisContext, ChooseOpponentActivity.class);
		intent.putExtra("testParam", "testValue");                        //optional params

//		startActivityForResult(intent, 1);
		startActivity(intent);	// maybe we don't need to return to this activity with a result... just move forward from there.
	}

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
		Log.d(TAG, "handleIncomingObject: " + obj);

		updateUI();

		if (obj instanceof SimpleMessage)
		{
			// if it's a handshake String, connect to the DB
			if(((SimpleMessage) obj).getMsg().equals(Constants.HELLO_CLIENT))	//TODO - Change to a new type e.g. ServerConnectionResponse.
			{
				Comm.connectToDB();
			}
			else
			{
				Log.d(TAG, "handleIncomingObject: SimpleMessage: " + ((SimpleMessage)obj).getMsg());
			}
		}
		else if (obj instanceof ConnectToDatabaseResponse)
		{
			if (((ConnectToDatabaseResponse) obj).getSuccess())
			{
				loginButton.setVisibility(View.VISIBLE);
			}
		}
		else if (obj instanceof LoginResponse)
		{
			if (((LoginResponse) obj).getLoginAccepted())
			{
				chooseOpponentButton.setVisibility(View.VISIBLE);
			}
		}
		else if (obj instanceof SelectOpponentRequest)
		{
			if (((SelectOpponentRequest) obj).getDestinationUserName().equals(Model.getUserLogin().getUserName()))
			{
				showSelectOpponentRequestDialog((SelectOpponentRequest) obj);
			}
		}
		else if (obj instanceof SelectOpponentResponse)
		{
			handleSelectOpponentResponse(((SelectOpponentResponse) obj));
		}
		else if(obj instanceof CreateGameResponse)
		{
			Log.d(TAG, "handleIncomingObject: CreateGameResponse CAME IN: " + obj);

			// store the GameBoard and game duration in the Model
			CreateGameResponse response = (CreateGameResponse) obj;
			Model.setGameBoard(response.getGameBoard());
			Model.setGameDurationMS(response.getGameDurationMS());

			Log.d(TAG, "handleIncomingObject: GAME BOARD RECEIVED: \n");
			Model.getGameBoard().printBoardData();

			Log.d(TAG, "handleIncomingObject: ***STARTING GAME***");
			launchBoardActivity();
		}
		else
		{
			Log.d(TAG, "handleIncomingObject: object ignored.");
		}
	}

	private void showSelectOpponentRequestDialog(SelectOpponentRequest request)
	{
		Log.d(TAG, "showSelectOpponentRequestDialog");

		final String sourceUsername = request.getSourceUsername();
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle("Opponent Request");
		dialog.setMessage("You have been invited to start a new game with: " + request.getSourceUsername());

		// set up and listener for Accept button
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Accept!", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// we can't get the request source player's username as an arg, so we have to retrieve it from the stored incomingObj
//				SelectOpponentRequest request = (SelectOpponentRequest) Model.getIncomingObj();
//				String sourceUsername = request.getSourceUsername();
				Model.setOpponentUsername(sourceUsername);
				SelectOpponentResponse response = new SelectOpponentResponse(true, Model.getUserLogin().getUserName(), sourceUsername);
				Comm.sendObject(response);
			}
		});

		// set up and listener for Decline button
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Decline", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// we can't get the request source player's username as an arg, so we have to retrieve it from the stored incomingObj
//				SelectOpponentRequest request = (SelectOpponentRequest) Model.getIncomingObj();
//				String sourceUsername = request.getSourceUsername();
				SelectOpponentResponse response = new SelectOpponentResponse(false, Model.getUserLogin().getUserName(), sourceUsername);
				Comm.sendObject(response);
			}
		});

		dialog.show();
	}

	private void handleSelectOpponentResponse(SelectOpponentResponse response)
	{
		Log.d(TAG, "handleSelectOpponentResponse: " + response);

//		publishObject(response);
		if (response.getRequestAccepted())
		{
			Log.d(TAG, "handleRequestToBecomeOpponent: REQUEST ACCEPTED! from: " + response.getSourceUserName());
			Model.setOpponentUsername(response.getSourceUserName());
			launchGameSetupActivity();
		}
		else
		{
			Log.d(TAG, "handleRequestToBecomeOpponent: REQUEST REJECTED! from: " + response.getSourceUserName());
		}
	}

	/**
	* Launch the Game Setup Activity
	*/
	private void launchGameSetupActivity()
	{
		Log.d(TAG, "launchGameSetupActivity from ConnectionsActivity");

		// create an Intent for launching the Game Setup Activity, with optional additional params
		Context thisContext = ConnectionsActivity.this;
		Intent intent = new Intent(thisContext, GameSetupActivity.class);
		intent.putExtra("testParam", "testValue");                        //optional params

		// start the activity
		startActivity(intent);
	}

	/**
	 * Launch the Board Activity
	 */
	private void launchBoardActivity()
	{
		Log.d(TAG, "launchBoardActivity from ConnectionsActivity");

		// create an Intent for launching the Board Activity, with optional additional params
		Context thisContext = ConnectionsActivity.this;
		Intent intent = new Intent(thisContext, BoardActivity.class);
		intent.putExtra("testParam", "testValue");                        //optional params

		// start the activity
		startActivity(intent);
	}
}
