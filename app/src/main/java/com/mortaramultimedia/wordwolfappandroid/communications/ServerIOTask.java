package com.mortaramultimedia.wordwolfappandroid.communications;

import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;

import com.mortaramultimedia.wordwolf.shared.constants.*;
import com.mortaramultimedia.wordwolf.shared.messages.*;
import com.mortaramultimedia.wordwolfappandroid.data.Model;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


/**
 * ServerIOTask - AsyncTask which handles server connections and Input/Output of message objects.
 */
public class ServerIOTask extends AsyncTask<Void, Integer, Integer>
{
	public static final String TAG = "ServerIOTask";

//	private Activity referenceActivity;		//TODO - remove? this stored Activity reference is handled by Comm now.
	private Socket s;
	private ObjectOutputStream s_objOut;
	private ObjectInputStream s_objIn;

	// constructor
	public ServerIOTask(/*Activity referenceActivity*/)
	{
		Log.d(TAG, "ServerIOTask constructor");
//		this.referenceActivity = referenceActivity;
	}

	/**
	 * Register an Activity to receive updates from Comm, for example to be notified of incoming objects from the server.
	 */
	/*public void registerCurrentActivity(Activity activity)
	{
		Log.d(TAG, "registerCurrentActivity: " + activity.getLocalClassName());
		referenceActivity = activity;
	}*/


