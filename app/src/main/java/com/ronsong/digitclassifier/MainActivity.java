package com.ronsong.digitclassifier;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.divyanshu.draw.widget.DrawView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawView draw_view;
    private FloatingActionButton clear_button;
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

        FloatingActionButton readMe = findViewById(R.id.readMe_button);
        readMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReadMe();
            }
        });

        /*digitClassifier
                .initialize()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error to setting up digit classifier.", e);
                    }
                });*/
        setupDigitClassifier();
    }

    private void setupDigitClassifier() {
        downloadModel("mnist_v1");
    }

    private void downloadModel(String modelName) {
        FirebaseCustomRemoteModel remoteModel = new FirebaseCustomRemoteModel.Builder(modelName).build();
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        FirebaseModelManager.getInstance().download(remoteModel, conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseCustomRemoteModel remoteModel = new FirebaseCustomRemoteModel.Builder("mnist_v1").build();
                        FirebaseModelManager.getInstance().getLatestModelFile(remoteModel)
                                .addOnCompleteListener(new OnCompleteListener<File>() {
                                    @Override
                                    public void onComplete(@NonNull Task<File> task) {
                                        File model = task.getResult();
                                        if (model == null) {
                                            toast("Failed to get model file.");
                                            toast("Use local model.");
                                            digitClassifier
                                                    .initialize(null)
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.e(TAG, "Error to setting up digit classifier.", e);
                                                            toast("Error to setting up digit classifier.");
                                                        }
                                                    });
                                        } else {
                                            toast("Downloaded remote model: " + model.getParentFile().getName());
                                            digitClassifier.initialize(model);
                                        }
                                    }
                                });
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

    private void ReadMe() {
        String message = "Copyright (c) 2020 Ronnie Song\n\n";
        message += "This is a simple Android Digit Classifier that allows its users to drawing a digit, then recognize it.";

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.baseline_help_outline_24)
                .setTitle("README")
                .setMessage(message)
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}