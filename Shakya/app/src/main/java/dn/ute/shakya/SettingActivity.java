package dn.ute.shakya;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import dn.ute.shakya.common.Const;
import dn.ute.shakya.common.FontManager;

public class SettingActivity extends AppCompatActivity{
    ImageView img_back;
    TextView tv_duration;
    LinearLayout ln_setDuration;
    TextView tv_wrongRate;
    LinearLayout ln_setWrongRate;
    TextView tv_fontName;
    LinearLayout ln_setFont;
    LinearLayout ln_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        addView();
        addEvent();

        tv_duration.setText(this.getSharedPreferences(Const.DURATION, Context.MODE_PRIVATE).getInt(Const.DURATION, 1) + " seconds");
        tv_wrongRate.setText( "> " + (int)this.getSharedPreferences(Const.WRONG_RATE, Context.MODE_PRIVATE).getFloat(Const.WRONG_RATE, 50) + " percent");
        tv_fontName.setText(this.getSharedPreferences(Const.FONT_NAME, Context.MODE_PRIVATE).getString(Const.FONT_NAME, Const.FONT_DEFAULT));
    }

    private void addView(){
        img_back = findViewById(R.id.img_back);
        tv_duration = findViewById(R.id.tv_duration);
        ln_setDuration = findViewById(R.id.ln_setDuration);
        tv_wrongRate = findViewById(R.id.tv_wrongRate);
        ln_setWrongRate = findViewById(R.id.ln_setWrongRate);
        tv_fontName = findViewById(R.id.tv_fontName);
        ln_setFont = findViewById(R.id.ln_setFont);
        ln_setting = findViewById(R.id.ln_setting);

        FontManager fontManager = new FontManager(this);
        fontManager.applyFont(ln_setting);
    }

    private void addEvent(){
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ln_setDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogNumberPicker();
            }
        });
        ln_setWrongRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogPercent();
            }
        });
        ln_setFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, FontActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showDialogNumberPicker() {
        final Dialog d = new Dialog(SettingActivity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_set_duration);
        LinearLayout ln_setDuration = d.findViewById(R.id.ln_setDuration);
        (new FontManager(this)).applyFont(ln_setDuration);
        Button btn_set = d.findViewById(R.id.btn_set);
        Button btn_cancel = d.findViewById(R.id.btn_cancel);
        final NumberPicker np = d.findViewById(R.id.numberPicker);
        np.setMaxValue(180);
        np.setMinValue(1);
        SharedPreferences sharedPref = this.getSharedPreferences(Const.DURATION, Context.MODE_PRIVATE);
        np.setValue(sharedPref.getInt(Const.DURATION, 1));
        np.setWrapSelectorWheel(false);
        final Activity mActivity = this;
        btn_set.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                int duration = np.getValue();
                SharedPreferences.Editor editor = mActivity.getSharedPreferences(Const.DURATION, Context.MODE_PRIVATE).edit();
                editor.putInt(Const.DURATION, duration);
                editor.commit();
                d.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                tv_duration.setText(mActivity.getSharedPreferences(Const.DURATION, Context.MODE_PRIVATE).getInt(Const.DURATION, 1) + " seconds");
            }
        });
        d.show();

        //Grab the window of the dialog, and change the width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = d.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    public void showDialogPercent() {
        final Dialog d = new Dialog(SettingActivity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_set_wrong_rate);
        LinearLayout ln_setWrongRate = d.findViewById(R.id.ln_setWrongRate);
        (new FontManager(this)).applyFont(ln_setWrongRate);
        Button btn_set = d.findViewById(R.id.btn_set);
        Button btn_cancel = d.findViewById(R.id.btn_cancel);
        final NumberPicker np = d.findViewById(R.id.numberPicker);
        np.setMaxValue(100);
        np.setMinValue(0);
        SharedPreferences sharedPref = this.getSharedPreferences(Const.WRONG_RATE, Context.MODE_PRIVATE);
        np.setValue((int)sharedPref.getFloat(Const.WRONG_RATE, 50));
        np.setWrapSelectorWheel(false);
        final Activity mActivity = this;
        btn_set.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                int wrongRate = np.getValue();
                SharedPreferences.Editor editor = mActivity.getSharedPreferences(Const.WRONG_RATE, Context.MODE_PRIVATE).edit();
                editor.putFloat(Const.WRONG_RATE, wrongRate);
                editor.commit();
                d.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                tv_wrongRate.setText( ">= " + (int)mActivity.getSharedPreferences(Const.WRONG_RATE, Context.MODE_PRIVATE).getFloat(Const.WRONG_RATE, 50) + " percent");

            }
        });
        d.show();

        //Grab the window of the dialog, and change the width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = d.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_fontName.setText(this.getSharedPreferences(Const.FONT_NAME, Context.MODE_PRIVATE).getString(Const.FONT_NAME, Const.FONT_DEFAULT));
        FontManager fontManager = new FontManager(this);
        fontManager.applyFont(ln_setting);
    }
}
