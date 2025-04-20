package com.example.voice_baseditineraryplanner;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final int VOICE_INPUT_REQUEST = 1;
    private TextView itineraryText;
    private Button speakButton;

    // Set of activity keywords for activities to add to itinerary
    private final Set<String> activityKeywords = new HashSet<>(Arrays.asList(
            "beach", "museum", "shopping", "temple", "park", "tower", "zoo", "fort", "palace"
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itineraryText = findViewById(R.id.itineraryText);
        speakButton = findViewById(R.id.speakButton);

        speakButton.setOnClickListener(v -> startVoiceInput());
    }

    private void startVoiceInput() {
        // Start voice input (Speech Recognition)
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        startActivityForResult(intent, VOICE_INPUT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_INPUT_REQUEST && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String userInput = results.get(0);
            itineraryText.setText("You said:\n" + userInput);
            generateItinerary(userInput);
        }
    }

    private void generateItinerary(String input) {
        StringBuilder itinerary = new StringBuilder("\n\nPlanned Itinerary:\n");

        Pattern cityPattern = Pattern.compile("to\\s+([A-Z][a-z]+)");
        Matcher cityMatcher = cityPattern.matcher(input);
        if (cityMatcher.find()) {
            itinerary.append("• Destination: ").append(cityMatcher.group(1)).append("\n");
        }

        // 2. Extract travel dates (e.g., "5th to 10th")
        Pattern datePattern = Pattern.compile("(\\d{1,2})(st|nd|rd|th)?\\s*(to|until|till|–|-)\\s*(\\d{1,2})(st|nd|rd|th)?");
        Matcher dateMatcher = datePattern.matcher(input);
        if (dateMatcher.find()) {
            itinerary.append("• Dates: ").append(dateMatcher.group()).append("\n");
        }

        // 3. Extract activities/places from user input
        itinerary.append("• Places to Visit:\n");
        for (String keyword : activityKeywords) {
            if (input.toLowerCase().contains(keyword)) {
                itinerary.append("   - ").append(capitalize(keyword)).append("\n");
            }
        }

        // 4. Fallback message if nothing was detected
        if (itinerary.toString().trim().equals("Planned Itinerary:")) {
            itinerary.append("No clear travel details were detected.\n");
        }

        itineraryText.append(itinerary.toString());
    }

    private String capitalize(String word) {
        if (word == null || word.length() == 0) return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}
