package dn.ute.shakya.adapter;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import dn.ute.shakya.R;
import dn.ute.shakya.common.Const;
import dn.ute.shakya.common.FontManager;

public class ResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    ArrayList<String> lstData;
    ArrayList<String> lstResult;
    Context mContext = null;
    TextToSpeech textToSpeech;

    public ResultAdapter(Context mContext, ArrayList<String> lstData, ArrayList<String> lstResult){
        this.lstData = lstData;
        this.lstResult = lstResult;
        this.mContext = mContext;
        initTextToSpeech();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_result, parent, false);
        viewHolder = new ResultViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ResultViewHolder vh = (ResultViewHolder) holder;
        String word = Const.DEFAULTANSWER;
        String answer = Const.DEFAULTANSWER;
        try {
            word = lstData.get(position);
            answer = lstResult.get(position);
        }
        catch (Exception e){

        }

        vh.tv_word.setText(word);
        vh.tv_answer.setText(answer);

        word = word.trim().toUpperCase();
        answer = answer.trim().toUpperCase();
        if(word.equals(answer) || removeAllNonWordCharacters(word).equals(removeAllNonWordCharacters(answer))){
            vh.tv_answer.setTextColor(mContext.getResources().getColor(R.color.green));
        }
        else {
            vh.tv_answer.setTextColor(mContext.getResources().getColor(R.color.red));
        }

        vh.ln_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv_word = view.findViewById(R.id.tv_word);
                textToSpeech.speak(tv_word.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        (new FontManager(mContext)).applyFont(vh.ln_result);
    }

    private void initTextToSpeech(){
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        textToSpeech = new TextToSpeech(mContext.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
                else {
                    Toast.makeText(mContext, "Feature not supported in your device!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstData.size();
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ln_result;
        TextView tv_word, tv_answer;

        public ResultViewHolder(final View itemView) {
            super(itemView);
            tv_word = itemView.findViewById(R.id.tv_word);
            tv_answer = itemView.findViewById(R.id.tv_answer);
            ln_result = itemView.findViewById(R.id.ln_result);
        }
    }

    private String removeAllNonWordCharacters(String str){
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(str.toLowerCase());

        for(int i = 0; i < sb.length(); i++){
            if(alphabet.indexOf(sb.charAt(i)) == -1){
                sb.deleteCharAt(i);
                i--;
            }
        }
        return sb.toString();
    }
}
