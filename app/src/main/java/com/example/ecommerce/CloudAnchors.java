package com.example.ecommerce;

import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecommerce.Model.CustomARFragment;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class CloudAnchors extends AppCompatActivity {

    private ArFragment arFragment;
    private CustomARFragment customARFragment;

    public CloudAnchors() {
    }

    private enum AppAnchorState {
        NONE,
        HOSTING,
        HOSTED,
        RESOLVING,
        RESOLVED
    }
    private Anchor anchor;
    private CloudAnchors.AppAnchorState appAnchorState = CloudAnchors.AppAnchorState.NONE;
    private boolean isPlaced = false;
     //private SnackbarHelper
    private ModelRenderable ObjectRenderable;
    private String anchorId = "0000";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_anchors);

        customARFragment=(CustomARFragment)getSupportFragmentManager().findFragmentById(R.id.sceneform_ux_fragment);
        //customARFragment.getPlaneDiscoveryController().hide();

       try {

           customARFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
               if (!isPlaced) {

                   anchor = customARFragment.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                   appAnchorState = AppAnchorState.HOSTING;
                   createModel(anchor);
                   isPlaced=true;
               }
           });
       }catch (Exception ex){
        System.out.println("Exception" +ex.getMessage());
       }
        customARFragment.getArSceneView().getScene().addOnUpdateListener(new Scene.OnUpdateListener() {
            @Override
            public void onUpdate(FrameTime frameTime) {

                if(appAnchorState != AppAnchorState.HOSTING){
                    return;
                }
                Anchor.CloudAnchorState cloudAnchorState = anchor.getCloudAnchorState();
                if(cloudAnchorState.isError()){
                    showToast(cloudAnchorState.toString() + "I am ur exception");
                }else if(cloudAnchorState == Anchor.CloudAnchorState.SUCCESS) {
                    appAnchorState = AppAnchorState.HOSTED;
                    anchorId = anchor.getCloudAnchorId();
                    showToast("Anchor Hosted Successfully" + anchorId);
                }
            }


        });
        Button resolve =findViewById(R.id.resolve);
        resolve.setOnClickListener(view ->{
              Anchor resolvedAnchor = arFragment.getArSceneView().getSession().resolveCloudAnchor(anchorId);
            createModel(resolvedAnchor);
        });
    }

    private void showToast(String s) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }
    private void createModel(Anchor anchor) {
        ModelRenderable.builder()
                .setSource(this, Uri.parse("chair2.sfb"))
                .build()
                .thenAccept( modelRenderable -> placeModel(anchor,modelRenderable))
                .exceptionally(throwable -> {
                    Toast.makeText(this,"unable to render 3D Soda model",Toast.LENGTH_LONG).show();
                    return null;
                });
    }

    private void placeModel(Anchor anchor, ModelRenderable modelRenderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setRenderable(modelRenderable);
        customARFragment.getArSceneView().getScene().addChild(anchorNode);
    }
    private void createModel1(AnchorNode anchorNode) {
        TransformableNode Object = new TransformableNode(arFragment.getTransformationSystem());
        Object.setParent(anchorNode);
        Object.setRenderable(ObjectRenderable);
        Object.setLocalPosition(new Vector3(0.0f,0.0f,0.0f));
        Object.select();
    }
}
