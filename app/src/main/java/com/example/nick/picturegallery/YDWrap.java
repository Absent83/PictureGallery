package com.example.nick.picturegallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;


import com.squareup.okhttp.OkHttpClient;


import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.DownloadListener;
import com.yandex.disk.rest.ProgressListener;
import com.yandex.disk.rest.ResourcesArgs;
import com.yandex.disk.rest.ResourcesHandler;
import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.RestClientImpl;
import com.yandex.disk.rest.exceptions.ServerException;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.exceptions.http.HttpCodeException;
import com.yandex.disk.rest.json.Resource;
import com.yandex.disk.rest.json.ResourceList;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;




public class YDWrap {

    private static volatile YDWrap ydWrapper;
    private static volatile boolean isInitializing;

    private static String TAG = "TPG " + "YDW";

    private final OkHttpClient okHttpClient;

    private final ImageCache imageCache;


    private final Context context; //use for download in file

    private String token;

    //region Init
    private YDWrap(Context context, OkHttpClient client, ImageCache imageCache, String token){
        this.context = context.getApplicationContext();
        this.okHttpClient = client;
        this.imageCache = imageCache;
        this.token = token;
    }

    public static void buid(@NonNull Context context, String token) {
        if (ydWrapper == null) {
            synchronized (YDWrap.class) {
                if (ydWrapper == null) {
                    checkAndInitializeWrapper(context, token);
                }
            }
        }
        else {
            Log.d(TAG, "YDWrap already was built");
        }

        //return ydWrapper;
    }


    private static void checkAndInitializeWrapper(@NonNull Context context, String token) {
        // In the thread running initializeYDWrapper(), one or more classes may call .get(context).
        // Without this check, those calls could trigger infinite recursion.
        if (isInitializing) {
            throw new IllegalStateException("You cannot call Glide.get() in registerComponents(),"
                    + " use the provided Glide instance instead");
        }
        isInitializing = true;
        initializeYDWrapper(context, token);
        isInitializing = false;
    }

    private static void initializeYDWrapper (@NonNull Context context, String token)  {

        context = context.getApplicationContext();

        //File cacheFile = new File(context.getCacheDir().getAbsolutePath(), "HttpCache");
        //int cacheSize = 50 * 1024 * 1024; // 50 MB  <-------- HERE
        //Log.d(TAG, "initializeYDWrapper: new Cache, cacheSize=" + cacheSize / 1024 / 1024 +" MB");
        //Cache cache = new Cache(cacheFile, cacheSize);

        OkHttpClient client = new OkHttpClient();
        //client.setCache(cache);



        ImageCache imageCache = new ImageCache(1024*1024*50); //TODO size

        YDWrap.ydWrapper = new YDWrap(context, client, imageCache, token);
    }
    //endregion



    //region Interface

    public static YDWrap get() {
        if (ydWrapper == null) {
            Log.d(TAG, "YDWrap not built");
        }

        return ydWrapper;
    }


    public void UpdateToken(@NonNull String token){
        this.token = token;
    }
    //endregion




