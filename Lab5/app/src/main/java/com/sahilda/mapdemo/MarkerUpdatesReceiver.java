package com.sahilda.mapdemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParseInstallation;

import org.json.JSONException;
import org.json.JSONObject;

public class MarkerUpdatesReceiver extends BroadcastReceiver {

    private static final String TAG = "MarkerUpdatesReceiver";

    public static final String intentAction = "com.parse.push.intent.RECEIVE";

    public interface PushInterface {
        void onMarkerUpdate(PushRequest pushRequest);
    }

    Activity mActivity;

    public MarkerUpdatesReceiver(Activity activity) {
        if (!(activity instanceof PushInterface)) {
            throw new IllegalStateException("activity needs to implement push interface");
        } else {
            mActivity = activity;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.d(TAG, "Receiver intent null");
        } else {
            // Parse push message and handle accordingly
            processPush(context, intent);
        }
    }

    private void processPush(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(intentAction)) {
            try {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                JSONObject customData = new JSONObject(json.getString("customData"));

                PushRequest pushRequest = new PushRequest(customData);

                // don't update markers from yourself
                if (!pushRequest.installationId.equals(ParseInstallation.getCurrentInstallation().getObjectId())) {
                    ((PushInterface) mActivity)
                            .onMarkerUpdate(pushRequest);
                }

                Log.d(TAG, "got action " + action + " with " + customData);
            } catch (JSONException ex) {
                Log.d(TAG, "JSON failed!");
            }
        } else {
            Log.d(TAG, "got action " + action);
        }
    }

}