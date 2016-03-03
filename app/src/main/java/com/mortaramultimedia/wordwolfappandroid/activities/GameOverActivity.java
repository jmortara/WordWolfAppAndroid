package com.mortaramultimedia.wordwolfappandroid.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mortaramultimedia.wordwolf.shared.messages.*;
import com.mortaramultimedia.wordwolfappandroid.R;
import com.mortaramultimedia.wordwolfappandroid.communications.Comm;
import com.mortaramultimedia.wordwolfappandroid.data.Model;
import com.mortaramultimedia.wordwolfappandroid.interfaces.IExtendedAsyncTask;


public class GameOverActivity extends Activity implements IExtendedAsyncTask
{
    private static final String TAG = "GameOverActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Comm.registerCurrentActivity(this);	// tell Comm to forward published progress updates to this Activity

        sendEndGameRequest();
    }

    @Override
    protected void onResume()
    {
        Log.d(TAG, "onResume");
        super.onResume();
        Comm.registerCurrentActivity(this);	// tell Comm to forward published progress updates to this Activity

        sendEndGameRequest();
    }

    /**
     * Update this activity's UI.
     */
    private void updateUI()
    {
        Log.d(TAG, "updateUI");
    }

    private void sendEndGameRequest()
    {
        Log.d(TAG, "sendEndGameRequest");

        // notify the server that the game has ended
        EndGameRequest request = new EndGameRequest(Model.getUserLogin().getUserName(), -1);
        Comm.sendObject(request);
    }

    private void showGameOverDialog(EndGameResponse response)
    {
        Log.d(TAG, "showGameOverDialog");

        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Game Over!");
        dialog.setMessage(
                "Your game with " + Model.getOpponentUsername() + " has ended." +
                "\nYour score: " + Model.getScore() +
                "\n" + Model.getOpponentUsername() + "'s score: " + response.getOpponentFinalScore() +
                "\n\nRematch?"
                );

        // set up and listener for Accept button
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes! Rematch!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "showGameOverDialog: dialog button pressed: positive");
                //TODO: start new game without starting a new Activity

                // send out a post-endgame request specifying a rematch
                //TODO - why not just use the existing GetOpponentRequest?
                final PostEndGameActionRequest rematchRequest = new PostEndGameActionRequest(1, Model.getUserLogin().getUserName(),
                        2, Model.getOpponentUsername(), "game_type_rematch", true, Model.getGameBoard().getRows(), Model.getGameBoard().getCols(),
                        Model.getGameDurationMS());
                Comm.sendObject(rematchRequest);
            }
        });

        // set up and listener for Decline button
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No thanks.\nChoose another opponent.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "showGameOverDialog: dialog button pressed: negative");
                dialog.dismiss();

                // send out a post-endgame request specifying a rematch
