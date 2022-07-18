package com.app7ub.movies.utils;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app7ub.movies.AppConfig;
import com.app7ub.movies.LoginSignup;
import com.app7ub.movies.R;
import com.app7ub.movies.Splash;
import com.app7ub.movies.Subscription;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import es.dmoral.toasty.Toasty;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import androidx.core.app.ActivityCompat;

public class HelperUtils {
    private Activity activity;

    public HelperUtils(Activity activity) {
        this.activity = activity;
    }

    public boolean isVpnConnectionAvailable(){
        String iface = "";
        try {
            for (NetworkInterface networkInst : Collections.list(NetworkInterface.getNetworkInterfaces())){
                if (networkInst.isUp())
                    iface = networkInst.getName();
                if ( iface.contains("tun") || iface.contains("ppp") || iface.contains("pptp")) {
                    return true;
                }
            }


        }catch (SocketException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean cr(Activity activity, boolean allowRoot) {
        if(!allowRoot) {
            for(String pathDir : System.getenv("PATH").split(":")){
                if(new File(pathDir, "su").exists()) {
                    return true;
                } else {
                    ApplicationInfo restrictedApp = getRootApp(activity);
                    if (restrictedApp != null){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    static ApplicationInfo getRootApp(Activity activity) {
        ApplicationInfo restrictPackageInfo = null;
        final PackageManager pm = activity.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.thirdparty.superuser") ||
                    packageInfo.packageName.equals("eu.chainfire.supersu") ||
                    packageInfo.packageName.equals("com.noshufou.android.su") ||
                    packageInfo.packageName.equals("com.koushikdutta.superuser") ||
                    packageInfo.packageName.equals("com.zachspong.temprootremovejb") ||
                    packageInfo.packageName.equals("com.ramdroid.appquarantine") ||
                    packageInfo.packageName.equals("com.topjohnwu.magisk")
            ) {
                //restrictPackageName = packageInfo.packageName;
                //restrictPackageName = packageInfo.loadLabel(activity.getPackageManager()).toString();
                restrictPackageInfo = packageInfo;
            }
        }

        return restrictPackageInfo;
    }

    public ApplicationInfo getRestrictApp() {
        ApplicationInfo restrictPackageInfo = null;
        final PackageManager pm = activity.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.guoshi.httpcanary") ||
                    packageInfo.packageName.equals("app.greyshirts.sslcapture") ||
                    packageInfo.packageName.equals("com.guoshi.httpcanary.premium") ||
                    packageInfo.packageName.equals("com.minhui.networkcapture.pro") ||
                    packageInfo.packageName.equals("com.minhui.networkcapture") ||
                    packageInfo.packageName.equals("com.egorovandreyrm.pcapremote") ||
                    packageInfo.packageName.equals("com.packagesniffer.frtparlak") ||
                    packageInfo.packageName.equals("jp.co.taosoftware.android.packetcapture") ||
                    packageInfo.packageName.equals("com.emanuelef.remote_capture") ||
                    packageInfo.packageName.equals("com.minhui.wifianalyzer") ||
                    packageInfo.packageName.equals("com.evbadroid.proxymon") ||
                    packageInfo.packageName.equals("com.evbadroid.wicapdemo") ||
                    packageInfo.packageName.equals("com.evbadroid.wicap") ||
                    packageInfo.packageName.equals("com.luckypatchers.luckypatcherinstaller") ||
                    packageInfo.packageName.equals("ru.UbLBBRLf.jSziIaUjL")
            ) {
                //restrictPackageName = packageInfo.packageName;
                //restrictPackageName = packageInfo.loadLabel(activity.getPackageManager()).toString();
                restrictPackageInfo = packageInfo;
            }
        }

        return restrictPackageInfo;
    }

    public boolean isForeground( String myPackage){
        ActivityManager manager = (ActivityManager) activity.getSystemService(ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > runningTaskInfo = manager.getRunningTasks(1);

        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        Log.e("test", "Background Apps: " + componentInfo.getPackageName());
        return componentInfo != null && componentInfo.getPackageName().equals(myPackage);
    }

    public boolean isAppRunning(Context context, String packageName){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        if (processInfos != null){
            for (ActivityManager.RunningAppProcessInfo processInfo : processInfos){
                if (processInfo.processName.equals(packageName)){
                    return true;
                }
            }
        }
        return false;
    }

    public static void showWarningDialog(Activity context, String title, String message, int Animation) {
        MaterialDialog mDialog = new MaterialDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setAnimation(Animation)
                .setPositiveButton("Exit", R.drawable.ic_baseline_exit, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        System.exit(0);
                        context.finish();
                    }
                })
                .build();

        // Show dialog
        mDialog.show();
    }

    public void showMsgDialog(Activity context, String title, String message, int Animation) {
        MaterialDialog mDialog = new MaterialDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setAnimation(Animation)
                .setPositiveButton("Dismiss", R.drawable.close, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .build();

        // Show dialog
        mDialog.show();
    }

    public void Buy_Premium_Dialog(Activity context, String title, String message, int Animation) {
        MaterialDialog mDialog = new MaterialDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setAnimation(Animation)
                .setPositiveButton("Upgrade", new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        SharedPreferences sharedPreferences = context.getSharedPreferences("SharedPreferences", MODE_PRIVATE);
                        if (sharedPreferences.getString("UserData", null) != null) {
                            dialogInterface.dismiss();
                            Intent subscriptionActivity = new Intent(context, Subscription.class);
                            context.startActivity(subscriptionActivity);
                        } else {
                            dialogInterface.dismiss();
                            Intent loginSingupActivity = new Intent(context, LoginSignup.class);
                            context.startActivity(loginSingupActivity);
                        }

                    }
                })
                .setNegativeButton("Cancel", R.drawable.close, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .build();

        // Show dialog
        mDialog.show();
    }


    public boolean isInt(String s)
    {
        try
        {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex)
        {
            return false;
        }
    }

    public static String getYearFromDate(String date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = null;
        try {
            parsedDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy");
        return df.format(parsedDate);
    }

    public static void setWatchLog(Context context, String user_id, int content_id, int content_type, String apiKey) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url + "/api/add_watchlog.php", response -> {
            try
            {
                Integer.parseInt(response);
                Log.d("test", "Watch Log Added!");
            } catch (NumberFormatException ex)
            {
                Log.d("test", "Watch Log Not Added!");
            }

        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("content_id", String.valueOf(content_id));
                params.put("content_type", String.valueOf(content_type));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", apiKey);
                return params;
            }
        };
        queue.add(sr);
    }

    public static void setViewLog(Context context, String user_id, int content_id, int content_type, String apiKey) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url + "/api/add_viewlog.php", response -> {
            try
            {
                Integer.parseInt(response);
                Log.d("test", "View Log Added!");
            } catch (NumberFormatException ex)
            {
                Log.d("test", "View Log Not Added!");
            }

        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("content_id", String.valueOf(content_id));
                params.put("content_type", String.valueOf(content_type));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", apiKey);
                return params;
            }
        };
        queue.add(sr);
    }

    public static boolean checkStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                Log.d("test", "Permission is granted");
                return true;

            } else {
                Log.d("test", "Permission is revoked");
                ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.d("test", "Permission is granted");
            return true;
        }
    }

    public String getStringBetweenTwoChars(String input, String startChar, String endChar) {
        try {
            int start = input.indexOf(startChar);
            if (start != -1) {
                int end = input.indexOf(endChar, start + startChar.length());
                if (end != -1) {
                    return input.substring(start + startChar.length(), end);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return input; // return null; || return "" ;
    }

    public static void resetPassword(Context context) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.forgot_password);

        TextView forgetPasswordEmailEditText = bottomSheetDialog.findViewById(R.id.Forget_Password_Email_EditText);
        View sendOtp = bottomSheetDialog.findViewById(R.id.Send_OTP);
        //---------Send OTP----------//
        sendOtp.setOnClickListener(view1 -> {
            if(forgetPasswordEmailEditText.getText().toString().matches("")) {
                Toasty.warning(context, "Please Enter Your Email Address Correctly.", Toast.LENGTH_SHORT, true).show();
            } else {
                RequestQueue queue = Volley.newRequestQueue(context);
                StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/password_recover_api.php?mail="+forgetPasswordEmailEditText.getText(), response -> {
                    switch (response) {
                        case "Email Not Registered":
                            Toasty.warning(context, "Email Not Registered", Toast.LENGTH_SHORT, true).show();
                            break;
                        case "Message has been sent":
                            Toasty.success(context, "Code Sended To the Mail Address!", Toast.LENGTH_SHORT, true).show();
                            bottomSheetDialog.dismiss();
                            changePassword(context);
                            break;
                        case "":
                        case "Something Went Wrong!":
                            Toasty.error(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                            break;
                        default:
                            Toasty.success(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                            break;
                    }

                }, error -> {
                    // Do nothing
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("x-api-key", AppConfig.apiKey);
                        return params;
                    }
                };
                queue.add(sr);
            }
        });

        bottomSheetDialog.show();
    }

    public static void changePassword(Context context) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.reset_password);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText codeEditText = bottomSheetDialog.findViewById(R.id.codeEditText);
        EditText newPassEditText = bottomSheetDialog.findViewById(R.id.newPassEditText);

        View resetPass = bottomSheetDialog.findViewById(R.id.resetPass);
        //---------Send OTP----------//
        resetPass.setOnClickListener(view1 -> {
            if(codeEditText.getText().toString().matches("") && newPassEditText.getText().toString().matches("")) {
                Toasty.warning(context, "Please Enter Your Email Address Correctly.", Toast.LENGTH_SHORT, true).show();
            } else {
                RequestQueue queue = Volley.newRequestQueue(context);
                StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/reset_password.php?token="+codeEditText.getText().toString()+"&pass="+Utils.getMd5(newPassEditText.getText().toString()), response -> {
                    switch (response) {
                        case "Password Updated successfully":
                            Toasty.success(context, "Password Updated successfully!", Toast.LENGTH_SHORT, true).show();
                            bottomSheetDialog.dismiss();
                            SharedPreferences settings = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
                            settings.edit().clear().apply();

                            Intent splashActivity = new Intent(context, Splash.class);
                            splashActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(splashActivity);
                            break;
                        case "":
                        case "Invalid Request":
                        case "Something Went Wrong!":
                            Toasty.error(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                            break;
                        default:
                            Toasty.success(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                            break;
                    }

                }, error -> {
                    // Do nothing
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put("x-api-key", AppConfig.apiKey);
                        return params;
                    }
                };
                queue.add(sr);
            }
        });

        bottomSheetDialog.show();
    }
}
