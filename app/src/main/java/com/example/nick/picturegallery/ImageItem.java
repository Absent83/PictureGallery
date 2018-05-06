package com.example.nick.picturegallery;


import java.io.Serializable;

class ImageItem implements Serializable {

    private final String name;
    private final String preview;
    private final String path;
    private final int size;

    public ImageItem(String name,
                     String path,
                     String preview,
                     int size) {
        this.name = name;
        this.path = path;
        this.preview = preview;
        //this.preview = "https://downloader.disk.yandex.ru/preview/4de3d69f270aa9bcdf52efd3dac27017f0cf334564a97902e60ecfda165a7471/inf/D4a2aekIqLKUiob2TxvSKUyvBzgPd2RC87kn-I8cVESzDWzRr39oohKLDS7vWtSnWLHL1N1m0MqvzU959GqXcw%3D%3D?uid=1663304&filename=s1200.jpg&disposition=inline&hash=&limit=0&content_type=image%2Fjpeg&tknv=v2&size=S&crop=0";
        this.size = size;
    }
    public String toString(){
        return preview ;
    } //todo

    public String getPreviewUrl (){
        return preview;
    }

    public String getPath (){
        return path;
    }

    public String getName(){
        return name;
    }

public int getSize(){
        return size;
}
}
