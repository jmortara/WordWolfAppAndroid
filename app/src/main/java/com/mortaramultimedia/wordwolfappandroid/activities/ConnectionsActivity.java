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

import com.mortaramultimedia.wordwolf.shared.messages.*;
import com.mortaramultimedia.wordwolf.shared.constants.Constants;
import com.mortaramultimedia.wordwolfappandroid.R;
import com.mortaramultimedia.wordwolfappandroid.communications.Comm;
import com.mortaramultimedia.wordwolfappandroid.data.Model;
import com.mortaramultimedia.wordwolfappandroid.interfaces.IExtendedAsyncTask;

import java.io.IOException;


public class ConnectionsActivity extends Activity implements IExtendedAsyncTask
{
	public static final String TAG = "ConnectionsActivity";

//	private ServerIOTask serverIOTask;            // async task which handles in/out socket streams
//	private DatabaseAsyncTask databaseTask;   // external async task
//	private LoginAsyncTask loginTask;         // external async task

	// connection, database and login View references
	private CheckBox connectedToServerCheckBox;
	private CheckBox connectedToDatabaseCheckBox;
	private CheckBox loggedInCheckBox;

	// username field references
	private TextView usernameText;
	private TextView opponentUsernameText;

	// login buttons
	private ImageButton loginButton;
	private ImageButton createNewAccountButton;

	// input text and related refs
	private TextView inputText;
	private Button hideKeyboardButton;
	private Button clearInputButton;

	// game prep buttons
	private ImageButton chooseOpponentButton;

