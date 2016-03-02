package com.mortaramultimedia.wordwolfappandroid.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mortaramultimedia.wordwolf.shared.messages.EndGameRequest;

import com.mortaramultimedia.wordwolfappandroid.data.Model;
import com.mortaramultimedia.wordwolfappandroid.communications.Comm;
import com.mortaramultimedia.wordwolfappandroid.fragments.BoardFragment;
import com.mortaramultimedia.wordwolfappandroid.DictionaryActivity;
import com.mortaramultimedia.wordwolfappandroid.GameManager;
import com.mortaramultimedia.wordwolfappandroid.R;

import java.util.ArrayList;
import java.util.Timer;

/**
 * Created by Jason Mortara on 11/15/14.
 */
public class BoardActivity extends Activity implements BoardFragment.OnFragmentInteractionListener
{

	public static final String TAG = "BoardActivity";
	private TextView wordSoFarText;
	private TextView scoreText;
	private Timer gameTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_board);

		init();
	}

	private void init() {
		Log.d(TAG, "init");

		initGameData();
		setupBoard();
		startGameTimer();
	}

	private void initGameData()
	{
		Log.d(TAG, "initGameData");
		Model.validWordsThisGame = new ArrayList<String>();
	}


	private void setupBoard()
	{
		Log.d(TAG, "setupBoard");
		wordSoFarText = (TextView) findViewById(R.id.wordSoFarText);
		scoreText = (TextView) findViewById(R.id.scoreText);
	}


	///////////////////////////////////////////
	// GAME TIMER - START AND END GAME

	/**
	 * Start the game timer, which counts down on both clients for the duration specified
	 * in the CreateGameResponse. When complete, the game ends and the client sends
	 * the server a related message with scores.
	 */
	private void startGameTimer()
	{
		Log.d(TAG, "startGameTimer *************************");

		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				handleGameOver();
			}
		}, Model.getGameDurationMS());	// end new Handler
	}


	/**
	 * Handle the completion of the game timer. Ths should trigger a Game Over sequence.
	 */
	/*private void handleGameTimerCompleted()
	{
		Log.d(TAG, "handleGameTimerCompleted");
		if(gameTimer != null)
		{
			gameTimer.cancel();
		}
		handleGameOver();
	}*/

	private void handleGameOver()
	{
		Log.d(TAG, "handleGameOver");

		// notify the server that the game has ended
		EndGameRequest request = new EndGameRequest(Model.getUserLogin().getUserName(), -1);
		Comm.sendObject(request);

		// show a Game Over dialog, maybe w/ some relevant params
		showGameOverDialog(request);
	}

	private void showGameOverDialog(EndGameRequest request)
	{
		Log.d(TAG, "showGameOverDialog");

		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle("Game Over!");
		dialog.setMessage("Your game with " + Model.getOpponentUsername() + " has ended.\nYour score: " + Model.getScore() + "\n\nRematch?");

		// set up and listener for Accept button
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes! Rematch!", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d(TAG, "showGameOverDialog: dialog button pressed: positive");
				//TODO: start new game without starting a new Activity
			}
		});

		// set up and listener for Decline button
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No thanks.", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Log.d(TAG, "showGameOverDialog: dialog button pressed: negative");
				//TODO: send to new Activity, or Choose Opponent Activity
			}
		});

		dialog.show();
	}


	////////////////////////////////
	//
	public void handleBoardFragmentButtonClick(View view)	//TODO: remove? unused.
	{
		Log.d(TAG, "handleBoardFragmentButtonClick");
	}

	public void updateWordDisplay()
	{
		Log.d( TAG, "updateWordDisplay" );
		if ( wordSoFarText != null )
		{
			wordSoFarText.setText( getResources().getString(R.string.word_so_far) + GameManager.getWordSoFar() );
		}
	}

	public void updateScoreDisplay()
	{
		Log.d( TAG, "updateScoreDisplay" );
		scoreText.setText( getResources().getString(R.string.score) + " " + Model.validWordsThisGame.size() );
	}

	public void handleSubmitButtonClick(View view)
	{
		Log.d(TAG, "handleSubmitButtonClick");

		// submit the word for comparison with dictionary and increment score if nec
		Boolean wordIsValid = GameManager.checkWordValidity();
		if ( wordIsValid )
		{
			Model.validWordsThisGame.add( GameManager.getWordSoFar() );
			GameManager.printValidWordsThisGame();
		}

		// reset the word and the word display
		GameManager.startNewWord();
		BoardFragment boardFragment = (BoardFragment) getFragmentManager().findFragmentById(R.id.boardFragment);	//TODO: make class var
		boardFragment.resetAllTileViews();
		updateWordDisplay();
		updateScoreDisplay();
	}

	public void handleDictionaryButtonClick(View view)
	{
		Log.d(TAG, "handleDictionaryButtonClick");

		Intent dictionaryIntent = new Intent(this, DictionaryActivity.class);
		startActivity(dictionaryIntent);
	}

	public void onFragmentInteraction(Uri uri)
	{
		Log.d(TAG, "onFragmentInteraction");
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.board, menu);
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


}

