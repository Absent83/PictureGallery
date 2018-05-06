package com.example.nick.picturegallery;



import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity {


    private MainPresenter mPresenter;
    private String TAG = "TPG " + MainActivity.class.getSimpleName();

    private static final String CLIENT_ID = "bbbc27d88ef0409db0de53899b7c8aea";
    private static final String AUTH_URL = "https://oauth.yandex.ru/authorize?response_type=token&client_id="+CLIENT_ID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreateView");

        setContentView(R.layout.mainlayout);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ProgressDialog pDialog = new ProgressDialog(this);



        mPresenter = new MainPresenter(this, savedInstanceState);
    }



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
            mPresenter.SaveImageList(savedInstanceState, 0);
        // etc.
    }



//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        // Restore UI state from the savedInstanceState.
//        // This bundle has also been passed to onCreate.
//        boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
//        double myDouble = savedInstanceState.getDouble("myDouble");
//        int myInt = savedInstanceState.getInt("MyInt");
//        String myString = savedInstanceState.getString("MyString");
//    }




    //region AuthDialog
    public void showAuthDialog(){
        new AuthDialog().show(getFragmentManager(), "auth");
    }



    public static class AuthDialog extends DialogFragment {

        public AuthDialog () {
            super();
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.auth_title)
                    .setMessage(R.string.auth_message)
                    .setPositiveButton(R.string.example_auth_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick (DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AUTH_URL)));
                        }
                    })
                    .setNegativeButton(R.string.example_auth_negative_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick (DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getActivity().finish();
                        }
                    })
                    .create();
        }
    }
    //endregion


    //region Gallery
    public void ShowGallery(final MainDataAdapter adapter){
        RecyclerView recyclerView = findViewById(R.id.gallery_recycler_view);
//        recyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager;



        @RecyclerView.Orientation int orientation =
                isPortrait() ? RecyclerView.VERTICAL : RecyclerView.HORIZONTAL;

         int spanCount =
                isPortrait() ? 2  : 2 ;


        gridLayoutManager = new GridLayoutManager(getApplicationContext(),spanCount, orientation, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        recyclerView.addOnItemTouchListener(new MainDataAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new MainDataAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                    mPresenter.PrepareSlideShow(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }
    //endregion



    public void StartSlideShow(Bundle bundle){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SlideshowFragment newFragment = SlideshowFragment.newInstance();
        newFragment.setArguments(bundle);
        newFragment.show(ft, "slideshow");
    }




    private boolean isPortrait(){
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                return true;
            case Configuration.ORIENTATION_LANDSCAPE:
                return false;
            default:
                return true;
        }
    }



















}
