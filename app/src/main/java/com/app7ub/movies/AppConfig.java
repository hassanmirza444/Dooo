package com.app7ub.movies;

public class AppConfig {
    static {
        System.loadLibrary("app_config");
    }
    public static native String getApiServerUrl();
    public static native String getApiKey();
    public static native String getOnesignalAppID();
    public static native boolean allowVPNStatus();
    public static native boolean flagSecureStatus();
    public static native boolean allowRootStatus();
    public static native String getYoutubeApiKey();
    public static native boolean unityAdTestModeStatus();

    //<!--DO NOT EDIT THESE DETAILS THESE WILL BE ASSIGNED FROM app_config.cpp-->//
    public static String url = getApiServerUrl();
    public static String apiKey = getApiKey();
    static final String ONESIGNAL_APP_ID = getOnesignalAppID();
    static boolean allowVPN = allowVPNStatus();
    public static final boolean FLAG_SECURE = flagSecureStatus();
    static boolean allowRoot = allowRootStatus();
    public static final String YOUTUBE_API_KEY = getYoutubeApiKey();
    public static Boolean unity_ad_testMode = unityAdTestModeStatus();
    //<!--END-->//

    //<!--DO NOT EDIT THESE DETAILS THESE WILL BE ASSIGNED FROM DASHBOARD-->//
    static String adMobNative = "";
    static String adMobBanner = "";
    static String adMobInterstitial = "";
    static String facebook_banner_ads_placement_id = "";
    static String facebook_interstitial_ads_placement_id = "";
    public static String AdColony_APP_ID = "";
    public static String AdColony_BANNER_ZONE_ID = "";
    public static String AdColony_INTERSTITIAL_ZONE_ID = "";
    public static String Unity_Game_ID = "";
    public static String Unity_Banner_ID = "";

    public static String Custom_Banner_url = "";
    public static int Custom_Banner_click_url_type;
    public static String Custom_Banner_click_url= "";
    public static String Custom_Interstitial_url = "";
    public static int Custom_Interstitial_click_url_type;
    public static String Custom_Interstitial_click_url= "";


    public static int all_live_tv_type;
    public static int all_movies_type;
    public static int all_series_type;

    public static int contentItem = R.layout.movie_item;
    public static int small_live_tv_channel_item = R.layout.small_live_tv_channel_item;
    public static int live_tv_channel_item = R.layout.live_tv_channel_item;

    public static String bGljZW5zZV9jb2Rl = "";
    //<!------------------------------------------------------------------->//

}
