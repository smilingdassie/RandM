package com.dsouchon.wayo.visualfusion;

import android.app.Application;

/**
 * Created by Daniel on 2018/06/01.
 */

public class MyApplication extends Application {

    private String someVariable;

    public String getSomeVariable() {
        return someVariable;
    }

    public void setSomeVariable(String someVariable) {
        this.someVariable = someVariable;
    }

    private String requestID;

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;

    }


}
