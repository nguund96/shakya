package dn.ute.shakya.adapter;

import android.app.Dialog;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import dn.ute.shakya.Interface.OnAdapterListening;
import dn.ute.shakya.R;
import dn.ute.shakya.common.FontManager;
import dn.ute.shakya.common.Format;
import dn.ute.shakya.database.TableAnalysis;
import dn.ute.shakya.database.TableWord;
import dn.ute.shakya.models.AnalysisItem;
import dn.ute.shakya.models.Lesson;
import dn.ute.shakya.models.Word;

public class AnalysisAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    List<AnalysisItem> lstData;
    Context mContext = null;
    OnAdapterListening mListening = null;
    TextToSpeech textToSpeech;

    public AnalysisAdapter(Context mContext, List<AnalysisItem> lstData, OnAdapterListening mListening){
        this.lstData = lstData;
        Collections.sort(this.lstData);
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
        View v = inflater.inflate(R.layout.item_analysis, parent, false);
        viewHolder = new AnalysisViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final AnalysisViewHolder vh = (AnalysisViewHolder) holder;
        final AnalysisItem analysisItem = lstData.get(position);

//        if(position % 2 == 0){
//            vh.ln_item.setBackgroundColor(mContext.getResources().getColor(R.color.white));
//        }
//        else {
//            vh.ln_item.setBackgroundColor(mContext.getResources().getColor(R.color.demo));
//        }

        vh.tv_id.setText(analysisItem.getId() + "");
        vh.tv_content.setText((position + 1) + ". " + analysisItem.getContent());
        vh.tv_wrongRate.setText("Wrong Rate: " + (int)analysisItem.getWrongRate() + "%");

        vh.ln_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv_content = view.findViewById(R.id.tv_content);
                textToSpeech.speak(tv_content.getText().toString().split(" ")[1], TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        vh.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout ln = (LinearLayout) ((ViewGroup) view.getParent()).getParent();
                long id = getItemId(ln);
                removeItem(id);
                Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
            }
        });

        vh.img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout ln = (LinearLayout) ((ViewGroup) view.getParent()).getParent();
                long id = getItemId(ln);
                editItem(id);
            }
        });

        (new FontManager(mContext)).applyFont(vh.ln_item);
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

    private void editItem(final long id) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_add_word);

        final EditText edt_word = dialog.findViewById(R.id.edt_word);

        LinearLayout ln_addWord = dialog.findViewById(R.id.ln_addWord);
        (new FontManager(mContext)).applyFont(ln_addWord);
        Button btn_save = dialog.findViewById(R.id.btn_save);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);

        final TableAnalysis tableAnalysis = new TableAnalysis(mContext);
        edt_word.setText(tableAnalysis.getItem(id).getContent());

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

                AnalysisItem analysisItem = tableAnalysis.getItemWithContent(edt_word.getText().toString().trim());
                if(analysisItem != null){
                    Toast.makeText(mContext,"This word already exist!", Toast.LENGTH_SHORT).show();
                    edt_word.requestFocus();
                    return;
                }
                analysisItem = tableAnalysis.getItem(id);
                analysisItem.setContent(Format.formatWord(edt_word.getText().toString().trim()));
                tableAnalysis.updateItem(analysisItem);
                Toast.makeText(mContext, "Done!", Toast.LENGTH_SHORT).show();
                for (AnalysisItem a : lstData) {
                    if (a.getId() == id) {
                        a.setContent(analysisItem.getContent());
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

    private boolean removeItem(long id) {
        TableAnalysis tableAnalysis = new TableAnalysis(mContext);
        AnalysisItem analysisItem = tableAnalysis.getItem(id);
        if (analysisItem == null) return false;

        tableAnalysis.deleteItem(analysisItem);
        for (AnalysisItem a : lstData) {
            if (a.getId() == id) {
                lstData.remove(a);
                break;
            }
        }
        if (mListening != null) mListening.onReloadUI(true);
        notifyDataSetChanged();
        return true;
    }

    private long getItemId(View view) {
        TextView tv_id = view.findViewById(R.id.tv_id);
        long id = -1;
        try {
            id = Integer.parseInt(tv_id.getText().toString());
        } catch (Exception e) {
            id = -1;
        }
        return id;
    }

    @Override
    public int getItemCount() {
        return lstData.size();
    }

    public class AnalysisViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ln_item;
        TextView tv_id, tv_content, tv_wrongRate;
        ImageView img_delete, img_edit;

        public AnalysisViewHolder(final View itemView) {
            super(itemView);
            ln_item = itemView.findViewById(R.id.ln_item);
            tv_id = itemView.findViewById(R.id.tv_id);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_wrongRate = itemView.findViewById(R.id.tv_wrongRate);
            img_delete = itemView.findViewById(R.id.img_delete);
            img_edit = itemView.findViewById(R.id.img_edit);
        }
    }
}
