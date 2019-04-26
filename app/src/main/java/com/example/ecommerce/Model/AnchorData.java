package com.example.ecommerce.Model;

import com.example.ecommerce.CloudAnchorFragment;
import com.google.ar.sceneform.ux.ArFragment;

public class AnchorData {
    public String getCloudAnchorId() {
        return cloudAnchorId;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setCloudAnchorId(String cloudAnchorId) {
        this.cloudAnchorId = cloudAnchorId;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    private String cloudAnchorId, shortCode;

    public CloudAnchorFragment getArFragment() {
        return arFragment;
    }

    public void setArFragment(CloudAnchorFragment arFragment) {
        this.arFragment = arFragment;
    }

    private CloudAnchorFragment arFragment;

    public AnchorData(){

    }

    public AnchorData(String shortCode, String cloudAnchorId) {
        this.cloudAnchorId = cloudAnchorId;
        this.shortCode = shortCode;

    }


}
