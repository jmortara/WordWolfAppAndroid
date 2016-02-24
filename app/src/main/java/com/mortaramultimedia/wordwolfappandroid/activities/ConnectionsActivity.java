package com.mortaramultimedia.wordwolfappandroid.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.mortaramultimedia.wordwolf.shared.messages.ConnectToDatabaseResponse;
import com.mortaramultimedia.wordwolf.shared.messages.LoginResponse;
import com.mortaramultimedia.wordwolf.shared.messages.SimpleMessage;
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
	private Button loginButton;

	// input text and related refs
	private TextView inputText;
	private Button hideKeyboardButton;
	private Button clearInputButton;

	// game prep buttons
	private Button chooseOpponentButton;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connections);

		createUIReferences();
		loginButton.setVisibility(View.INVISIBLE);
		updateUI();

		Comm.registerCurrentActivity(this);	// tell Comm to forward published progress updates to this Activity
		Comm.connectToServer();
	}

	/**
	 * Create the needed references to buttons and UI elements.
	 */
	private void createUIReferences()
	{
		connectedToServerCheckBox 		= (CheckBox) 	findViewById(R.id.connectedToServerCheckBox);
		connectedToDatabaseCheckBox 	= (CheckBox) 	findViewById(R.id.connectedToDatabaseCheckBox);
		loggedInCheckBox 					= (CheckBox)	findViewById(R.id.loggedInCheckBox);

		usernameText 						= (TextView) 	findViewById(R.id.usernameText);
		opponentUsernameText 			= (TextView) 	findViewById(R.id.opponentUsernameText);

		loginButton 						= (Button)  	findViewById(R.id.loginButton);

		inputText 							= (EditText)	findViewById(R.id.inputText);
		hideKeyboardButton 				= (Button)		findViewById(R.id.hideKeyboardButton);
		clearInputButton 					= (Button)		findViewById(R.id.clearInputButton);

		chooseOpponentButton 			= (Button)		findViewById(R.id.chooseOpponentButton);
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

		// Choose Opponent button
		chooseOpponentButton.setVisibility(View.INVISIBLE);
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

		// create an Intent for launching the Choose Opponent Activity, with optional additional params
		Context thisContext = ConnectionsActivity.this;
		Intent intent = new Intent(thisContext, ChooseOpponentActivity.class);
		intent.putExtra("testParam", "testValue");                        //optional params

		// start the activity
		startActivityForResult(intent, 1);      //TODO: note that in order for this class' onActivityResult to be called when the LoginActivity has completed, the requestCode here must be > 0
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

		//TODO - we are assuming the first object from the server is a SimpleMessage. Change it to a new type e.g. ServerConnectionResponse.
		if (obj instanceof SimpleMessage)
		{
			Comm.connectToDB();
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
		else
		{
			Log.d(TAG, "handleIncomingObject: object ignored.");
		}
	}
}
