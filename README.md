# Digit Classifier

Copyright (c) 2019 Ronnie Song

This is a simple Android Digit Classifier that allows its users to drawing a digit, then recognize it using the TFLite File. 

Demo: [here](https://github.com/ronniesong0809/Digit-Classifier-App/releases/tag/v1.0)

This project is adapted from the original [Tensorflow digit_classifier](https://github.com/tensorflow/examples/blob/master/lite/codelabs/digit_classifier/README.md), then convert from Koltin to Java.

## Goals
 * Experience the difference between Java and Kotlin in Android
 * Create Android interface that takes user input
 * Train models on mnist with Keras in Google Colab and then converting them into TFLIte format
 * Deploy a static TFLite model to Android app
 * Deploy TFLite models dynamically served from *Firebase ML* to Android app
 * Track user feedback to measure model accuracy with *Firebase Analytics*
 * Analyze performance of the model via *Firebase Performance*
 * Select a model via Remote Config via *Firebase Remote Config*
 * Experiment with different models Effectiveness via *Firebase A/B Testing*
 
## Sources:
* Tensorflow Lite Tutorials: https://www.tensorflow.org/lite/tutorials
* Build a digit classifier with TFLite: https://codelabs.developers.google.com/codelabs/digit-classifier-tflite
* Add Firebase to your Android App: https://codelabs.developers.google.com/codelabs/digitclassifier-android
* Use a custom TFLite model on Android: https://firebase.google.com/docs/ml/android/use-custom-models
* Deploy and manage custom models: https://firebase.google.com/docs/ml/manage-hosted-models
* Firebase Analytics: https://firebase.google.com/docs/analytics
* Firebase Performance Monitoring for Android: https://firebase.google.com/docs/perf-mon/get-started-android
* Firebase Remote Config on Android: https://firebase.google.com/docs/remote-config/use-config-android
* Firebase A/B Testing: https://firebase.google.com/docs/ab-testing

## License
This program is licensed under the "Apache License". Please see the file LICENSE in the source distribution of this software for license terms.
