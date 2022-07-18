package com.app7ub.movies.utils;

import android.content.Context;

import com.android.volley.VolleyError;
import com.app7ub.movies.list.YTStreamList;
/*import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;
import com.yausername.youtubedl_android.YoutubeDLRequest;
import com.yausername.youtubedl_android.mapper.VideoInfo;*/

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class Yts {

    private static CompositeDisposable compositeDisposable = new CompositeDisposable();

    public interface VolleyCallback {
        void onSuccess(List<YTStreamList> result);
        void onError(VolleyError error);
    }
    public interface VolleyCallback2 {
        void onSuccess(String result);
        void onError(VolleyError error);
    }


    public static void getlinks(Context context, String url, final VolleyCallback callback) {
        List<YTStreamList> qualityItems = new ArrayList<>();



      /*  Disposable disposable = Observable.fromCallable(() -> {
            YoutubeDL.getInstance().init(context);
            return YoutubeDL.getInstance().getInfo(url);
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(streamInfo -> {
                    int listSize =  streamInfo.getFormats().size();
                    for (int i = 0; i<listSize; i++) {
                        if(streamInfo.getFormats().get(i).getFormatId().equals("17")
                                || streamInfo.getFormats().get(i).getFormatId().equals("18")
                                || streamInfo.getFormats().get(i).getFormatId().equals("22")) {
                            qualityItems.add(new YTStreamList(streamInfo.getFormats().get(i).getFormatNote(), streamInfo.getFormats().get(i).getUrl(), streamInfo.getFormats().get(i).getFormat()));

                        }
                    }
                    if(!qualityItems.isEmpty()) {
                        callback.onSuccess(qualityItems);
                    } else {
                        callback.onError(new VolleyError("Something Went Wrong!"));
                    }

                }, e -> {
                    callback.onError((VolleyError) e);
                });
        compositeDisposable.add(disposable);*/
    }

    public static void getStreamLinks(Context context, String k, String vid, final VolleyCallback2 callback) {
        callback.onSuccess(vid);
    }
}