//                final PostEndGameActionRequest chooseNewOpponentRequest = new PostEndGameActionRequest(1, Model.getUserLogin().getUserName(),
//                        -1, null, "game_type_choose_new_opponent", false, -1, -1, -1);
//                Comm.sendObject(chooseNewOpponentRequest);

                switchToChooseOpponentActivity();
            }
        });

        dialog.show();
    }

    private void switchToChooseOpponentActivity()
    {
        Log.d(TAG, "switchToChooseOpponentActivity");

        Intent intent = new Intent(GameOverActivity.this, ChooseOpponentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    /**
     * IExtendedAsyncTask overrides
     * TODO = still called?
     */
    @Override
    public void onTaskCompleted()
    {
        Log.d(TAG, "onTaskCompleted");
        updateUI();
    }

    @Override
    public void handleIncomingObject(Object obj)
    {
        Log.d(TAG, "handleIncomingObject: " + obj);

        updateUI();

        if(obj instanceof SimpleMessage)
        {
            Log.d(TAG, "handleIncomingObject: SimpleMessage: " + ((SimpleMessage) obj).getMsg());
        }
        else if (obj instanceof EndGameResponse)
        {
            // show a Game Over dialog with the incoming object's opponent score
            showGameOverDialog((EndGameResponse) obj);
        }
        else if (obj instanceof SelectOpponentRequest)
        {
            // show a Select Opponent Request dialog, similar to the one in ConnectionActivity
            if (((SelectOpponentRequest) obj).getDestinationUserName().equals(Model.getUserLogin().getUserName()))
            {
                showSelectOpponentRequestDialog((SelectOpponentRequest) obj);
            }
        }
        else if (obj instanceof SelectOpponentResponse)
        {
            handleSelectOpponentResponse(((SelectOpponentResponse) obj));
        }
        else if(obj instanceof CreateGameResponse)
        {
            Log.d(TAG, "handleIncomingObject: CreateGameResponse CAME IN: " + obj);

            // store the GameBoard and game duration in the Model
            CreateGameResponse response = (CreateGameResponse) obj;
            Model.setGameBoard(response.getGameBoard());
            Model.setGameDurationMS(response.getGameDurationMS());

            Log.d(TAG, "handleIncomingObject: GAME BOARD RECEIVED: \n");
            Model.getGameBoard().printBoardData();

            Log.d(TAG, "handleIncomingObject: ***STARTING GAME***");
            switchToGameSetupActivity();
        }
        /*else if (obj instanceof PostEndGameActionResponse)
        {
            Log.d(TAG, "handleIncomingObject: PostEndGameActionResponse - BEHAVIOR TBD **************");
            //doStuff(PostEndGameActionRequest) obj);
        }*/
        else
        {
            Log.d(TAG, "handleIncomingObject: object ignored.");
        }
    }

    private void handleSelectOpponentResponse(SelectOpponentResponse response)
    {
        Log.d(TAG, "handleSelectOpponentResponse: " + response);

//		publishObject(response);
        if (response.getRequestAccepted())
        {
            Log.d(TAG, "handleRequestToBecomeOpponent: REQUEST ACCEPTED! from: " + response.getSourceUserName());
            Model.setOpponentUsername(response.getSourceUserName());
            switchToGameSetupActivity();
        }
        else
        {
            Log.d(TAG, "handleRequestToBecomeOpponent: REQUEST REJECTED! from: " + response.getSourceUserName());
        }
    }

    /**
     * Launch the Game Setup Activity
     */
    private void switchToGameSetupActivity()
    {
        Log.d(TAG, "switchToGameSetupActivity from GameOverActivity");

        // create an Intent for launching the Game Setup Activity, with optional additional params
        Context thisContext = GameOverActivity.this;
        Intent intent = new Intent(thisContext, GameSetupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("testParam", "testValue");                        //optional params

        // start the activity
        startActivity(intent);
    }

    private void showSelectOpponentRequestDialog(SelectOpponentRequest request)
    {
        Log.d(TAG, "showSelectOpponentRequestDialog");

        final String sourceUsername = request.getSourceUsername();
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Opponent Request");
        dialog.setMessage("You have been invited to have a rematch with: " + request.getSourceUsername());

        // set up and listener for Accept button
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Accept!", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // we can't get the request source player's username as an arg, so we have to retrieve it from the stored incomingObj
//				SelectOpponentRequest request = (SelectOpponentRequest) Model.getIncomingObj();
//				String sourceUsername = request.getSourceUsername();
                SelectOpponentResponse response = new SelectOpponentResponse(true, Model.getUserLogin().getUserName(), sourceUsername);
                Comm.sendObject(response);
            }
        });

        // set up and listener for Decline button
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // we can't get the request source player's username as an arg, so we have to retrieve it from the stored incomingObj
//				SelectOpponentRequest request = (SelectOpponentRequest) Model.getIncomingObj();
//				String sourceUsername = request.getSourceUsername();
                SelectOpponentResponse response = new SelectOpponentResponse(false, Model.getUserLogin().getUserName(), sourceUsername);
                Comm.sendObject(response);
            }
        });

        dialog.show();
    }


    /*private void showRematchRequestDialog(SelectOpponentRequest request)
    {
        Log.d(TAG, "showRematchRequestDialog");

        final String sourceUsername = request.getSourceUsername();
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Rematch Request");
        dialog.setMessage("You have been invited to have a rematch with: " + request.getSourceUsername());

        // set up and listener for Accept button
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Accept!", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // we can't get the request source player's username as an arg, so we have to retrieve it from the stored incomingObj
//				SelectOpponentRequest request = (SelectOpponentRequest) Model.getIncomingObj();
//				String sourceUsername = request.getSourceUsername();
                SelectOpponentResponse response = new SelectOpponentResponse(true, Model.getUserLogin().getUserName(), sourceUsername);
                Comm.sendObject(response);
            }
        });

        // set up and listener for Decline button
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // we can't get the request source player's username as an arg, so we have to retrieve it from the stored incomingObj
//				SelectOpponentRequest request = (SelectOpponentRequest) Model.getIncomingObj();
//				String sourceUsername = request.getSourceUsername();
                SelectOpponentResponse response = new SelectOpponentResponse(false, Model.getUserLogin().getUserName(), sourceUsername);
                Comm.sendObject(response);
            }
        });

        dialog.show();
    }*/


}
