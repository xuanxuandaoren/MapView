package com.scaleview.mingda.mapview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.scaleview.mingda.mapview.view.MapView;
import com.scaleview.mingda.mapview.view.TestAdapter;

public class MainActivity extends AppCompatActivity {

    private MapView mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        mp = (MapView) findViewById(R.id.mp);
        mp.setAdapter(new TestAdapter(this));
    }
}
