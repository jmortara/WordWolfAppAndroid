package com.mortaramultimedia.wordwolfappandroid.fragments;

/**
 * Created by Jason Mortara on 11/1/14.
 */

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;

import com.mortaramultimedia.wordwolf.shared.messages.GameBoard;
import com.mortaramultimedia.wordwolf.shared.messages.TileData;

import com.mortaramultimedia.wordwolfappandroid.game.GameManager;
import com.mortaramultimedia.wordwolfappandroid.activities.BoardActivity;
import com.mortaramultimedia.wordwolfappandroid.data.Model;

/**
 * A simple {@link android.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BoardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class BoardFragment extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	public static final String TAG = "BoardFragment";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private GameBoard gameBoardData;
	private GridLayout gl;
	private ArrayList<ImageButton> letterButtons;
	private ArrayList<ArrayList> buttonViews2d;
	private int item;
	private int rows;
	private int cols;
	private BoardActivity boardActivity;

	private OnFragmentInteractionListener mListener;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment BoardFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static BoardFragment newInstance(String param1, String param2)
	{
		BoardFragment fragment = new BoardFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public BoardFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}

		createReferences();
		setupBoardLayout();
	}

	private void createReferences()
	{
		Log.d(TAG, "createReferences");

		this.gameBoardData = Model.getGameBoard();
		this.rows = gameBoardData.getRows();
		this.cols = gameBoardData.getRows();
	}

	private void setupBoardLayout()
	{
		Log.d(TAG, "setupBoardLayout");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreateView");

		//TODO - refactor this method for clarity

		boardActivity = (BoardActivity) getActivity();

//		createLettersArray();
//		createBoardData();
		printBoardData();
		GameManager.init();

		// DisplayMetrics method of getting nec width and height needed to size letterButtons
		DisplayMetrics displayMetrics = new DisplayMetrics();
		boardActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int dmWidth  = displayMetrics.widthPixels;
		int dmHeight = displayMetrics.heightPixels;
		Log.d(TAG, "onCreateView: dmWidth:  " + dmWidth);
		Log.d(TAG, "onCreateView: dmHeight: " + dmHeight);


		gl = new GridLayout( getActivity() );
		gl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		gl.setColumnCount(this.cols);
		gl.setRowCount(this.rows);
		gl.setBackgroundColor(0xDEE1F7);



		ImageButton newButton;
		letterButtons = new ArrayList<ImageButton>();	//TODO - remove if unused
		ArrayList<ImageButton> buttonCol;
		Character letter = 'y';
		LinearLayout rowLayout;
		String debugStr = "";

        int viewWidth    = gl.getWidth();
        int viewHeight   = gl.getHeight();
		int buttonWidth  = dmWidth  / ( this.cols + 1 );
		int buttonHeight = buttonWidth;		//dmHeight / ( this.rows + 1 );

        Log.d(TAG, "onCreateView: view width: " + viewWidth);
        Log.d(TAG, "onCreateView: view viewHeight: " + viewHeight);
        Log.d(TAG, "onCreateView: view buttonWidth: " + buttonWidth);
        Log.d(TAG, "onCreateView: view buttonHeight: " + buttonHeight);

		buttonViews2d = new ArrayList<ArrayList>();

		for(int row=0; row<this.rows; row++)
		{
			buttonCol = new ArrayList<ImageButton>();

			//rowLayout = new LinearLayout( getActivity() );
			for (int col=0; col<this.cols; col++)
			{
				letter = Model.getGameBoard().getLetterAtPos(row, col); // get from data

				// optionally add some debug info onscreen
				if ( Model.DEV_DEBUG_MODE )
				{
					debugStr = "\n" + " c" + col + " r" + row;
				}

				String idStr = "@drawable/letter_button_" + letter.toString().toLowerCase();		// e.g. "letter_button_a"
				//Log.d(TAG, "onCreateView: idStr: " + idStr);
				int drawableID = getResources().getIdentifier(idStr, "drawable", getActivity().getPackageName());
				//Log.d(TAG, "onCreateView: drawableID: " + drawableID);
				Drawable buttonDrawable = getResources().getDrawable(drawableID);
				//Log.d(TAG, "onCreateView: buttonDrawable: " + buttonDrawable);
				newButton = new ImageButton(this.getActivity().getBaseContext());
				//Log.d(TAG, "onCreateView: newButton: " + newButton);
				newButton.setImageDrawable(buttonDrawable);

				TileData tagObj = new TileData( row, col, letter, false );
				Log.d(TAG, "onCreateView: tagObj: " + tagObj);

				newButton.setTag(tagObj);
				newButton.setLayoutParams(new LinearLayout.LayoutParams(buttonWidth, buttonHeight));
				//newButton.setText(new String(c.toString() + debugStr));		//TODO: add debug text field on top of image?
				//newButton.setTextSize(buttonWidth / 4);
				newButton.setPadding(1, 1, 1, 1);
				newButton.setOnClickListener(onLetterButtonClick);
				letterButtons.add(newButton);
				buttonCol.add(newButton);
				gl.addView( newButton );
			}
			//gl.addView( rowLayout );
			buttonViews2d.add(buttonCol);
		}

		Log.d(TAG, "TEST BUTTON IS AT :::::::::::::::::: " + getButtonViewAtPosition(1, 3).getTag());	//TODO - remove

		return gl;
		//setContentView(gl);

		// Inflate the layout for this fragment
		// return inflater.inflate(R.layout.fragment_board, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		Log.d(TAG, "onViewCreated");
		resetAllTileViews();
	}

	//////////////////////////////////////////
	// BOARD VIEW AND LETTER BUTTON VIEWS

	public ImageButton getButtonViewAtPosition(int row, int col)
	{
		return (ImageButton) buttonViews2d.get(row).get(col);
	}

	/*private void createBoardData()
	{
		Character c = 'Z';

		for(int row=0; row<this.rows; row++)
		{
			for (int col = 0; col < this.cols; col++)
			{
				c = getRandomLetter();
				Log.d(TAG, "createBoardData: col " + col + " row " + row + ": " + c);
				Model.boardData[col][row] = c;
			}
		}
	}*/

	/**
	 * DEPRECATED VERSION
	 */
