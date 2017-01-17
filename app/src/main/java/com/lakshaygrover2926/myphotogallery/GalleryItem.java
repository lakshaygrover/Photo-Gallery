package com.lakshaygrover2926.myphotogallery;

/**
 * Created by LAKSHAY on 1/16/2017.
 */
public class GalleryItem {

    private String mCaption;
    private String mUrl;
    private String mId;


    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String mcaption) {
        this.mCaption = mcaption;
    }



    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }



    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }



    @Override
    public String toString(){
        return mCaption;
    }
}
