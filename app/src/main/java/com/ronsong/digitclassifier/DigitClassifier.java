/* Copyright 2020 Ronnie Song

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */

package com.ronsong.digitclassifier;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class DigitClassifier {

    private static final int PIXEL_SIZE = 1;
    private static final int FLOAT_TYPE_SIZE = 4;
    private static final int OUTPUT_CLASSES_COUNT = 10;
    private static final String TAG = "DigitClassifier";
    private static final String MODEL_FILE = "mnist.tflite";

    private Context context;
    private Interpreter interpreter;
    private boolean isInitialized = false;
    private ExecutorService executorService;
    private int inputImageWidth = 0;
    private int inputImageHeight = 0;
    private int modelInputSize = 0;

    DigitClassifier(Context context) {
        this.context = context;
        executorService = Executors.newCachedThreadPool();
    }

    Task<Void> initialize(final Object model) {
        return Tasks.call(executorService, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                initInterpreter(model);
                return null;
            }
        });
    }

    private void initInterpreter(Object model) throws IOException {

        Interpreter.Options options = new Interpreter.Options();
        options.setUseNNAPI(true);
        Interpreter interpreter;
        if (model!=null) {
            interpreter = new Interpreter((File) model, options);
        } else {
            AssetManager assetManager = this.context.getAssets();
            model = loadModelFile(assetManager);
            interpreter = new Interpreter((ByteBuffer) model, options);
        }


        int[] inputShape = interpreter.getInputTensor(0).shape();
        inputImageWidth = inputShape[1];
        inputImageHeight = inputShape[2];
        modelInputSize = FLOAT_TYPE_SIZE * inputImageHeight * inputImageWidth * PIXEL_SIZE;
        this.interpreter = interpreter;
        isInitialized = true;
        Log.d(TAG, "Initialized TF-Lite interpreter");
    }

    private ByteBuffer loadModelFile(AssetManager assetManager) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private String classify(Bitmap bitmap) {
        if (!isInitialized)
            throw new IllegalStateException("TF Lite Interpreter is not initialized yet.");
        long startTime, elapsedTime;
        startTime = System.nanoTime();
        Bitmap resizedImage = Bitmap.createScaledBitmap(bitmap, inputImageWidth, inputImageHeight, true);
        ByteBuffer byteBuffer = convertBitmapToByteBuffer(resizedImage);
        elapsedTime = (System.nanoTime() - startTime) / 1_000_000;
        Log.d(TAG, "Pre-processing time = " + elapsedTime + " ms");

        startTime = System.nanoTime();
        float[][] result = {new float[OUTPUT_CLASSES_COUNT]};
        interpreter.run(byteBuffer, result);
        elapsedTime = (System.nanoTime() - startTime) / 1_000_000;
        Log.d(TAG, "Inference time = " + elapsedTime + " ms");

        return getOutputString(result[0]);
    }

    Task<String> classifyAsync(final Bitmap bitmap) {
        return Tasks.call(executorService, new Callable<String>() {
            @Override
            public String call() throws Exception {
                return classify(bitmap);
            }
        });
    }

    void close() {
        Tasks.call(executorService, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                interpreter.close();
                Log.d(TAG, "Closed TF-Lite interpreter.");
                return null;
            }
        });
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(modelInputSize);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] pixels = new int[inputImageWidth * inputImageHeight];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int pixelValue : pixels) {
            int r = (pixelValue >> 16) & 0xFF;
            int g = (pixelValue >> 8) & 0xFF;
            int b = pixelValue & 0xFF;
            float normalizedPixelValue = (r + g + b) / 3.0f / 255.0f;
            byteBuffer.putFloat(normalizedPixelValue);
        }
        return byteBuffer;
    }

    private String getOutputString(float[] output) {
        int maxIndex = -1;
        float max = output[0];
        for (int i = 0; i < output.length; i++) {
            if (output[i] > max) {
                max = output[i];
                maxIndex = i;
            }
        }
        return "Prediction: " + maxIndex + "\nConfidence: " + max;
    }

    boolean isInitialized() {
        return isInitialized;
    }
}