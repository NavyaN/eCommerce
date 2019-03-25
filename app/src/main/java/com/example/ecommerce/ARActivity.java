package com.example.ecommerce;

import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class ARActivity extends AppCompatActivity implements View.OnClickListener {

    private ArFragment arFragment;
    private ModelRenderable ObjectRenderable;
    private enum AppAnchorState {
        NONE,
        HOSTING,
        HOSTED
    }
    private AppAnchorState appAnchorState = AppAnchorState.NONE;
    Anchor anchor;
    private boolean isPlaced = false;
    private String anchorId;
   // ImageView object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        arFragment=(ArFragment)getSupportFragmentManager().findFragmentById(R.id.sceneform_ux_fragment);
      //  String value = "object1";
       // object = (ImageView)findViewById(R.id.object1);
        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {

                if(!isPlaced){
                    anchor =  arFragment.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                    appAnchorState = AppAnchorState.HOSTING;
                    setUpModel();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());
                    createModel(anchorNode);
                    isPlaced = true;
                }
            }
        });
        arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            if( appAnchorState != appAnchorState.HOSTING){
                return;
            }
            Anchor.CloudAnchorState cloudAnchorState = anchor.getCloudAnchorState();
            if(!cloudAnchorState.isError()){
                appAnchorState = AppAnchorState.HOSTED;
                anchorId = anchor.getCloudAnchorId();
                Toast.makeText(ARActivity.this,"Hosted Successfully"+anchorId, Toast.LENGTH_LONG);
            }
        });

        Button resolve =findViewById(R.id.resolve);
        resolve.setOnClickListener(view ->{
          Anchor resolvedAnchor = arFragment.getArSceneView().getSession().resolveCloudAnchor(anchorId);
            AnchorNode anchorNode = new AnchorNode(resolvedAnchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());
            createModel(anchorNode);
        });
    }

    private void setUpModel() {
        ModelRenderable.builder()
                .setSource(this,R.raw.chair)
                .build()
                .thenAccept( renderable -> ObjectRenderable =renderable)
                .exceptionally(throwable -> {
                    Toast.makeText(this,"unable to render 3D Soda model",Toast.LENGTH_LONG).show();
                    return null;
                });
    }

    private void createModel(AnchorNode anchorNode) {
            TransformableNode Object = new TransformableNode(arFragment.getTransformationSystem());
            Object.setParent(anchorNode);
            Object.setRenderable(ObjectRenderable);
            Object.select();
    }


    @Override
    public void onClick(View v) {

    }
}
