package com.mortaramultimedia.wordwolfappandroid.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mortaramultimedia.wordwolf.shared.constants.*;
import com.mortaramultimedia.wordwolf.shared.messages.*;

import com.mortaramultimedia.wordwolfappandroid.R;
import com.mortaramultimedia.wordwolfappandroid.communications.Comm;
import com.mortaramultimedia.wordwolfappandroid.data.Model;
import com.mortaramultimedia.wordwolfappandroid.interfaces.IExtendedAsyncTask;

import java.util.ArrayList;


public class ChooseOpponentActivity extends Activity implements IExtendedAsyncTask
{
	public static final String TAG = "ChooseOpponentActivity";

	// UI refs
	private ListView opponentsListView;
	private ArrayAdapter<String> playersAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_opponent);

		createUIReferences();
		createUIListeners();
		updateUI();

		Comm.registerCurrentActivity(this);	// tell Comm to forward published progress updates to this Activity
		requestPlayerList();
	}

	/**
	 * Create the needed references to buttons and UI elements.
	 */
	private void createUIReferences()
	{
		Log.d(TAG, "createUIReferences");
		opponentsListView 		= (ListView) 	findViewById(R.id.opponentsListView);
	}

	private void createUIListeners()
	{
		Log.d(TAG, "createUIListeners");

		// Opponents List item click listener
		opponentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				final String opponentName = (String) parent.getItemAtPosition(position);
				Log.d(TAG, "Opponent List Item Click Listener: opponent list item clicked: " + opponentName);
				SelectOpponentRequest request = new SelectOpponentRequest(Model.getUserLogin().getUserName(), opponentName);
				Comm.sendObject(request);
			}
		});
	}

	/**
	 * Update this activity's UI.
	 */
	private void updateUI()
	{
		Log.d(TAG, "updateUI");
	}

	/**
	 * Request a list of players/potential opponents from the server.
	 */
	private void requestPlayerList()
	{
		Log.d(TAG, "requestPlayerList");

		GetPlayerListRequest request = new GetPlayerListRequest(PlayerListType.ALL_UNMATCHED_PLAYERS);
		Comm.sendObject(request);
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

		if(obj instanceof SimpleMessage)
		{
			Log.d(TAG, "handleIncomingObject: SimpleMessage: " + ((SimpleMessage)obj).getMsg());
		}
		else if (obj instanceof GetPlayerListResponse)
		{
			ArrayList<String> players = (((GetPlayerListResponse) obj).getPlayersCopy());
			if(players != null && players.size() > 0)
			{
				Log.d(TAG, "handleIncomingObject: players list: " + players);
				playersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, players);
				opponentsListView.setAdapter(playersAdapter);
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
		Log.d(TAG, "launchGameSetupActivity");

		// create an Intent for launching the Game Setup Activity, with optional additional params
		Context thisContext = ChooseOpponentActivity.this;
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
		Log.d(TAG, "launchBoardActivity from ChooseOpponentActivity");

		// create an Intent for launching the Board Activity, with optional additional params
		Context thisContext = ChooseOpponentActivity.this;
		Intent intent = new Intent(thisContext, BoardActivity.class);
		intent.putExtra("testParam", "testValue");                        //optional params

		// start the activity
		startActivity(intent);
	}

}
