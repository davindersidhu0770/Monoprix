package com.production.monoprix.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;

public class ForceUpdateChecker {
    private Context context;

    public static final String KEY_STORE_URL = "android_force_update_store_url";
    public static final String KEY_FORCE_REQUIRED = "force_update_android";
    public static final String KEY_SOFT_REQUIRED = "soft_update_android";
    public static final String KEY_CURRENT_VERSION = "android_app_version";
    public static final String KEY_MESSAGE = "update_message_android";
    private OnUpdateNeededListener onUpdateNeededListener;

    public interface OnUpdateNeededListener {
        void onUpdateNeeded(String updateMsg, boolean isSoft, boolean isForce, String storeUrl);
    }

    public static Builder with(@NonNull Context context) {
        return new Builder(context);
    }

    public ForceUpdateChecker(@NonNull Context context,
                              OnUpdateNeededListener onUpdateNeededListener) {
        this.context = context;
        this.onUpdateNeededListener = onUpdateNeededListener;
    }

    public void check() {


        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        String currentVersion1 = remoteConfig.getString(KEY_CURRENT_VERSION);

//        Log.d("2Jan", "currentVersion: " + currentVersion1 + "App version :" + getAppVersion(context));
        Log.d("2Jan", "Force update val : " + remoteConfig.getBoolean(KEY_FORCE_REQUIRED) + "Soft " + remoteConfig.getBoolean(KEY_SOFT_REQUIRED));
        if (remoteConfig.getBoolean(KEY_FORCE_REQUIRED) || remoteConfig.getBoolean(KEY_SOFT_REQUIRED)) {
            String currentVersion = remoteConfig.getString(KEY_CURRENT_VERSION);
            String appVersion = getAppVersion(context);
            String updateMsg = remoteConfig.getString(KEY_MESSAGE);
            boolean softUpdate = remoteConfig.getBoolean(KEY_SOFT_REQUIRED);
            boolean forceUpdate = remoteConfig.getBoolean(KEY_FORCE_REQUIRED);

            Log.d("18Jan: "," softUpdate "+ softUpdate+ "forceUpdate "+forceUpdate +" appVersion "+appVersion+"FirebaseCurrentVersion "+currentVersion);

            String storeUrl = remoteConfig.getString(KEY_STORE_URL);
            int verres = compareVersionNames(currentVersion, appVersion);

            Log.d("18Jan: "," verres "+ verres);

            if (verres == 1 && onUpdateNeededListener != null) {
                onUpdateNeededListener.onUpdateNeeded(updateMsg, softUpdate, forceUpdate, storeUrl);
            }
        }

    }

    public int compareVersionNames(String oldVersionName, String newVersionName) {
        int res = 0;

        String[] oldNumbers = oldVersionName.split("\\.");
        String[] newNumbers = newVersionName.split("\\.");

        // To avoid IndexOutOfBounds
        int maxIndex = Math.min(oldNumbers.length, newNumbers.length);

        for (int i = 0; i < maxIndex; i++) {
            int oldVersionPart = Integer.valueOf(oldNumbers[i]);
            int newVersionPart = Integer.valueOf(newNumbers[i]);

            if (oldVersionPart < newVersionPart) {
                res = -1;
                break;
            } else if (oldVersionPart > newVersionPart) {
                res = 1;
                break;
            }
        }

        // If versions are the same so far, but they have different length...
        if (res == 0 && oldNumbers.length != newNumbers.length) {
            res = oldNumbers.length > newNumbers.length ? 1 : -1;
        }

        // -1 if oldVersionName is less than newVersionName,
        // 1 if oldVersionName is greater than newVersionName,
        return res;
    }


    private String getAppVersion(Context context) {
        String result = "";

        try {
            result = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("forceupdate", e.getMessage());
        }

        return result;
    }

    public static class Builder {

        private Context context;
        private OnUpdateNeededListener onUpdateNeededListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder onUpdateNeeded(OnUpdateNeededListener onUpdateNeededListener) {
            this.onUpdateNeededListener = onUpdateNeededListener;
            return this;
        }

        public ForceUpdateChecker build() {
            return new ForceUpdateChecker(context, onUpdateNeededListener);
        }

        public ForceUpdateChecker check() {
            ForceUpdateChecker forceUpdateChecker = build();
            forceUpdateChecker.check();

            return forceUpdateChecker;
        }
    }
}