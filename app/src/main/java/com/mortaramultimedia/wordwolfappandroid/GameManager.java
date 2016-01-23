package com.mortaramultimedia.wordwolfappandroid;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;

public class GameManager
{
	private static String TAG = "GameManager";
	private static Activity boardActivity;

	public static Character getLetterAtPosition( PositionObj p )
	{
		Character c = Model.boardData[ p.col ][ p.row ];
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
		Model.moves = new ArrayList<PositionObj>();
	}

	public static void processMove( PositionObj p )
	{
		Character c = getLetterAtPosition( p );
		Log.d( TAG, "processMove: " +  p.toString() + ": " + c);

		// first move
		if ( Model.moves.size() == 0 )
		{
			// Model.moves = new ArrayList<PositionObj>();
			Model.moves.add( p );
		}
		else
		{
			if ( isValidMove( p ) )
			{
				handleValidMove( p );
			}
			else
			{
				handleInvalidMove();
			}
		}

		printMoves();
	}

	private static void handleValidMove( PositionObj p )
	{
		Log.d(TAG, "handleValidMove: move is ok");
		Model.moves.add(p);
	}

	private static void handleInvalidMove()
	{
		Log.d(TAG, "handleInvalidMove: MOVE INVALID");
	}

	private static Boolean isValidMove( PositionObj p )
	{
		PositionObj lastMove = Model.moves.get( Model.moves.size() - 1 );
		Boolean sameCol = ( p.col == lastMove.col );
		Boolean sameRow = ( p.row == lastMove.row );
		Boolean adjCol  = ( Math.abs( p.col - lastMove.col ) == 1 );
		Boolean adjRow  = ( Math.abs( p.row - lastMove.row ) == 1 );

		Log.d( TAG, "isValidMove: sameCol: " + sameCol );
		Log.d( TAG, "isValidMove: sameRow: " + sameRow );
		Log.d( TAG, "isValidMove: adjCol:  " + adjCol );
		Log.d( TAG, "isValidMove: adjRow:  " + adjRow );


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
			Log.d( TAG, "isValidMove: UNHANDLED CASE" );
			return false;
		}
		//TODO:insert case for using a button already selected
		//return true;
	}

	public static String getWordSoFar()
	{
		String str = "";
		Character c = 'g';
		if ( Model.moves != null && Model.moves.size() > 0 )
		{
			for (PositionObj move : Model.moves)
			{
				c = getLetterAtPosition(move);
				str += c;
			}
		}
		return str;
	}

	private static void printMoves()
	{
		Log.d( TAG, "printMoves: the word so far: " + getWordSoFar() );
	}

	public static void printFoundWords()
	{
		Log.d( TAG, "printFoundWords: foundWords: " + Model.foundWords.toString() );
	}

	public static boolean checkWordValidity()
	{
		Log.d( TAG, "checkWordValidity: checking " + getWordSoFar() );

		Boolean isValid = false;
		String submittedWord = getWordSoFar();

		//TODO: check dictionary
		if ( submittedWord.length() == 0 )
		{
			Log.w( TAG, "checkWordValidity: word string is empty" );
			isValid =  false;
		}
		else if( Model.globalDictionary == null )
		{
			Log.w( TAG, "checkWordValidity: global dictionary is null" );
			isValid =  false;
		}
		else if ( Model.globalDictionary.size() == 0 )
		{
			Log.w( TAG, "checkWordValidity: global dictionary is empty" );
			isValid =  false;
		}
		else if ( !Model.globalDictionary.containsValue( submittedWord ) )
		{
			Log.d( TAG, "checkWordValidity: word not found: " + submittedWord );
			isValid = false;
		}
		else if ( Model.globalDictionary.containsValue( submittedWord ) )
		{
			Log.d( TAG, "checkWordValidity: FOUND: " + submittedWord );
			isValid = true;
		}
		return isValid;
	}

}