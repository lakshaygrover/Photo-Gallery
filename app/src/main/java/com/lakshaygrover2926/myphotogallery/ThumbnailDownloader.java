package com.lakshaygrover2926.myphotogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Created by LAKSHAY on 1/17/2017.
 */
public class ThumbnailDownloader<T> extends HandlerThread {

    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private Handler mRequestHandler;
    private Handler mResponseHandler;
    private ThumbnailDownloadListner<T> mThumbnailDownloadListner;

    public interface ThumbnailDownloadListner<T>{
        void onThumbnailDownload(T target, Bitmap thumbnail);
    }

    public void setThumbnailDownloadListner(ThumbnailDownloadListner<T> listener){
        mThumbnailDownloadListner = listener;
    }

    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();

    public ThumbnailDownloader(Handler responseHandler){
        super(TAG);
        mResponseHandler = responseHandler;
    }


    @Override
    protected void onLooperPrepared(){
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD){
                    T target = (T) msg.obj;
                    Log.i(TAG, "Gor request of url: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    private void handleRequest(final T target){
        try {
            final String url = mRequestMap.get(target);
            if(url == null){
                return;
            }
            byte[] bitmapBytes = new FlickrFetcher().getURLBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "bitmap created");
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mRequestMap.get(target)!=url){
                        return;
                    }else{
                        mRequestMap.remove(target);
                        mThumbnailDownloadListner.onThumbnailDownload(target, bitmap);
                    }
                }
            });
        }catch (IOException ioe){
            Log.e(TAG, "error downloading image", ioe);
        }
    }

    public void queueThumbnail(T target, String url){
        Log.i(TAG, "got url: "+url);
        if(url == null){
            mRequestMap.remove(target);

        }else{
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }

    public void clearQueue(){
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }
}
