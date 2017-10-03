package org.apache.cordova.labs.keyboard;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;
import android.content.res.Configuration;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;

public class Keyboard extends CordovaPlugin {

    public String keyboardShow;
    PagerAdapter adapterViewPager;
    ViewPager viewPager;

    private boolean wasOpened;
    private final int DefaultKeyboardDP = 100;
    // Lollipop includes button bar in the root. Add height of button bar (48dp) to maxDiff

    private View getAndroidViewByCordovaWebView() {
        if (webView instanceof View) {
            return ((View) webView);
        } else {
            return webView.getView();
        }
    }

    public int isKeyBoardShowing() {
        int heightDiff = getAndroidViewByCordovaWebView().getRootView().getHeight() - getAndroidViewByCordovaWebView().getHeight();
        if (100 < heightDiff) {
            return heightDiff;
        } else {
            return heightDiff;
        }
    }

    public float dpToPx(int dp) {
        //return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getAndroidViewByCordovaWebView().getRootView().getResources().getDisplayMetrics());
        //return dp * getAndroidViewByCordovaWebView().getRootView().getResources().getDisplayMetrics().density;
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getAndroidViewByCordovaWebView().getRootView().getResources().getDisplayMetrics());
    }

    public final void setKeyboardListener() {
        final View activityRootView = getAndroidViewByCordovaWebView().getRootView();
        final View cordovaView = getAndroidViewByCordovaWebView();
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private final Rect r = new Rect();

            @Override
            public void onGlobalLayout() {
                activityRootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = activityRootView.getHeight();
                int keyboardHeight = screenHeight - (r.bottom - r.top);

                Log.d("Keyboard APIZEE", "Active  KEYBOARD HEIGHT->" + keyboardHeight + " SCREEN HEIGHT=" + screenHeight);
                //String event = String.format("javascript:cordova.fireDocumentEvent('yourEventHere', { 'param1': '%s' });", "some string for param1");
                try {
                    webView.loadUrl("javascript:Keyboard.keyboardEventShow(" + screenHeight + "," + keyboardHeight + ");");
                } catch (Exception e) {
                    Log.d("APIZEE", "CANNNOT SET MESSAGE APIZEE OK");
                }
            }
        });
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        Activity activity = this.cordova.getActivity();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        Log.w("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Apizee execute0 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", action);
        View view;
        try {
            view = (View) webView.getClass().getMethod("getView").invoke(webView);
        } catch (Exception e) {
            view = (View) webView;
        }
        Log.w("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Apizee execute1 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", Integer.toString(this.isKeyBoardShowing()));
        if ("showing".equals(action)) {
            if (imm.isAcceptingText()) {
                callbackContext.success("100");
            } else {
                callbackContext.success("0");
            }
            Log.w("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Apizee execute2 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", Integer.toString(this.isKeyBoardShowing()));

            return true;
        } else if ("show".equals(action)) {
            imm.showSoftInput(view, 0);
            callbackContext.success();
            return true;
        } else if ("hide".equals(action)) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            callbackContext.success();
            return true;
        } else if ("init".equals(action)) {
            setKeyboardListener();
            callbackContext.success();
            return true;
        }
        Log.w("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Apizee execute false ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", action);
        callbackContext.error(action + " is not a supported action");
        return false;
    }
}