	// dialogs
	private AlertDialog selectOpponentRequestDialog  = null;
	private AlertDialog createNewAccountResultDialog = null;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connections);

		createUIReferences();
		loginButton.setVisibility(View.INVISIBLE);
		createNewAccountButton.setVisibility(View.INVISIBLE);
		chooseOpponentButton.setVisibility(View.INVISIBLE);
		updateUI();

		Comm.registerCurrentActivity(this);	// tell Comm to forward published progress updates to this Activity
		Comm.connectToServer();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Log.d(TAG, "onResume");
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
		createNewAccountButton			= (ImageButton) findViewById(R.id.createNewAccountButton);

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
		Log.d(TAG, "updateUI: user login obj is: " + Model.getUserLogin());

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
		else
		{
			usernameText.setText("(username)");
		}

		// Opponent info
		if (Model.getOpponentUsername() != null)
		{
			opponentUsernameText.setText(Model.getOpponentUsername());
		}

		if (!Model.getLoggedIn())
		{
			chooseOpponentButton.setVisibility(View.INVISIBLE);
		}
		else
		{
			chooseOpponentButton.setVisibility(View.VISIBLE);
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
		if (serverIOTask == null && !Model.getConnected())
		{
			serverIOTask = new ServerIOTask(this);
			serverIOTask.execute();
		}
		else
		{
			Log.d(TAG, "connectToServer: not connected and serverIOTask is null.");
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
	 * Create New Account Button handler - sends new account request to server
	 * @param view
	 * @throws IOException
	 */
	public void handleCreateNewAccountButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleCreateNewAccountButtonClick");

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
	 * ResultCodes coming back from launched Activity:
	 * RESULT_OK (0), RESULT_CANCELED (-1), RESULT_CREATE_NEW_ACCOUNT_OK (-2), RESULT_CREATE_NEW_ACCOUNT_CANCELED (-3)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		if (resultCode == LoginActivity.RESULT_CREATE_NEW_ACCOUNT_OK)
		{
			Log.d(TAG, "onActivityResult: Create New Account SUCCESS returned from LoginActivity. resultCode: " + resultCode);
			createNewAccountButton.setVisibility(View.INVISIBLE);
			showCreateNewAccountResultDialog();
		}
		else if (resultCode == LoginActivity.RESULT_CREATE_NEW_ACCOUNT_CANCELED)
		{
			Log.d(TAG, "onActivityResult: Create New Account FAILURE returned from LoginActivity. resultCode: " + resultCode);
			showCreateNewAccountResultDialog();
		}
		else if (resultCode == RESULT_OK)   // see the note in the startActivityForResult above
		{
			Log.d(TAG, "onActivityResult: LOGIN SUCCESS returned from LoginActivity. resultCode: " + resultCode);
		}
		else if (resultCode == RESULT_CANCELED)
		{
			Log.d(TAG, "onActivityResult: LOGIN FAILURE returned from LoginActivity. resultCode: " + resultCode);

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
				Log.d(TAG, "handleIncomingObject: SimpleMessage: " + ((SimpleMessage) obj).getMsg());
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
			dismissSelectOpponentRequestDialog();
			launchBoardActivity();
		}
		else
		{
			Log.d(TAG, "handleIncomingObject: object ignored.");
		}
	}

	private void showCreateNewAccountResultDialog()
	{
		Log.d(TAG, "showCreateNewAccountResultDialog");

		createNewAccountResultDialog = new AlertDialog.Builder(this).create();
		if (Model.getNewAccountCreated())
		{
			createNewAccountResultDialog.setTitle("Account Created!");
			createNewAccountResultDialog.setMessage("Your new account has been created! \n\n" +
													"Please Log In to continue.");
		}
		else
		{
			createNewAccountResultDialog.setTitle("New Account Failed");
			createNewAccountResultDialog.setMessage("Hmm. We could not create a new account for you. \n\n" +
													"Please try creating an account again, " +
													"or log in under another username.");
		}
		createNewAccountResultDialog.setCancelable(false);

		// set up and listener for Accept button
		createNewAccountResultDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		createNewAccountResultDialog.show();
	}

	private void showSelectOpponentRequestDialog(SelectOpponentRequest request)
	{
		Log.d(TAG, "showSelectOpponentRequestDialog");

		final String sourceUsername = request.getSourceUsername();
		selectOpponentRequestDialog = new AlertDialog.Builder(this).create();
		selectOpponentRequestDialog.setTitle("Opponent Request");
		selectOpponentRequestDialog.setMessage("You have been invited to start a new game with: " + request.getSourceUsername());
		selectOpponentRequestDialog.setCancelable(false);

		// set up and listener for Accept button
		selectOpponentRequestDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Accept!", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// we can't get the request source player's username as an arg, so we have to retrieve it from the stored incomingObj
//				SelectOpponentRequest request = (SelectOpponentRequest) Model.getIncomingObj();
//				String sourceUsername = request.getSourceUsername();
				Model.setOpponentUsername(sourceUsername);
				SelectOpponentResponse response = new SelectOpponentResponse(true, Model.getUserLogin().getUserName(), sourceUsername);
				Comm.sendObject(response);
			}
		});

		// set up and listener for Decline button
		selectOpponentRequestDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Decline", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// we can't get the request source player's username as an arg, so we have to retrieve it from the stored incomingObj
//				SelectOpponentRequest request = (SelectOpponentRequest) Model.getIncomingObj();
//				String sourceUsername = request.getSourceUsername();
				SelectOpponentResponse response = new SelectOpponentResponse(false, Model.getUserLogin().getUserName(), sourceUsername);
				Comm.sendObject(response);
			}
		});

		selectOpponentRequestDialog.show();
	}

	private void handleSelectOpponentResponse(SelectOpponentResponse response)
	{
		Log.d(TAG, "handleSelectOpponentResponse: " + response);

//		publishObject(response);
		if (response.getRequestAccepted())
		{
			Log.d(TAG, "handleRequestToBecomeOpponent: REQUEST ACCEPTED! from: " + response.getSourceUserName());
			Model.setOpponentUsername(response.getSourceUserName());
			dismissSelectOpponentRequestDialog();
			launchGameSetupActivity();
		}
		else
		{
			Log.d(TAG, "handleRequestToBecomeOpponent: REQUEST REJECTED! from: " + response.getSourceUserName());
		}
	}

	private void dismissSelectOpponentRequestDialog()
	{
		Log.d(TAG, "dismissSelectOpponentRequestDialog");

		if(selectOpponentRequestDialog != null)
		{
			selectOpponentRequestDialog.dismiss();
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
