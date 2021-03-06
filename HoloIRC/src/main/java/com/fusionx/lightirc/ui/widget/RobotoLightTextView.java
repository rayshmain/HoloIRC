package com.fusionx.lightirc.ui.widget;

import com.fusionx.lightirc.util.UIUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobotoLightTextView extends TextView {

    public RobotoLightTextView(Context context) {
        super(context);
        if (!isInEditMode()) {
            UIUtils.setRobotoLight(getContext(), this);
        }
    }

    public RobotoLightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            UIUtils.setRobotoLight(getContext(), this);
        }
    }

    public RobotoLightTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            UIUtils.setRobotoLight(getContext(), this);
        }
    }
}