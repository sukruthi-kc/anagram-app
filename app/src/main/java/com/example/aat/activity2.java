package com.example.aat;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class activity2 extends AppCompatActivity {
    public static final String START_MESSAGE = "Find as many words as possible that can be formed by adding one letter to <big>%s</big> (that may or may not contain the substring %s).";
    private AnagramDictionary dictionary;
    private String currentWord;
    private int score = 0;
    private List<String> anagrams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anagrams);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Initialize dictionary
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            dictionary = new AnagramDictionary(new InputStreamReader(inputStream));
        } catch (IOException e) {
            Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG).show();
        }

        // Set up the EditText box to process the content when the user hits 'enter'
        final EditText editText = findViewById(R.id.editText);
        editText.setImeOptions(EditorInfo.IME_ACTION_GO);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO ||
                    (actionId == EditorInfo.IME_NULL && event != null && event.getAction() == KeyEvent.ACTION_DOWN)) {
                processWord(editText);
                return true;
            }
            return false;
        });

        // Animate FloatingActionButton when activity starts
        FloatingActionButton fab = findViewById(R.id.fab);
        animateFab(fab);
    }

    // Method to process word input
    private void processWord(EditText editText) {
        FloatingActionButton fab = findViewById(R.id.fab);
        animateFab(fab); // Trigger animation
        TextView resultView = findViewById(R.id.resultView);
        String word = editText.getText().toString().trim().toLowerCase();
        if (word.isEmpty()) return;

        String color = "#cc0029";
        if (dictionary.isGoodWord(word, currentWord) && anagrams.contains(word)) {
            anagrams.remove(word);
            color = "#00aa29";
            score += 10;
        } else {
            word = "X " + word;
        }
        resultView.append(Html.fromHtml(String.format("<font color=%s>%s</font><BR>", color, word)));
        editText.setText("");
    }

    // Animation for FloatingActionButton
    private void animateFab(FloatingActionButton fab) {
        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(fab, "scaleX", 0.0f, 4.5f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(fab, "scaleY", 0.0f, 4.5f);
        scaleOutX.setInterpolator(new AccelerateInterpolator());
        scaleOutY.setInterpolator(new AccelerateInterpolator());
        scaleOutX.setDuration(500);
        scaleOutY.setDuration(500);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(fab, "scaleX", 1.5f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(fab, "scaleY", 1.5f, 1.0f);
        scaleInX.setInterpolator(new DecelerateInterpolator());
        scaleInY.setInterpolator(new DecelerateInterpolator());
        scaleInX.setDuration(500);
        scaleInY.setDuration(500);

        scaleInX.setStartDelay(500);
        scaleInY.setStartDelay(500);

        scaleInX.start();
        scaleInY.start();
        scaleOutX.start();
        scaleOutY.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_anagrams, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_go_back) {
            // Go back to MainActivity
            Intent intent = new Intent(activity2.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to handle default action (e.g., button click)
    public boolean defaultAction(View view) {
        TextView gameStatus = findViewById(R.id.gameStatusView);
        FloatingActionButton fab = findViewById(R.id.fab);
        EditText editText = findViewById(R.id.editText);
        TextView resultView = findViewById(R.id.resultView);
        if (currentWord == null) {
            currentWord = dictionary.pickGoodStarterWord();
            anagrams = dictionary.getAnagramsWithOneMoreLetter(currentWord);
            gameStatus.setText(Html.fromHtml(String.format(START_MESSAGE, currentWord.toUpperCase(), currentWord)));
            fab.setImageResource(android.R.drawable.ic_menu_help);
            fab.hide();
            resultView.setText("");
            editText.setText("");
            editText.setEnabled(true);
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        } else {
            editText.setText(currentWord);
            editText.setEnabled(false);
            fab.setImageResource(android.R.drawable.ic_media_play);
            currentWord = null;
            resultView.append(TextUtils.join("\n", anagrams));
            gameStatus.append(" Hit 'Play' to start again");
            Intent intent = new Intent(activity2.this, ActivitySummary.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
            // Reset the score
            score = 0;
        }
        return true;
    }
}
