package com.example.aat;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class AnagramActivity extends AppCompatActivity {

    private TextView wordTextView;
    private EditText guessEditText;
    private Button checkButton;
    private TextView resultTextView;
    private Spinner themeSpinner;

    private DictionaryDatabaseHelper databaseHelper;
    private String originalWord;

    private static final String TAG = "AnagramActivity";
    private int score = 0;
    private int level = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        Button summaryButton = findViewById(R.id.summaryButton);
        summaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch ActivitySummary when the button is clicked
                Intent intent = new Intent(AnagramActivity.this, ActivitySummary.class);
                startActivity(intent);
                intent.putExtra("SCORE", score); // Pass score to ActivitySummary
                startActivity(intent);
            }
        });

        wordTextView = findViewById(R.id.wordTextView);
        guessEditText = findViewById(R.id.guessEditText);
        checkButton = findViewById(R.id.checkButton);
        resultTextView = findViewById(R.id.resultTextView);
        themeSpinner = findViewById(R.id.themeSpinner);

        // Initialize the database helper
        databaseHelper = new DictionaryDatabaseHelper(this);

        // Populate the theme spinner
        populateThemeSpinner();

        // Set listener for checkButton
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGuess();
            }
        });
    }

    private void populateThemeSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.theme_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        themeSpinner.setAdapter(adapter);

        // Set a listener to handle spinner item selections
        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle theme selection
                String selectedTheme = parentView.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Selected theme: " + selectedTheme, Toast.LENGTH_SHORT).show();
                // Fetch words based on the selected theme
                if (selectedTheme.equals("General")) {
                    // Fetch words from anagram_pair.txt for the General theme
                    databaseHelper.populateDatabaseFromRaw(R.raw.anagram_pair);
                } else if (selectedTheme.equals("Animal")) {
                    // Fetch words from theme.txt for the Animal theme
                    databaseHelper.populateDatabaseFromRaw(R.raw.theme);
                }
                // Fetch a random word from the database
                originalWord = databaseHelper.getRandomWord();
                // Display the original word for the user
                wordTextView.setText("Original Word: " + originalWord);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
    }

    private void checkGuess() {
        String userGuess = guessEditText.getText().toString().trim();

        Log.d(TAG, "Original Word: " + originalWord);
        Log.d(TAG, "User Guess: " + userGuess);

        // Generate an anagram of the original word
        String anagram = AnagramGenerator.generateAnagram(originalWord);

        // Check if the user's guess is an anagram of the original word
        if (isAnagram(userGuess, anagram)) {
            // Check if the user's guess is present in the "anpair.txt" file
            if (isWordPresent(userGuess)) {
                resultTextView.setTextColor(Color.BLACK);
                resultTextView.setText("Correct! You guessed the word.");
                score += 10; // Increase score by 10 for correct guess
                checkLevelProgression(); // Check for level progression
            } else {
                resultTextView.setTextColor(Color.BLACK);
                resultTextView.setText("The word is an anagram but not in the list.");
            }
        } else {
            resultTextView.setTextColor(Color.BLACK);
            resultTextView.setText("Incorrect. Try again!");
        }
    }

    private void checkLevelProgression() {
        // Check if the score has reached the threshold for the next level
        if (score >= 100) {
            level++; // Increment level
            score = 0; // Reset score for the next level
            Toast.makeText(this, "Congratulations! You've reached Level " + level, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isAnagram(String str1, String str2) {
        // Check if both strings have the same length
        if (str1.length() != str2.length()) {
            return false;
        }

        // Convert strings to character arrays
        char[] charArray1 = str1.toLowerCase().toCharArray();
        char[] charArray2 = str2.toLowerCase().toCharArray();

        // Sort the character arrays
        Arrays.sort(charArray1);
        Arrays.sort(charArray2);

        // Compare sorted character arrays
        return Arrays.equals(charArray1, charArray2);
    }

    private boolean isWordPresent(String word) {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.anagram_pair);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equalsIgnoreCase(word)) {
                    inputStream.close();
                    return true;
                }
            }
            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Error reading anpair.txt", e);
        }
        return false;
    }
}



