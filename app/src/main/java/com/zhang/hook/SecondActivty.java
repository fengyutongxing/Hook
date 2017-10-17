package com.zhang.hook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by zhang_shuai on 2017/10/17.
 * Del:
 */

public class SecondActivty extends Activity {

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("第二个Activity");
        final Intent intent = new Intent(this,ThirdActivity.class);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
        setContentView(tv);
    }
}
