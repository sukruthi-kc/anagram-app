package com.example.aat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ActivitySummary extends AppCompatActivity {

    private TextView scoreTextView;
    private TextView levelTextView;
    private Button finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        scoreTextView = findViewById(R.id.scoreTextView);
        levelTextView = findViewById(R.id.levelTextView);
        finishButton = findViewById(R.id.finishButton);

        // Retrieve score from intent extras
        int score = getIntent().getIntExtra("SCORE", 0);
        // Assuming you also pass the level from the previous activity, replace "1" with the correct level value
        int level = getIntent().getIntExtra("LEVEL", 1);

        // Display score and level
        scoreTextView.setText("Score: " + score);
        levelTextView.setText("Level: " + level);

        // Set click listener for the finish button
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to MainActivity
                Intent intent = new Intent(ActivitySummary.this, MainActivity.class);
                startActivity(intent);
                // Finish this activity
                finish();
            }
        });
    }
}
