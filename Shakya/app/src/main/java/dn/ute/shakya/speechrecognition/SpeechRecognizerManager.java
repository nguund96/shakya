package dn.ute.shakya.speechrecognition;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

public class SpeechRecognizerManager {
    private final static String TAG = "SpeechRecognizerManager";
    protected SpeechRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;
    private Context mContext;
    protected boolean mIsListening;
    protected String language = "en-US";
    protected long timeout; // 2000 ms

    private onResultsReady mListener;

    public SpeechRecognizerManager(Context context, onResultsReady listener, long timeout)
    {
        try {
            mListener = listener;
        } catch(ClassCastException e) {
            Log.e(TAG, e.toString());
        }

        this.mContext = context;
        this.mIsListening = false;
        this.timeout = timeout;
    }

    public void startListening()
    {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
        mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener(mContext, mListener));

        // Create new intent
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mContext.getPackageName());
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true); // For streaming result
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, timeout);

        if (!mIsListening) {
            mIsListening = true;
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }
    }

    public void stop() {
        if (mIsListening && mSpeechRecognizer != null) {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
            mSpeechRecognizer = null;
        }

        mIsListening = false;
    }

    public void destroy(){
        mIsListening = false;
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
            mSpeechRecognizer = null;
        }

    }

    public boolean ismIsListening() {
        return mIsListening;
    }
}