package com.example.nick.picturegallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class MainPresenter  {
    private String TAG = "TPG " + MainPresenter.class.getSimpleName();


    // create your own client id/secret pair with callback url on oauth.yandex.ru
    private static final String USERNAME = "username";
    private static final String TOKEN = "token";

    private final MainActivity mView;

    private MainDataAdapter adapter;
    //private ArrayList<ImageItem> imageItems;

    public MainPresenter(@NonNull MainActivity mainView, Bundle bundle) {
        Log.d(TAG, "MainPresenter: Creat new MainPresenter");
        mView = mainView;
        init(bundle);
    }

    private void init(Bundle bundle) {

        String tempToken = getTokenFromIntent();

        if (tempToken == null) {
            tempToken = getTokenFromSharedPreferences(mView);
        }


        if (tempToken == null) {
            Log.d(TAG, "MainPresenter.init not complete: " + "token == null, showAuthDialog");
            mView.showAuthDialog();
            return;
        }


        String token = tempToken;

        YDWrap.buid(mView, token);

        ArrayList imageItems = GetImageList(bundle);

        adapter = new MainDataAdapter(imageItems);


        mView.ShowGallery(adapter);
    }


    private ArrayList<ImageItem> GetImageList(Bundle bundle){


        ArrayList<ImageItem> tempList = new ArrayList<>();

        if (bundle != null) {
            Log.d(TAG, "GetImageList: GetImageList from Bundle");
            tempList = (ArrayList<ImageItem>)bundle.getSerializable("images");
        }

        Log.d(TAG, "GetImageList: Count=" + tempList.size());

        if (tempList == null || tempList.size() == 0) {
            Log.d(TAG, "GetImageList: GetImageList from Yandex");
            tempList = YDWrap.get().GetFlatImgeArrayList();
        }

        Log.d(TAG, "GetImageList: Count=" + tempList.size());
        return tempList;
    }



    public void SaveImageList(Bundle bundle, int position){
      bundle.putSerializable("images", adapter.imageItems);
      bundle.putInt("position", position);
    }



    public void PrepareSlideShow(/*ArrayList<ImageItem> imageItems,*/  int position) {
        Log.d(TAG, "PrepareSlideShow");
        Bundle bundle = new Bundle();

        SaveImageList(bundle, position);
        mView.StartSlideShow(bundle);
    }


    public void onImageLongClick(int position) {

    }


    //region Get Token
    private String getTokenFromIntent(){
        String tempToken;
        if (mView.getIntent() != null && mView.getIntent().getData() != null) {
            Uri data = mView.getIntent().getData();
            mView.setIntent(null); //TODO ???
            tempToken = getTokenFromUriData(data);
            saveToken(tempToken);

            Log.d(TAG, "getTokenFromIntent: token=" + tempToken);
            return tempToken;
        }
        else{
            return null;
        }

    }

    private String getTokenFromUriData (@NonNull Uri data) {

        Pattern pattern = Pattern.compile("access_token=(.*?)(&|$)");
        Matcher matcher = pattern.matcher(data.toString());
        if (matcher.find()) {
            final String token = matcher.group(1);
            if (!TextUtils.isEmpty(token)) {
                Log.d(TAG, "onLogin: token: "+token);
                return token;
            } else {
                Log.w(TAG, "onRegistrationSuccess: empty token");
            }
        } else {
            Log.w(TAG, "onRegistrationSuccess: token not found in return url");
        }

        return null;
    }





    private String getTokenFromSharedPreferences (Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String tempToken = preferences.getString(TOKEN, null);
        Log.d(TAG, "getTokenFromSharedPreferences: token=" + tempToken);
        return tempToken;
    }




    private void saveToken(String token) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mView).edit();
        editor.putString(USERNAME, "");
        editor.putString(TOKEN, token);
        editor.apply();
    }
    //endregion





}
