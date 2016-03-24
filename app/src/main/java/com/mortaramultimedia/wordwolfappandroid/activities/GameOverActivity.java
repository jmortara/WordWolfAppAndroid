package com.mortaramultimedia.wordwolfappandroid.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mortaramultimedia.wordwolf.shared.messages.*;
import com.mortaramultimedia.wordwolfappandroid.game.GameManager;
import com.mortaramultimedia.wordwolfappandroid.R;
import com.mortaramultimedia.wordwolfappandroid.communications.Comm;
import com.mortaramultimedia.wordwolfappandroid.data.Model;
import com.mortaramultimedia.wordwolfappandroid.interfaces.IExtendedAsyncTask;


public class GameOverActivity extends Activity implements IExtendedAsyncTask
{
    private static final String TAG = "GameOverActivity";
    private AlertDialog gameOverDialog = null;
    private AlertDialog selectOpponentRequestDialog = null;
    private AlertDialog opponentNotAvailableDialog = null;


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

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.w(TAG, "onPause ************************");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.w(TAG, "onStop ************************");
        //Comm.kill();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.e(TAG, "onDestroy ************************");
    }

    @Override
    public void onBackPressed()
    {
        Log.d(TAG, "onBackPressed: Ignoring.");
        // do nothing
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

        dismissGameOverDialog();

        gameOverDialog = new AlertDialog.Builder(this).create();
        gameOverDialog.setCancelable(false);
        gameOverDialog.setTitle("Game Over, " + Model.getUserLogin().getUserName() + "!");
        gameOverDialog.setMessage(
                "Your game with " + Model.getOpponentUsername() + " has ended.\n" +
                        "\nYour score: " + response.getFinalScoreFromServer() +
                        "\n" + Model.getOpponentUsername() + "'s score: " + response.getOpponentFinalScore() +
                        "\n\nRematch?"
        );

        // set up and listener for Accept button
        gameOverDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes! Rematch!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "showGameOverDialog: dialog button pressed: positive");

                // send out a post-endgame request specifying a rematch
                //TODO - why not just use the existing GetOpponentRequest? it now has an isRematch param...
                final PostEndGameActionRequest rematchRequest = new PostEndGameActionRequest(1, Model.getUserLogin().getUserName(),
                        2, Model.getOpponentUsername(), "game_type_rematch", true, Model.getGameBoard().getRows(), Model.getGameBoard().getCols(),
                        Model.getGameDurationMS());
                Comm.sendObject(rematchRequest);
                //TODO - show test or toast saying "Waiting for Opponent to respond..."
                showContactingOpponentToast(rematchRequest.getOpponentUserName());
            }
        });

        // set up and listener for Decline button
        gameOverDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No thanks.\nChoose another opponent.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "showGameOverDialog: dialog button pressed: negative");
                dialog.dismiss();

                // send out a post-endgame request specifying NO rematch
                final PostEndGameActionRequest declineRematchRequest = new PostEndGameActionRequest(1, Model.getUserLogin().getUserName(),
                        2, Model.getOpponentUsername(), null, false, -1, -1, -1);
                Comm.sendObject(declineRematchRequest);

                switchToChooseOpponentActivity();
            }
        });

        gameOverDialog.show();
    }

    private void showContactingOpponentToast(String opponentUsername)
    {
        Log.d(TAG, "showContactingOpponentToast");

        Toast.makeText(this, "Contacting " + opponentUsername + ", one moment...", Toast.LENGTH_LONG).show();
    }

    private void dismissGameOverDialog()
    {
        Log.d(TAG, "dismissGameOverDialog");

        if(gameOverDialog != null)
        {
            gameOverDialog.dismiss();
        }
    }

    private void dismissSelectOpponentRequestDialog()
    {
        Log.d(TAG, "dismissSelectOpponentRequestDialog");

        if(selectOpponentRequestDialog != null)
        {
            selectOpponentRequestDialog.dismiss();
        }
    }

    private void dismissOpponentNotAvailableDialog()
    {
        Log.d(TAG, "dismissOpponentNotAvailableDialog");

        if(opponentNotAvailableDialog != null)
        {
            opponentNotAvailableDialog.dismiss();
        }
    }

    private void switchToChooseOpponentActivity()
    {
        Log.d(TAG, "switchToChooseOpponentActivity");

        dismissGameOverDialog();
        dismissSelectOpponentRequestDialog();

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
            // show a Select Opponent Request dialog, similar to the one in ConnectionActivity (this is a rematch invite dialog)
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
        else if (obj instanceof PostEndGameActionResponse)
        {
            handlePostEndGameActionResponse((PostEndGameActionResponse) obj);
        }
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
            Log.d(TAG, "handleSelectOpponentResponse: (case A) REQUEST for rematch ACCEPTED! from: " + response.getSourceUsername());
            GameManager.resetScore();
            Model.setOpponentUsername(response.getSourceUsername());
            dismissGameOverDialog();
            switchToGameSetupActivity();
        }
        else
        {
            // if this client was the recipient of a rematch invitation and declined it, this response is a confirmation, so just go back to Choose Opponent Activity
            if(!response.getSourceUsername().equals(Model.getUserLogin().getUserName()) && response.getShowDialog())
            {
                Log.d(TAG, "handleSelectOpponentResponse: (case B, inviter) REQUEST for rematch REJECTED! (or opponent offline) from: " + response.getSourceUsername());
                showOpponentNotAvailableDialog(response.getSourceUsername());
            }
            // if this client is the one who initiated a rematch invitation, show a dialog stating that the current opponent is not available
            else
            {
                Log.d(TAG, "handleSelectOpponentResponse: (case C, decliner) REQUEST for rematch REJECTED! (or opponent offline) from: " + response.getSourceUsername());
                Model.setOpponentUsername(null);
                switchToChooseOpponentActivity();
            }
        }
    }

    private void handlePostEndGameActionResponse(PostEndGameActionResponse response)
    {
        Log.d(TAG, "handlePostEndGameActionResponse: " + response);

        if(response.getRequestAccepted())
        {
            Log.d(TAG, "handlePostEndGameActionResponse: REQUEST for rematch ACCEPTED! from: " + response.getSourceUserName());
            GameManager.resetScore();
            Model.setOpponentUsername(response.getSourceUserName());
            dismissGameOverDialog();
            switchToGameSetupActivity();
        }
        else
        {
            Log.d(TAG, "handlePostEndGameActionResponse: REQUEST for rematch REJECTED! (or opponent offline) from: " + response.getSourceUserName());
            if (response.getSourceUserName().equals(Model.getUserLogin().getUserName()))
            {
                showOpponentNotAvailableDialog(response.getSourceUserName());
            }
            else
            {
                Model.setOpponentUsername(null);
                switchToChooseOpponentActivity();
            }
        }
    }

    /**
     * Launch the Game Setup Activity
     */
    private void switchToGameSetupActivity()
    {
        Log.d(TAG, "switchToGameSetupActivity from GameOverActivity");

        dismissGameOverDialog();
        dismissSelectOpponentRequestDialog();

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

        dismissGameOverDialog();
        dismissSelectOpponentRequestDialog();

        selectOpponentRequestDialog = new AlertDialog.Builder(this).create();
        selectOpponentRequestDialog.setTitle("Opponent Request");
        selectOpponentRequestDialog.setMessage("You have been invited to have a rematch with: " + request.getSourceUsername());
        selectOpponentRequestDialog.setCancelable(false);

        final String sourceUsername = Model.getUserLogin().getUserName();
        final String destinationUsername = request.getSourceUsername();     // the source of the incoming request becomes the destination for this response

        // set up and listener for Accept button
        selectOpponentRequestDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Accept!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // we can't get the request source player's username as an arg, so we have to retrieve it from the stored incomingObj
                dismissGameOverDialog();
                SelectOpponentResponse response = new SelectOpponentResponse(true, sourceUsername, destinationUsername, true, false);
                Comm.sendObject(response);
            }
        });

        // set up and listener for Decline button
        selectOpponentRequestDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // we can't get the request source player's username as an arg, so we have to retrieve it from the stored incomingObj
                SelectOpponentResponse response = new SelectOpponentResponse(false, sourceUsername, destinationUsername, true, false);
                Comm.sendObject(response);
            }
        });

        selectOpponentRequestDialog.show();
    }

    private void showOpponentNotAvailableDialog(String opponentUsername)
    {
        Log.d(TAG, "showOpponentNotAvailableDialog");

        dismissGameOverDialog();
        dismissSelectOpponentRequestDialog();
        dismissOpponentNotAvailableDialog();

        opponentNotAvailableDialog = new AlertDialog.Builder(this).create();
        opponentNotAvailableDialog.setCancelable(false);
        opponentNotAvailableDialog.setTitle("This Opponent Is Not Available, " + Model.getUserLogin().getUserName() + "!");
        opponentNotAvailableDialog.setMessage("Unfortunately, " + opponentUsername + " is not available for a rematch.\n\n" +
                        "Try another opponent!"
        );

        // set up and listener for Accept button
        opponentNotAvailableDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "showOpponentNotAvailableDialog: dialog button pressed: positive");
                switchToChooseOpponentActivity();
            }
        });

        opponentNotAvailableDialog.show();
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
