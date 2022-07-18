package com.app7ub.movies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app7ub.movies.utils.HelperUtils;
import com.app7ub.movies.utils.LoadingDialog;
import com.app7ub.movies.utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jetradarmobile.snowfall.SnowfallView;

import org.imaginativeworld.oopsnointernet.dialogs.signal.DialogPropertiesSignal;
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class LoginSignup extends AppCompatActivity {
    Context context = this;

    LoadingDialog loadingAnimation;

    int google_login;

    private boolean vpnStatus;
    private HelperUtils helperUtils;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppConfig.FLAG_SECURE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.login_signup_TitleBar_BG));

        setContentView(R.layout.activity_login_signup);

        loadingAnimation = new LoadingDialog(this);

        loadConfig();

        if (!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(LoginSignup.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(LoginSignup.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }


        View loginScreen = findViewById(R.id.login_screen);
        View signupScreen = findViewById(R.id.signup_screen);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.TabLayout);
        TextView forgetPasswordTextView = findViewById(R.id.Forget_Password_TextView);
        View forgotPasswordLayout = findViewById(R.id.Forgot_Password_Layout);
        TextView forgetPasswordEmailEditText = findViewById(R.id.Forget_Password_Email_EditText);

        TextView loginEditTextEmailAddress = findViewById(R.id.Login_editTextEmailAddress);
        CardView loginEmailBottombar = findViewById(R.id.Login_email_bottombar);
        TextView loginEditTextPassword = findViewById(R.id.login_editTextPassword);
        CardView loginPasswordBottombar = findViewById(R.id.Login_password_bottombar);

        TextView signupFullnameEdittext = findViewById(R.id.signup_fullname_edittext);
        CardView signupFullnameButtombar = findViewById(R.id.signup_fullname_buttom_bar);
        TextView signupEditTextTextEmailAddress = findViewById(R.id.signup_editTextTextEmailAddress);
        CardView signupEmailBottombar = findViewById(R.id.signup_email_bottombar);
        TextView signupPasswordEdittext = findViewById(R.id.signup_password_edittext);
        CardView signupPasswordBottombar = findViewById(R.id.signup_password_bottombar);
        TextView signupConfirmPasswordEdittext = findViewById(R.id.signup_confirm_password_edittext);
        CardView signupConfirmPasswordBottombar = findViewById(R.id.signup_confirm_password_bottombar);
        TextView my_urls = findViewById(R.id.my_urls);

        View loginButton = findViewById(R.id.Login_Button);
        View signupButton = findViewById(R.id.Signup_Button);

        //---------Login----------//
        loginButton.setOnClickListener(view -> {
            loadingAnimation.animate(true);

            if (loginEditTextEmailAddress.getText().toString().matches("") && loginEditTextPassword.getText().toString().matches("")) {
                loadingAnimation.animate(false);
                Toasty.warning(context, "Please Enter Your Details.", Toast.LENGTH_SHORT, true).show();
            } else if (!loginEditTextEmailAddress.getText().toString().matches("") && loginEditTextPassword.getText().toString().matches("")) {
                loadingAnimation.animate(false);
                Toasty.warning(context, "Please Enter Your Password.", Toast.LENGTH_SHORT, true).show();
            } else if (loginEditTextEmailAddress.getText().toString().matches("") && !loginEditTextPassword.getText().toString().matches("")) {
                loadingAnimation.animate(false);
                Toasty.warning(context, "Please Enter Your Email.", Toast.LENGTH_SHORT, true).show();
            } else {
                String email = loginEditTextEmailAddress.getText().toString();
                String pass = loginEditTextPassword.getText().toString();
                String originalInput = "login:" + email + ":" + getMd5(pass);
                String encoded = Utils.toBase64(originalInput);
                RequestQueue queue = Volley.newRequestQueue(context);
                StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url + "/api/authentication.php", response -> {
                    if (!response.equals("") || response != null) {
                        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                        String status = jsonObject.get("Status").toString();
                        status = status.substring(1, status.length() - 1);

                        if (response.equals("invalid")) {
                            Toasty.error(context, "Invalid Request.", Toast.LENGTH_SHORT, true).show();
                        } else if (status.equals("Successful")) {
                            Toasty.success(context, "Logged in Successfully.", Toast.LENGTH_SHORT, true).show();
                            saveData(response);
                            Intent intent = new Intent(LoginSignup.this, Splash.class);
                            startActivity(intent);
                            finish();
                        } else if (status.equals("Invalid Credential")) {
                            Toasty.error(context, "Invalid Credential.", Toast.LENGTH_SHORT, true).show();
                        } else {
                            Toasty.error(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                        }

                    } else {
                        Toasty.error(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                    }

                    loadingAnimation.animate(false);

                }, error -> {
                    loadingAnimation.animate(false);
                    Toasty.error(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("x-api-key", AppConfig.apiKey);
                        params.put("X-Requested-With", encoded);
                        return params;
                    }
                };
                queue.add(sr);
            }


        });
        //---------Sign Up----------//
        signupButton.setOnClickListener(view -> {
            loadingAnimation.animate(true);

            if (signupFullnameEdittext.getText().toString().matches("") && signupEditTextTextEmailAddress.getText().toString().matches("") && signupPasswordEdittext.getText().toString().matches("") && signupConfirmPasswordEdittext.getText().toString().matches("")) {
                loadingAnimation.animate(false);
                Toasty.warning(context, "Please Enter Your Details Correctly.", Toast.LENGTH_SHORT, true).show();
            } else {
                if (!signupPasswordEdittext.getText().toString().equals(signupConfirmPasswordEdittext.getText().toString())) {
                    loadingAnimation.animate(false);
                    Toasty.warning(context, "Password and Confirm Password Not Matching.", Toast.LENGTH_SHORT, true).show();
                } else {
                    String signupName = signupFullnameEdittext.getText().toString();
                    String signupEmail = signupEditTextTextEmailAddress.getText().toString();
                    String signupPassword = signupPasswordEdittext.getText().toString();


                    String originalSignupInput = "signup:" + signupName + ":" + signupEmail + ":" + getMd5(signupPassword);
                    String encodedSignupData = Utils.toBase64(originalSignupInput);

                    RequestQueue queue = Volley.newRequestQueue(context);
                    StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url + "/api/authentication.php", response -> {
                        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                        String status = jsonObject.get("Status").toString();
                        status = status.substring(1, status.length() - 1);

                        switch (status) {
                            case "Successful":
                                Toasty.success(context, "Registered Successfully.", Toast.LENGTH_SHORT, true).show();
                                saveData(response);
                                Intent intent = new Intent(LoginSignup.this, Splash.class);
                                startActivity(intent);
                                finish();
                                break;
                            case "Email Already Regestered":
                                Toasty.error(context, "Email Already Regestered", Toast.LENGTH_SHORT, true).show();
                                break;
                            case "Something Went Wrong!":
                                Toasty.error(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                                break;
                            default:
                                break;
                        }

                        loadingAnimation.animate(false);

                    }, error -> {
                        loadingAnimation.animate(false);
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("x-api-key", AppConfig.apiKey);
                            params.put("X-Requested-With", encodedSignupData);
                            return params;
                        }
                    };
                    queue.add(sr);
                }
            }
        });

        //---------Login Layout------------//
        loginEditTextEmailAddress.setOnFocusChangeListener((view, b) -> {
            if (loginEditTextEmailAddress.hasFocus()) {
                loginEmailBottombar.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.Red_Smooth)));
            } else {
                loginEmailBottombar.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.w_Dark)));
            }
        });
        loginEditTextPassword.setOnFocusChangeListener((view, b) -> {
            if (loginEditTextPassword.hasFocus()) {
                loginPasswordBottombar.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.Red_Smooth)));
            } else {
                loginPasswordBottombar.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.w_Dark)));
            }
        });

        //---------Sing UP Layout------------//
        signupFullnameEdittext.setOnFocusChangeListener((view, b) -> {
            if (signupFullnameEdittext.hasFocus()) {
                signupFullnameButtombar.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.Red_Smooth)));
            } else {
                signupFullnameButtombar.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.w_Dark)));
            }
        });
        signupEditTextTextEmailAddress.setOnFocusChangeListener((view, b) -> {
            if (signupEditTextTextEmailAddress.hasFocus()) {
                signupEmailBottombar.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.Red_Smooth)));
            } else {
                signupEmailBottombar.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.w_Dark)));
            }
        });
        signupPasswordEdittext.setOnFocusChangeListener((view, b) -> {
            if (signupPasswordEdittext.hasFocus()) {
                signupPasswordBottombar.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.Red_Smooth)));
            } else {
                signupPasswordBottombar.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.w_Dark)));
            }
        });
        signupConfirmPasswordEdittext.setOnFocusChangeListener((view, b) -> {
            if (signupConfirmPasswordEdittext.hasFocus()) {
                signupConfirmPasswordBottombar.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.Red_Smooth)));
            } else {
                signupConfirmPasswordBottombar.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.w_Dark)));
            }
        });


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectedTabPosition = tabLayout.getSelectedTabPosition();

                if (selectedTabPosition == 0) {
                    loginScreen.setVisibility(View.VISIBLE);
                    signupScreen.setVisibility(View.INVISIBLE);
                } else if (selectedTabPosition == 1) {
                    loginScreen.setVisibility(View.INVISIBLE);
                    signupScreen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
// called when tab unselected
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
// called when a tab is reselected
            }
        });

        forgetPasswordTextView.setOnClickListener(view -> {
            HelperUtils.resetPassword(context);
        });

        // No Internet Dialog: Signal
        NoInternetDialogSignal.Builder builder = new NoInternetDialogSignal.Builder(
                this,
                getLifecycle()
        );
        DialogPropertiesSignal properties = builder.getDialogProperties();
        // Optional
        properties.setConnectionCallback(hasActiveConnection -> {
            // ...
        });
        properties.setCancelable(true); // Optional
        properties.setNoInternetConnectionTitle("No Internet"); // Optional
        properties.setNoInternetConnectionMessage("Check your Internet connection and try again"); // Optional
        properties.setShowInternetOnButtons(true); // Optional
        properties.setPleaseTurnOnText("Please turn on"); // Optional
        properties.setWifiOnButtonText("Wifi"); // Optional
        properties.setMobileDataOnButtonText("Mobile data"); // Optional

        properties.setOnAirplaneModeTitle("No Internet"); // Optional
        properties.setOnAirplaneModeMessage("You have turned on the airplane mode."); // Optional
        properties.setPleaseTurnOffText("Please turn off"); // Optional
        properties.setAirplaneModeOffButtonText("Airplane mode"); // Optional
        properties.setShowAirplaneModeOffButtons(true); // Optional
        builder.build();
        ///
        ///
        my_urls.setText(AppConfig.getApiKey() + "          " + AppConfig.getApiServerUrl() + "\n"
                + AppConfig.getOnesignalAppID() + "" + AppConfig.allowVPNStatus() + "\n"
                + AppConfig.getYoutubeApiKey() + "" + AppConfig.allowRootStatus());

        CardView google_sign_in_button = findViewById(R.id.google_sign_in_button);
        CardView google_sign_up_button = findViewById(R.id.google_sign_up_button);
        if (google_login == 1) {
            google_sign_in_button.setVisibility(View.VISIBLE);
            google_sign_up_button.setVisibility(View.VISIBLE);
            google_sign_in_button.setOnClickListener(view -> {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                gSignIn();
            });

            google_sign_up_button.setOnClickListener(view -> {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                gSignIn();
            });
        } else {
            google_sign_in_button.setVisibility(View.GONE);
            google_sign_up_button.setVisibility(View.GONE);
        }

    }

    private void gSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            String G_Name = account.getDisplayName();
            String G_Email = account.getEmail();
            String G_pass = account.getId();

            gLogin(G_Name, G_Email, G_pass);
        } catch (ApiException e) {

        }
    }

    private void gLogin(String gName, String G_Email, String G_pass) {
        String originalInput = "login:" + G_Email + ":" + getMd5(G_pass);
        String encoded = Utils.toBase64(originalInput);
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url + "/api/authentication.php", response -> {
            if (!response.equals("") || response != null) {
                JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                String status = jsonObject.get("Status").toString();
                status = status.substring(1, status.length() - 1);

                if (response.equals("invalid")) {
                    Toasty.error(context, "Invalid Request.", Toast.LENGTH_SHORT, true).show();
                } else if (status.equals("Successful")) {
                    Toasty.success(context, "Logged in Successfully.", Toast.LENGTH_SHORT, true).show();
                    saveData(response);
                    Intent intent = new Intent(LoginSignup.this, Splash.class);
                    startActivity(intent);
                    finish();
                } else if (status.equals("Invalid Credential")) {
                    //Toasty.error(context, "Invalid Credential.", Toast.LENGTH_SHORT, true).show();
                    gSignup(gName, G_Email, G_pass);
                } else {
                    Toasty.error(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                }

            } else {
                Toasty.error(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
            }

            loadingAnimation.animate(false);

        }, error -> {
            loadingAnimation.animate(false);
            Toasty.error(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                params.put("X-Requested-With", encoded);
                return params;
            }
        };
        queue.add(sr);

    }

    private void gSignup(String gName, String G_Email, String G_pass) {
        String originalSignupInput = "signup:" + gName + ":" + G_Email + ":" + getMd5(G_pass);
        String encodedSignupData = Utils.toBase64(originalSignupInput);

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url + "/api/authentication.php", response -> {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            String status = jsonObject.get("Status").toString();
            status = status.substring(1, status.length() - 1);

            switch (status) {
                case "Successful":
                    Toasty.success(context, "Registered Successfully.", Toast.LENGTH_SHORT, true).show();
                    saveData(response);
                    Intent intent = new Intent(LoginSignup.this, Splash.class);
                    startActivity(intent);
                    finish();
                    break;
                case "Email Already Regestered":
                    Toasty.error(context, "Email Already Regestered", Toast.LENGTH_SHORT, true).show();
                    break;
                case "Something Went Wrong!":
                    Toasty.error(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                    break;
                default:
                    break;
            }

            loadingAnimation.animate(false);

        }, error -> {
            loadingAnimation.animate(false);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                params.put("X-Requested-With", encodedSignupData);
                return params;
            }
        };
        queue.add(sr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //---------Login Layout------------//
        EditText loginEditTextEmailAddress = findViewById(R.id.Login_editTextEmailAddress);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (loginEditTextEmailAddress.isFocused()) {
                Rect outRect = new Rect();
                loginEditTextEmailAddress.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    loginEditTextEmailAddress.clearFocus();

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        EditText loginEditTextPassword = findViewById(R.id.login_editTextPassword);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (loginEditTextPassword.isFocused()) {
                Rect outRect = new Rect();
                loginEditTextPassword.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    loginEditTextPassword.clearFocus();

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        EditText forgetPasswordEmailEditText = findViewById(R.id.Forget_Password_Email_EditText);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (forgetPasswordEmailEditText.isFocused()) {
                Rect outRect = new Rect();
                forgetPasswordEmailEditText.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    forgetPasswordEmailEditText.clearFocus();

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        //---------Sing UP Layout------------//
        EditText signupFullnameEdittext = findViewById(R.id.signup_fullname_edittext);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (signupFullnameEdittext.isFocused()) {
                Rect outRect = new Rect();
                signupFullnameEdittext.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    signupFullnameEdittext.clearFocus();

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        EditText signupEditTextTextEmailAddress = findViewById(R.id.signup_editTextTextEmailAddress);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (signupEditTextTextEmailAddress.isFocused()) {
                Rect outRect = new Rect();
                signupEditTextTextEmailAddress.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    signupEditTextTextEmailAddress.clearFocus();

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        EditText signupPasswordEdittext = findViewById(R.id.signup_password_edittext);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (signupPasswordEdittext.isFocused()) {
                Rect outRect = new Rect();
                signupPasswordEdittext.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    signupPasswordEdittext.clearFocus();

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        EditText signupConfirmPasswordEdittext = findViewById(R.id.signup_confirm_password_edittext);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (signupConfirmPasswordEdittext.isFocused()) {
                Rect outRect = new Rect();
                signupConfirmPasswordEdittext.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    signupConfirmPasswordEdittext.clearFocus();

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }


        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public static String getMd5(String input) {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashText = no.toString(16);
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }
            return hashText;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadConfig() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String config = sharedPreferences.getString("Config", null);

        JsonObject jsonObject = new Gson().fromJson(config, JsonObject.class);
        google_login = jsonObject.get("google_login").getAsInt();

        int onScreenEffect = jsonObject.get("onscreen_effect").getAsInt();
        SnowfallView SnowfallView = findViewById(R.id.SnowfallView);
        switch (onScreenEffect) {
            case 0:
                SnowfallView.setVisibility(View.GONE);
                break;
            case 1:
                //SnowfallView.setVisibility(View.VISIBLE);
                SnowfallView.setVisibility(View.GONE);
                break;
            default:
                SnowfallView.setVisibility(View.GONE);

        }
    }

    private void saveData(String userData) {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserData", userData);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(LoginSignup.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(LoginSignup.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }
    }
}