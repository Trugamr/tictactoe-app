package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
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

        counter.setAlpha(0f);

        if(playerActive == 1) {
            counter.setImageResource(R.drawable.cross);
            spawnAnimation(counter);
            playerActive = 2;
        } else {
            counter.setImageResource(R.drawable.circle);
            spawnAnimation(counter);
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
        SpringAnimation alphaUp = new SpringAnimation(winningLine, DynamicAnimation.ALPHA, 1f);
        alphaUp.start();
        winningLine.setScaleY(0f);
        SpringAnimation scaleY = new SpringAnimation(winningLine, DynamicAnimation.SCALE_Y, 1.2f);
        scaleY.getSpring().setStiffness(SpringForce.STIFFNESS_VERY_LOW).setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
        scaleY.start();
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
        winningLine.setAlpha(0f);
        Log.i("XD", "Game Reset");
    }

    public void spawnAnimation(View v) {
        SpringAnimation alphaIn = new SpringAnimation(v, DynamicAnimation.ALPHA, 1f);
        alphaIn.getSpring().setStiffness(SpringForce.STIFFNESS_LOW).setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY);
        alphaIn.start();

        SpringAnimation scaleDownX = new SpringAnimation(v, DynamicAnimation.SCALE_X, 0.5f);
        SpringAnimation scaleDownY = new SpringAnimation(v, DynamicAnimation.SCALE_Y, 0.5f);
        scaleDownX.getSpring().setStiffness(SpringForce.STIFFNESS_MEDIUM).setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY);
        scaleDownY.getSpring().setStiffness(SpringForce.STIFFNESS_MEDIUM).setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY);
        scaleDownX.start();
        scaleDownY.start();

        final SpringAnimation scaleUpX = new SpringAnimation(v, DynamicAnimation.SCALE_X, 1f);
        final SpringAnimation scaleUpY = new SpringAnimation(v, DynamicAnimation.SCALE_Y, 1f);

        scaleDownX.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                scaleUpX.start();
                scaleUpY.start();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        winningLine = findViewById(R.id.winningLine);
        winningLine.setScaleX(1.4f);
        winningLine.setAlpha(0f);
    }
}