	@Override
	protected Integer doInBackground(Void... unused)
	{
		// need to force wait for debugger to breakpoint in this thread
		if (Debug.isDebuggerConnected())
		{
			Debug.waitForDebugger();
		}

		String host = Model.getHostIP();
		s = new Socket();

		try
		{
			Log.d(TAG, "Attempting to connect to " + host + " " + Model.PORT);
			try
			{
				Log.d(TAG, "Socket timeout is:  " + s.getSoTimeout());
				s.setKeepAlive(true);
				s.connect(new InetSocketAddress(host, Model.PORT));
			}
			//Host not found
			catch (UnknownHostException e)
			{
				System.err.println("Don't know about host : " + host);
//					System.exit(1);	// exit app
				try
				{
					s_objOut.close();
				}
				catch (RuntimeException e2)
				{
					System.err.println("Object output stream may be null or unavailable to close.");
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}

				try
				{
					s_objIn.close();
				}
				catch (RuntimeException e3)
				{
					System.err.println("Object input stream may be null or unavailable to close.");
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}

				try
				{
					s.close();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
			catch(ConnectException e)
			{
				Log.e(TAG, "ConnectException: " + e.getMessage());
				if(Model.DEV_DEBUG_USE_LOCAL_IP)
				{
					Log.e(TAG, "Can't connect to IP. Check that actual IP is same as one defined in Model. Model.DEV_DEBUG_LOCAL_IP_ADDR is " + Model.DEV_DEBUG_LOCAL_IP_ADDR);
				}
			}
			catch (SecurityException e)
			{
				Log.d(TAG, "SecurityException: " + e.getMessage());
				e.printStackTrace();
			}
			catch (IOException e)
			{
				Log.d(TAG, "IOException: " + e.getMessage());
				e.printStackTrace();
			}

			// update Model with connection status
			Model.setConnected(s.isConnected());

			if (s.isConnected())
			{
				Log.d(TAG, "Connected to wwss: " + Model.getConnected());

				// create writer for socket
				try
				{
					if (s_objOut == null)
					{
						s_objOut = new ObjectOutputStream(s.getOutputStream());
						Comm.setOut(s_objOut);
						Log.d(TAG, "Created ObjectOutputStream.");
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

				//Send initial message to server
				SimpleMessage msgObj = new SimpleMessage(Constants.HELLO_SERVER, true);
				sendOutgoingObject(msgObj);

				// Obj reader for socket
				try
				{
					if (s_objIn == null)
					{
						s_objIn = new ObjectInputStream(s.getInputStream());
						Comm.setIn(s_objIn);   // create reference for other classes to use
						Log.d(TAG, "Created ObjectInputStream.");
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

				// Get obj response from server
				Object responseObj;
				if (s_objIn != null)
				{
					try
					{
						while ((responseObj = s_objIn.readObject()) != null)
						{
							Log.d(TAG, "RECEIVED Server response obj: " + responseObj);
							//Model.setIncomingMessageObj(responseObj);
							handleIncomingObject(responseObj);
						}
					}
					catch (SocketException e3)
					{
						Log.e(TAG, "SocketException. Killing connections.");
						Comm.kill();
					}
					catch (SocketTimeoutException e2)
					{
						Log.e(TAG, "SocketTimeoutException. Killing connections.");
						Comm.kill();
					}
					catch(EOFException e)		// could be caused by server going down
					{
						Log.e(TAG, "EOFException. Killing connections.");
						Comm.kill();
					}
					catch (IOException | ClassNotFoundException e1)
					{
						e1.printStackTrace();
					}
				}

			}
			else
			{
				Log.d(TAG, "Not connected.");
			}

		}
		catch (Error e)
		{
			e.printStackTrace();
		}


		// other stuff? close?
		Log.d(TAG, "Continuing... attempting to close I/O streams...");
		if (s_objOut != null)
		{
			try
			{
				s_objOut.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		if (s_objIn != null)
		{
			try
			{
				s_objIn.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			s.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return 1;
	}

	/**
	 * Handle all incoming object types.
	 *
	 * @param obj
	 */
	private void handleIncomingObject(Object obj)
	{
		Log.d(TAG, "handleIncomingObject: " + obj);

		/**
		 * If receiving a SimpleMessage, log the message. If echo was requested, send it back to the client as well.
		 */
		if (obj instanceof SimpleMessage)
		{
			handleSimpleMessage(((SimpleMessage) obj));
		}
		/**
		 * If receiving a ConnectToDatabaseResponse, if it is a successful one, that means we can then attempt a login or create account.
		 */
		else if (obj instanceof ConnectToDatabaseResponse)
		{
			handleConnectToDatabaseResponse(((ConnectToDatabaseResponse) obj));
		}
		/**
		 * If receiving a LoginResponse...
		 */
		else if (obj instanceof LoginResponse)
		{
			handleLoginResponse(((LoginResponse) obj));
		}
		/**
		 * If receiving a GetPlayerListResponse, its player list will be used by ChooseOpponentActivity
		 */
		else if (obj instanceof GetPlayerListResponse)
		{
			handleGetPlayerListResponse(((GetPlayerListResponse) obj));
		}
		/**
		 * If receiving a SelectOpponentRequest, which is a request from another player to become opponents, show a dialog asking to Accept or Decline the invitation.
		 */
		else if (obj instanceof SelectOpponentRequest)
		{
			handleRequestToBecomeOpponent(((SelectOpponentRequest) obj));
		}
		/**
		 * If receiving a SelectOpponentResponse, which is a response to a request to become this player's opponent. Will contain accepted/declined param.
		 */
		else if (obj instanceof SelectOpponentResponse)
		{
			handleSelectOpponentResponse(((SelectOpponentResponse) obj));
		}
		/**
		 * If receiving an OpponentBoundMessage, which is a message to this client from this player's opponent.
		 */
		else if (obj instanceof OpponentBoundMessage)
		{
			handleMessageFromOpponent(((OpponentBoundMessage) obj));
		}
		/**
		 * If receiving a CreateNewAccountResponse...
		 */
		else if(obj instanceof CreateNewAccountResponse)
		{
			handleCreateNewAccountResponse(((CreateNewAccountResponse) obj));
		}
		/**
		 * If receiving a CreateGameResponse, create a GameBoard and distribute it to matched players.
		 */
		else if (obj instanceof CreateGameResponse)
		{
			handleCreateGameResponse(((CreateGameResponse) obj));
		}
		/**
		 * If receiving a GameMoveResponse, if the response is invalid, ignore it. If valid, add the score it contains.
		 */
		else if (obj instanceof GameMoveResponse)
		{
			handleGameMoveResponse(((GameMoveResponse) obj));
		}
		/**
		 * If receiving a EndGameResponse, if the response is invalid, ignore it. If valid, add the score it contains.
		 */
		else if (obj instanceof EndGameResponse)
		{
			handleEndGameResponse(((EndGameResponse) obj));
		}
		/**
		 * If receiving a PostEndGameActionResponse, which might for example contain a rematch request or decline
		 */
		else if (obj instanceof PostEndGameActionResponse)
		{
			handlePostEndGameActionResponse(((PostEndGameActionResponse) obj));
		}
		else
		{
			Log.w(TAG, "handleIncomingObject: WARNING: unhandled object type! " + obj);
		}

	}

	public void sendOutgoingObject(Object obj)
	{
		Log.d(TAG, "sendOutgoingObject: " + obj);

		if (/*serverTask == null ||*/ !Model.getConnected())
		{
			Log.d(TAG, "sendOutgoingObject: WARNING: not connected. Ignoring.");
			return;
		}

		// send it to the server
		if (s.isConnected() && obj != null)
		{
			if (s_objOut != null)
			{
				try
				{
					s_objOut.writeObject(obj);
					s_objOut.flush();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private void handleSimpleMessage(SimpleMessage msgObj)
	{
		Log.d(TAG, "handleSimpleMessage: " + msgObj.getMsg());
		publishObject(msgObj);
	}

	private void handleConnectToDatabaseResponse(ConnectToDatabaseResponse response)
	{
		Log.d(TAG, "handleConnectToDatabaseResponse: " + response);
		Model.setConnectedToDatabase(response.getSuccess());
		publishObject(response);
	}

	private void handleCreateNewAccountResponse(CreateNewAccountResponse response)
	{
		Log.d(TAG, "handleCreateNewAccountResponse: " + response);
		publishObject(response);
	}

	private void handleLoginResponse(LoginResponse response)
	{
		Log.d(TAG, "handleLoginResponse: " + response);
		publishObject(response);
	}

	private void handleGetPlayerListResponse(GetPlayerListResponse response)
	{
		Log.d(TAG, "handleGetPlayerListResponse: " + response);
		publishObject(response);
		Log.d(TAG, "handleGetPlayerListResponse: player list: " + response.getPlayersCopy());
	}

	private void handleRequestToBecomeOpponent(SelectOpponentRequest request)
	{
		Log.d(TAG, "handleRequestToBecomeOpponent: " + request);
		publishObject(request);
		Log.d(TAG, "handleRequestToBecomeOpponent: YOU HAVE BEEN OFFERED TO BECOME AN OPPONENT OF: " + request.getSourceUsername());

		// store the request in the Model until it is accepted or rejected by the user
		Model.setSelectOpponentRequest(request);
	}

	private void handleSelectOpponentResponse(SelectOpponentResponse response)
	{
		Log.d(TAG, "handleSelectOpponentResponse: " + response);
		publishObject(response);
		if (response.getRequestAccepted())
		{
			Log.d(TAG, "handleSelectOpponentResponse: REQUEST ACCEPTED! from: " + response.getSourceUsername());
			Model.setOpponentUsername(response.getSourceUsername());
		}
		else
		{
			Log.d(TAG, "handleSelectOpponentResponse: REQUEST REJECTED! from: " + response.getSourceUsername());
		}
	}

	private void handleMessageFromOpponent(OpponentBoundMessage msgObj)
	{
		Log.d(TAG, "handleMessageFromOpponent: " + msgObj);
		publishObject(msgObj);
	}

	private void handleCreateGameResponse(CreateGameResponse response)
	{
		Log.d(TAG, "handleCreateGameResponse: " + response);
		GameBoard gameBoard = response.getGameBoard();
		publishObject(response);	// publishing the gameBoard for the debug activity is interfering with the normal object forwarding flow
		logGameBoard(gameBoard);
		Model.setGameBoard(gameBoard);
		Model.setGameDurationMS(response.getGameDurationMS());
		Log.d(TAG, "handleCreateGameResponse: game duration is (ms): " + Model.getGameDurationMS());
	}

	private void handleGameMoveResponse(GameMoveResponse response)
	{
		Log.d(TAG, "handleGameMoveResponse: " + response);

		// if the response was for an accepted move request, add the score to the player's score
		/*if (response.getRequestAccepted())
		{
			int movePointsAwarded = response.getPointsAwarded();
			int newTotalScore = response.getNewScore();
			Model.setScore(newTotalScore);
			Log.d(TAG, "handleGameMoveResponse: move accepted for " + movePointsAwarded + " points. New score: " + Model.getScore());
		}
		else
		{
			Log.d(TAG, "handleGameMoveResponse: WARNING: submitted move was not accepted by server.");
		}*/
		publishObject(response);   // this should also update the UI with any score updates
	}

	private void handleEndGameResponse(EndGameResponse response)
	{
		Log.d(TAG, "handleEndGameResponse: " + response);
		publishObject(response);

		//TODO: note that one EndGameRequest send to the server results in one EndGameResponse going to each matched player, so each will receive 2 EndGames.
		Log.d(TAG, "handleEndGameResponse: *****GAME OVER!***** final score according to server: " + response.getFinalScoreFromServer());
		Model.setScore(response.getFinalScoreFromServer());
	}

	private void handlePostEndGameActionResponse(PostEndGameActionResponse response)
	{
		Log.d(TAG, "handlePostEndGameActionResponse: " + response);
		publishObject(response);
	}

	private void logGameBoard(GameBoard gameBoard)
	{
		Log.d(TAG, "logGameBoard: ");
		gameBoard.printBoardData();
	}

	/**
	 * Store the most recent incoming message Object in the Model,
	 * and use the AsyncTasks's onProgressUpdate to forward any data to Comm, which in turn
	 * should forward it to the relevant or current Activity, for example so it can update its UI.
	 * @param obj
	 */
	private void publishObject(Object obj)
	{
		Log.d(TAG, "publishObject: " + obj);
		if (obj != null)
		{
			Model.setIncomingObj(obj);
		}
		publishProgress(1);

		/*if(referenceActivity != null)
		{

		}*/

	}


	@Override
	protected void onProgressUpdate(Integer... progress)
	{
		Log.d(TAG, "onProgressUpdate");
		Comm.handleProgressUpdate(Model.getIncomingObj());
	}

	@Override
	protected void onPostExecute(Integer result)
	{
		String str = "onPostExecute: " + result;
		Log.d(TAG, str);
	}

	public void kill()
	{
		Log.d(TAG, "kill **********************");

		try
		{
			s_objIn.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			s_objOut.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			s.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

}