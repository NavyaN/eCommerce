package com.example.ecommerce;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.ecommerce.Model.AllAnchorsData;
import com.example.ecommerce.Model.AnchorData;
import com.example.ecommerce.Model.CurrentUser;
import com.example.ecommerce.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Random;

import static android.support.constraint.Constraints.TAG;
import static android.view.View.resolveSize;

public class FireBaseAnchor  {
    private static final String NEXT_SHORT_CODE ="next_short_code";
    private static final String KEY_PREFIX ="anchor;";
    private static final int INITIAL_SHORT_CODE=142;
    final int min = 100;
    final int max = 1000;
    int random ;
    private String anchorId;
    private Context context;

    public FireBaseAnchor(Context context){
        this.context = context;
    }
    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
   // databaseReference = FirebaseDatabase.getInstance().getReference();
    /**Gets a new short code that can be used to store the anchorID.*/
    int nextShortCode(Activity activity){
        random = new Random().nextInt((max - min) + 1) + min;
        return random;
    }
    /**Stores the cloud anchor ID in the activity's SharedPreference.*/
    void storeUsingShortCode(Activity activity,String shortcode,String cloudAnchorId, String value){
        final DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                if(!(dataSnapshot.child("CloudAnchorData").child(String.valueOf(shortcode)).exists())){
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("shortCode",shortcode);
                    userDataMap.put("cloudAnchorId", cloudAnchorId);
                    userDataMap.put("objectPlaced", value);
                    databaseReference.child("CloudAnchorData").child(shortcode).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //
                        }
                    });
                }
                else {
                    String newshortcode =  String.valueOf(nextShortCode(activity));
                    if(!(dataSnapshot.child("CloudAnchorData").child(String.valueOf(newshortcode)).exists())){
                        HashMap<String, Object> userDataMap = new HashMap<>();
                        userDataMap.put("shortCode",newshortcode);
                        userDataMap.put("cloudAnchorId", cloudAnchorId);
                        userDataMap.put("objectPlaced", value);
                        databaseReference.child("CloudAnchorData").child(newshortcode).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //
                            }
                        });
                    }
                }}catch (Exception ex){
                    System.out.print(ex.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    /***
     * Retrives the cloud anchor ID using a shortcode.Returns an empty string if a cloud anchor ID
     * was not stored for this short code
     */
    String getCloudAchorID(Activity activity,String shortCode, Session session){
        getValueFromDataBase(shortCode);
        return anchorId;
    }

    String getValueFromDataBase(String shortCode){

        databaseReference.child("CloudAnchorData").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "step 2" + anchorId.toString());
                    AnchorData usersData = dataSnapshot.child("CloudAnchorData").child(shortCode).getValue(AnchorData.class);
                    Log.d(TAG, "inside method" +usersData.toString());
                    if(usersData.getShortCode().equals(shortCode)){
                        anchorId = usersData.getCloudAnchorId().toString();
                        Log.d(TAG, anchorId.toString());
                        return;
                    }
                 Log.d(TAG, anchorId.toString());
                Log.d("TAG", "Inside onDataChange() method!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        Log.d("TAG", "After attaching the listener!" +anchorId.toString());
        return anchorId;
    }

    void getAllCloudAnchorsData(){
        final DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();
         SingleTonClass singleton =  (SingleTonClass) context.getApplicationContext();
        Log.d(TAG, "step 1");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("TAG", "Inside onDataChange() method!");
                for(DataSnapshot ds: dataSnapshot.child("CloudAnchorData").getChildren()){
                    singleton.allAnchorsData.add(ds.getValue(AnchorData.class));
                }
               // singleton.allAnchorsData = dataSnapshot.child("CloudAnchorData").getValue(AllAnchorsData.class);
                System.out.print(singleton.allAnchorsData);
                Log.d(TAG, "hey I am working");
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"after cancellation");
            }
        });
        Log.d(TAG,"AFter Listerning");
    }

    void getValueFromDB(String shortCode, Session session){
        final DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();
       // SingleTonClass singleton =  (SingleTonClass) context.getApplicationContext();
        Log.d(TAG, "step 1");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("TAG", "Inside onDataChange() method!");
                if(dataSnapshot.child("CloudAnchorData").child(shortCode).exists()){
                    Log.d(TAG, "step 2");
                    AnchorData usersData = dataSnapshot.child("CloudAnchorData").child(shortCode).getValue(AnchorData.class);
                    if(usersData.getShortCode().equals(shortCode))
                        System.out.print(usersData);
                        anchorId = usersData.getCloudAnchorId();
                        Log.d(TAG, anchorId.toLowerCase());
//                       // singleton.setArFragment(usersData.getArFragment());
//                        Intent intent = new Intent(context, ARActivity.class);
//                        intent.putExtra("id", anchorId);
//                        context.startActivity(intent);
                }
                Log.d(TAG, "hey I am working");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
              Log.d(TAG,"after cancellation");
            }
        });
        Log.d(TAG,"AFter Listerning");
    }
}
