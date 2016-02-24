package com.mortaramultimedia.wordwolfappandroid.database;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.mortaramultimedia.wordwolfappandroid.data.Model;
import com.mortaramultimedia.wordwolfappandroid.communications.Comm;
import com.mortaramultimedia.wordwolfappandroid.interfaces.IExtendedAsyncTask;
import com.mortaramultimedia.wordwolf.shared.messages.*;

import java.io.InputStream;
import java.util.Properties;



/**
 * DatabaseTask - AsyncTask which handles server connections and messaging
 * Note: This class is now only used by ServerActivity for debugging. The Connections Acvitiy uses a request.
 * Created by Jason Mortara on 1/24/2016
 */
public class DatabaseAsyncTask extends AsyncTask<Void, Integer, Integer>
{
	// statics
	private static final String TAG = "DatabaseTask";

	// privates
	private Activity activity = null;
	private IExtendedAsyncTask taskCompleteCallbackObj;


	// constructor
	public DatabaseAsyncTask(Activity activity, IExtendedAsyncTask caller)
	{
		Log.d(TAG, "DatabaseTask constructor, called from " + activity.getLocalClassName());

		this.activity = activity;
		this.taskCompleteCallbackObj = caller;
	}

	@Override
	protected void onPreExecute()
	{
		Log.d(TAG, "onPreExecute");
		// called on thread init
		readDatabaseProperties();
	}

	private void readDatabaseProperties()
	{
		Log.d(TAG, "readDatabaseProperties");
		try
		{
			Properties dbProps = new Properties();

			InputStream in = activity.getBaseContext().getAssets().open("database.properties");
			dbProps.load(in);

			// store in Model
			Model.setDatabaseProps(dbProps);

			// once props are loaded, test the db with the values defined therein
			Log.d(TAG, "readDatabaseProperties: Properties read.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected Integer doInBackground(Void... unused)
	{
		// need to force wait for debugger to breakpoint in this thread
		if(android.os.Debug.isDebuggerConnected())
		{
			android.os.Debug.waitForDebugger();
		}

		Log.d(TAG, "doInBackground: dbProps? " + Model.getDatabaseProps().toString());

		int testSucceeded = 0;	// unused now

		if (Model.getDatabaseProps() != null)
		{
			Log.d(TAG, "doInBackground: Attempting to test database");
			try
			{
				// first test the connection and run some queries
//				testSucceeded = MySQLAccessTester.test(Model.databaseProps);
				ConnectToDatabaseRequest connectToDatabaseRequest = new ConnectToDatabaseRequest();
				Comm.out().writeObject(connectToDatabaseRequest);
				Comm.out().flush();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				testSucceeded = 0;
			}
		}
		// store result of login attempt in Model
		/*if(testSucceeded == 0)
		{
			Model.dbTestOK = false;
		}
		else Model.dbTestOK = true;

		Log.d(TAG, "doInBackground: DB test passed? " + Model.dbTestOK);*/

		return testSucceeded;
	}


	@Override
	protected void onProgressUpdate(Integer... progress)
	{
		 Log.d(TAG, "onProgressUpdate: " + progress);
	}

	@Override
	protected void onPostExecute(Integer result)
	{
		String str = "onPostExecute: " + result;
		taskCompleteCallbackObj.onTaskCompleted();
		Log.d(TAG, str);
	}
} // end class DatabaseAsyncTask
