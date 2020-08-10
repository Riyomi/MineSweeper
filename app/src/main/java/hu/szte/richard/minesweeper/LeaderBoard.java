package hu.szte.richard.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashSet;
import java.util.Set;

public class LeaderBoard extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        TextView top1 = this.findViewById(R.id.top1);
        TextView top2 = this.findViewById(R.id.top2);
        TextView top3 = this.findViewById(R.id.top3);

        //Retrieve the values
        sharedPreferences = getSharedPreferences("application", Context.MODE_PRIVATE);

        top1.setText(prepareString("Top1", 1));
        top2.setText(prepareString("Top2", 2));
        top3.setText(prepareString("Top3", 3));

        // code to hide the menu
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private String prepareString(String key, int num) {
        String result = num + ". " + sharedPreferences.getString(key, "Player");
        result = result + "  " + sharedPreferences.getInt(key+"_score", 999);
        return result;
    }
}