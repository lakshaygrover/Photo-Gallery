package com.lakshaygrover2926.myphotogallery;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
   public Fragment createFragment(){
        return  PhotoGalleryFragment.newInstance();
    }
}
