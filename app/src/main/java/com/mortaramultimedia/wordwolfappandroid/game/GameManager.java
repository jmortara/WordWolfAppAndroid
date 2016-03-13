package com.mortaramultimedia.wordwolfappandroid.game;

import android.app.Activity;
import android.util.Log;

import com.mortaramultimedia.wordwolf.shared.messages.GameMove;
import com.mortaramultimedia.wordwolf.shared.messages.TileData;

import com.mortaramultimedia.wordwolfappandroid.data.Model;

import java.util.ArrayList;
import java.util.HashMap;


public class GameManager
{
	private static String TAG = "GameManager";
	private static Activity boardActivity;

	public static Character getLetterAtPosition( TileData td )
	{
		Character c = Model.getGameBoard().getLetterAtPos(td.getRow(), td.getCol());
		return c;
	}

	public static void init()
	{
		Log.d(TAG, "init");
		startNewWord();
	}

	public static void startNewWord()
	{
		Log.d( TAG, "startNewWord" );

		// reset all TileData to default state
		if(Model.getSelectedTiles() != null && Model.getSelectedTiles().size() > 0)
		{
			for(TileData td : Model.getSelectedTiles())
			{
				td.setSelected(false);
			}
		}

		// start a new list of TileDatas for a new word
		Model.setSelectedTiles(new ArrayList<TileData>());
	}

	public static void processTileSelection( TileData td )
	{
		Character c = getLetterAtPosition(td);
		Log.d( TAG, "processTileSelection: " +  td.toString() + ": " + c);

		// first selected tile
		if ( Model.getSelectedTiles().size() == 0 )
		{
			Model.getSelectedTiles().add(td);
		}
		else
		{
			if ( isValidTileSelection(td) )
			{
				handleValidTileSelection(td);
			}
			else
			{
				handleInvalidTileSelection();
			}
		}

		printMoves();
	}

	private static void handleValidTileSelection(TileData td)
	{
		Log.d(TAG, "handleValidTileSelection: move is ok");
		td.setSelected(true);
		Model.getSelectedTiles().add(td);
	}

	private static void handleInvalidTileSelection()
	{
		Log.d(TAG, "handleInvalidTileSelection: MOVE INVALID");
	}

	public static Boolean isValidTileSelection( TileData td )
	{
		TileData lastTileSelected = Model.getSelectedTiles().get(Model.getSelectedTiles().size() - 1);
		Boolean sameCol = ( td.getCol() == lastTileSelected.getCol() );
		Boolean sameRow = ( td.getRow() == lastTileSelected.getRow() );
		Boolean adjCol  = ( Math.abs( td.getCol() - lastTileSelected.getCol() ) == 1 );
		Boolean adjRow  = ( Math.abs( td.getRow() - lastTileSelected.getRow() ) == 1 );

		Log.d( TAG, "isValidTileSelection: sameCol: " + sameCol );
		Log.d( TAG, "isValidTileSelection: sameRow: " + sameRow );
		Log.d( TAG, "isValidTileSelection: adjCol:  " + adjCol );
		Log.d( TAG, "isValidTileSelection: adjRow:  " + adjRow );


		// if same move as last, it's invalid
		if ( sameCol && sameRow )
		{
			return false;
		}
		// if same row and adjacent col, or same col and and row, it's valid
		else if( ( sameRow && adjCol ) || ( sameCol && adjRow ) )
		{
			return true;
		}
		else
		{
			Log.d( TAG, "isValidTileSelection: UNHANDLED CASE" );
			return false;
		}
		//TODO:insert case for using a button already selected
		//return true;
	}

	public static String getWordSoFar()
	{
		String str = "";
		Character c = 'g';
		if ( Model.getSelectedTiles() != null && Model.getSelectedTiles().size() > 0 )
		{
			for (TileData td : Model.getSelectedTiles())
			{
				c = getLetterAtPosition(td);
				str += c;
			}
		}
		return str;
	}

	private static void printMoves()
	{
		Log.d( TAG, "printMoves: the word so far: " + getWordSoFar() );
	}

	public static void printValidWordsThisGame()
	{
		Log.d( TAG, "printValidWordsThisGame: foundWords: " + Model.getValidWordsThisGame().toString() );		//TODO: store found words locally? or on server? both?
	}

	public static boolean checkWordValidity()
	{
		Log.d( TAG, "checkWordValidity: checking " + getWordSoFar() );

		Boolean isValid = false;
		String submittedWord = getWordSoFar().toLowerCase();
		HashMap<String, String> dict = Model.getClientDictionary();

		if ( submittedWord.length() == 0 )
		{
			Log.w( TAG, "checkWordValidity: word string is empty" );
			isValid =  false;
		}
		else if( dict == null )
		{
			Log.w( TAG, "checkWordValidity: client dictionary is null" );
			isValid =  false;
		}
		else if ( dict.size() == 0 )
		{
			Log.w( TAG, "checkWordValidity: client dictionary is empty" );
			isValid =  false;
		}
		else if ( !dict.containsValue( submittedWord ) )
		{
			Log.d( TAG, "checkWordValidity: word not found in client dictionary: " + submittedWord );
			isValid = false;
		}
		else if ( dict.containsValue( submittedWord ) )
		{
			Log.d( TAG, "checkWordValidity: FOUND: " + submittedWord );
			isValid = true;
		}
		return isValid;
	}

	public static void resetScore()
	{
		Log.d( TAG, "resetScore");
		Model.setScore(0);
	}


}