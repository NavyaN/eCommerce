package com.example.ecommerce;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class ResolveARActivity extends AppCompatActivity {


    private CloudAnchorFragment arFragment;
    private Anchor cloudAnchor;

    private enum AppAnchorState {
        NONE,
        HOSTING,
        HOSTED,
        RESOLVING,
        RESOLVED
    }
    private ResolveARActivity.AppAnchorState appAnchorState = ResolveARActivity.AppAnchorState.NONE;
    private SnackbarHelper snackbarHelper = new SnackbarHelper();
    private FireBaseAnchor storageManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolve_ar);
        arFragment = (CloudAnchorFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
        //to add
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
        storageManager = new FireBaseAnchor(ResolveARActivity.this);
        Intent intent = getIntent();
        String id = intent.getStringExtra("anchordId");
        if(id != null){
            resolveAnchor(id);
        }
    }
    private void onUpdateFrame(FrameTime frameTime) {
        checkUpdatedAnchor();
    }
    public void resolveAnchor(String cloudAnchorId){
        try {
            Anchor resolvedAnchor = arFragment.getArSceneView().getSession().resolveCloudAnchor(cloudAnchorId);
            setCloudAnchor(resolvedAnchor);
            placeObject(arFragment, cloudAnchor, Uri.parse("chair1.sfb"));
            snackbarHelper.showMessage(this, "Now resolving anchor..");
            appAnchorState = ResolveARActivity.AppAnchorState.RESOLVING;
        }catch (Exception ex){
            System.out.print(ex.getMessage());
        }

    }
    private synchronized void checkUpdatedAnchor() {
        if (appAnchorState != ResolveARActivity.AppAnchorState.HOSTING && appAnchorState != ResolveARActivity.AppAnchorState.RESOLVING) {
            return;
        }
        Anchor.CloudAnchorState cloudAnchorState = cloudAnchor.getCloudAnchorState();
        if (appAnchorState == ResolveARActivity.AppAnchorState.HOSTING) {
            if (cloudAnchorState.isError()) {
                snackbarHelper.showMessageDismiss(this, "Error hosting the anchor.." + cloudAnchorState);
                appAnchorState = ResolveARActivity.AppAnchorState.NONE;
            } else if (cloudAnchorState == Anchor.CloudAnchorState.SUCCESS) {
                int shortcode = storageManager.nextShortCode(this);//Add this line
               // storageManager.storeUsingShortCode(this, String.valueOf(shortcode), cloudAnchor.getCloudAnchorId());
                snackbarHelper.showMessageDismiss(this, "Anchor hosted Sucessfully!cloudshortcode" + shortcode);//change
                appAnchorState = ResolveARActivity.AppAnchorState.HOSTED;
            }
        } else if (appAnchorState == ResolveARActivity.AppAnchorState.RESOLVING) {
            if (cloudAnchorState.isError()) {
                snackbarHelper.showMessageDismiss(this, "Error resolving anchor.." + cloudAnchorState);
                appAnchorState = ResolveARActivity.AppAnchorState.NONE;
            } else if (cloudAnchorState == Anchor.CloudAnchorState.SUCCESS) {
                snackbarHelper.showMessageDismiss(this, "Anchor resolved Succesfully");
                appAnchorState = ResolveARActivity.AppAnchorState.RESOLVED;
            }
        }
    }
    private void setCloudAnchor(Anchor newAnchor) {
        if (cloudAnchor != null) {
            cloudAnchor.detach();

        }
        cloudAnchor = newAnchor;
        appAnchorState = ResolveARActivity.AppAnchorState.NONE;
        snackbarHelper.hide(this);
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
