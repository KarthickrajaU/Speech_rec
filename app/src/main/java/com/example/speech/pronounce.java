package com.example.speech;


import static com.example.speech.MainActivity.databasehelper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

public class pronounce extends AppCompatActivity implements TextToSpeech.OnInitListener {
    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private TextView responsetext, question_data, number;
    private ImageView micButton;
    ImageButton speaker;
    FloatingActionButton previous, next;
    TextView score, hint, mic_hint;
    String Curr_question;
    int curr_pos = 0, question_number = 1;
    int length = 0, curr_score = 0;
    final Intent speechRecognizerIntent;
    int response;
    private TextToSpeech tt1;
    Handler handler = new Handler();
    String tmp_Response;
    ArrayList<String> question = new ArrayList<>();
    String utternce_id = "current";

    public pronounce() {
        super(R.layout.activity_pronounce);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

    }

    UtteranceProgressListener utteranceProgressListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pronounce);

        Bundle bundle1 = getIntent().getExtras();
        if (bundle1 != null) {
            this.response = bundle1.getInt("response");

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }
        tt1 = new TextToSpeech(this, this);
        tt1.setOnUtteranceProgressListener(utteranceProgressListener);
        mic_hint = findViewById(R.id.mic_hint);
        speaker = findViewById(R.id.speaker);
        question_data = findViewById(R.id.question_data);
        responsetext = findViewById(R.id.text);
        micButton = findViewById(R.id.button);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        score = findViewById(R.id.score);
        number = findViewById(R.id.number);
        hint = findViewById(R.id.hint);
        question = databasehelper.get_data();
        length = question.size();
        setQuestion();
        StringBuilder sb = new StringBuilder();


        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                mic_hint.setText("");
                micButton.setImageResource(R.drawable.ic_baseline_mic_24_red);

                mic_hint.setHint("speak now...");
            }

            @Override
            public void onBeginningOfSpeech() {
                micButton.setImageResource(R.drawable.ic_baseline_mic_24_red);
                mic_hint.setHint("Listening...");
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                mic_hint.setText("Listening completed");
            }

            @Override
            public void onError(int i) {
                mic_hint.setText("not audible/some technical error.please try again");
                micButton.setImageResource(R.drawable.ic_baseline_mic_24);
            }

            @Override
            public void onResults(Bundle bundle) {
                mic_hint.setText("Listening completed");

                micButton.setImageResource(R.drawable.ic_baseline_mic_24);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String re = data.get(0);
                String res = "Question : " + question_number + "\nAnswer: " + Curr_question + "\nYour voice :" + re + "\n";
                if (response == 1) {
                    responsetext.setText(res);
                } else if (response == 0) {

               /*     sb.append(res);
                    responsetext.setText(sb.toString());*/
                    responsetext.setText(res);
                }
                if (re.equals("")) {
                    Toast.makeText(getApplicationContext(), "can't audible try again", Toast.LENGTH_LONG).show();
                }
                mic_hint.setText("tap here to listen");
                check(re);
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }


            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                micButton.setImageResource(R.drawable.ic_baseline_mic_24_red);
                speechRecognizer.startListening(speechRecognizerIntent);
            }
        });

        previous.setOnClickListener(view -> gotopre());

        next.setOnClickListener(view -> goto_next());


        speaker.setOnClickListener(view -> {

            String text = Curr_question;


            if (!text.equals("")) {
                tt1.speak(text, TextToSpeech.QUEUE_FLUSH, null, utternce_id);

                speaker.setImageResource(R.drawable.ic_baseline_volume_up_24_red);


            } else {
                speaker.setImageResource(R.drawable.ic_baseline_volume_up_24);

            }
        });
        tt1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {

            }

            @Override
            public void onDone(String s) {
                new Thread() {
                    @Override
                    public void run() {
                        pronounce.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                handler.removeCallbacksAndMessages(null);
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                                    @Override
                                    public void run() {

                                        speaker.setImageResource(R.drawable.ic_baseline_volume_up_24);
                                        speechRecognizer.startListening(speechRecognizerIntent);
                                    }
                                }, 2000);

                            }
                        });
                    }
                }.start();

            }

            @Override
            public void onError(String s) {

            }
        });


    }


    private boolean checkspeakerstatus(TextToSpeech tt1) {
        handler.postDelayed((Runnable) () -> {

        }, 1000);
        return tt1.isSpeaking();
    }

    private void goto_next() {

        if (curr_pos + 1 > length - 1) {
            //next.setClickable(false);
        } else {
            curr_pos = curr_pos + 1;
            setQuestion();
        }
    }

    private void gotopre() {
        if (curr_pos > 0) {
            curr_pos = curr_pos - 1;
            setQuestion();
        }
    }

    private void setQuestion() {


        if (curr_pos < length && curr_pos >= 0) {
            Curr_question = question.get(curr_pos);
            if (response == 0) {

                hint.setText(R.string.pro_hint);
                question_data.setVisibility(View.VISIBLE);
                question_data.setText(Curr_question);
                if (curr_pos != 0)
                    speechRecognizer.startListening(speechRecognizerIntent);
            } else if (response == 1) {
                hint.setText(R.string.comp_hint);
                speaker.setVisibility(View.VISIBLE);
                previous.setVisibility(View.GONE);

            }
            question_number = curr_pos + 1;
            number.setText("Question : " + question_number);


        } else {
            speechRecognizer.stopListening();
            micButton.setImageResource(R.drawable.ic_baseline_mic_24);
        }

    }

    public void check(String responsetext) {
        if (!responsetext.equals("a")) {
            String s;


            s = Curr_question;
            String s1 = s.replaceAll("[^a-zA-Z0-9/s]", "");
            boolean a = Pattern.compile(Pattern.quote(responsetext), Pattern.CASE_INSENSITIVE).matcher(s1).find();

            if (a) {
                curr_score++;
            }
            score.setText("SCORE : " + String.valueOf(curr_score) + "/" + length);
            //  goto_next();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        curr_pos = 0;
        length = 0;
        question_number = 1;
        curr_score = 0;
        super.onStop();
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {


            int result = tt1.setLanguage(Locale.ENGLISH);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speaker.setEnabled(true);

            }

        } else {
            Toast.makeText(getApplicationContext(), "text to speech not enabled", Toast.LENGTH_LONG).show();
            Log.e("TTS", "Initilization Failed!");
        }
    }

    public void previous_action(View view) {
        gotopre();
    }

    public void next_action(View view) {
        goto_next();
    }
}
