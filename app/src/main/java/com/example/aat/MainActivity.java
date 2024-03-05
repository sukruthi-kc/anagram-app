package com.example.aat;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnStartGame;
    private Button btnStartGame1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartGame = findViewById(R.id.btnStartGame);
        btnStartGame1 = findViewById(R.id.btnStartGame1);

        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnagramGame();
            }
        });

        btnStartGame1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity2();
            }
        });
    }

    private void startAnagramGame() {
        Intent intent = new Intent(MainActivity.this, AnagramActivity.class);
        startActivity(intent);
    }

    private void startActivity2() {
        Intent intent = new Intent(MainActivity.this, activity2.class);
        startActivity(intent);
    }
}

