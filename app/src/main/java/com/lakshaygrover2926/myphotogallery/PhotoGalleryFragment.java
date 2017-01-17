package com.lakshaygrover2926.myphotogallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LAKSHAY on 1/14/2017.
 */
public class PhotoGalleryFragment extends Fragment {
    private static final String TAG = "PhotoGalleryFragment";
    private RecyclerView mPhotoRecyclerView;
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;
    private List<GalleryItem> mItems = new ArrayList<>();

    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();

        Handler reponseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(reponseHandler);
        mThumbnailDownloader.setThumbnailDownloadListner(
                new ThumbnailDownloader.ThumbnailDownloadListner<PhotoHolder>(){
                    @Override
                public void onThumbnailDownload(PhotoHolder photoHolder, Bitmap bitmap){
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        photoHolder.bindDrawable(drawable);
                    }
                }
        );
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        setupAdapter();
        return v;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "background thread destroyed");

    }

    private void setupAdapter(){
        if(isAdded()){
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return new FlickrFetcher().fetchItems();
        }
        @Override
        protected void onPostExecute(List<GalleryItem> items){
            mItems = items;
            setupAdapter();
        }
    }
        private class PhotoHolder extends RecyclerView.ViewHolder{
            private ImageView mItemImageView;
            public PhotoHolder(View itemView){
                 super(itemView);
                 mItemImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
             }

            public void bindDrawable(Drawable drawable){
                mItemImageView.setImageDrawable(drawable);
            }
        }

        private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{
            private List<GalleryItem> mGalleryItems;

            public PhotoAdapter(List<GalleryItem> galleryItems){
                mGalleryItems = galleryItems;
            }

            @Override
            public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View view = inflater.inflate(R.layout.gallery_view, parent, false);
                return new PhotoHolder(view);
            }

            @Override
            public void onBindViewHolder(PhotoHolder holder, int position) {
                GalleryItem galleryItem = mGalleryItems.get(position);
                Drawable placeHolder = getResources().getDrawable(R.drawable.bill_up_close);
                holder.bindDrawable(placeHolder);
                mThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());

            }

            @Override
            public int getItemCount() {
                return mGalleryItems.size();
            }
        }
    }