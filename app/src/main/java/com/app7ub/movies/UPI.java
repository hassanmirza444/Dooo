package com.app7ub.movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class UPI extends AppCompatActivity {

    String name;
    int subscriptionType, amount, time;
    String strCurrency;

    int userID;
    String userName, email;

    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.activity_upi);

        rootView = findViewById(R.id.Razorpay_Payment_gatway);

        loadConfig();
        loadData();


        Intent intent = getIntent();
        int id = intent.getExtras().getInt("id");
        loadSubscriptionDetails(id);
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        if (sharedPreferences.getString("UserData", null) != null) {
            String userData = sharedPreferences.getString("UserData", null);
            JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);

            userID = jsonObject.get("ID").getAsInt();
            userName = jsonObject.get("Name").getAsString();
            email = jsonObject.get("Email").getAsString();
        }
    }

    private void loadConfig() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String config = sharedPreferences.getString("Config", null);

        JsonObject jsonObject = new Gson().fromJson(config, JsonObject.class);

        //razorpay_status = jsonObject.get("razorpay_status").getAsInt();
        //razorpay_key_id = jsonObject.get("razorpay_key_id").getAsString();
        //razorpay_key_secret = jsonObject.get("razorpay_key_secret").getAsString();


    }

    void loadSubscriptionDetails(int id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_subscription_details.php?ID="+id, response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                int id1 = jsonObject.get("id").getAsInt();
                name = jsonObject.get("name").getAsString();
                time = jsonObject.get("time").getAsInt();
                amount = jsonObject.get("amount").getAsInt();
                int currency = jsonObject.get("currency").getAsInt();
                String background = jsonObject.get("background").getAsString();
                subscriptionType = jsonObject.get("subscription_type").getAsInt();
                int status = jsonObject.get("status").getAsInt();

                switch (currency) {
                    case 0:
                        strCurrency = "INR";
                        break;
                    case 1:
                        strCurrency = "USD";
                        break;
                }


                UPI(name, amount);
                /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                Checkout.preload(getApplicationContext());

                try {
                    RazorpayClient razorpayClient = new RazorpayClient(razorpay_key_id, razorpay_key_secret);

                    JSONObject orderRequest = new JSONObject();
                    orderRequest.put("amount", amount*100); // amount in the smallest currency unit
                    orderRequest.put("currency", strCurrency);
                    orderRequest.put("receipt", "order_rcptid_11");

                    Order order = razorpayClient.Orders.create(orderRequest);

                    startPayment(order);

                } catch (RazorpayException | JSONException e) {
                    // Handle Exception
                    System.out.println(e.getMessage());
                }*/
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

    public void UPI(String name, int amount)
    {
        Long tsLong = System.currentTimeMillis()/1000;
        String transaction_ref_id = tsLong.toString()+"UPI"; // This is your Transaction Ref id - Here we used as a timestamp -

        String sOrderId= tsLong +"UPI";// This is your order id - Here we used as a timestamp -

        //Uri myAction = Uri.parse("upi://pay?pa="+"kaustuvmahanti@ybl"+"&pn="+getResources().getString(R.string.app_name)+"&mc="+"&tid="+transaction_ref_id +"&tr="+transaction_ref_id +"&tn="+name+"&am="+amount+"&mam=null&cu=INR&url=''");
        Uri myAction = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", "kaustuvmahanti@ybl")  // google pay business id
                .appendQueryParameter("pn", getResources().getString(R.string.app_name))
                .appendQueryParameter("mc", "")            /// 1st param - use it
                //.appendQueryParameter("tid", "02125412")
                .appendQueryParameter("tr", transaction_ref_id)   /// 2nd param - use it
                .appendQueryParameter("tn", name)
                .appendQueryParameter("am", String.valueOf(amount))
                .appendQueryParameter("cu", "INR")
                //.appendQueryParameter("refUrl", "blueapp")
                .build();

        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage("com.google.android.apps.nbu.paisa.user"); // Comment line - if you want to open specific application then you can pass that package name For example if you want to open Bhim app then pass Bhim app package name -
        //Intent intent = new Intent();

        if (intent != null) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(myAction);
            // startActivity(intent);
            Intent chooser = Intent.createChooser(intent, "Pay with...");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startActivityForResult(chooser, 1, null);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try
        {
            //Log.e("UPI RESULT REQUEST CODE-->",""+requestCode);
            //Log.e("UPI RESULT RESULT CODE-->",""+resultCode);
            //Log.e("UPI RESULT DATA-->",""+data);

            Log.d("test", String.valueOf(resultCode));
            Log.d("test", String.valueOf(data.toUri(0)));
            if(resultCode == -1)
            {

                // 200 Success
                //Log.d("test", "Success");

            }
            else
            {
                // 400 Failed
                //Log.d("test", "Failed");
            }


            UPI.this.finish();


        }
        catch(Exception e)
        {
            Log.e("Error in UPI onActivityResult->",""+e.getMessage());
        }
    }
}