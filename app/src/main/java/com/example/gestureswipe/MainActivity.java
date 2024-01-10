package com.example.gestureswipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import java.util.Locale;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, OnInitListener , OnUtteranceCompletedListener {
    private static final String TAG = "Swipe Position";
    private float x1, x2, y1, y2;
    private static int MIN_DISTANCE = 150;
    private GestureDetector gestureDetector;

    private TextToSpeech textToSpeech;

    private Handler handler;

    private boolean shouldContinueSpeech = true;
    private static final int SPEECH_INTERVAL = 10000; // 10 seconds in milliseconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialise gesturedetector
        this.gestureDetector = new GestureDetector(MainActivity.this,this);
        textToSpeech = new TextToSpeech(this, this);
        handler = new Handler();

        scheduleSpeech();
    }

    private void scheduleSpeech() {
        // Speak the initial message
        String initialMessage = "Swipe right for object detection, swipe left for currency detection.";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(initialMessage, TextToSpeech.QUEUE_FLUSH, null, null);
        }

        // Schedule subsequent speeches with a 5-second interval
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String message = "Swipe right for object detection, swipe left for currency detection.";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
                }
                // Schedule the next speech
                handler.postDelayed(this, SPEECH_INTERVAL);
            }
        }, SPEECH_INTERVAL);
    }

    //override on touch event

    private boolean isSpeechPaused = false; // Add this variable
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetector.onTouchEvent(event);
        switch(event.getAction()){

            //starting time swipe gesture
            case MotionEvent.ACTION_DOWN:
                x1= event.getX();
                y1= event.getY();
                break;

            //ending time swipe gesture
            case MotionEvent.ACTION_UP:
                x2= event.getX();
                y2= event.getY();


            //getting value for horizontal swipe
                float valueX = x2 - x1;

            //getting value for vertical swipe
                float valueY = y2 - y1;

            if (Math.abs(valueX) > MIN_DISTANCE)
            {
                //detect left to right swipe
                if (x2>x1)
                {
                    Toast.makeText(this, "Right is swiped", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Right Swiped");

                    openRightApp();
                }

                else
                {
                    //detect right to left swipe
                    Toast.makeText(this, "left is Swiped", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Left Swiped");

                    openAnotherApp();

                }
            }
                // Stop speech when a swipe is detected
            if (textToSpeech != null && textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                    isSpeechPaused = true;
                    handler.removeCallbacksAndMessages(null);
                    shouldContinueSpeech = false;
            }

            else if(Math.abs(valueY) > MIN_DISTANCE)
            {
                if(y2>y1)
                {
                    Toast.makeText(this, "Bottom Swipe", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Bottom Swiped");
                }
                
                else
                {
                    Toast.makeText(this, "Top Swipe", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Top Swiped");
                }

            }
        }

        return super.onTouchEvent(event);
    }

    private void openRightApp() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("org.tensorflow.lite.examples.objectdetection","org.tensorflow.lite.examples.objectdetection.MainActivity"));
        startActivity(intent);

        finish();

    }

    private void openAnotherApp() {
//        String packageName = "org.tensorflow.lite.examples.classification"; // Replace with the package name of the app you want to open
//        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
//
//        if (intent != null) {
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        } else {
//            // The app is not installed, or there is an issue with the package name
//            Toast.makeText(this, "The app is not installed or the package name is incorrect.", Toast.LENGTH_SHORT).show();
//        }

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("com.akash2099.blindassistant","com.akash2099.blindassistant.MainActivity"));
        startActivity(intent);

        onSwipeDetected();

        shouldContinueSpeech = false;

        finish();

    }





    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        // Stop speech when a swipe is detected
        textToSpeech.stop();
        return true;

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(new Locale("en", "IN")); // Set the desired language

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "TTS language is not available or not supported.");
            } else {
                // Speak the initial message when TTS is ready
                String initialMessage = "Swipe right for object detection, swipe left for currency detection.";
                HashMap<String, String> params = new HashMap<>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "initialMessage");
                textToSpeech.setOnUtteranceCompletedListener(this);
                textToSpeech.speak(initialMessage, TextToSpeech.QUEUE_FLUSH, params);
            }
        } else {
            Log.e(TAG, "TTS initialization failed.");
        }
    }



    @Override
    public void onUtteranceCompleted(String utteranceId) {

    }



    public void onSwipeDetected() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        // Handle the swipe event here, e.g., start object detection or currency detection.
    }

    // ...

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}
