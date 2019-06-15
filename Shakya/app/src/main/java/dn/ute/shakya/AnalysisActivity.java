package dn.ute.shakya;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dn.ute.shakya.Interface.OnAdapterListening;
import dn.ute.shakya.adapter.AnalysisAdapter;
import dn.ute.shakya.common.Const;
import dn.ute.shakya.common.FontManager;
import dn.ute.shakya.database.TableAnalysis;
import dn.ute.shakya.models.AnalysisItem;

public class AnalysisActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout ln_analysis;
    ImageView img_back, img_start;
    TextView tv_notify;
    RecyclerView rv_item;
    List<AnalysisItem> lstItem = new ArrayList<>();
    AnalysisAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        addView();
        addEvent();
        loadData();
    }

    private void addView(){
        img_back = findViewById(R.id.img_back);
        img_start = findViewById(R.id.img_start);
        tv_notify = findViewById(R.id.tv_notify);
        rv_item = findViewById(R.id.rv_item);
        ln_analysis = findViewById(R.id.ln_analysis);
        (new FontManager(this)).applyFont(ln_analysis);
    }

    private void addEvent(){
        img_back.setOnClickListener(this);
        img_start.setOnClickListener(this);
    }

    private void loadData() {
        float wrongRate = this.getSharedPreferences(Const.WRONG_RATE, Context.MODE_PRIVATE).getFloat(Const.WRONG_RATE, 50);
        TableAnalysis tableAnalysis = new TableAnalysis(this);
        if(tableAnalysis.getItemWithWrongRate(wrongRate) != null)
            lstItem = tableAnalysis.getItemWithWrongRate(wrongRate);

        if(lstItem.size() == 0)
            tv_notify.setVisibility(View.VISIBLE);
        else tv_notify.setVisibility(View.GONE);

        mAdapter = new AnalysisAdapter(this, lstItem, new OnAdapterListening() {
            @Override
            public void onReloadUI(boolean reload) {
                if(reload) loadData();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_item.setLayoutManager(layoutManager);
        rv_item.setHasFixedSize(true);
        rv_item.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.img_back:
                intent = new Intent(AnalysisActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                this.finish();
                break;
            case R.id.img_start:
                if(lstItem == null || lstItem.size() == 0){
                    Toast.makeText(this, "Empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<String> lstData = new ArrayList<>();
                for (int i = 0; i < lstItem.size(); i++){
                    lstData.add(lstItem.get(i).getContent());
                }
                intent = new Intent(AnalysisActivity.this, PracticeActivity.class);
                intent.putExtra("lstData", lstData);
                intent.putExtra("lessonId", 0L);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                this.finish();
                break;
        }
    }
}
