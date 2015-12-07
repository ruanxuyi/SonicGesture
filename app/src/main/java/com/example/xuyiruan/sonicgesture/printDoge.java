package com.example.xuyiruan.sonicgesture;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by kofe on 11/1/15.
 */

public class printDoge extends View {

    private boolean right=MainActivity.right;

    public printDoge (Context context, AttributeSet attrs) {
        super(context, attrs);
        new Thread(new Runnable() {

            @Override
            public void run() {
                while(true){


                }
            }
        }).start();
    }

}