/*	private void createBoardData()
	{
		Character c = 'z';
		Model.boardData = new Character[ Model.cols ][ Model.rows ];

		for(int row=0; row<Model.rows; row++)
		{
			for (int col = 0; col < Model.cols; col++)
			{
				c = getRandomLetter();
				Log.d(TAG, "createBoardData: col " + col + " row " + row + ": " + c);
				Model.boardData[col][row] = c;
			}
		}
	}*/

	private void printBoardData()
	{
		Character c = 'w';

		for (int row=0; row<this.rows; row++)
		{
			for (int col = 0; col < this.cols; col++)
			{
				c =  Model.getGameBoard().getLetterAtPos(row, col);
				Log.d(TAG, "printBoardData: col " + col + " row " + row + ": " + c);
			}
		}

		String rowStr = "";
		for (int row=0; row<this.rows; row++)
		{
			rowStr = "";
			for (int col = 0; col < this.cols; col++)
			{
				c =  Model.getGameBoard().getLetterAtPos(row, col);
				rowStr += " ";
				rowStr += c;
			}
			Log.d(TAG, "printBoardData:" + rowStr);
		}
	}

	/*
	DEPRECATED - SINGLE PLAYER
	Convert the list of available letters into an array for picking from
	 */
	/*private void createLettersArray()
	{
		Log.d(TAG, "createLettersArray");

		Character c;
		Model.letterSetArray = new ArrayList<Character>();
		for ( int i=0; i<Model.letterSet.length(); i++ )
		{
			c =  Model.letterSet.charAt( i );
			Model.letterSetArray.add(c);
		}
		Log.d( TAG, "createLettersArray: Model.letterSetArray: " + Model.letterSetArray );
	}*/

	/**
	 * DEPRECATED - SINGLE PLAYER
	 * @return
	 */
	/*private Character getRandomLetter()
	{
		int rand = -1;
		Character randLetter = new Character('x');
		int numLetters = Model.letterSetArray.size();

		if ( numLetters > 0 )
		{
			rand = (int) Math.floor(Math.random() * numLetters);
			randLetter = new Character( Model.letterSetArray.get( rand ) );
		}
		Log.d( TAG, "getRandomLetter: " + randLetter );

		return randLetter;
	}*/

	View.OnClickListener onLetterButtonClick = new View.OnClickListener()
	{
		Character c = 'v';
		//Log.d( TAG, "onLetterButtonClick: " + b.getText() );
		public void onClick(View v)
		{
			TileData td = (TileData) v.getTag();
//			c = Model.boardData[ p.col ][ p.row ];
			c = Model.getGameBoard().getLetterAtPos(td.getRow(), td.getCol());
			Log.d(TAG, "onLetterButtonClick: " + td.toString() + ": " + c);

			ImageButton b = (ImageButton) v;
			updateLetterButtonColor(b, td, "selected");

			GameManager.processTileSelection(td);
			updateActivity();
		}
	};

	/**
	 * Highlighting of letters as word is constructed (if it's the first letter, or the next valid letter)
	 * @param button
	 * @param td
	 * @param type ("selected", "unselected")
	 */
	private void updateLetterButtonColor(ImageButton button, TileData td, String type)
	{
		Log.d( TAG, "updateLetterButtonColor: " + type);

		if(Model.getSelectedTiles().size() == 0 || (Model.getSelectedTiles().size() > 0 && GameManager.isValidTileSelection(td)))
		{
			Log.d( TAG, "VALID TILE SELECTION" );

			String idStr = null;
			int drawableID = -1;
			Drawable buttonDrawable = null;

			if(type.equals("selected"))
			{
				idStr = "@drawable/letter_button_selected_" + ((Character) td.getLetter()).toString().toLowerCase();		// e.g. "letter_button_selected_a"
//				button.setPressed(false);
//				button.setSelected(true);
			}
			else if (type.equals("unselected"))
			{
				idStr = "@drawable/letter_button_up_" + ((Character) td.getLetter()).toString().toLowerCase();				// e.g. "letter_button_up_a"
			}
			drawableID = getResources().getIdentifier(idStr, "drawable", getActivity().getPackageName());
			buttonDrawable = getResources().getDrawable(drawableID);
//			button.setImageDrawable(buttonDrawable);
			button.setBackground(buttonDrawable);

			/*
			// set state via color, rather than image
			Log.d( TAG, "VALID TILE SELECTION" );
			final int BG_COLOR_LIGHT = getResources().getColor(R.color.button_material_light);
			final int BG_COLOR_DARK  = getResources().getColor(R.color.button_material_dark);
			int bgColor = BG_COLOR_LIGHT;
			if(type.equals("selected"))
			{
				bgColor = BG_COLOR_DARK;
			}
			else if (type.equals("unselected"))
			{
				bgColor = BG_COLOR_LIGHT;
			}
			button.setBackgroundColor(bgColor);
			*/
		}
	}

	/**
	 * Should be called only after all the TileDatas are reset in the Model.gameBoard
	 */
	public void resetAllTileViews()
	{
		Log.d( TAG, "resetAllTileViews" );

		TileData td;
		ImageButton button;
		for (int row=0; row<this.rows; row++)
		{
			for (int col = 0; col < this.cols; col++)
			{
				td = Model.getGameBoard().getTileDataAtPos(row, col);
				button = getButtonViewAtPosition(row, col);
				updateLetterButtonColor(button, td, "unselected");
			}
		}


		/*for(int row=0; row < buttonViews2d.size(); row++)
		{
			List<Button> colList = (List<Button>) buttonViews2d.get(row);
			for(int col=0; col<colList.size(); col++)
			{
				Button button = (Button) colList.get(col);
				if(button != null)
				{
					updateLetterButtonColor(button, null, "selected");
				}
			}
		}*/
	}

	private void updateActivity()
	{
		Log.d( TAG, "updateActivity" );

		if ( boardActivity != null )
		{
			boardActivity.updateWordDisplay();
		}
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	public void handleBoardFragmentButtonClick(View view)
	{
		Log.d(TAG, "handleBoardFragmentButtonClick");

	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}

}