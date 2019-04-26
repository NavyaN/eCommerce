package com.example.ecommerce.Model;

import java.util.ArrayList;
import java.util.List;

public class AllAnchorsData {
    public List<AnchorData> getMyList() {
        return myList;
    }

    public void setMyList(List<AnchorData> myList) {
        this.myList = myList;
    }

    List<AnchorData> myList = new ArrayList<AnchorData>();

}
