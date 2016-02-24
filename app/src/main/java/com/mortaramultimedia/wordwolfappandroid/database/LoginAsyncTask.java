package com.mortaramultimedia.wordwolfappandroid.database;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.mortaramultimedia.wordwolfappandroid.data.Model;
import com.mortaramultimedia.wordwolfappandroid.communications.Comm;
import com.mortaramultimedia.wordwolfappandroid.interfaces.IExtendedAsyncTask;
import com.mortaramultimedia.wordwolf.shared.messages.LoginRequest;

import java.io.InputStream;
import java.util.Properties;

/**
 * LoginAsyncTask - AsyncTask which handles logging the app user in
 * Created by Jason Mortara on 1/24/2016
 */
public class LoginAsyncTask extends AsyncTask<Void, Integer, Integer>
{
	// statics
	private static final String TAG = "LoginAsyncTask";

	// privates
	private Activity activity = null;
	private IExtendedAsyncTask taskCompleteCallbackObj;


	// constructor
	public LoginAsyncTask(Activity activity, IExtendedAsyncTask caller)
	{
		Log.d(TAG, "LoginAsyncTask constructor, called from " + activity.getLocalClassName());

		this.activity = activity;
		this.taskCompleteCallbackObj = caller;
	}

	@Override
	protected void onPreExecute()
	{
		Log.d(TAG, "onPreExecute");
		// called on thread init
		if (Model.getDatabaseProps() == null)
		{
			readDatabaseProperties();
		}
	}

	/**
	 * Read in DB props from bundle. Only nec if not already set into Model during app startup.
	 */
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

		int loginSucceeded = 0;

		if (Model.getDatabaseProps() != null)
		{
			Log.d(TAG, "doInBackground: Attempting user login through server command");
			try
			{
				// try a login
//				LoginMessage newLogin = new LoginMessage(1, "test1", "test1pass", "test1@wordwolfgame.com");    //TODO: HARDCODED
//				Model.userLogin = newLogin;
//				loginSucceeded = MySQLAccessTester.attemptLogin();

				LoginRequest loginRequest = Model.getUserLogin();
				Comm.out().writeObject(loginRequest);
				Comm.out().flush();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		//TODO:FIX
		// store result of login attempt in Model
		/*if(loginSucceeded == 0)
		{
			Model.loggedIn = false;
		}
		else Model.loggedIn = true;

		Log.d(TAG, "doInBackground: login succeeded? ****************************** " + Model.loggedIn);*/

		return loginSucceeded;
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

} // end class LoginAsyncTask
