package com.example.ecommerce;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ecommerce.Model.AllAnchorsData;
import com.example.ecommerce.Model.AnchorData;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ARActivity extends AppCompatActivity {

    private CloudAnchorFragment arFragment;
    private Anchor cloudAnchor;

    private enum AppAnchorState {
        NONE,
        HOSTING,
        HOSTED,
        RESOLVING,
        RESOLVED
    }

    private AppAnchorState appAnchorState = AppAnchorState.NONE;
    private SnackbarHelper snackbarHelper = new SnackbarHelper();
    private FireBaseAnchor storageManager;
    private SingleTonClass singleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        storageManager = new FireBaseAnchor(ARActivity.this);
        arFragment = (CloudAnchorFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
        //to add
         singleton =  (SingleTonClass) getApplicationContext();
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);

        Button clearButton = findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCloudAnchor(null);
            }
        });
        Button resolveButton = findViewById(R.id.resolve_button);
        resolveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cloudAnchor != null) {
                    snackbarHelper.showMessageDismiss(getParent(), "please clear anchor");
                    return;
                }
                ResolveDialogFragment dialogFragment = new ResolveDialogFragment();
                dialogFragment.setOkListener(ARActivity.this::onResolveOkPressed);
                dialogFragment.show(getSupportFragmentManager(), "Resolve");
            }
        });


        Intent intent = getIntent();
       // String id = intent.getStringExtra("id");
       // Session session = arFragment.getArSceneView().getSession();
       // Session session1 =singleton.getArFragment().getArSceneView().getSession();
        //arFragment.getArSceneView().setupSession(singleton.getArFragment().getArSceneView().getSession());
       // Session session1 = (Object) session;
//        if(id != null){
//
//            try{
//                Session session = arFragment.getArSceneView().getSession();
//                System.out.print(session);
//                //resolveAnchor(id);
//            }catch (Exception ex){
//                System.out.print(ex.getMessage());
//            }
//
//            return;
//        }
       // String name = intent.getStringExtra("name");

        //singleton.setArFragment(arFragment);
        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING || appAnchorState != AppAnchorState.NONE) {
                        return;
                    }

                  //  Session session = arFragment.getArSceneView().getSession();
                    //create an anchor
                    Anchor newanchor = arFragment.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                    setCloudAnchor(newanchor);
                    appAnchorState = AppAnchorState.HOSTING;
                    snackbarHelper.showMessage(this, "Now hosting anchor..");
                    placeObject(arFragment, cloudAnchor, Uri.parse("chair1.sfb"));
                });
        storageManager.getAllCloudAnchorsData();
       //AllAnchorsData allAnchorsData = singleton.allAnchorsData;
       // List<AnchorData> anchorDatas = allAnchorsData.getMyList();
//        if(id != null){
//            resolveAnchor(id);
//        }
    }

    private void onResolveOkPressed(String dialogValue) {
        int shortcode = Integer.parseInt(dialogValue);
        List<AnchorData> allAnchorsData = singleton.getAllAnchorsData();
        String cloudAnchorId = null;
        for (AnchorData c : allAnchorsData) {
            if (c.getShortCode().equals(String.valueOf(shortcode))) {
                cloudAnchorId = c.getCloudAnchorId();
            }
        }
        //String cloudAnchorId = storageManager.getCloudAchorID(this, String.valueOf(shortcode), arFragment.getArSceneView().getSession());
        Anchor resolvedAnchor = arFragment.getArSceneView().getSession().resolveCloudAnchor(cloudAnchorId);
        setCloudAnchor(resolvedAnchor);
        placeObject(arFragment, cloudAnchor, Uri.parse("chair1.sfb"));
        snackbarHelper.showMessage(this, "Now resolving anchor..");
        appAnchorState = AppAnchorState.RESOLVING;
    }


//    public void resolveAnchor(String cloudAnchorId){
//        try {
//
//        }catch (Exception ex){
//            System.out.print(ex.getMessage());
//        }
//
//    }

    private void setCloudAnchor(Anchor newAnchor) {
        if (cloudAnchor != null) {
            cloudAnchor.detach();

        }
        cloudAnchor = newAnchor;
        appAnchorState = AppAnchorState.NONE;
        snackbarHelper.hide(this);
    }

    private void onUpdateFrame(FrameTime frameTime) {
        checkUpdatedAnchor();
    }

    private synchronized void checkUpdatedAnchor() {
        if (appAnchorState != AppAnchorState.HOSTING && appAnchorState != AppAnchorState.RESOLVING) {
            return;
        }
        Anchor.CloudAnchorState cloudAnchorState = cloudAnchor.getCloudAnchorState();
        if (appAnchorState == AppAnchorState.HOSTING) {
            if (cloudAnchorState.isError()) {
                snackbarHelper.showMessageDismiss(this, "Error hosting the anchor.." + cloudAnchorState);
                appAnchorState = AppAnchorState.NONE;
            } else if (cloudAnchorState == Anchor.CloudAnchorState.SUCCESS) {
                int shortcode = storageManager.nextShortCode(this);//Add this line
                storageManager.storeUsingShortCode(this, String.valueOf(shortcode), cloudAnchor.getCloudAnchorId(), arFragment);
                snackbarHelper.showMessageDismiss(this, "Anchor hosted Sucessfully!cloudshortcode" + shortcode);//change
                appAnchorState = AppAnchorState.HOSTED;
            }
        } else if (appAnchorState == AppAnchorState.RESOLVING) {
            if (cloudAnchorState.isError()) {
                snackbarHelper.showMessageDismiss(this, "Error resolving anchor.." + cloudAnchorState);
                appAnchorState = AppAnchorState.NONE;
            } else if (cloudAnchorState == Anchor.CloudAnchorState.SUCCESS) {
                snackbarHelper.showMessageDismiss(this, "Anchor resolved Succesfully");
                appAnchorState = AppAnchorState.RESOLVED;
            }
        }
    }

    private void placeObject(ArFragment arFragment, Anchor anchor, Uri model) {
        ModelRenderable.builder()
                .setSource(arFragment.getContext(), model)
                .build()
                .thenAccept(renderable -> addNodeToScene(arFragment, anchor, renderable))
                .exceptionally((throwable -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(throwable.getMessage())
                            .setTitle("Error");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return null;
                }));
    }

    private void addNodeToScene(ArFragment arFragment, Anchor anchor, Renderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        node.getScaleController().setMaxScale(0.50f);
        node.getScaleController().setMinScale(0.20f);
        node.setLocalPosition(new Vector3(0.0f,0.0f,0.0f));
        node.select();
    }

}
