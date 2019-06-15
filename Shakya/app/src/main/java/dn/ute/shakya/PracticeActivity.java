package dn.ute.shakya;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import dn.ute.shakya.common.Const;
import dn.ute.shakya.common.FontManager;
import dn.ute.shakya.speechrecognition.SpeechRecognizerManager;
import dn.ute.shakya.speechrecognition.onResultsReady;

public class PracticeActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout ln_practice;
    TextView tv_count, tv_timeLeft, tv_word, tv_answer;
    ImageView img_stop;
    ProgressBar progressBarCircle;
    CountDownTimer countDownTimer;
    long timeCountInMilliSeconds = 1000;
    ArrayList<String> lstData = new ArrayList<>();
    ArrayList<String> lstResult = new ArrayList<>();
    long lessonId = -1;
    int index = -1;
    int smooth = 1;
    boolean finish = false;
    SpeechRecognizerManager speechRecognizerManager;
    protected PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        addView();
        addEvent();
        receiveData();
        Collections.shuffle(lstData); //shuffle item

        index = -1;
        timeCountInMilliSeconds = this.getSharedPreferences(Const.DURATION, Context.MODE_PRIVATE).getInt(Const.DURATION, 1)*1000;
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "shakya:screenOn");
        wl.acquire(lstData.size() * timeCountInMilliSeconds + Const.DEFAULT_DELAYTIME);

        setupListening();
        nextWord();
    }

    private void addView() {
        tv_count = findViewById(R.id.tv_count);
        tv_timeLeft = findViewById(R.id.tv_timeLeft);
        tv_word = findViewById(R.id.tv_word);
        tv_answer = findViewById(R.id.tv_answer);
        img_stop = findViewById(R.id.img_stop);
        progressBarCircle = findViewById(R.id.progressBarCircle);
        ln_practice = findViewById(R.id.ln_practice);
        (new FontManager(this)).applyFont(ln_practice);
    }

    private void addEvent() {
        img_stop.setOnClickListener(this);
        tv_answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String word = tv_word.getText().toString().trim().toUpperCase();
                String answer = tv_answer.getText().toString().trim().toUpperCase();
                if (word.equals(answer) || removeAllNonWordCharacters(word).equals(removeAllNonWordCharacters(answer))) {
                    tv_answer.setTextColor(getResources().getColor(R.color.green));
                    countDownTimer.cancel();
                    speechRecognizerManager.stop();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(finish) return;
                            lstResult.add(tv_answer.getText().toString());
                            nextWord();
                        }
                    }, 1500);
                }
                else {
                    tv_answer.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });
    }

    private void receiveData() {
        Intent intent = getIntent();
        if (intent != null) {
            lessonId = getIntent().getLongExtra("lessonId", -1);
            if (intent.getSerializableExtra("lstData") != null) {
                lstData = (ArrayList<String>) getIntent().getSerializableExtra("lstData");
                return;
            }
            return;
        }
    }

    private void nextWord() {
        speechRecognizerManager.stop();
        if (lstData.size() == 0) return;
        index++;
        if (index >= lstData.size()) {
            Intent intent = new Intent(PracticeActivity.this, ResultActivity.class);
            intent.putExtra("lstData", lstData);
            intent.putExtra("lstResult", lstResult);
            intent.putExtra("lessonId", lessonId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            this.finish();
            return;
        }
        finish = false;
        tv_count.setText((index + 1) + "/" + lstData.size());
        tv_word.setText(lstData.get(index));
        tv_answer.setText(Const.DEFAULTANSWER);
        speechRecognizerManager.startListening();
        startCountDownTimer();
    }

    private void startCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        progressBarCircle.setMax((int) timeCountInMilliSeconds / smooth);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / smooth);
        tv_timeLeft.setText(timeCountInMilliSeconds / 1000 + "");
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, smooth) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_timeLeft.setText(millisUntilFinished / 1000 + "");
                progressBarCircle.setProgress((int) (millisUntilFinished / smooth));
            }

            @Override
            public void onFinish() {
                finish = true;
                lstResult.add(tv_answer.getText().toString());
                nextWord();
            }
        };
        countDownTimer.start();
    }

    private void setupListening() {
        speechRecognizerManager = new SpeechRecognizerManager(this, new onResultsReady() {
            @Override
            public void onResults(ArrayList<String> results) {
                if (results != null && results.size() > 0) {
                    if(results.get(0).trim().length() == 0){
                        tv_answer.setText(Const.DEFAULTANSWER);
                        tv_answer.setTextColor(getResources().getColor(R.color.black));
                        return;
                    }
                    tv_answer.setText(results.get(0));
                }
            }

            @Override
            public void onStreamingResult(ArrayList<String> partialResults) {
                if (partialResults != null && partialResults.size() > 0) {
                    if(partialResults.get(0).trim().length() == 0){
                        tv_answer.setText(Const.DEFAULTANSWER);
                        tv_answer.setTextColor(getResources().getColor(R.color.black));
                        return;
                    }
                    tv_answer.setText(partialResults.get(0));
                }
            }
        }, timeCountInMilliSeconds);
    }

    private String removeAllNonWordCharacters(String str) {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(str.toLowerCase());

        for (int i = 0; i < sb.length(); i++) {
            if (alphabet.indexOf(sb.charAt(i)) == -1) {
                sb.deleteCharAt(i);
                i--;
            }
        }
        return sb.toString();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_stop:
                speechRecognizerManager.stop();
                speechRecognizerManager = null;
                countDownTimer.cancel();
                countDownTimer = null;

                Intent intent = new Intent(PracticeActivity.this, ResultActivity.class);
                intent.putExtra("lstData", lstData);
                intent.putExtra("lstResult", lstResult);
                intent.putExtra("lessonId", lessonId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                this.finish();

                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            speechRecognizerManager.stop();
            speechRecognizerManager = null;
            countDownTimer.cancel();
            countDownTimer = null;

            Intent intent = new Intent(PracticeActivity.this, ResultActivity.class);
            intent.putExtra("lstData", lstData);
            intent.putExtra("lstResult", lstResult);
            intent.putExtra("lessonId", lessonId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        super.onPause();
    }
}
