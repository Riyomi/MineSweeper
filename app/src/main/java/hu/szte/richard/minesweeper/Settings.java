package hu.szte.richard.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    private EditText userInput;
    SharedPreferences sharedPreferences;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        userInput = this.findViewById(R.id.username);
        sharedPreferences = getSharedPreferences("application", Context.MODE_PRIVATE);
        userInput.setText(sharedPreferences.getString("Name", "Player"));

        // code to hide the menu
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void saveName(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Name", userInput.getText().toString());
        editor.apply();
        Toast toast=Toast.makeText(getApplicationContext(),"Name saved!",Toast.LENGTH_SHORT);
        toast.show();
    }
}
