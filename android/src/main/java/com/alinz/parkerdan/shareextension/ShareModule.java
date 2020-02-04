package com.alinz.parkerdan.shareextension;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.ArrayList;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.os.Build;
import android.provider.DocumentsContract;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class ShareModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public ShareModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  protected void onNewIntent(Intent intent) {
    Activity mActivity = getCurrentActivity();
    
    if(mActivity == null) { return; }

    mActivity.setIntent(intent);
  }

  @ReactMethod
  public void getFilepath(Callback successCallback) {
    Activity mActivity = getCurrentActivity();
    
    if(mActivity == null) { return; }
    
    Intent intent = mActivity.getIntent();
    String action = intent.getAction();
    String type = intent.getType();

    if (Intent.ACTION_SEND.equals(action) && type != null) {
      if ("text/plain".equals(type)) {
        String input = intent.getStringExtra(Intent.EXTRA_TEXT);
        WritableArray promiseArray = Arguments.createArray();
        promiseArray.pushString(input);
        successCallback.invoke(promiseArray);
      } else if (type.startsWith("image/") || type.startsWith("application/")) {
        Uri fileUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (fileUri != null) {
          String resUri = "file://" + RealPathUtil.getRealPathFromURI(mActivity, fileUri);
          WritableArray promiseArray = Arguments.createArray();
          promiseArray.pushString(resUri);
          successCallback.invoke(promiseArray);
        }
      }else {
        Toast.makeText(reactContext, "Type is not support", Toast.LENGTH_SHORT).show();
      }
    } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
        if (type.startsWith("image/") || type.startsWith("application/")) {
          ArrayList<Uri> fileUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
          if (fileUris != null) {
            WritableArray promiseArray = Arguments.createArray();
            for (Uri uri: fileUris) {
              String resUri = "file://" + RealPathUtil.getRealPathFromURI(mActivity, uri);
              promiseArray.pushString(resUri);
            }
            successCallback.invoke(promiseArray);
          }
        } else {
          Toast.makeText(reactContext, "Type is not support", Toast.LENGTH_SHORT).show();
        }
    }
  }

  @ReactMethod
  public void clearFilePath() {
    Activity mActivity = getCurrentActivity();
    
    if(mActivity == null) { return; }

    Intent intent = mActivity.getIntent();
    String type = intent.getType();
    if ("text/plain".equals(type)) {
      intent.removeExtra(Intent.EXTRA_TEXT);
    } else if (type.startsWith("image/") || type.startsWith("video/") || type.startsWith("application/")) {
      intent.removeExtra(Intent.EXTRA_STREAM);
    }
  }

  @ReactMethod
  public void openURL(String url) {
      Uri uri = Uri.parse(url);
      Intent intent = new Intent(Intent.ACTION_VIEW, uri);
      intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
      try
      {
          getReactApplicationContext().startActivity(intent);
      }
      catch (ActivityNotFoundException e)
      {
          System.out.println("Error:" + e.getMessage());
      }
  }

  @ReactMethod
  public void close() {
    this.clearFilePath();

    Activity mActivity = getCurrentActivity();

    if(mActivity == null) { return; }

    mActivity.finish();
  }

  @Override
  public String getName() {
    return "ReactNativeShareExtension";
  }
}
