package com.dsouchon.wayo.visualfusion;

import java.util.Comparator;

public class CustomComparator implements Comparator<AndroidStoreUnitExplicit> {
    @Override
    public int compare(AndroidStoreUnitExplicit o1, AndroidStoreUnitExplicit o2) {
        return o1.getItemTypeName().compareTo(o2.getItemTypeName());
    }
}
