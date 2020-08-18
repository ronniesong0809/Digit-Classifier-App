package com.ronsong.digitclassifier;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.divyanshu.draw.widget.DrawView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawView draw_view;
    private Button clear_button;
    private TextView text_view;
    private DigitClassifier digitClassifier = new DigitClassifier(this);

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n", "ShowToast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        draw_view = findViewById(R.id.draw_view);
        draw_view.setStrokeWidth(70f);
        draw_view.setColor(Color.WHITE);
        draw_view.setBackgroundColor(Color.BLACK);
        draw_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                draw_view.onTouchEvent(motionEvent);
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    classifyDrawing();
                }
                return true;
            }
        });

        text_view = findViewById(R.id.predicted_text);

        clear_button = findViewById(R.id.clear_button);
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                draw_view.clearCanvas();
                text_view.setText("Please draw a digit");
                toast("Canvas cleared");
            }
        });

        digitClassifier
                .initialize()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error to setting up digit classifier.", e);
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void classifyDrawing() {
        Bitmap bitmap = draw_view.getBitmap();
        if (digitClassifier.isInitialized()) {
            digitClassifier
                    .classifyAsync(bitmap)
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String resultText) {
                            text_view.setText(resultText);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            text_view.setText("Error");
                            Log.e(TAG, "Error classifying drawing.", e);
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        digitClassifier.close();
        super.onDestroy();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}