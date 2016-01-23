package com.mortaramultimedia.wordwolfappandroid;

/**
 * Created by Jason Mortara on 11/1/14.
 */

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;

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

	GridLayout gl;
	ArrayList<Button> buttons;
	int item;
	BoardActivity boardActivity;

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
	public static BoardFragment newInstance(String param1, String param2) {
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

		setupBoardLayout();
	}

	private void setupBoardLayout()
	{
		Log.d(TAG, "setupBoardLayout");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreateView");

		boardActivity = (BoardActivity) getActivity();

		createLettersArray();
		createBoardData();
		printBoardData();
		GameManager.init();

		gl = new GridLayout( getActivity() );
		gl.setLayoutParams( new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT) );
		gl.setColumnCount( Model.cols );
		gl.setRowCount( Model.rows );
		gl.setBackgroundColor(0xDEE1F7);



		Button newButton;
		buttons = new ArrayList<Button>();
		Character c = 'y';
		LinearLayout rowLayout;
		String debugStr = "";

//        int viewWidth    = gl.getWidth();
//        int viewHeight   = gl.getHeight();
		int buttonWidth  = 260 / ( Model.cols + 1 );
		int buttonHeight = 260 / ( Model.rows + 1 );

//        Log.d(TAG, "onCreateView: view width: " + viewWidth);
//        Log.d(TAG, "onCreateView: view viewHeight: " + viewHeight);
//        Log.d(TAG, "onCreateView: view buttonWidth: " + buttonWidth);
//        Log.d(TAG, "onCreateView: view buttonHeight: " + buttonHeight);

		for(int row=0; row<Model.rows; row++)
		{
			//rowLayout = new LinearLayout( getActivity() );
			for (int col=0; col<Model.cols; col++)
			{
				c = Model.boardData[ col ][ row ];  // get from data

				if ( Model.DEBUG_MODE )
				{
					debugStr = "\n" + " c" + col + " r" + row;
				}

				newButton = new Button( getActivity() );
				PositionObj tagObj = new PositionObj( col, row );

				newButton.setTag( tagObj );
				newButton.setLayoutParams( new LinearLayout.LayoutParams( buttonWidth, buttonHeight ) );
				newButton.setText(new String(c.toString() + debugStr));
				newButton.setTextSize(10);
				newButton.setPadding(1, 1, 1, 1);
				newButton.setOnClickListener( onLetterButtonClick );
				buttons.add( newButton );
				gl.addView( newButton );
			}
			//gl.addView( rowLayout );
		}

		return gl;
		//setContentView(gl);

		// Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_board, container, false);

	}

	private void createBoardData()
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
	}

	private void printBoardData()
	{
		Character c = 'w';

		for (int row=0; row<Model.rows; row++)
		{
			for (int col = 0; col < Model.cols; col++)
			{
				c =  Model.boardData[col][row];
				Log.d(TAG, "printBoardData: col " + col + " row " + row + ": " + c);
			}
		}

		String rowStr = "";
		for (int row=0; row<Model.rows; row++)
		{
			rowStr = "";
			for (int col = 0; col < Model.cols; col++)
			{
				c =  Model.boardData[col][row];
				rowStr += " ";
				rowStr += c;
			}
			Log.d(TAG, "printBoardData:" + rowStr);
		}
	}

	/*
	Convert the list of available letters into an array for picking from
	 */
	private void createLettersArray()
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
	}

	private Character getRandomLetter()
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
	}

	View.OnClickListener onLetterButtonClick = new View.OnClickListener()
	{
		Character c = 'v';
		//Log.d( TAG, "onLetterButtonClick: " + b.getText() );
		public void onClick(View v)
		{
			PositionObj p = (PositionObj) v.getTag();
			c = Model.boardData[ p.col ][ p.row ];
			Log.d( TAG, "onLetterButtonClick: " +  p.toString() + ": " + c);
			GameManager.processMove( p );
			updateActivity();
		}
	};

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