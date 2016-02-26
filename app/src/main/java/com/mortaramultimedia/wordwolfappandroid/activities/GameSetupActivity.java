package com.mortaramultimedia.wordwolfappandroid.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mortaramultimedia.wordwolf.shared.messages.CreateGameRequest;
import com.mortaramultimedia.wordwolf.shared.messages.CreateGameResponse;
import com.mortaramultimedia.wordwolf.shared.messages.GameBoard;
import com.mortaramultimedia.wordwolf.shared.messages.GetPlayerListResponse;
import com.mortaramultimedia.wordwolf.shared.messages.SelectOpponentResponse;
import com.mortaramultimedia.wordwolf.shared.messages.SimpleMessage;
import com.mortaramultimedia.wordwolfappandroid.BoardActivity;
import com.mortaramultimedia.wordwolfappandroid.R;
import com.mortaramultimedia.wordwolfappandroid.communications.Comm;
import com.mortaramultimedia.wordwolfappandroid.data.Model;
import com.mortaramultimedia.wordwolfappandroid.interfaces.IExtendedAsyncTask;

import java.io.IOException;
import java.util.ArrayList;


public class GameSetupActivity extends Activity implements IExtendedAsyncTask
{
	public static final String TAG = "GameSetupActivity";

	TextView usernameText;
	TextView opponentUsernameText;
	Button playButton;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_setup);

		createUIReferences();
		//createUIListeners();
		updateUI();

		Comm.registerCurrentActivity(this);	// tell Comm to forward published progress updates to this Activity
	}

	/**
	 * Create the needed references to buttons and UI elements.
	 */
	private void createUIReferences()
	{
		Log.d(TAG, "createUIReferences");
		usernameText 					= (TextView) 	findViewById(R.id.usernameText);
		opponentUsernameText 		= (TextView) 	findViewById(R.id.opponentUsernameText);
		playButton 						= (Button) 		findViewById(R.id.playButton);
	}

	/**
	 * Update this activity's UI.
	 */
	private void updateUI()
	{
		Log.d(TAG, "updateUI");
	}

	/**
	 * Handle the PLAY button press by sending a new CreateGameRequest.
	 * @param view
	 * @throws IOException
	 */
	public void handlePlayButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handlePlayButtonClick: sending create game request, server will need to hear same from opponent to start game...");
		int rows = 5;   //TODO allow player selection of grid size
		int cols = 5;   //TODO allow player selection of grid size
		CreateGameRequest request = new CreateGameRequest(-1, Model.getUserLogin().getUserName(), "defaultGameType", rows, cols, false, -1, -1, Model.getOpponentUsername(), 9000);
		Comm.sendObject(request);
	}

	/////////////////////////////////////
	// IExtendedAsyncTask overrides
	@Override
	public void onTaskCompleted()
	{

	}

	@Override
	public void handleIncomingObject(Object obj)
	{
		Log.d(TAG, "handleIncomingObject: " + obj);

		updateUI();

		if(obj instanceof SimpleMessage)
		{
			Log.d(TAG, "handleIncomingObject: SimpleMessage: " + ((SimpleMessage)obj).getMsg());
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
			Log.d(TAG, "handleIncomingObject: object ignored: " + obj);
		}
	}

	/**
	 * Launch the Board Activity
	 */
	private void launchBoardActivity()
	{
		Log.d(TAG, "launchBoardActivity");

		// create an Intent for launching the Board Activity, with optional additional params
		Context thisContext = GameSetupActivity.this;
		Intent intent = new Intent(thisContext, BoardActivity.class);
		intent.putExtra("testParam", "testValue");                        //optional params

		// start the activity
		startActivity(intent);
	}
}
