package com.ronsong.digitclassifier;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawView draw_view;
    private FloatingActionButton yes_button;
    private FloatingActionButton no_button;
    private TextView text_view;
    private DigitClassifier digitClassifier = new DigitClassifier(this);
    private FirebasePerformance firebasePerformance = FirebasePerformance.getInstance();
    private FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n", "ShowToast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        yes_button = findViewById(R.id.check_button);
        no_button = findViewById(R.id.cross_button);


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
                    yes_button.setVisibility(View.VISIBLE);
                    no_button.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        text_view = findViewById(R.id.predicted_text);

        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log("correct");
            }
        });

        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log("incorrect");
            }
        });

        ImageView readMe = findViewById(R.id.readMe_button);
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

    @SuppressLint("SetTextI18n")
    private void log(String input) {
        Bundle bundle = new Bundle();
        bundle.putString("user_input", input);
        FirebaseAnalytics.getInstance(this).logEvent("correct_inference", bundle);
        toast("Thanks for your input, event logged!");

        draw_view.clearCanvas();
        text_view.setText("Please draw a digit");
        yes_button.setVisibility(View.INVISIBLE);
        no_button.setVisibility(View.INVISIBLE);
    }

    private void setupDigitClassifier() {
        configureRemoteConfig();
        firebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            String modelName = firebaseRemoteConfig.getString("model_name");
                            downloadModel(modelName);
                            toast("Downloaded remote model: " + modelName);
                        } else {
                            toast("Failed to fetch model name");
                        }
                    }
                });
    }

    private void downloadModel(final String modelName) {
        final Trace downloadTrace = firebasePerformance.newTrace("download_model");
        downloadTrace.start();

        FirebaseCustomRemoteModel remoteModel = new FirebaseCustomRemoteModel.Builder(modelName).build();
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        FirebaseModelManager.getInstance().download(remoteModel, conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseCustomRemoteModel remoteModel = new FirebaseCustomRemoteModel.Builder(modelName).build();
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
                                            downloadTrace.stop();
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
            final Trace classifyTrace = firebasePerformance.newTrace("classify");
            classifyTrace.start();
            digitClassifier
                    .classifyAsync(bitmap)
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String resultText) {
                            classifyTrace.stop();
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

    private void configureRemoteConfig() {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}