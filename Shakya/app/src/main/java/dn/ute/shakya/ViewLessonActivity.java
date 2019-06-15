package dn.ute.shakya;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import dn.ute.shakya.Interface.OnAdapterListening;
import dn.ute.shakya.adapter.LessonAdapter;
import dn.ute.shakya.adapter.ObjectWordAdapter;
import dn.ute.shakya.common.Const;
import dn.ute.shakya.common.FontManager;
import dn.ute.shakya.database.TableLesson;
import dn.ute.shakya.database.TableWord;
import dn.ute.shakya.models.Lesson;
import dn.ute.shakya.models.Word;

public class ViewLessonActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView img_back, img_add;
    LinearLayout ln_start;
    LinearLayout ln_viewLesson;
    TextView tv_title, tv_notify;
    RecyclerView rv_word;
    long lessonId = -1;
    List<Word> lstWord = new ArrayList<>();
    ObjectWordAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lesson);

        addView();
        addEvent();
        receiveData();
    }

    private void addView(){
        img_back = findViewById(R.id.img_back);
        ln_start = findViewById(R.id.ln_start);
        img_add = findViewById(R.id.img_add);
        tv_title = findViewById(R.id.tv_title);
        tv_notify = findViewById(R.id.tv_notify);
        rv_word = findViewById(R.id.rv_word);
        ln_viewLesson = findViewById(R.id.ln_viewLesson);
        (new FontManager(this)).applyFont(ln_viewLesson);
    }

    private void addEvent(){
        img_back.setOnClickListener(this);
        ln_start.setOnClickListener(this);
        img_add.setOnClickListener(this);
    }

    private void receiveData(){
        Intent intent = getIntent();
        if (intent != null) {
            lessonId = getIntent().getLongExtra("lessonId", -1);
        }
        if(lessonId == -1){
            tv_title.setText("null");
            return;
        }

        TableLesson tableLesson = new TableLesson(this);
        tv_title.setText(tableLesson.getLesson(lessonId).getTitle());

        lstWord = new ArrayList<>();
        loadData();
    }

    private void loadData(){
        lstWord.clear();
        TableWord tableWord = new TableWord(this);
        if(tableWord.getWordsWithLessonId(lessonId) != null)
            lstWord = tableWord.getWordsWithLessonId(lessonId);

        if(lstWord.size() == 0)
            tv_notify.setVisibility(View.VISIBLE);
        else tv_notify.setVisibility(View.GONE);


        mAdapter = new ObjectWordAdapter(this, lstWord, new OnAdapterListening() {
            @Override
            public void onReloadUI(boolean reload) {
                if(reload) loadData();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_word.setLayoutManager(layoutManager);
        rv_word.setHasFixedSize(true);
        rv_word.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.img_back:
                intent = new Intent(ViewLessonActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                this.finish();
                break;
            case R.id.ln_start:
                if(lstWord == null || lstWord.size() == 0){
                    Toast.makeText(this, "Empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<String> lstData = new ArrayList<>();
                for (int i = 0; i < lstWord.size(); i++){
                    lstData.add(lstWord.get(i).getContent());
                }
                intent = new Intent(ViewLessonActivity.this, PracticeActivity.class);
                intent.putExtra("lstData", lstData);
                intent.putExtra("lessonId", lessonId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                this.finish();
                break;
            case R.id.img_add:
                showDialogChooseTypeInput();
                break;
        }
    }

    private void showDialogAddWord(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_add_word);

        final EditText edt_word = dialog.findViewById(R.id.edt_word);

        LinearLayout ln_addWord = dialog.findViewById(R.id.ln_addWord);
        (new FontManager(this)).applyFont(ln_addWord);
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
                if (edt_word.getText().toString().trim().equals("")) {
                    Toast.makeText(ViewLessonActivity.this, "Please enter word!!!", Toast.LENGTH_SHORT).show();
                    edt_word.setText("");
                    edt_word.requestFocus();
                    return;
                }
                TableWord tableWord = new TableWord(ViewLessonActivity.this);
                addWord(edt_word.getText().toString());

                Toast.makeText(ViewLessonActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                loadData();
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

    private void showDialogChooseTypeInput(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_choose_type_input);

        LinearLayout ln_chooseTypeInput = dialog.findViewById(R.id.ln_chooseTypeInput);
        (new FontManager(this)).applyFont(ln_chooseTypeInput);
        Button btn_fromFile = dialog.findViewById(R.id.btn_fromFile);
        Button btn_multipleWord = dialog.findViewById(R.id.btn_multipleWord);
        Button btn_oneWord = dialog.findViewById(R.id.btn_oneWord);


        btn_fromFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialFilePicker()
                        .withActivity(ViewLessonActivity.this)
                        .withFilter(Pattern.compile(".*\\.txt$"))
                        .withRequestCode(Const.PICKFILE_REQUEST_CODE)
                        .withHiddenFiles(true)
                        .withTitle("Choose file...")
                        .start();
                dialog.dismiss();
            }
        });

        btn_multipleWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewLessonActivity.this, InputActivity.class);
                startActivityForResult(intent, Const.INPUT_REQUEST_CODE);
                dialog.dismiss();
            }
        });

        btn_oneWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddWord();
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

    private void showDialogChooseTypeInput1(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cách nhập dữ liệu?");
        builder.setPositiveButton("Nhập tay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(ViewLessonActivity.this, InputActivity.class);
                startActivityForResult(intent, Const.INPUT_REQUEST_CODE);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Từ file", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                new MaterialFilePicker()
                        .withActivity(ViewLessonActivity.this)
                        .withFilter(Pattern.compile(".*\\.txt$"))
                        .withRequestCode(Const.PICKFILE_REQUEST_CODE)
                        .withHiddenFiles(true)
                        .withTitle("Choose file...")
                        .start();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        TableWord tableWord = new TableWord(this);
        if (requestCode == Const.PICKFILE_REQUEST_CODE && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            List<String> list = readDataFromFile(filePath);
            if(list.size() == 0 || lessonId == -1) return;

            for (int i = 0; i < list.size(); i++){
                addWord(list.get(i));
            }
        }
        else if (requestCode == Const.INPUT_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> lstData = (ArrayList<String>) data.getSerializableExtra("lstData");
            if(lstData == null || lstData.size() == 0) return;

            for (int i = 0; i < lstData.size(); i++){
                addWord(lstData.get(i));
            }
        }
        loadData();
    }

    private void addWord(String str){
        TableWord tableWord = new TableWord(this);
        String arr[] = str.trim().split(" ");
        for (String content: arr) {
            if(content.trim().length() == 0) continue;
            Word word = new Word(System.currentTimeMillis(), lessonId, content);

            if(tableWord.getWordWithContent(word.getLessonId(), word.getContent()) == null){
                tableWord.addWord(word);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    public List<String> readDataFromFile(String filetPath) {
        List<String> lstData = new ArrayList<>();
        File file = new File(filetPath);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if(line.trim().length() > 0)
                    lstData.add(line.trim());
            }
            br.close();
        } catch (Exception e) {
            lstData.clear();
        }
        return lstData;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(ViewLessonActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
