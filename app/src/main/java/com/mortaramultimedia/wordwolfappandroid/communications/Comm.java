package com.mortaramultimedia.wordwolfappandroid.communications;

import android.util.Log;

import com.mortaramultimedia.wordwolf.shared.messages.ConnectToDatabaseRequest;
import com.mortaramultimedia.wordwolfappandroid.data.Model;
import com.mortaramultimedia.wordwolfappandroid.interfaces.IExtendedAsyncTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * Comm - Communications Singleton
 * Created by jason mortara on 1/30/2016.
 */
public class Comm
{
	private static final String TAG = "Comm";

	private static ObjectInputStream  inStream  = null;
	private static ObjectOutputStream outStream = null;

	private static Comm instance = new Comm();		// Singleton instance

	private static ServerIOTask serverIOTask = null;
	private static IExtendedAsyncTask currentActivity = null;


	public static Comm getInstance()
	{
		return instance;
	}

	private Comm()
	{
		Log.d(TAG, "Singleton constructor.");
	}

	/**
	 * Register an Activity to receive updates from Comm, for example to be notified of incoming objects from the server.
	 * @param activity
	 */
	public static void registerCurrentActivity(IExtendedAsyncTask activity)
	{
		Log.d(TAG, "registerCurrentActivity: " + activity.toString());
		currentActivity = activity;
	}

	/**
	 * Connect to the WordWolf Socket Server.
	 */
	public static void connectToServer()
	{
		Log.d(TAG, "connectToServer");

		if (Model.getConnected())
		{
			Log.d(TAG, "connectToServer: already connected!");
			return;
		}

		// Start network tasks separate from the main UI thread
		if (serverIOTask == null && !Model.getConnected())
		{
			serverIOTask = new ServerIOTask();
			serverIOTask.execute();
		}
		else
		{
			Log.d(TAG, "connectToServer: not connected and serverIOTask is null.");
		}
	}

	public static void connectToDB()
	{
		Log.d(TAG, "connectToDB");

		ConnectToDatabaseRequest request = new ConnectToDatabaseRequest();
		try
		{
			outStream.writeObject(request);
			outStream.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		/*databaseTask = new DatabaseAsyncTask(this, this);	//TODO: note, the second param can be a different Activity if that is more useful

		// workaround for issues with execute() not working properly on AsyncTasks
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			databaseTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else
		{
			databaseTask.execute();
		}*/
	}

	public static ObjectInputStream in()
	{
		return inStream;
	}

	public static void setIn(ObjectInputStream in)
	{
		if (inStream == null)
		{
			inStream = in;
		}
		else Log.w(TAG, "IGNORING REQUEST TO CREATE NEW GLOBAL ObjectInputStream FOR SERVER COMMUNICATIONS");
	}

	public static ObjectOutputStream out()
	{
		return outStream;
	}

	public static void setOut(ObjectOutputStream out)
	{
		if (outStream == null)
		{
			outStream = out;
		}
		else Log.w(TAG, "IGNORING REQUEST TO CREATE NEW GLOBAL ObjectOutputStream FOR SERVER COMMUNICATIONS");
	}

	/**
	 * Forward the incoming object from the server (which should be coming from serverIOTask) to the currently registered Activity.
	 * @param obj
	 */
	public static void handleIncomingObject(Object obj)
	{
		Log.d(TAG, "handleIncomingObject: " + obj);
		if(currentActivity != null)
		{
			currentActivity.handleIncomingObject(obj);
		}
	}

	public static void handleProgressUpdate(Object obj)
	{
		Log.d(TAG, "handleProgressUpdate: " + obj);
		if(currentActivity != null && obj != null)
		{
			Log.d(TAG, "handleProgressUpdate: forwarding object to currentActivity: " + currentActivity.toString());
			currentActivity.handleIncomingObject(obj);
		}
	}

	public static void sendObject(Object obj)
	{
		Log.d(TAG, "sendObject: " + obj);
		try
		{
			outStream.writeObject(obj);
			outStream.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
