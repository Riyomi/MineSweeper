package hu.szte.richard.minesweeper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Gameplay extends AppCompatActivity {
    final static int SIZE_X = 8;
    final static int SIZE_Y = 10;
    final static int NUMBER_OF_MINES = 10;

    private Field[][] board = new Field[SIZE_X][SIZE_Y];
    private TextView timer;
    private TextView flags;
    private SharedPreferences sharedPreferences;
    int count = 0;
    boolean placeFlagNext = false;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);
        timer = this.findViewById(R.id.timer);
        sharedPreferences = getSharedPreferences("application", Context.MODE_PRIVATE);
        drawBoard();
        initMines();
        setActionListeners();
        refreshTimer();
        flags = this.findViewById(R.id.flag);

        // code to hide the menu
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private void drawBoard() {
        int sizeOfCell = (int) Math.round(getScreenWidth()/(SIZE_X*1.0));
        LinearLayout ln = this.findViewById(R.id.board);

        for (int x = 0; x < SIZE_X; x++) {
            LinearLayout linearLayout = new LinearLayout(getApplicationContext());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 0);
            params.width = sizeOfCell;
            linearLayout.setLayoutParams(params);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            for (int y = 0; y < SIZE_Y; y++) {
                ImageView imageView = new ImageView(this);

                if((x % 2 == 0 && y % 2 != 0) || (x % 2 != 0 && y % 2 == 0) ) {
                    imageView.setImageResource(R.drawable.mine_field_2);
                } else {
                    imageView.setImageResource(R.drawable.mine_field);
                }

                //setting image position
                imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                imageView.getLayoutParams().height = sizeOfCell;

                board[x][y] = new Field();
                board[x][y].setPicture(imageView);

                //adding view to layout
                linearLayout.addView(imageView);
            }

            ln.addView(linearLayout);
        }
    }

    private void initMines() {
        new RandomizeMinePositions().execute();
    }

    private void setActionListeners(){
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                final int finalX = x;
                final int finalY = y;
                board[x][y].getPicture().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(placeFlagNext && !board[finalX][finalY].isRevealed()){
                            flags.setText(R.string.off);
                            placeFlagNext = false;
                            if(board[finalX][finalY].isLocked()) {
                                board[finalX][finalY].setLocked(false);
                                if((finalX % 2 == 0 && finalY % 2 != 0) || (finalX % 2 != 0 && finalY % 2 == 0) ) {
                                    board[finalX][finalY].getPicture().setImageResource(R.drawable.mine_field_2);
                                } else {
                                    board[finalX][finalY].getPicture().setImageResource(R.drawable.mine_field);
                                }
                            } else {
                                board[finalX][finalY].setLocked(true);
                                board[finalX][finalY].getPicture().setImageResource(R.drawable.mine2);
                            }
                        }
                        else if(board[finalX][finalY].hasMine() && !board[finalX][finalY].isLocked()) {
                            board[finalX][finalY].setRevealed(true);
                            board[finalX][finalY].getPicture().setImageResource(R.drawable.mine);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else if (getNumOfMines(finalX, finalY) == 0 && !board[finalX][finalY].isLocked()) {
                            floodFill(finalX, finalY, 0);
                        } else if(!board[finalX][finalY].isLocked()) {
                            board[finalX][finalY].setRevealed(true);
                            board[finalX][finalY].getPicture().setImageResource(choosePicForTheField(finalX, finalY));
                        }
                        if (checkForWinCondition()) {
                            Toast toast=Toast.makeText(getApplicationContext(),"You won!",Toast.LENGTH_SHORT);
                            toast.show();
                            saveResult();
                        }
                    }
                });
            }
        }
    }

    private void refreshTimer() {
        final Handler h = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                count++;
                timer.setText(String.format("%1$" + 3 + "s", Integer.toString(count)).
                        replace(' ', '0'));
                h.postDelayed(this, 1000); //ms
            }
        };
        h.postDelayed(r, 1000); // one second in ms
    }

    private int choosePicForTheField(int x, int y) {
        int count = getNumOfMines(x, y);
        boolean light = true;

        if((x % 2 == 0 && y % 2 != 0) || (x % 2 != 0 && y % 2 == 0) ) {
            light = false;
        }

        if (count == 0 && light) {
            return R.drawable.mine_field_4;
        } else if (count == 0) {
            return R.drawable.mine_field_3;
        } else if (count == 1 && light) {
            return R.drawable.number1;
        } else if (count == 1) {
            return R.drawable.number1_dark;
        } else if (count == 2 && light) {
            return R.drawable.number2;
        } else if (count == 2) {
            return R.drawable.number2_dark;
        } else if (count == 3 && light) {
            return R.drawable.number3;
        } else if (count == 3) {
            return R.drawable.number3_dark;
        }  else if (count == 4 && light) {
            return R.drawable.number4;
        }  else if (count == 4) {
            return R.drawable.number4_dark;
        }
        return R.drawable.mine_field;
    }

    private int getNumOfMines(int x, int y) {
        int count = 0;
        for (int x_offset = -1; x_offset < 2; x_offset++) {
            for (int y_offset = -1; y_offset < 2; y_offset++) {
                try {
                    if(board[x+x_offset][y+y_offset].hasMine()) {
                        count++;
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {
                    /* ignored */
                }
            }
        }
        return count;
    }

    public void placeFlag(View view) {
        placeFlagNext = true;
        flags.setText(R.string.on);
    }

    @SuppressLint("StaticFieldLeak")
    private class RandomizeMinePositions extends AsyncTask<List, Integer, List> {
        @Override
        protected List doInBackground(List... lists) {
            List<Integer> list = new ArrayList<>();

            for (int i = 0; i < SIZE_X * SIZE_Y; i++) {
                list.add(i);
            }

            Collections.shuffle(list);
            List<Integer> minePositions = list.subList(0, NUMBER_OF_MINES);

            for (Integer element: minePositions) {
                board[element/10][element%10].setHasMine(true);
            }

            return null;
        }

        @Override
        protected void onPostExecute(List result) {
            super.onPostExecute(result);
        }
    }

    private void floodFill(int x, int y, int countOnPreviousField){
        if (x >= 0 && x < SIZE_X && y >= 0 && y < SIZE_Y && !board[x][y].isRevealed() && countOnPreviousField == 0) {
            if (!board[x][y].hasMine()) {
                board[x][y].setRevealed(true);
                board[x][y].getPicture().setImageResource((choosePicForTheField(x, y)));
                int count = getNumOfMines(x, y);
                floodFill(x-1, y, count);
                floodFill(x+1, y, count);
                floodFill(x, y-1, count);
                floodFill(x, y+1, count);
            }
        }
    }

    private boolean checkForWinCondition(){
        int count = 0;
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                if (board[x][y].isRevealed() && !board[x][y].hasMine()) {
                    count++;
                }
            }
        }

        return count == SIZE_X * SIZE_Y - NUMBER_OF_MINES;
    }

    private void saveResult(){
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int[] scores = new int[3];
        String[] names = new String[2];

        scores[0] = sharedPreferences.getInt("Top1_score", 999);
        scores[1] = sharedPreferences.getInt("Top2_score", 999);
        scores[2] = sharedPreferences.getInt("Top3_score", 999);
        names[0] = sharedPreferences.getString("Top1", "Player");
        names[1] = sharedPreferences.getString("Top2", "Player");

        if (count < scores[0]) {
            editor.putInt("Top1_score", count);
            editor.putString("Top1", sharedPreferences.getString("Name", "Player"));
            editor.putInt("Top2_score", scores[0]);
            editor.putString("Top2", names[0]);
            editor.putInt("Top3_score", scores[1]);
            editor.putString("Top3", names[1]);
            editor.apply();
        } else if (count < scores[1]) {
            editor.putInt("Top2_score", count);
            editor.putString("Top2", sharedPreferences.getString("Name", "Player"));
            editor.putInt("Top3_score", scores[1]);
            editor.putString("Top3", names[1]);
            editor.apply();
        } else if (count < scores[2]) {
            editor.putInt("Top3_score", count);
            editor.putString("Top3", sharedPreferences.getString("Name", "Player"));
            editor.apply();
        }

    }
}
