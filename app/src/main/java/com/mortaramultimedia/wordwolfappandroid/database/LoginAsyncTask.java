package com.mortaramultimedia.wordwolfappandroid.database;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.mortaramultimedia.wordwolf.shared.messages.CreateNewAccountResponse;
import com.mortaramultimedia.wordwolf.shared.messages.LoginResponse;
import com.mortaramultimedia.wordwolfappandroid.data.Model;
import com.mortaramultimedia.wordwolfappandroid.communications.Comm;
import com.mortaramultimedia.wordwolfappandroid.interfaces.IExtendedAsyncTask;
import com.mortaramultimedia.wordwolf.shared.messages.LoginRequest;

import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.util.Properties;

/**
 * LoginAsyncTask - AsyncTask which handles logging the app user in
 * Created by Jason Mortara on 1/24/2016
 */
public class LoginAsyncTask extends AsyncTask<Void, Integer, Integer> implements IExtendedAsyncTask
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
		int result;

		// need to force wait for debugger to breakpoint in this thread
		if(android.os.Debug.isDebuggerConnected())
		{
			android.os.Debug.waitForDebugger();
		}

		Log.d(TAG, "doInBackground: dbProps? " + Model.getDatabaseProps().toString());


		if (Model.getDatabaseProps() != null)
		{
			Log.d(TAG, "doInBackground: Attempting user login through server command");
			try
			{
//				LoginMessage newLogin = new LoginMessage(1, "<username>", "<pass>", "<email>");		// for debugging a specific user
//				Model.userLogin = newLogin;
//				loginSucceeded = MySQLAccessTester.attemptLogin();

				// try loggin in
				LoginRequest loginRequest = Model.getUserLogin();
				Comm.out().writeObject(loginRequest);
				Comm.out().flush();
			}
			catch(StreamCorruptedException e)
			{
				e.printStackTrace();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}

		// loop until either Model.loggedIn is true or the login attempt exceeds specified wait time.
		final long MAX_WAIT_TIME_MS = 5000;
		long msWaited = 0;
		long waitIncrement = 100;

		try
		{
			while(!Model.getLoggedIn() && msWaited < MAX_WAIT_TIME_MS)
			{
				synchronized (this)
				{
					Log.d(TAG, "doInBackground: waiting for login response... " + msWaited + "ms...");
					wait(waitIncrement);
					msWaited += waitIncrement;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if(Model.getLoggedIn())
		{
			Log.d(TAG, "doInBackground: login SUCCEEDED after " + msWaited + "ms.");
			result = Activity.RESULT_OK;
		}
		else
		{
			Log.d(TAG, "doInBackground: login FAILED after " + msWaited + "ms.");
			result = Activity.RESULT_CANCELED;
		}

		Log.d(TAG, "doInBackground: Returning value of " + result);

		return result;
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

	@Override
	public void onTaskCompleted()
	{
		Log.d(TAG, "onTaskCompleted: TBD");
	}

	@Override
	public void handleIncomingObject(Object obj)
	{
		Log.d(TAG, "handleIncomingObject: " + obj);

		/**
		 * If receiving a CreateNewAccountResponse...
		 */
		if(obj instanceof CreateNewAccountResponse)
		{
			handleCreateNewAccountResponse(((CreateNewAccountResponse) obj));
		}
		/**
		 * If receiving a LoginResponse...
		 */
		else if (obj instanceof LoginResponse)
		{
			handleLoginResponse(((LoginResponse) obj));
		}
	}

	private void handleCreateNewAccountResponse(CreateNewAccountResponse response)
	{
		Log.d(TAG, "handleCreateNewAccountResponse ******************** : " + response);

		if(response.getAccountCreationSuccess())
		{
			Log.d(TAG, "handleLoginResponse: Create New Account SUCCEEDED. Calling Model.setNewAccountCreated(true)");
			Model.setNewAccountCreated(true);
		}
		else
		{
			Log.w(TAG, "handleLoginResponse: Create New Account FAILED. Calling Model.setNewAccountCreated(false)");
			Model.setNewAccountCreated(false);
		}
	}

	private void handleLoginResponse(LoginResponse response)
	{
		Log.d(TAG, "handleLoginResponse ********************** : " + response);

		if(response.getLoginAccepted())
		{
			Log.d(TAG, "handleLoginResponse: login succeeded. Calling Model.setLoggedIn(true)");
			Model.setLoggedIn(true);
		}
		else
		{
			Log.w(TAG, "handleLoginResponse: login FAILED. Calling Model.setLoggedIn(false)");
			Model.setLoggedIn(false);
		}

//		onTaskCompleted();
	}

} // end class LoginAsyncTask
