package com.mortaramultimedia.wordwolfappandroid.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
				Log.d(TAG, "handleIncomingObject: opponent list item clicked: " + opponentName);
				SelectOpponentRequest request = new SelectOpponentRequest(Model.getUserLogin().getUserName(), opponentName);
				try
				{
					Comm.out().writeObject(request);
					Comm.out().flush();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

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
		try
		{
			Comm.out().writeObject(request);
			Comm.out().flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
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

		if (obj instanceof GetPlayerListResponse)
		{
			ArrayList<String> players = (((GetPlayerListResponse) obj).getPlayersCopy());
			if(players != null && players.size() > 0)
			{
				Log.d(TAG, "handleIncomingObject: players list: " + players);
				playersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, players);
				opponentsListView.setAdapter(playersAdapter);
			}
		}
		else
		{
			Log.d(TAG, "handleIncomingObject: object ignored.");
		}
	}
}
