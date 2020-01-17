package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.gridlayout.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    // x: 1, o: 2, empty: 0
    int playerActive = 1;
    boolean gameActive = true;
    int[] gameState = {
            0, 0, 0,
            0, 0, 0,
            0, 0, 0
    };
    int[][] winningPatterns = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // horizontal
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // vertical
            {0, 4, 8}, {2, 4, 6} // diagonals
    };

    ImageView winningLine;
    float[][] winningLinePositions = {
            // x, y, rotation angle
            {0, -338, 90}, {0, 0, 90}, {0, 338, 90},
            {-338, 0, 0}, {0, 0, 0}, {338, 0, 0},
            {0, 0, -45}, {0, 0, 45}
    };

    public void popIn(View view) {
        ImageView counter = (ImageView) view;
        int tappedCounter = Integer.parseInt(counter.getTag().toString());
        // if game not active return
        if(!gameActive) return;

        // if non empty return and don't update
        if(gameState[tappedCounter] != 0) return;
        gameState[tappedCounter] = playerActive;

        int winningPlayer = checkForWinner()[0];
        int winningLinePosition = checkForWinner()[1];

        if(playerActive == 1) {
            counter.setImageResource(R.drawable.cross);
            playerActive = 2;
        } else {
            counter.setImageResource(R.drawable.circle);
            playerActive = 1;
        }

        if(winningPlayer != 0) {
            gameActive = false;
            ((TextView) findViewById(R.id.statusTextView)).setText((winningPlayer == 1 ? 'X' : '0') + " Wins");
            Log.i("XD", "Game Ended, Winning Player: " + winningPlayer + " " + (winningPlayer == 1 ? 'X' : '0'));
            showWinningLine(winningLinePosition);
        }

        if(winningPlayer == 0 && checkForDraw()) {
            Log.i("XD", "Game Drawn");
            ((TextView) findViewById(R.id.statusTextView)).setText("Draw");
            return;
        }

        Log.i("XD", "popIn, tag: " + tappedCounter + " gameState: " + Arrays.toString(gameState));
    }

    public int[] checkForWinner() {
        int[] winningPlayer = {0, 0};
        int i = 0;
        for(int[] pattern : winningPatterns) {
            if(gameState[pattern[0]] == gameState[pattern[1]] && gameState[pattern[1]] == gameState[pattern[2]] && gameState[pattern[0]] != 0) {
                winningPlayer[0] = playerActive;
                winningPlayer[1] = i;
            }
            i++;
        }
        return winningPlayer;
    }

    public void showWinningLine(int patternNumber) {
        Log.i("XD", "" + patternNumber + winningLinePositions[patternNumber][0]);
        winningLine.setImageAlpha(200);
        winningLine.setTranslationX(winningLinePositions[patternNumber][0]);
        winningLine.setTranslationY(winningLinePositions[patternNumber][1]);
        winningLine.setRotation(winningLinePositions[patternNumber][2]);
    }

    public boolean checkForDraw() {
        boolean emptySpotFound = false;
        for(int currentSpot : gameState) {
            if(currentSpot == 0) {
                emptySpotFound = true;
                break;
            }
        }
        return checkForWinner()[0] == 0 && !emptySpotFound;
    }

    public void resetGame(View view) {
        for(int i = 0; i < gameState.length; i++) {
            gameState[i] = 0;
        }
        playerActive = 1;
        gameActive = true;

        GridLayout gridLayout = findViewById(R.id.gridLayout);
        for(int i = 0; i < gridLayout.getChildCount(); i++) {
            ImageView counter = (ImageView) gridLayout.getChildAt(i);
            counter.setImageDrawable(null);
        }
        ((TextView) findViewById(R.id.statusTextView)).setText("Reset Done");
        winningLine.setImageAlpha(0);
        Log.i("XD", "Game Reset");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        winningLine = findViewById(R.id.winningLine);
        winningLine.setImageAlpha(0);
    }
}
