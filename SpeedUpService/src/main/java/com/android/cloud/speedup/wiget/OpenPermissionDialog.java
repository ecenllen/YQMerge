package com.android.cloud.speedup.wiget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.cloud.speedup.R;

public class OpenPermissionDialog extends Dialog {
    private Activity context;
    private TextView mTitle;
    private int titleRes;
    private String title;
    private ImageView imageView1;
    private int imageView1Res;
    private ImageView imageView2;
    private int imageView2Res;
    private View.OnClickListener listener;


    public OpenPermissionDialog(@NonNull Activity context) {
        super(context, R.style.dialog);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_open_permission);

        initView();

        findViewById(R.id.open_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onClick(v);
                dismiss();
            }
        });
    }

    public OpenPermissionDialog setListener(View.OnClickListener listener) {
        this.listener = listener;
        return this;
    }

    private void initView() {
        imageView1 = findViewById(R.id.iv_image1);
        imageView2 = findViewById(R.id.iv_image2);
        mTitle = findViewById(R.id.tv_title);

        setImageView1(imageView1Res);
        setImageView2(imageView2Res);
        if(!TextUtils.isEmpty(title)) {
            setTitles(title);
        } else {
            setTitles(titleRes);
        }
    }

    private OpenPermissionDialog setImageView1(int res) {
        if(imageView1 != null)
            imageView1.setImageResource(res);
        return this;
    }

    private OpenPermissionDialog setImageView2(int res) {
        if(imageView2 != null) {
            imageView2.setImageResource(res);
        }
        return this;
    }

    private OpenPermissionDialog setTitles(int s) {
        if(mTitle != null) {
            mTitle.setText(context.getText(s));
        }
        return this;
    }

    private OpenPermissionDialog setTitles(String s) {
        if(mTitle != null) {
            mTitle.setText(s);
        }
        return this;
    }

    public OpenPermissionDialog setTitleRes(int titleRes) {
        this.titleRes = titleRes;
        return this;
    }

    public OpenPermissionDialog setTitleRes(String title) {
        this.title = title;
        return this;
    }

    public OpenPermissionDialog setImageView1Res(int imageView1Res) {
        this.imageView1Res = imageView1Res;
        return this;
    }

    public OpenPermissionDialog setImageView2Res(int imageView2Res) {
        this.imageView2Res = imageView2Res;
        return this;
    }
}
