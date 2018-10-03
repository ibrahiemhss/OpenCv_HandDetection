package selfie_app.ibrahim.selfie.com.selfiecamebyhand;

import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;

public class ESAlertDialog {
    public MainActivity mActivity;
    public String mAlertTex;
    public String mAlertTitle;
    public OnKeyListener mKeyListener;
    public OnClickListener mNegativelistener;
    public String mNnegativeText;
    public String mPositiveText;
    public OnClickListener mPositivelistener;

    public ESAlertDialog(MainActivity activity) {
        this.mPositivelistener = null;
        this.mNegativelistener = null;
        this.mAlertTitle = "set your Alert Title";
        this.mAlertTex = "set your Alert Text";
        this.mNnegativeText = "set your Nnegative text";
        this.mPositiveText = "set your Positive Text";
        this.mKeyListener = null;
        this.mActivity = activity;
    }

    public ESAlertDialog() {
        this.mPositivelistener = null;
        this.mNegativelistener = null;
        this.mAlertTitle = "set your Alert Title";
        this.mAlertTex = "set your Alert Text";
        this.mNnegativeText = "set your Nnegative text";
        this.mPositiveText = "set your Positive Text";
        this.mKeyListener = null;
        this.mActivity = null;
    }

    public void setActivity(MainActivity activity) {
        this.mActivity = activity;
    }

    public void setPositiveButton(String positiveText, OnClickListener listener) {
        this.mPositivelistener = listener;
        this.mPositiveText = positiveText;
    }

    public void setNegativeButton(String negativeText, OnClickListener listener) {
        this.mNegativelistener = listener;
        this.mNnegativeText = negativeText;
    }

    public void setAlertTitle(String alertTitle) {
        this.mAlertTitle = alertTitle;
    }

    public void setAlertText(String alertText) {
        this.mAlertTex = alertText;
    }

    public void setKeyListener(OnKeyListener keyListener) {
        this.mKeyListener = keyListener;
    }

    public void triggerAlertDialog() {
        if (this.mActivity != null) {
          //  this.mActivity.showAlert(this);
        }
    }
}
