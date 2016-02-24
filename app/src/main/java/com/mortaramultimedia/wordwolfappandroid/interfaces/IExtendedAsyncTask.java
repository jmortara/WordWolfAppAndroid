package com.mortaramultimedia.wordwolfappandroid.interfaces;

/**
 * Interface Extended Async Task
 * Required to be implemented by some Activities for receiving updates from an Async task to update associated Activities,
 * such as including methods which facilitate updating the main UI thread.
 * Created by Jason Mortara on 1/25/2016.
 */
public interface IExtendedAsyncTask
{
	/**
	 * Called when the Async task has completed, typically during onPostExecute()
	 */
	void onTaskCompleted();

	/**
	 * Handle an incoming object from the server.
	 * @param obj
	 */
	void handleIncomingObject(Object obj);
}
