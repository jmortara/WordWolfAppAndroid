package com.mortaramultimedia.wordwolfappandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;


public class MainActivity extends Activity {

    public static final String TAG = "WelcomeActivity";
    private TextView statusText;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init()
    {
        Log.d(TAG, "init");

        statusText = (TextView) findViewById(R.id.statusText);
        startButton = (Button) findViewById(R.id.startButton);

        statusText.setText( getResources().getString(R.string.startup));
        setStartButtonVisibility(View.INVISIBLE);
        populateDictionary();

    }

    private void setStartButtonVisibility( int v )
    {
        startButton.setVisibility( v );
    }

    private void populateDictionary()
    {
        Log.d(TAG, "populateDictionary");

        statusText.setText( getResources().getString(R.string.loading_dictionary));

        Model.globalDictionary = new HashMap<String, String>();
        Log.d( TAG, "populateDictionary: globalDictionary length before load: " + Model.globalDictionary.size() );

        Model.globalDictionary = loadDictionary(this);
        Log.d( TAG, "populateDictionary: globalDictionary length after load:  " + Model.globalDictionary.size() );
        statusText.setText(getResources().getString(R.string.loaded_dictionary) + " " + Model.globalDictionary.size() + " words. ");
        setStartButtonVisibility(View.VISIBLE);

        // EditText editText = (EditText) findViewById(R.id.dictEditText);
        //editText.setText("TEXT");

        //ScrollView scrollView = (ScrollView) findViewById(R.id.dictScrollView);

    }

    public static HashMap<String, String> loadDictionary(Context context)
    {
        Log.d(TAG, "loadDictionary");

        HashMap<String, String> myDict = new HashMap<String, String>();
        AssetManager assetManager = context.getAssets();
        String line;
        int currentLine = 0;
        int lastLineToPrint = 1000;
        try {
            InputStream ims = assetManager.open("dictionary_GIANT.txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(ims));
            try {
                while ((line=r.readLine()) != null) {
                    myDict.put(line, line);
                    if ( currentLine <= lastLineToPrint)
                    {
                        Log.d(TAG, "read line: " + line);
                    }
                    currentLine++;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return myDict;
    }

    public void handleStartButtonClick(View view)
    {
        Log.d(TAG, "handleStartButtonClick");

        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

