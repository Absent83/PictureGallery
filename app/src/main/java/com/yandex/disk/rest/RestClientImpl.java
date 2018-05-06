package com.yandex.disk.rest;

import com.squareup.okhttp.OkHttpClient;
import com.yandex.disk.rest.exceptions.ServerException;



import java.io.File;
import java.io.IOException;



public class RestClientImpl extends RestClient {


    private final Credentials credentials;
    private final OkHttpClient client;


    public RestClientImpl(final Credentials credentials) {
        this(credentials, OkHttpClientFactory.makeClient());
    }

    public RestClientImpl(final Credentials credentials, final OkHttpClient client) {
        this(credentials, client, "https://cloud-api.yandex.net");
    }

    private RestClientImpl(Credentials credentials, OkHttpClient client, String serverUrl) {
        super(credentials, client, serverUrl);

        this.credentials = credentials;
        this.client = client;
    }



    public void downloadFileLink(final String url, final File saveTo, final ProgressListener progressListener)
            throws IOException, ServerException {
        //Link link = cloudApi.getDownloadLink(path);
        new RestClientIO(client, credentials.getHeaders())
                .downloadUrl(url, new FileDownloadListener(saveTo, progressListener));
    }


    public void downloadFileLink(final String url, final DownloadListener downloadListener)
            throws IOException, ServerException {
        //Link link = cloudApi.getDownloadLink(path);
        new RestClientIO(client, credentials.getHeaders())
                .downloadUrl(url, downloadListener);
    }


    public void downloadFile(final String path, final File saveTo, final ProgressListener progressListener)
            throws IOException, ServerException {
        super.downloadFile(path, saveTo, progressListener);
    }


    public void downloadFile(final String path, final DownloadListener downloadListener)
            throws IOException, ServerException {
        super.downloadFile(path, downloadListener);
    }
}
