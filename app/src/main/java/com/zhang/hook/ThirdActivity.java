package com.zhang.hook;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by zhang_shuai on 2017/10/17.
 * Del:
 */

public class ThirdActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("第三个Activity");
        setContentView(tv);
    }
}
