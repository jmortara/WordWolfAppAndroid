package com.mortaramultimedia.wordwolfappandroid.layout.array_adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.mortaramultimedia.wordwolfappandroid.R;

import java.util.ArrayList;
import java.util.List;

import data.PublicPlayerData;

/**
 * Created by Jason Mortara on 3/17/16.
 */
public class PublicPlayerDataListAdapter extends ArrayAdapter<PublicPlayerData>
{
    public static final String TAG = "PublicPlayerDataAdapter";

    private int layoutResource;
    public ArrayList<PublicPlayerData> playersList;

    /*public PublicPlayerDataListAdapter(Context context, int layoutResource, int textViewResourceId, List<PublicPlayerData> playersList)
    {
        super(context, layoutResource, textViewResourceId, playersList);
        this.layoutResource = layoutResource;
        Log.d(TAG, "PublicPlayerDataListAdapter: Constructor. playersList: " + playersList);
    }*/

    public PublicPlayerDataListAdapter(Context context, int resource, ArrayList<PublicPlayerData> playersList, int layoutResource) {
        super(context, resource, playersList);
        this.layoutResource = layoutResource;
        this.playersList = playersList;
        Log.d(TAG, "Constructor. playersList: " + playersList);
    }

    @Override
    public int getCount()
    {
        Log.d(TAG, "getCount: playersList.size: " + playersList.size());
        return playersList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.d(TAG, "getView at (position) for playersList: (" + position + ") " + playersList.toString());

        View view = convertView;
/*
        if (view == null) {
            Log.d(TAG, "getView: view is null");
            //LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutResource, null);
        }*/

        if (view == null)
        {
            view = LayoutInflater.from(getContext()).inflate(R.layout.public_player_data_list_adapter, parent, false);
        }

        try
        {
            PublicPlayerData playerData = playersList.get(position);
            //PublicPlayerData playerData = getItem(position);

            if (playerData != null)
            {
                Log.d(TAG, "getView: player data OK");
                TextView playerDataUsernameTextView   = (TextView) view.findViewById(R.id.playerDataUsernameTextView);
                TextView playerDataHighScoreTextView  = (TextView) view.findViewById(R.id.playerDataHighScoreTextView);
                TextView playerDataTotalScoreTextView = (TextView) view.findViewById(R.id.playerDataTotalScoreTextView);

                if (playerDataUsernameTextView != null)
                {
                    Log.d(TAG, "getView: getUsername: " + playerData.getUsername());
                    playerDataUsernameTextView.setText(playerData.getUsername());
                }

                if (playerDataHighScoreTextView != null)
                {
                    Log.d(TAG, "getView: getHighScore: " + playerData.getHighScore());
                    playerDataHighScoreTextView.setText(Integer.toString(playerData.getHighScore()));
                }

                if (playerDataTotalScoreTextView != null)
                {
                    Log.d(TAG, "getView: getTotalScore: " + playerData.getTotalScore());
                    playerDataTotalScoreTextView.setText(Integer.toString(playerData.getTotalScore()));
                }
            }
        }
        catch( IndexOutOfBoundsException e)
        {
            Log.w(TAG, "getView: WARNING: no data");
        }

        return view;
    }

    @Override
    public PublicPlayerData getItem(int position)
    {
        Log.d(TAG, "getItem: " + position);
        return playersList.get(position);
    }

    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
        Log.d(TAG, "notifyDataSetChanged: " + playersList.size());

    }
}