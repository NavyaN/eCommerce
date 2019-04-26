package com.example.ecommerce;

import android.app.Application;

import com.example.ecommerce.Model.AllAnchorsData;
import com.example.ecommerce.Model.AnchorData;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.ArrayList;
import java.util.List;

public class SingleTonClass  extends Application {
    public CloudAnchorFragment getArFragment() {
        return arFragment;
    }

    public List<AnchorData> getAllAnchorsData() {
        return allAnchorsData;
    }

    public void setAllAnchorsData(List<AnchorData> allAnchorsData) {
        this.allAnchorsData = allAnchorsData;
    }

    List<AnchorData> allAnchorsData = new ArrayList<AnchorData>();

   // public AllAnchorsData allAnchorsData;

    public void setArFragment(CloudAnchorFragment arFragment) {
        this.arFragment = arFragment;
    }


    CloudAnchorFragment arFragment;
   // private static  SingleTonClass instance;

//    public static  SingleTonClass getInstance(){
//        if(instance == null)
//            return  new SingleTonClass();
//         return instance;
//    }
}