package dn.ute.shakya.speechrecognition;

import android.content.Context;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;

public class SpeechRecognitionListener implements RecognitionListener
{
    private onResultsReady mListener;
    private Context mContext;

    public SpeechRecognitionListener(Context mContext, onResultsReady mListener){
        this.mListener = mListener;
        this.mContext = mContext;
    }

    @Override
    public void onBeginningOfSpeech() {}

    @Override
    public void onBufferReceived(byte[] buffer) { }

    @Override
    public void onEndOfSpeech() {}

    @Override
    public synchronized void onError(int error) {
        if (error == SpeechRecognizer.ERROR_NETWORK) {
            ArrayList<String> errorList = new ArrayList<String>(1);
            errorList.add("STOPPED LISTENING");
            if (mListener != null) {
                mListener.onResults(errorList);
            }
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params) {}

    @Override
    public void onPartialResults(Bundle partialResults) {
        if (partialResults != null && mListener != null) {
            ArrayList<String> texts = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            mListener.onStreamingResult(texts);
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {}

    @Override
    public void onResults(Bundle results)  {
        if (results != null && mListener != null) {
            ArrayList<String> ahihi = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            mListener.onResults(ahihi);
        }
    }

    @Override
    public void onRmsChanged(float rmsdB) {}
}