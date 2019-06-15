package dn.ute.shakya.adapter;

import android.app.Dialog;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import dn.ute.shakya.Interface.OnAdapterListening;
import dn.ute.shakya.R;
import dn.ute.shakya.common.FontManager;
import dn.ute.shakya.common.Format;
import dn.ute.shakya.database.TableLesson;
import dn.ute.shakya.database.TableWord;
import dn.ute.shakya.models.Lesson;
import dn.ute.shakya.models.Word;

public class ObjectWordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Word> lstData;
    Context mContext = null;
    OnAdapterListening mListening = null;
    TextToSpeech textToSpeech;

    public ObjectWordAdapter(Context mContext, List<Word> lstData, OnAdapterListening mListening) {
        this.lstData = lstData;
        this.mListening = mListening;
        this.mContext = mContext;
        initTextToSpeech();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_word, parent, false);
        viewHolder = new ObjectWordAdapter.WordViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ObjectWordAdapter.WordViewHolder vh = (ObjectWordAdapter.WordViewHolder) holder;
        Word word = lstData.get(position);

        vh.tv_wordId.setText(word.getId() + "");
        vh.tv_word.setText((position + 1) + ". " + word.getContent());

        vh.ln_wordItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv_word = view.findViewById(R.id.tv_word);
                textToSpeech.speak(tv_word.getText().toString().split(" ")[1], TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        vh.img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout ln = (LinearLayout) ((ViewGroup) view.getParent()).getParent();
                long wordId = getWordId(ln);
                editWord(wordId);
            }
        });

        vh.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout ln = (LinearLayout) ((ViewGroup) view.getParent()).getParent();
                long wordId = getWordId(ln);
                removeWord(wordId);
                Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
            }
        });

        (new FontManager(mContext)).applyFont(vh.ln_wordItem);
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

    private void editWord(final long wordId) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_add_word);

        final EditText edt_word = dialog.findViewById(R.id.edt_word);

        LinearLayout ln_addWord = dialog.findViewById(R.id.ln_addWord);
        (new FontManager(mContext)).applyFont(ln_addWord);
        Button btn_save = dialog.findViewById(R.id.btn_save);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);

        final TableWord tableWord = new TableWord(mContext);
        edt_word.setText(tableWord.getWord(wordId).getContent());

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListening != null) mListening.onReloadUI(false);
                dialog.dismiss();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_word.getText().toString().trim().equals("")) {
                    Toast.makeText(mContext, "Please enter content!!!", Toast.LENGTH_SHORT).show();
                    edt_word.setText("");
                    edt_word.requestFocus();
                    return;
                }

                Word word = tableWord.getWord(wordId);
                word = tableWord.getWordWithContent(word.getLessonId(), word.getContent());
                if(word != null){
                    Toast.makeText(mContext,"This word already exist!", Toast.LENGTH_SHORT).show();
                    edt_word.requestFocus();
                    return;
                }

                word = tableWord.getWord(wordId);
                word.setContent(Format.formatWord(edt_word.getText().toString().trim()));
                tableWord.updateWord(word);
                Toast.makeText(mContext, "Done!", Toast.LENGTH_SHORT).show();
                for (Word w : lstData) {
                    if (w.getId() == wordId) {
                        w.setContent(word.getContent());
                        break;
                    }
                }
                if (mListening != null) mListening.onReloadUI(false);
                notifyDataSetChanged();
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

    private boolean removeWord(long wordId) {
        TableWord tableWord = new TableWord(mContext);
        Word word = tableWord.getWord(wordId);
        if (word == null) return false;

        tableWord.deleteWord(word);
        for (Word w : lstData) {
            if (w.getId() == wordId) {
                lstData.remove(w);
                break;
            }
        }
        if (mListening != null) mListening.onReloadUI(true);
        notifyDataSetChanged();
        return true;
    }

    private long getWordId(View view) {
        TextView tv_wordId = view.findViewById(R.id.tv_wordId);
        long wordId = -1;
        try {
            wordId = Integer.parseInt(tv_wordId.getText().toString());
        } catch (Exception e) {
            wordId = -1;
        }
        return wordId;
    }

    @Override
    public int getItemCount() {
        return lstData.size();
    }

    public class WordViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ln_wordItem;
        TextView tv_wordId, tv_word;
        ImageView img_delete, img_edit;

        public WordViewHolder(final View itemView) {
            super(itemView);
            tv_wordId = itemView.findViewById(R.id.tv_wordId);
            tv_word = itemView.findViewById(R.id.tv_word);
            ln_wordItem = itemView.findViewById(R.id.ln_wordItem);
            img_delete = itemView.findViewById(R.id.img_delete);
            img_edit = itemView.findViewById(R.id.img_edit);
        }
    }
}