    //region GetFlatList
    public ArrayList<ImageItem> GetFlatImgeArrayList(){
        GetFlatList GetFlatListObj = new GetFlatList(okHttpClient);

        Credentials cred = new Credentials("", token);

        ArrayList<ImageItem> imageItems = null;

        GetFlatListObj.execute(cred);
        try {
            imageItems = GetFlatListObj.get();

        } catch (InterruptedException e) {e.printStackTrace();

        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return imageItems;
    }

    private static class GetFlatList extends AsyncTask<Credentials, Void, ArrayList<ImageItem>> {

        final OkHttpClient okHttpClient;

        GetFlatList(OkHttpClient okHttpClien){
            this.okHttpClient = okHttpClien;
        }


        @Override
        protected ArrayList<ImageItem> doInBackground(Credentials... params) {
            Log.d(TAG,"start get flat list");

            final ArrayList<ImageItem> yandItemList = new ArrayList<>();

            RestClient restclient = new RestClient(params[0], okHttpClient);

            //RestClient restclient = new RestClient(params[0]);

            ResourceList reslist = new ResourceList();
            ResourcesArgs args = new ResourcesArgs.Builder()
                    .setPath("/")
                    .setSort(ResourcesArgs.Sort.name)
                    .setMediaType("image")
                    .setLimit(20)
                    .setOffset(0)
                    .setParsingHandler(new ResourcesHandler() {
                        @Override
                        public void handleItem(Resource item) {
                            ImageItem yandItem =
                                    new ImageItem(item.getName(),
                                            item.getPath().toString(),
                                            item.getPreview(),
                                            (int) item.getSize());
                            yandItemList.add(yandItem);
                        }
                    })
                    .build();


            try {
                reslist = restclient.getFlatResourceList(args);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ServerIOException e) {
                e.printStackTrace();
            }


//            for (int i=0; i < yandItemList.size(); i++){
//                Log.d(TAG, yandItemList.get(i).getName());
//            }

            Log.d(TAG,"end get flat list. Elements count=" + yandItemList.size() );
            return yandItemList;
        }
    }
    //endregion



    //region DownloadFile

    public  void DownloadImage(@NonNull ImageItem item, @NonNull ImageView view, boolean fullSize){




        final Bitmap tempBitmapFromCache = imageCache.getBitmapFromMemory(item.getPath(), fullSize);

        if (tempBitmapFromCache == null) {

            DownloadPathFileInMemeory DownloadFileObj = new DownloadPathFileInMemeory(okHttpClient, imageCache);
            Credentials cred = new Credentials("", token);
            DownloadFileObj.loadFile(cred, item, view, fullSize);

        } else {
            view.setImageBitmap(tempBitmapFromCache);
            Log.d(TAG, "DownloadImage: from Cache, mame = \" + item.getPath()");
        }









        //region Download from yandex to the storage without cache
        //            DownloadPathFileInStorage DownloadFileObj = new DownloadPathFileInStorage(okHttpClient);
        //            Credentials cred = new Credentials("", token);
        //            DownloadFileObj.loadFile(cred, item, context.getFilesDir(), view, fullSize);
        //endregion


    }



    private static class DownloadPathFileInMemeory {


        final OkHttpClient okHttpClient;
        final ImageCache imageCache;

        DownloadPathFileInMemeory(OkHttpClient okHttpClien, ImageCache imageCache){
            this.okHttpClient = okHttpClien;
            this.imageCache = imageCache;
        }

        public void loadFile(final Credentials credentials, final ImageItem item, final ImageView view, final boolean fullSize) {

            final ByteArrayOutputStream myStream = new ByteArrayOutputStream();


            final long[] Length = {0};

            final DownloadListener DL = new DownloadListener() {
                @Override
                public OutputStream getOutputStream(boolean append) {
                    return myStream;
                }

                @Override
                public void setContentLength(long length) {
                    Length[0] = length;
                    //Log.d(TAG, "DownloadImage: length=" + length);
                }
            };



            new Thread(new Runnable() {
                @Override
                public void run() {


                    try {
                        RestClientImpl client = new RestClientImpl(credentials, okHttpClient);

                        if (fullSize) {
                            client.downloadFile(item.getPath(), DL);
                        }
                        else {
                            client.downloadFileLink(item.getPreviewUrl(), DL);
                        }
                        Log.d(TAG, "DownloadImage: from Yandex Disk, mame = " + item.getPath());

                    } catch (HttpCodeException ex) {
                        Log.d(TAG, "loadFile", ex);
                        //sendException(ex.getResponse().getDescription()); //TODO
                    } catch (IOException | ServerException ex) {
                        Log.d(TAG, "loadFile", ex);
                        //sendException(ex); //TODO
                    }



                    final Bitmap bitmap = BitmapFactory.decodeByteArray(myStream.toByteArray(), 0, (int) Length[0]);

                    imageCache.setBitmapToMemory(item.getPath(), bitmap, fullSize);


                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            view.setImageBitmap(bitmap);
                        }
                    });
                }


            }).start();
        }

    }





    private static class DownloadPathFileInStorage /*extends IODialogRetainedFragment*/ implements ProgressListener {

        final OkHttpClient okHttpClient;

        DownloadPathFileInStorage(OkHttpClient okHttpClient){
            this.okHttpClient = okHttpClient;
        }


        private boolean cancelled;
        private File result;

        void loadFile(final Credentials credentials,
                      final ImageItem item,
                      final File SaveDirectory,
                      //final String SaveName,
                      final ImageView view,
                      final boolean fullSize) {


            view.setImageResource(R.drawable.progress_animation);

            String fileName = fullSize ? item.getName() : "pr_"+item.getName();
            //result = new File(context.getFilesDir(), new File(mainimageitem.getPath()).getName()); //TODO new File(mainimageitem.getPath()).getName() ???
            result = new File(SaveDirectory, fileName); //TODO file with the same name already exists (maybe add folder)
            //TODO файл удаляется после кэширования?


            Log.d(TAG, "downloadStart: file: " + result.getPath());

            new Thread(new Runnable() {
                @Override
                public void run () {
                    try {
                        //RestClient client = RestClientUtil.getInstance(credentials);

                        RestClientImpl client = new RestClientImpl(credentials, okHttpClient);

                        if (fullSize) {
                            client.downloadFile(item.getPath(), result, DownloadPathFileInStorage.this);
                        }
                        else {
                            client.downloadFileLink(item.getPreviewUrl(), result, DownloadPathFileInStorage.this);
                        }

                        downloadComplete(view, result);
                    } catch (HttpCodeException ex) {
                        Log.d(TAG, "loadFile", ex);
                        downLoadError(view);
                        //sendException(ex.getResponse().getDescription());
                    } catch (IOException | ServerException ex) {
                        downLoadError(view);
                        Log.d(TAG, "loadFile", ex);
                        //sendException(ex);
                    }
                }
            }).start();
        }

        @Override
        public void updateProgress (final long loaded, final long total) {
        }

        @Override
        public boolean hasCancelled () {
            return cancelled;
        }

        private void downloadComplete(final ImageView view, File file) {
            Log.d(TAG, "downloadComplete: file: " + result.getPath());


            if(file.exists()){

                final Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());



                Log.d(TAG, "downloadComplete: bitmap: " + bitmap.getByteCount()/1024 +"KB");

                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.setImageBitmap(bitmap);
                    }
                });

            }
        }


        private void downLoadError(@NonNull final ImageView view){
            Log.d(TAG, "downloadComplete: file: " + result.getPath());

            view.post(new Runnable() {
                @Override
                public void run() {
                    view.setImageResource(R.mipmap.ic_launcher);
                }
            });

        }



        public void cancelDownload() {
            cancelled = true;
        }
    }




    //endregion


}
