package com.imin.newprinterdemo.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.imin.newprinterdemo.R;

public class EditDialog extends BaseDialog{

    private TextView tvTitle;
    private EditText etText;
    private TextView tvCancel;
    private TextView tvSure;

    public EditDialog(@NonNull Context context) {
        super(context);
    }

    public EditDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected EditDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit_dialog);
        tvTitle = findViewById(R.id.setTitleDialog);
        etText = findViewById(R.id.etText);
        tvCancel = findViewById(R.id.tvCancel);
        tvSure = findViewById(R.id.tvSure);
        tvTitle.setText(title);
        tvCancel.setOnClickListener(v -> {
            if (clickListener !=null){
                clickListener.cancel();
            }
            dismiss();
        });
        etText.setText(string);
        tvSure.setOnClickListener(v -> {
            if (clickListener !=null){
                String inputText = etText.getText().toString().trim();
                if (inputText != null && inputText.length() >0){
                    string = inputText;
                }
                clickListener.sure(string);
            }
            dismiss();
        });
    }
    String string="";

    public EditDialog setEdText(String edText) {
        this.string = edText;
        return this;
    }

}
