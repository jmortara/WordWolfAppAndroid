package com.mortaramultimedia.wordwolfappandroid;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jason Mortara on 11/15/14.
 */
public class BoardActivity extends Activity implements BoardFragment.OnFragmentInteractionListener {

    public static final String TAG = "BoardActivity";
    TextView wordSoFarText;
    TextView scoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        init();
    }

    private void init()
    {
        Log.d(TAG, "init");

        initGameData();
        setupBoard();
    }

    private void initGameData()
    {
        Log.d(TAG, "initGameData");
        Model.foundWords = new ArrayList<String>();
    }


    private void setupBoard()
    {
        Log.d(TAG, "setupBoard");
        wordSoFarText = (TextView) findViewById(R.id.wordSoFarText);
        scoreText = (TextView) findViewById(R.id.scoreText);
    }

    public void handleBoardFragmentButtonClick(View view)
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
        scoreText.setText( getResources().getString(R.string.score) + " " + Model.foundWords.size() );
    }

    public void handleSubmitButtonClick(View view)
    {
        Log.d(TAG, "handleSubmitButtonClick");

        // submit the word for comparison with dictionary and increment score if nec
        Boolean wordIsValid = GameManager.checkWordValidity();
        if ( wordIsValid )
        {
            Model.foundWords.add( GameManager.getWordSoFar() );
            GameManager.printFoundWords();
        }

        // reset the word and the word display
        GameManager.startNewWord();
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

