package dn.ute.shakya.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import dn.ute.shakya.Interface.OnFontSelectedListening;
import dn.ute.shakya.R;
import dn.ute.shakya.common.Const;
import dn.ute.shakya.common.FontManager;

public class FontAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<String> lstData;
    OnFontSelectedListening mListening;
    private int lastSelectedPosition = -1;

    public  FontAdapter(Context mContext, List<String> lstData, OnFontSelectedListening mListening) {
        this.mContext = mContext;
        this.lstData = lstData;
        this.mListening = mListening;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_font, parent, false);
        viewHolder = new FontViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FontViewHolder vh = (FontViewHolder) holder;
        String fontName = lstData.get(position);

        try{
            (new FontManager(mContext)).applyFontWithFontName(vh.tv_fontName, fontName);
            vh.tv_fontName.setText(fontName);
            vh.tv_fontName.setTextColor(mContext.getResources().getColor(R.color.black));
            vh.ln_fontItem.setEnabled(true);
            vh.rb_select.setEnabled(true);
        }
        catch (Exception e){
            vh.tv_fontName.setText(fontName + " (ERROR)");
            vh.tv_fontName.setTextColor(mContext.getResources().getColor(R.color.red));
            vh.ln_fontItem.setEnabled(false);
            vh.rb_select.setEnabled(false);
        }

        String str = mContext.getSharedPreferences(Const.FONT_NAME, Context.MODE_PRIVATE).getString(Const.FONT_NAME, Const.FONT_DEFAULT);
        if(fontName.trim().toUpperCase().equals(str.trim().toUpperCase())){
            lastSelectedPosition = position;
        }

        vh.rb_select.setChecked(lastSelectedPosition == position);
    }

    @Override
    public int getItemCount() {
        return lstData.size();
    }

    public class FontViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ln_fontItem;
        TextView tv_fontName;
        RadioButton rb_select;

        public FontViewHolder(View itemView) {
            super(itemView);
            ln_fontItem = itemView.findViewById(R.id.ln_fontItem);
            tv_fontName = itemView.findViewById(R.id.tv_fontName);
            rb_select = itemView.findViewById(R.id.rb_select);

            rb_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateFont(tv_fontName.getText().toString());
                }
            });

            ln_fontItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateFont(tv_fontName.getText().toString());
                }
            });
        }

        private void updateFont(String fontName){
            SharedPreferences.Editor editor = FontAdapter.this.mContext.getSharedPreferences(Const.FONT_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(Const.FONT_NAME, fontName);
            editor.commit();
            lastSelectedPosition = getAdapterPosition();
            notifyDataSetChanged();
            if(mListening != null) mListening.onSelected(fontName);
        }
    }
}
