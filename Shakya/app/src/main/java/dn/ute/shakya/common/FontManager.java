package dn.ute.shakya.common;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FontManager {
    Context mContext;
    Typeface typeface;

    public FontManager (Context mContext){
        this.mContext = mContext;
    }

    public void applyFont(View v) {
        String fontName = mContext.getSharedPreferences(Const.FONT_NAME, Context.MODE_PRIVATE).getString(Const.FONT_NAME, Const.FONT_DEFAULT);
        typeface= Typeface.createFromAsset(mContext.getAssets(), fontName + ".ttf");
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                applyFont(child);
            }
        } else if (v instanceof TextView) {
            ((TextView) v).setTypeface(typeface);
        } else if(v instanceof Button){
            ((Button) v).setTypeface(typeface);
        }
    }

    public void applyFontWithFontName(View v, String fontName) {
        typeface = Typeface.createFromAsset(mContext.getAssets(),  fontName + ".ttf");
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                applyFont(child);
            }
        } else if (v instanceof TextView) {
            ((TextView) v).setTypeface(typeface);
        } else if(v instanceof Button){
            ((Button) v).setTypeface(typeface);
        }
    }
}
