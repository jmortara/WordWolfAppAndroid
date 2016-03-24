package com.mortaramultimedia.wordwolfappandroid.activities;

import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mortaramultimedia.wordwolf.shared.messages.*;

import com.mortaramultimedia.wordwolfappandroid.game.GameManager;
import com.mortaramultimedia.wordwolfappandroid.R;
import com.mortaramultimedia.wordwolfappandroid.communications.Comm;
import com.mortaramultimedia.wordwolfappandroid.data.Model;
import com.mortaramultimedia.wordwolfappandroid.interfaces.IExtendedAsyncTask;

import java.io.IOException;


public class GameSetupActivity extends Activity implements IExtendedAsyncTask
{
	public static final String TAG = "GameSetupActivity";

	private TextView mUsernameText;
	private TextView mOpponentUsernameText;
	private ImageButton mPlayButton;
	private Toast waitingToast;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_setup);

		GameManager.resetScore();
		createUIReferences();
		//createUIListeners();
		updateUI();

		Comm.registerCurrentActivity(this);	// tell Comm to forward published progress updates to this Activity
	}

	@Override
	protected void onResume()
	{
		Log.d(TAG, "onResume");
		super.onResume();
		Comm.registerCurrentActivity(this);	// tell Comm to forward published progress updates to this Activity
		Comm.handleAppSentToForeground(this);
		updateUI();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		Log.w(TAG, "onPause ************************");
		dismissWaitingForOpponentToast();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		Log.w(TAG, "onStop ************************");
		//Comm.kill();
	}

	@Override
	public void onTrimMemory(int i)
	{
		Log.w(TAG, "onTrimMemory ************************ " + i);
		if(i == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN || i == ComponentCallbacks2.TRIM_MEMORY_COMPLETE)	// 20 or 80
		{
			Log.d(TAG, "app went to background *******************");
			Comm.handleAppSentToBackground(this);
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Log.e(TAG, "onDestroy ************************");
	}

	@Override
	public void onBackPressed()
	{
		Log.d(TAG, "onBackPressed: Ignoring.");		//TODO - instead of Back, add a Cancel Game button
		// do nothing
	}

	/**
	 * Create the needed references to buttons and UI elements.
	 */
	private void createUIReferences()
	{
		Log.d(TAG, "createUIReferences");

		mUsernameText 			= (TextView) 	findViewById(R.id.usernameText);
		mOpponentUsernameText 	= (TextView) 	findViewById(R.id.opponentUsernameText);
		mPlayButton 			= (ImageButton) findViewById(R.id.playButton);
	}

	/**
	 * Update this activity's UI.
	 */
	private void updateUI()
	{
		Log.d(TAG, "updateUI");

		mUsernameText.setText(Model.getUserLogin().getUserName());
		mOpponentUsernameText.setText(Model.getOpponentUsername());

		updatePlayButtonVisibility();
	}

	/**
	 * Update the visibility of the Play button. Only one client should see it so that multiple start games are not initiated.
	 * Note that the client initiating a SelectOpponentRequest would not have stored the request in the Model; only the receiving client stores it.
	 */
	private void updatePlayButtonVisibility()
	{
		if(Model.getSelectOpponentRequest() == null)
		{
			Log.w(TAG, "updatePlayButtonVisibility: Model.getSelectOpponentRequest() is null... hiding Play button so opponent can start the game.");
			mPlayButton.setVisibility(View.INVISIBLE);
			showWaitingForOpponentToast();
		}
		else if(Model.getSelectOpponentRequest().getSourceUsername().equals(Model.getUserLogin().getUserName()))
		{
			Log.d(TAG, "updatePlayButtonVisibility: this client initiated the SelectOpponentRequest... hiding Play button so opponent can start the game.");
			mPlayButton.setVisibility(View.INVISIBLE);
			showWaitingForOpponentToast();
		}
		else if(Model.getSelectOpponentRequest().getDestinationUserName().equals(Model.getUserLogin().getUserName()))
		{
			Log.d(TAG, "updatePlayButtonVisibility: this client received the SelectOpponentRequest... showing Play button on this client (and hiding on opponent's client)");
			mPlayButton.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Handle the PLAY button press by sending a new CreateGameRequest.
	 * @param view view
	 * @throws IOException
	 */
	public void handlePlayButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handlePlayButtonClick: sending create game request, server will need to hear same from opponent to start game...");
		int rows = 5;   //TODO allow player selection of grid size, e.g. via SettingsActivity menu
		int cols = 5;
		CreateGameRequest request = new CreateGameRequest(-1, Model.getUserLogin().getUserName(), "defaultGameType", rows, cols, false, -1, -1, Model.getOpponentUsername(), 9000);
		Comm.sendObject(request);
	}

	private void showWaitingForOpponentToast()
	{
		Log.d(TAG, "showWaitingForOpponentToast");

		waitingToast = Toast.makeText(this, "Waiting for " + Model.getOpponentUsername() + " to start the game, one moment...", Toast.LENGTH_LONG);
		waitingToast.setGravity(Gravity.CENTER, 0, 0);
		waitingToast.show();
	}

	private void dismissWaitingForOpponentToast()
	{
		Log.d(TAG, "dismissWaitingForOpponentToast");

		if (waitingToast != null)
		{
			waitingToast.cancel();
			waitingToast = null;
		}
	}

	/////////////////////////////////////
	// IExtendedAsyncTask overrides
	@Override
	public void onTaskCompleted()
	{
		Log.d(TAG, "onTaskCompleted");
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
			Model.setOpponentUsername(response.getOpponentUserName());	// this may have already been set via an earlier dialog, but here it is validated by coming from the server
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
		Log.d(TAG, "launchBoardActivity from GameSetupActivity");

		dismissWaitingForOpponentToast();

		// create an Intent for launching the Board Activity, with optional additional params
		Context thisContext = GameSetupActivity.this;
		Intent intent = new Intent(thisContext, BoardActivity.class);
		intent.putExtra("testParam", "testValue");                        //optional params

		// start the activity
		startActivity(intent);
	}
}
