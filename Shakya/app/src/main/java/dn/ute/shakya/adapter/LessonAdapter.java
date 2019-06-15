package dn.ute.shakya.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.List;

import dn.ute.shakya.Interface.OnAdapterListening;
import dn.ute.shakya.R;
import dn.ute.shakya.ViewLessonActivity;
import dn.ute.shakya.common.FontManager;
import dn.ute.shakya.common.Format;
import dn.ute.shakya.database.TableLesson;
import dn.ute.shakya.database.TableWord;
import dn.ute.shakya.models.Lesson;

public class LessonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    List<Lesson> lstData;
    Context mContext = null;
    OnAdapterListening mListening = null;

    public LessonAdapter(List<Lesson> lstData, OnAdapterListening mListening){
        this.lstData = lstData;
        this.mListening = mListening;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_lesson, parent, false);
        viewHolder = new LessonAdapter.LessonViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final LessonAdapter.LessonViewHolder vh = (LessonAdapter.LessonViewHolder) holder;
        final Lesson lesson = lstData.get(position);

        vh.tv_lessonId.setText(lesson.getId() + "");
        vh.tv_lesson.setText((position + 1) + ". " + lesson.getTitle());
        TableWord tableWord = new TableWord(mContext);
        int countWord = tableWord.getWordsCountWithLessonId(lesson.getId());
        vh.tv_wordCount.setText(countWord + " word");

        vh.ln_lessonItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long lessonId = getLessonId(view);
                Intent intent = new Intent(mContext, ViewLessonActivity.class);
                intent.putExtra("lessonId", lessonId);
                mContext.startActivity(intent);
            }
        });

        vh.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Do you want to delete this lesson?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {
                                LinearLayout ln = (LinearLayout) ((ViewGroup) view.getParent()).getParent();
                                long lessonId = getLessonId(ln);
                                removeLesson(lessonId);
                                Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });

        vh.img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout ln = (LinearLayout) ((ViewGroup) view.getParent()).getParent();
                long lessonId = getLessonId(ln);
                editLession(lessonId);
            }
        });

        FontManager fontManager = new FontManager(mContext);
        fontManager.applyFont(vh.ln_lessonItem);
    }

    private void editLession(final long lessonId){
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_add_lesson);

        final EditText edt_title = dialog.findViewById(R.id.edt_title);

        LinearLayout ln_addLesson = dialog.findViewById(R.id.ln_addLesson);
        (new FontManager(mContext)).applyFont(ln_addLesson);
        Button btn_save = dialog.findViewById(R.id.btn_save);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);

        final TableLesson tableLesson = new TableLesson(mContext);
        edt_title.setText(tableLesson.getLesson(lessonId).getTitle());

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListening != null) mListening.onReloadUI(false);
                dialog.dismiss();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_title.getText().toString().trim().equals("")) {
                    Toast.makeText(mContext, "Please enter title!!!", Toast.LENGTH_SHORT).show();
                    edt_title.setText("");
                    edt_title.requestFocus();
                    return;
                }

                Lesson lesson = tableLesson.getLessonWithTitle(edt_title.getText().toString().trim());
                if(lesson != null){
                    Toast.makeText(mContext,"This lesson name already exist!", Toast.LENGTH_SHORT).show();
                    edt_title.requestFocus();
                    return;
                }
                lesson = tableLesson.getLesson(lessonId);
                lesson.setTitle(Format.formatWord(edt_title.getText().toString().trim()));
                tableLesson.updateLesson(lesson);
                Toast.makeText(mContext, "Done!", Toast.LENGTH_SHORT).show();
                for (Lesson l: lstData) {
                    if(l.getId() == lessonId){
                        l.setTitle(lesson.getTitle());
                        break;
                    }
                }
                if(mListening != null) mListening.onReloadUI(false);
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

    private boolean removeLesson(long lessonId){
        TableLesson tableLesson = new TableLesson(mContext);
        TableWord tableWord = new TableWord(mContext);
        Lesson lesson = tableLesson.getLesson(lessonId);
        if(lesson == null) return false;

        tableLesson.deleteLesson(lesson);
        tableWord.deleteWordWithLessonId(lessonId);
        for (Lesson l: lstData) {
            if(l.getId() == lessonId) {
                lstData.remove(l);
                break;
            }
        }
        if(mListening != null) mListening.onReloadUI(true);
        notifyDataSetChanged();
        return true;
    }

    private long getLessonId(View view){
        TextView tv_lessonId = view.findViewById(R.id.tv_lessonId);
        long lessonId = -1;
        try {
            lessonId = Integer.parseInt(tv_lessonId.getText().toString());
        }
        catch (Exception e){
            lessonId = -1;
        }
        return lessonId;
    }

    @Override
    public int getItemCount() {
        return lstData.size();
    }

    public class LessonViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ln_lessonItem;
        TextView tv_lessonId, tv_lesson, tv_wordCount;
        ImageView img_delete, img_edit;

        public LessonViewHolder(final View itemView) {
            super(itemView);
            ln_lessonItem = itemView.findViewById(R.id.ln_lessonItem);
            tv_lessonId = itemView.findViewById(R.id.tv_lessonId);
            tv_lesson = itemView.findViewById(R.id.tv_lesson);
            tv_wordCount = itemView.findViewById(R.id.tv_wordCount);
            img_delete = itemView.findViewById(R.id.img_delete);
            img_edit = itemView.findViewById(R.id.img_edit);
        }
    }
}
