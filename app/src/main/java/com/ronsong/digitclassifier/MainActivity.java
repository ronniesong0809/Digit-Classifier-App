package com.ronsong.digitclassifier;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.divyanshu.draw.widget.DrawView;

public class MainActivity extends AppCompatActivity {

    private DrawView draw_view;
    private Button clear_button;
    private TextView text_view;

    @SuppressLint({"ClickableViewAccessibility", "ShowToast", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        draw_view = findViewById(R.id.draw_view);
        draw_view.setStrokeWidth(70f);
        draw_view.setColor(Color.WHITE);
        draw_view.setBackgroundColor(Color.BLACK);
        draw_view.setOnTouchListener((view, motionEvent) -> {
            draw_view.onTouchEvent(motionEvent);
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                Toast.makeText(this, MotionEvent.ACTION_UP, Toast.LENGTH_LONG);
            }
            return true;
        });

        text_view = findViewById(R.id.predicted_text);

        clear_button = findViewById(R.id.clear_button);
        clear_button.setOnClickListener(event -> {
            draw_view.clearCanvas();
            text_view.setText("Please draw a digit");
        });
    }
}