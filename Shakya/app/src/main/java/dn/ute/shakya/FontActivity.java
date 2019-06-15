package dn.ute.shakya;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import dn.ute.shakya.Interface.OnFontSelectedListening;
import dn.ute.shakya.adapter.FontAdapter;
import dn.ute.shakya.common.FontManager;

public class FontActivity extends AppCompatActivity {

    TextView tv_notify;
    ImageView img_back;
    RecyclerView rv_font;
    LinearLayout ln_font, ln_review;
    RelativeLayout rl_title;
    List<String> lstData = new ArrayList<>();
    FontAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font);

        addView();
        addEvent();
        loadData();
    }

    private void addView(){
        img_back = findViewById(R.id.img_back);
        tv_notify = findViewById(R.id.tv_notify);
        rv_font = findViewById(R.id.rv_font);
        ln_font = findViewById(R.id.ln_font);
        ln_review = findViewById(R.id.ln_review);
        rl_title = findViewById(R.id.rl_title);

        (new FontManager(FontActivity.this)).applyFont(ln_review);
        (new FontManager(FontActivity.this)).applyFont(rl_title);
    }

    private void addEvent(){
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadData(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                tv_notify.setVisibility(View.VISIBLE);
                lstData = new ArrayList<>();
                listAssetFiles("");
                mAdapter = new FontAdapter(FontActivity.this, lstData, new OnFontSelectedListening() {
                    @Override
                    public void onSelected(String fontName) {
                        (new FontManager(FontActivity.this)).applyFontWithFontName(ln_review, fontName);
                        (new FontManager(FontActivity.this)).applyFontWithFontName(rl_title, fontName);
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayoutManager layoutManager = new LinearLayoutManager(FontActivity.this);
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        rv_font.setLayoutManager(layoutManager);
                        rv_font.setHasFixedSize(true);
                        rv_font.setAdapter(mAdapter);
                        tv_notify.setVisibility(View.GONE);
                    }
                });
            }
        });
        thread.start();
    }

    private boolean listAssetFiles(String path) {
        String [] list;
        try {
            list = getAssets().list(path);
            if (list.length > 0) {
                // This is a folder
                for (String file : list) {
                    if (!listAssetFiles(path + "/" + file))
                        return false;
                    else if(file.endsWith(".ttf") || (file.endsWith(".TTF"))) {
                        lstData.add(file.substring(0, file.length() - 4));
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
