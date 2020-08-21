# Digit Classifier

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fronniesong0809%2FDigit-Classifier-App.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fronniesong0809%2FDigit-Classifier-App?ref=badge_shield)
[![CodeFactor](https://www.codefactor.io/repository/github/ronniesong0809/digit-classifier-app/badge)](https://www.codefactor.io/repository/github/ronniesong0809/digit-classifier-app)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/aad29daa9412448d995cb8e33c019226)](https://www.codacy.com/manual/ronsong/Digit-Classifier-App?utm_source=github.com&utm_medium=referral&utm_content=ronniesong0809/Digit-Classifier-App&utm_campaign=Badge_Grade)

Copyright (c) 2019 Ronnie Song

This is a simple Android Digit Classifier that allows its users to drawing a digit, then recognize it using the TFLite File.

Demo: [here](https://github.com/ronniesong0809/Digit-Classifier-App/releases/tag/v1.0)

This project is adapted from the original [Tensorflow digit_classifier](https://github.com/tensorflow/examples/blob/master/lite/codelabs/digit_classifier/README.md), then convert from Koltin to Java.

## What I've Learned

-   Experience the difference between Java and Kotlin in Android
-   Create Android interface that takes user input
-   Train models on mnist with Keras in Google Colab and then converting them into TFLIte format
-   Deploy a static TFLite model to Android app
-   Deploy TFLite models dynamically served from _Firebase ML_ to Android app
-   Track user feedback to measure model accuracy with _Firebase Analytics_
-   Analyze performance of the model via _Firebase Performance_
-   Select a model via Remote Config via _Firebase Remote Config_
-   Experiment with different models Effectiveness via _Firebase A/B Testing_

## Sources

-   Tensorflow Lite Tutorials: <https://www.tensorflow.org/lite/tutorials>
-   Build a digit classifier with TFLite: <https://codelabs.developers.google.com/codelabs/digit-classifier-tflite>
-   Add Firebase to your Android App: <https://codelabs.developers.google.com/codelabs/digitclassifier-android>
-   Use a custom TFLite model on Android: <https://firebase.google.com/docs/ml/android/use-custom-models>
-   Deploy and manage custom models: <https://firebase.google.com/docs/ml/manage-hosted-models>
-   Firebase Analytics: <https://firebase.google.com/docs/analytics>
-   Firebase Performance Monitoring for Android: <https://firebase.google.com/docs/perf-mon/get-started-android>
-   Firebase Remote Config on Android: <https://firebase.google.com/docs/remote-config/use-config-android>
-   Firebase A/B Testing: <https://firebase.google.com/docs/ab-testing>

## License

This program is licensed under the "Apache License". Please see the [LICENSE](https://github.com/ronniesong0809/Digit-Classifier-App/blob/master/LICENSE) file in the source distribution of this software for license terms.

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fronniesong0809%2FDigit-Classifier-App.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Fronniesong0809%2FDigit-Classifier-App?ref=badge_large)
