package com.ronsong.digitclassifier;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.divyanshu.draw.widget.DrawView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawView draw_view;
    private Button clear_button;
    private TextView text_view;
    private DigitClassifier digitClassifier = new DigitClassifier(this);

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        draw_view = findViewById(R.id.draw_view);
        if(draw_view != null){
            draw_view.setStrokeWidth(70f);
            draw_view.setColor(Color.WHITE);
            draw_view.setBackgroundColor(Color.BLACK);
        }
        clear_button = findViewById(R.id.clear_button);
        text_view = findViewById(R.id.text);

        clear_button.setOnClickListener(event -> {
            draw_view.clearCanvas();
            text_view.setText("Please draw a digit");
        });

        draw_view.setOnTouchListener((view, motionEvent) -> {
            draw_view.onTouchEvent(motionEvent);
            if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                classifyDrawing();
            }
            return true;
        });
        digitClassifier
                .initialize()
                .addOnFailureListener(e -> Log.e(TAG, "Error to setting up digit classifier.", e));
    }

    @SuppressLint("SetTextI18n")
    private void classifyDrawing() {
        Bitmap bitmap = draw_view.getBitmap();
        if(digitClassifier.isInitialized()){
            digitClassifier
                    .classifyAsync(bitmap)
                    .addOnSuccessListener(resultText -> text_view.setText(resultText))
                    .addOnFailureListener(e -> {
                        text_view.setText("Error");
                        Log.e(TAG, "Error classifying drawing.", e);
                    });
        }
    }

    @Override
    protected void onDestroy() {
        digitClassifier.close();
        super.onDestroy();
    }
}