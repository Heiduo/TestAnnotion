package com.heiduo.testannotion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.heiduo.annotation.ViewById;
import com.heiduo.annotationlibrary.ViewInjector;

public class MainActivity extends AppCompatActivity {

    @ViewById(R.id.tvText)
    TextView tvText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewInjector.injectView(this);
        tvText.setOnClickListener(v -> tvText.setText("测试"));
    }
}