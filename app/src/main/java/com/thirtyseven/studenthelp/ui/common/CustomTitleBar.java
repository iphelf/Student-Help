package com.thirtyseven.studenthelp.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.thirtyseven.studenthelp.R;

public class CustomTitleBar extends RelativeLayout {

    private ImageView imageViewBack;
    private TextView textViewTitle;
    private ImageView imageViewRefresh;
    private TextView textViewOperation;

    public CustomTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context, attrs);
    }

    //初始化视图
    private void initView(final Context context, AttributeSet attributeSet) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_titlebar, this);
        imageViewBack = inflate.findViewById(R.id.imageView_back);
        textViewTitle = inflate.findViewById(R.id.textView_title);
        imageViewRefresh = inflate.findViewById(R.id.imageView_refresh);
        textViewOperation = inflate.findViewById(R.id.textView_operation);

        init(context, attributeSet);
    }

    //初始化资源文件
    public void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CustomTitleBar);
        String title = typedArray.getString(R.styleable.CustomTitleBar_title);//标题
        int leftIcon = typedArray.getResourceId(R.styleable.CustomTitleBar_left_icon, R.drawable.ic_arrow_back_ios_24px);//左边图片
        int rightIcon = typedArray.getResourceId(R.styleable.CustomTitleBar_right_icon, R.drawable.ic_refresh_24px);//右边图片
        String rightText = typedArray.getString(R.styleable.CustomTitleBar_right_text);
        int titleBarType = typedArray.getInt(R.styleable.CustomTitleBar_titlebar_type, 1);//标题栏类型,默认为1

        //赋值进去我们的标题栏
        textViewTitle.setText(title);
        imageViewBack.setImageResource(leftIcon);
        imageViewRefresh.setImageResource(rightIcon);
        textViewOperation.setText(rightText);

        //可以传入type值,可自定义判断值
        if (titleBarType == 0) { // No back No refresh
            imageViewBack.setVisibility(GONE);
            imageViewRefresh.setVisibility(GONE);
            textViewOperation.setVisibility(GONE);
        } else if (titleBarType == 1) { // Has back No refresh
            imageViewBack.setVisibility(VISIBLE);
            imageViewRefresh.setVisibility(GONE);
            textViewOperation.setVisibility(GONE);
        } else if (titleBarType == 2) { // Has back and refresh
            imageViewBack.setVisibility(VISIBLE);
            imageViewRefresh.setVisibility(VISIBLE);
            textViewOperation.setVisibility(GONE);
        } else if (titleBarType == 3) { // Has back and text refresh
            imageViewBack.setVisibility(VISIBLE);
            imageViewRefresh.setVisibility(GONE);
            textViewOperation.setVisibility(VISIBLE);
        }
    }

    //左边图片点击事件
    public void setLeftIconOnClickListener(OnClickListener l) {
        imageViewBack.setOnClickListener(l);
    }

    //右边图片点击事件
    public void setRightIconOnClickListener(OnClickListener l) {
        imageViewRefresh.setOnClickListener(l);
    }

    //右边文字点击事件
    public void setRightTextOnClickListener(OnClickListener l) {
        textViewOperation.setOnClickListener(l);
    }

    public void setTitle(String title) {
        textViewTitle.setText(title);
    }

    public void setTitle(@StringRes int title) {
        textViewTitle.setText(title);
    }
}
