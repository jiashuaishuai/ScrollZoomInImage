package com.example.administrator.scrollzoominimage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    MyScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        scrollView = (MyScrollView) findViewById(R.id.myscrollView);
        scrollView.setHeaderView(tv);
        scrollView.setOpen(false);
    }
}
