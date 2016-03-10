package com.mortaramultimedia.wordwolfappandroid.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.io.IOException;
import java.util.ArrayList;


public class ChooseOpponentActivity extends Activity implements IExtendedAsyncTask
{
	public static final String TAG = "ChooseOpponentActivity";

	// UI refs
	private ListView opponentsListView;
	private ArrayAdapter<String> playersAdapter;

	// dialog
	private AlertDialog selectOpponentRequestDialog = null;


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

	@Override
	protected void onResume()
	{
		Log.d(TAG, "onResume");
		super.onResume();
		Comm.registerCurrentActivity(this);	// tell Comm to forward published progress updates to this Activity
		updateUI();
		requestPlayerList();
	}

	@Override
	public void onBackPressed()
	{
		Log.d(TAG, "onBackPressed: Ignoring.");
		// do nothing
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
		opponentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

	public void handleRefreshOpponentsListButtonClick(View view) throws IOException
	{
		Log.d(TAG, "handleRefreshOpponentsListButtonClick");
		requestPlayerList();
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
			handleGetPlayerListResponse((GetPlayerListResponse) obj);
		}
		else if (obj instanceof SelectOpponentRequest)
		{
			if (((SelectOpponentRequest) obj).getDestinationUserName().equals(Model.getUserLogin().getUserName()))
			{
				showSelectOpponentRequestDialog((SelectOpponentRequest) obj);
			}
			else
			{
				Log.w(TAG, "handleIncomingObject: WARNING: SelectOpponentRequest name mismatch: " + obj);
			}
		}
		else if (obj instanceof SelectOpponentResponse)
		{
			handleSelectOpponentResponse(((SelectOpponentResponse) obj));
		}
		else if(obj instanceof CreateGameResponse)
		{
			handleCreateGameResponse((CreateGameResponse) obj);
		}
		else
		{
			Log.d(TAG, "handleIncomingObject: object ignored.");
		}
	}


	private void handleGetPlayerListResponse(GetPlayerListResponse response)
	{
		Log.d(TAG, "handleGetPlayerListResponse: " + response);

		ArrayList<String> playersList = (response.getPlayersCopy());

		if(playersList != null)
		{
			if (playersList.size() > 0)
			{
				Log.d(TAG, "handleGetPlayerListResponse: players list: " + playersList);
			}

			if (playersList.size() == 0)
			{
				Log.w(TAG, "handleGetPlayerListResponse: WARNING: empty players list. UI may show no avail opponents.");
			}

			playersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playersList);
			opponentsListView.setAdapter(playersAdapter);
		}
		else
		{
			Log.w(TAG, "handleGetPlayerListResponse: WARNING: null players list. UI may show no avail opponents.");
		}
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
			launchGameSetupActivity();
		}
		else
		{
			Log.d(TAG, "handleRequestToBecomeOpponent: REQUEST REJECTED! from: " + response.getSourceUserName());
		}
	}

	private void handleCreateGameResponse(CreateGameResponse response)
	{
		Log.d(TAG, "handleCreateGameResponse: CreateGameResponse CAME IN: " + response);

		// store the GameBoard and game duration in the Model
		Model.setGameBoard(response.getGameBoard());
		Model.setGameDurationMS(response.getGameDurationMS());

		Log.d(TAG, "handleCreateGameResponse: GAME BOARD RECEIVED: \n");
		Model.getGameBoard().printBoardData();

		Log.d(TAG, "handleCreateGameResponse: ***STARTING GAME***");
		launchBoardActivity();
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
