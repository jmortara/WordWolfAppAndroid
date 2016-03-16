package com.mortaramultimedia.wordwolfappandroid.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mortaramultimedia.wordwolf.shared.messages.*;
import com.mortaramultimedia.wordwolfappandroid.communications.Comm;
import com.mortaramultimedia.wordwolfappandroid.data.Model;
import com.mortaramultimedia.wordwolfappandroid.fragments.BoardFragment;
import com.mortaramultimedia.wordwolfappandroid.game.GameManager;
import com.mortaramultimedia.wordwolfappandroid.R;
import com.mortaramultimedia.wordwolfappandroid.interfaces.IExtendedAsyncTask;
import com.mysql.jdbc.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jason Mortara on 11/15/14.
 */
public class BoardActivity extends Activity implements BoardFragment.OnFragmentInteractionListener, IExtendedAsyncTask
{

	private static final String TAG = "BoardActivity";

	// views
	private ImageView mGameTimerForeground;
	private TextView mWordSoFarText;
	private TextView mScoreText;
	private TextView mTimerText;
	private int gameTimerFGOrigWidth;

	BoardFragment boardFragment = null;
	Timer gameTimer = null;
	TimerTask gameTimerTask = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_board);

		Comm.registerCurrentActivity(this);	// tell Comm to forward published progress updates to this Activity
		init();
	}



	@Override
	protected void onResume()
	{
		super.onResume();
		Log.d(TAG, "onResume");
		Comm.registerCurrentActivity(this);	// tell Comm to forward published progress updates to this Activity
	}

	@Override
	public void onBackPressed()
	{
		Log.d(TAG, "onBackPressed: Ignoring.");
		// do nothing
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
		Model.setValidWordsThisGame( new ArrayList<String>() );
		Model.setGameMovesThisGame( new HashSet<GameMove>() );
	}


	private void setupBoard()
	{
		Log.d(TAG, "setupBoard");
		mWordSoFarText 			= (TextView) 	findViewById(R.id.wordSoFarText);
		mScoreText 				= (TextView) 	findViewById(R.id.scoreText);
		mTimerText 				= (TextView) 	findViewById(R.id.timerText);
		mGameTimerForeground 	= (ImageView) 	findViewById(R.id.gameTimerForeground);
	}


	///////////////////////////////////////////
	// GAME TIMER

	/**
	 * Start the game timer, which counts down on both clients for the duration specified
	 * in the CreateGameResponse. When complete, the game ends and the client sends
	 * the server a related message with scores.
	 */
	private void startGameTimer()
	{
		Log.d(TAG, "startGameTimer for duration (ms): " + Model.getGameDurationMS());

		final int GREEN 	= Color.rgb(26, 219, 62);
		final int YELLOW 	= Color.rgb(219, 203, 26);
		final int RED 		= Color.rgb(219, 26, 26);
		final long duration = Model.getGameDurationMS();
		final int MIN_WIDTH = 1;
		final int ORIG_WIDTH = mGameTimerForeground.getLayoutParams().width;		//TODO - get dynamically after layout inflated
		final Integer decrement = ORIG_WIDTH / 60;									//TODO - calculate dynamically e.g. ORIG_WIDTH / (int) (long) Model.getGameDurationMS();
		final Handler gameTimerHandler = new Handler();

		mTimerText.setText(Long.toString(duration/1000));
		mGameTimerForeground.setColorFilter(GREEN, android.graphics.PorterDuff.Mode.MULTIPLY);

		gameTimerHandler.postDelayed(new Runnable()
		{
			long timeElapsedMS = 0;
			long timeRemaingSecs = duration;
			String timeElapsedTensStr = "";

			@Override
			public void run()
			{
				timeElapsedMS += 1000;
				if(timeElapsedMS < duration)
				{
					if(mGameTimerForeground.getLayoutParams().width > MIN_WIDTH)
					{
						//Log.d(TAG, "gameTimer going for... " + timeElapsedMS);

						gameTimerHandler.postDelayed(this, 1000);
						mGameTimerForeground.getLayoutParams().width -= decrement;
						timeRemaingSecs = (duration - timeElapsedMS)/1000;

						// color indicator as time runs low
						if(timeElapsedMS > duration * 0.80)
						{
							mGameTimerForeground.setColorFilter(RED, android.graphics.PorterDuff.Mode.MULTIPLY);
						}
						else if (timeElapsedMS > duration * 0.60)
						{
							mGameTimerForeground.setColorFilter(YELLOW, android.graphics.PorterDuff.Mode.MULTIPLY);
						}
						else if(timeElapsedMS > 0)
						{
							mGameTimerForeground.setColorFilter(GREEN, android.graphics.PorterDuff.Mode.MULTIPLY);
						}

						// set timer text, seconds remaining
						if(timeRemaingSecs < 10)
						{
							timeElapsedTensStr = "0";		// tens place zero when needed
						}
						mTimerText.setText(":" + timeElapsedTensStr + Long.toString(timeRemaingSecs));
						mGameTimerForeground.requestLayout();

						//Log.d(TAG, "gameTimer decrement, new width: " + decrement + ", " + mGameTimerForeground.getLayoutParams().width);
					}
				}
				// when time elapsed hits the limit of the game duration, it's game over
				else
				{
					gameTimerHandler.removeCallbacks(this);
					handleGameOver();
				}
			}
		}, 1000);	// delay, ms
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







	////////////////////////////////
	//
	public void handleBoardFragmentButtonClick(View view)	//TODO: remove? unused.
	{
		Log.d(TAG, "handleBoardFragmentButtonClick");
	}

	public void updateWordDisplay()
	{
		Log.d(TAG, "updateWordDisplay");
		if ( mWordSoFarText != null )
		{
			mWordSoFarText.setText( getResources().getString(R.string.word_so_far) + GameManager.getWordSoFar() );
		}
	}

	public void updateScoreDisplay()
	{
		Log.d(TAG, "updateScoreDisplay");
		Integer newScore = Model.getScore();	//Integer.valueOf(Model.validWordsThisGame.size());
		String newScoreStr = newScore.toString();
		mScoreText.setText(newScoreStr);
//		mScoreText.setText(getResources().getString(R.string.score) + " " + Model.validWordsThisGame.size());
	}

	public void handleSubmitWordButtonClick(View view)
	{
		//Log.d(TAG, "handleSubmitWordButtonClick");

		// submit the word for comparison with dictionary and increment score if nec
		Boolean wordIsValid = GameManager.checkWordValidity();
		if(wordIsValid)
		{
			ArrayList<TileData> selectedTilesCopy = new ArrayList<TileData>(Model.getSelectedTiles());
			GameMove gameMove = new GameMove(selectedTilesCopy);

			// make sure the same exact move can't be submitted twice (although the same word made with different TileDatas is OK)
			Boolean gameMoveIsUnique = Model.getGameMovesThisGame().add(gameMove);					// add to the Set in the Model. If trying to add a dupe, will not add, and return false.
			Log.d(TAG, "handleSubmitWordButtonClick: is gameMove unique? hashcode? total # of moves made? " + gameMoveIsUnique + ", " + gameMove.hashCode() + ", " + Model.getGameMovesThisGame().size());

			if(gameMoveIsUnique)
			{
				// client
				Model.getValidWordsThisGame().add(GameManager.getWordSoFar());
				GameManager.printValidWordsThisGame();

				// server: put a copy of the sequence of TileData stored in the Model
				// into a GameMove obj and send out for server-side validation
				GameMoveRequest request = new GameMoveRequest(Model.getUserLogin().getUserName(), -1, gameMove);
				Comm.sendObject(request);
			}
			else
			{
				Log.w(TAG, "handleSubmitWordButtonClick: This move has already been played. Ignoring. " + gameMove);
			}
		}

		// reset the word and the word display
		GameManager.startNewWord();
		boardFragment = (BoardFragment) getFragmentManager().findFragmentById(R.id.boardFragment);
		boardFragment.resetAllTileViews();
		updateWordDisplay();
		//updateScoreDisplay();
	}

	public void handleViewDictionaryButtonClick(View view)
	{
		Log.d(TAG, "handleViewDictionaryButtonClick");

		Intent dictionaryIntent = new Intent(this, DictionaryActivity.class);
		startActivity(dictionaryIntent);
	}

	public void onFragmentInteraction(Uri uri)
	{
		Log.d(TAG, "onFragmentInteraction");
	}


	//////////////////////////////
	// END GAME SEQUENCE
	/**
	 * Handle the end of the game, as triggered by the Game Timer.	//TODO - set a state in the Model so that play cannot continue on the client
	 */
	private void handleGameOver()
	{
		Log.d(TAG, "handleGameOver");

		launchGameOverActivity();
	}

	/**
	 * Launch the Game Over Activity, where the user can choose between a rematch or choosing a new opponent.
	 */
	private void launchGameOverActivity() {
		Log.d(TAG, "launchGameOverActivity");

		Intent intent = new Intent(BoardActivity.this, GameOverActivity.class);
		startActivity(intent);
	}


	////////////////////////////////
	//

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


	@Override
	public void handleIncomingObject(Object obj)
	{
		Log.d(TAG, "handleIncomingObject: " + obj);

		if(obj instanceof SimpleMessage)
		{
			Log.d(TAG, "handleIncomingObject: SimpleMessage: " + ((SimpleMessage)obj).getMsg());
		}
		else if(obj instanceof GameMoveResponse)
		{
			handleGameMoveResponse((GameMoveResponse) obj);
		}
		else
		{
			Log.d(TAG, "handleIncomingObject: object ignored.");
		}
	}

	private void handleGameMoveResponse(GameMoveResponse response)
	{
		Log.d(TAG, "handleGameMoveResponse");

		// if the response was for an accepted move request, add the score to the player's score
		if(response.getRequestAccepted() && response.getGameMoveValid() && response.getPointsAwarded() > 0)
		{
			String wordSubmitted 	= response.getWordSubmitted();
			int movePointsAwarded 	= response.getPointsAwarded();
			int newTotalScore 		= response.getNewScore();
			showAcceptedPlayerWordToast(wordSubmitted, movePointsAwarded);
			Model.setScore(newTotalScore);
			updateScoreDisplay();
			Log.d(TAG, "handleGameMoveResponse: move accepted: " + wordSubmitted + ", " + movePointsAwarded + " points. New score: " + Model.getScore());
		}
		else
		{
			Log.w(TAG, "handleGameMoveResponse: WARNING: matching request was not accepted, or submitted move was not accepted by server, or move was worth no points. Ignoring.");
		}
	}

	private void showAcceptedPlayerWordToast(String wordSubmitted, int pointsAwarded)
	{
		Log.d(TAG, "showAcceptedPlayerWordToast: " + wordSubmitted + ", " + pointsAwarded);

		final long TOAST_DURATION_MS	= 500;
		final int TOAST_OFFSET_X		= 0;
		final int TOAST_OFFSET_Y 		= 65;
		final int TOAST_BG_COLOR		= getResources().getColor(R.color.wordwolf_toast_color_light_green);

		final Toast playerWordToast = Toast.makeText(BoardActivity.this, wordSubmitted + ": " + pointsAwarded, Toast.LENGTH_SHORT);
		playerWordToast.setGravity(Gravity.TOP | Gravity.CENTER, TOAST_OFFSET_X, TOAST_OFFSET_Y);

		View toastView = playerWordToast.getView();
		toastView.setBackgroundColor(TOAST_BG_COLOR);

		playerWordToast.show();

		Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				playerWordToast.cancel();
			}
		}, TOAST_DURATION_MS);
	}

	/**
	 * IExtendedAsyncTask overrides
	 * TODO = still called?
	 */
	@Override
	public void onTaskCompleted()
	{
		Log.d(TAG, "onTaskCompleted - NO BEHAVIOR");
	}

}

