package dn.ute.shakya;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import dn.ute.shakya.adapter.ResultAdapter;
import dn.ute.shakya.common.Const;
import dn.ute.shakya.common.FontManager;
import dn.ute.shakya.database.TableAnalysis;
import dn.ute.shakya.models.AnalysisItem;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout ln_result;
    ImageView img_back, img_retry;
    TextView tv_scores;
    RecyclerView rv_result;
    ResultAdapter mAdapter;
    ArrayList<String> lstData = new ArrayList<>();
    ArrayList<String> lstResult = new ArrayList<>();
    long lessonId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        addView();
        addEvent();
        checkScores();
    }

    private void addView(){
        img_back = findViewById(R.id.img_back);
        img_retry = findViewById(R.id.img_retry);
        tv_scores = findViewById(R.id.tv_scores);
        rv_result = findViewById(R.id.rv_result);
        ln_result = findViewById(R.id.ln_result);
        (new FontManager(this)).applyFont(ln_result);

        lstData = new ArrayList<>();
        lstResult = new ArrayList<>();
        receiveData();

        mAdapter = new ResultAdapter(this, lstData, lstResult);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_result.setLayoutManager(layoutManager);
        rv_result.setHasFixedSize(true);
        rv_result.setAdapter(mAdapter);
    }

    private void addEvent(){
        img_back.setOnClickListener(this);
        img_retry.setOnClickListener(this);
    }

    private void receiveData(){
        Intent intent = getIntent();
        if (intent != null) {
            lessonId = getIntent().getLongExtra("lessonId", -1);
            if (intent.getSerializableExtra("lstData") != null) {
                lstData = (ArrayList<String>) getIntent().getSerializableExtra("lstData");
            }
            if (intent.getSerializableExtra("lstResult") != null) {
                lstResult = (ArrayList<String>) getIntent().getSerializableExtra("lstResult");
            }
        }
    }

    private void checkScores(){
        int scores = 0;
        for(int i = 0; i < lstData.size(); i++){
            String word = lstData.get(i).trim().toUpperCase();
            String answer = i < lstResult.size()? lstResult.get(i).trim().toUpperCase() : Const.DEFAULTANSWER;
            if(word.equals(answer) || removeAllNonWordCharacters(word).equals(removeAllNonWordCharacters(answer))){
                scores++;
                analysis(lstData.get(i), false);
            }
            else {
                analysis(lstData.get(i), true);
            }
        }
        tv_scores.setText(scores + "/" + lstData.size());
    }

    private void analysis(String content, boolean isWrong){
        TableAnalysis tableAnalysis = new TableAnalysis(this);
        AnalysisItem analysisItem = tableAnalysis.getItemWithContent(content);
        if(analysisItem == null){
            analysisItem = new AnalysisItem(System.currentTimeMillis(), content, 0, 1);
            tableAnalysis.addItem(analysisItem);
        }
        else {
            analysisItem.setNumberOfTimeIsWrong(isWrong);
            tableAnalysis.updateItem(analysisItem);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.img_back:
                intent = new Intent(ResultActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                this.finish();
                break;
            case R.id.img_retry:
                if(lessonId == 0){
                    intent = new Intent(ResultActivity.this, AnalysisActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    this.finish();
                    break;
                }
                intent = new Intent(ResultActivity.this, ViewLessonActivity.class);
                intent.putExtra("lessonId", lessonId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                this.finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            Intent intent = new Intent(ResultActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
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
