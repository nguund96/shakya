package dn.ute.shakya;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dn.ute.shakya.Interface.OnAdapterListening;
import dn.ute.shakya.adapter.LessonAdapter;
import dn.ute.shakya.common.Const;
import dn.ute.shakya.common.FontManager;
import dn.ute.shakya.database.TableAnalysis;
import dn.ute.shakya.database.TableLesson;
import dn.ute.shakya.models.AnalysisItem;
import dn.ute.shakya.models.Lesson;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout ln_home;
    RecyclerView rv_lesson;
    TextView tv_itemCount;
    ImageView img_setting, img_add;
    LinearLayout ln_analysis;
    List<Lesson> lstLesson = new ArrayList<>();
    LessonAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        addView();
        addEvent();
    }

    private void addView(){
        ln_home = findViewById(R.id.ln_home);
        img_setting = findViewById(R.id.img_setting);
        img_add = findViewById(R.id.img_add);
        rv_lesson = findViewById(R.id.rv_lesson);
        ln_analysis = findViewById(R.id.ln_analysis);
        tv_itemCount = findViewById(R.id.tv_itemCount);

        FontManager fontManager = new FontManager(this);
        fontManager.applyFont(ln_home);

        lstLesson = new ArrayList<>();
        loadData();
    }

    private void addEvent(){
        img_setting.setOnClickListener(this);
        img_add.setOnClickListener(this);
        ln_analysis.setOnClickListener(this);
    }

    private void loadData(){
        float wrongRate = this.getSharedPreferences(Const.WRONG_RATE, Context.MODE_PRIVATE).getFloat(Const.WRONG_RATE, 50);
        TableAnalysis tableAnalysis = new TableAnalysis(this);
        if(tableAnalysis.getItemWithWrongRate(wrongRate) != null)
            tv_itemCount.setText(tableAnalysis.getItemWithWrongRate(wrongRate).size() + " words");
        lstLesson.clear();
        TableLesson tableLesson = new TableLesson(this);
        if(tableLesson.getAllLessons() != null)
            lstLesson = tableLesson.getAllLessons();

        mAdapter = new LessonAdapter(lstLesson, new OnAdapterListening() {
            @Override
            public void onReloadUI(boolean reload) {
                if(reload) loadData();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_lesson.setLayoutManager(layoutManager);
        rv_lesson.setHasFixedSize(true);
        rv_lesson.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.img_setting:
                intent = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.img_add:
                showDialogAddLesson();
                break;
            case R.id.ln_analysis:
                intent = new Intent(HomeActivity.this, AnalysisActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void showDialogAddLesson(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_add_lesson);

        final EditText edt_title = dialog.findViewById(R.id.edt_title);

        LinearLayout ln_addLesson = dialog.findViewById(R.id.ln_addLesson);
        (new FontManager(this)).applyFont(ln_addLesson);
        Button btn_save = dialog.findViewById(R.id.btn_save);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_title.getText().toString().trim().equals("")) {
                    Toast.makeText(HomeActivity.this, "Please enter title!!!", Toast.LENGTH_SHORT).show();
                    edt_title.setText("");
                    edt_title.requestFocus();
                    return;
                }
                TableLesson tableLesson = new TableLesson(HomeActivity.this);
                Lesson lesson = new Lesson(System.currentTimeMillis(), edt_title.getText().toString().trim());

                if(tableLesson.getLessonWithTitle(lesson.getTitle()) != null){
                    Toast.makeText(HomeActivity.this, "Lesson already exist!!!", Toast.LENGTH_SHORT).show();
                    edt_title.requestFocus();
                    return;
                }

                tableLesson.addLesson(lesson);
                Toast.makeText(HomeActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                loadData();
                //rv_lesson.getAdapter().notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        dialog.show();

        //Grab the window of the dialog, and change the width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FontManager fontManager = new FontManager(this);
        fontManager.applyFont(ln_home);
        loadData();
    }
}
