# IEE Apps

Android application with services of the [Information and Electronic Engineering](https://www.iee.ihu.gr) department of [International Hellenic University](https://www.ihu.gr).

This is a part of my University thesis "Development of an Android application for the services of Information and Electronic Engineering department"

![application_demos](https://i.imgur.com/cmudfV2.png)
[Download on Playstore](https://play.google.com/store/apps/details?id=gr.teithe.it.it_app)

## Background

The development started on 22/11/2018 and the first version (0.1.0-beta) released on Playstore on 09/12/2018. Major and minor changes were made until 26/01/2019. In September 2019, I was offered to assign the project as my thesis for the last semester of my studies. On 11/10/2019, the last update (0.2.3-beta) was made to the beta stage of development. 

At that time, I wanted to re-write the whole project in a much better implementation, making it maintainable and understandable for anyone who may be interested to check and/or make changes to it. Beta version code will remain closed.

Official version (1.0.0) released on 06/11/2019 and since then minor changes were made. By September 2020, the application counts 1250+ downloads, 850+ active devices and ~200 users on holidays, ~350 users during semester time and ~500 users during exams, who visit the application daily.

## Implementation

Project is implemented following [MVVM architectural pattern](https://developer.android.com/jetpack/guide) and uses:

* IT_API: [https://github.com/apavlidi/IT_API](https://github.com/apavlidi/IT_API)
* Navigation: [https://developer.android.com/guide/navigation](https://developer.android.com/guide/navigation)
* LiveData: [https://developer.android.com/topic/libraries/architecture/livedata](https://developer.android.com/topic/libraries/architecture/livedata)
* ViewModel: [https://developer.android.com/topic/libraries/architecture/viewmodel](https://developer.android.com/topic/libraries/architecture/viewmodel)
* DataBinding: [https://developer.android.com/topic/libraries/data-binding](https://developer.android.com/topic/libraries/data-binding)
* Paging: [https://developer.android.com/topic/libraries/architecture/paging](https://developer.android.com/topic/libraries/architecture/paging)
* RxJava/RxAndroid: [https://github.com/ReactiveX/RxAndroid](https://github.com/ReactiveX/RxAndroid)
* Retrofit: [https://square.github.io/retrofit/](https://square.github.io/retrofit/)
* Glide: [https://github.com/bumptech/glide](https://github.com/bumptech/glide)
* Cloud Messaging: [https://firebase.google.com/products/cloud-messaging](https://firebase.google.com/products/cloud-messaging)
* Realtime Database: [https://firebase.google.com/products/realtime-database](https://firebase.google.com/products/realtime-database)
* Analytics: [https://firebase.google.com/products/analytics](https://firebase.google.com/products/analytics)
* Crashlytics: [https://firebase.google.com/products/crashlytics](https://firebase.google.com/products/crashlytics)
* In-App Messaging: [https://firebase.google.com/products/in-app-messaging](https://firebase.google.com/products/in-app-messaging)

## Installation

#### Android Application
- Clone the project and open it on Android Studio
- Create your Apps application [here](https://login.it.teithe.gr/apps) with the following access fields
  - `Ανακοινώσεις (Προβολή)`
  - `Ειδοποιήσεις`
  - `Πλήρη στοιχεία χρήστη (Προβολή)`
  - `Διαχείριση ειδοποιήσεων`
  - `Διαχείριση δημοσίου προφίλ`
  - `Αλλαγή κωδικού πρόσβασης`
  - `Αλλαγή βασικού email`
  - `Refresh Token`
- Copy-paste your `Client ID` and `Secret` into the `IEE-Apps/app/src/main/java/gr/teithe/it/it_app/util/Constants.java` file on line `23/24`

#### Firebase (for notifications)
- Create a new Firebase project
- Create a Real Time database
- Copy rules from `firebase/rules.json` and paste them into your database rules
- Deploy `firebase/index.js` into Cloud Functions
- Add Android application to your Firebase project by registering the application's package name and downloading `google-services.json` file into `IEE-Apps/app/` folder

You can now build and run the application.

## Contribution and feedback

At this point, most of the IT_API features are implemented into the application (except the features that require higher rank), although few TODOs/Issues can be found. The application currently ranges from 99.5% to 100% crash-free rates, and most of the crash issues have been fixed.

In case you want to fix/add something, make sure you:
- Follow application's code style
- Increase the `versionCode` and `versionName` into `IEE-Apps/app/build.gradle` file
- Document changes into `CHANGELOG.md`
- Make a proper pull request

Feel free to create an issue about any problem/suggestion you may find useful for the future development of the application.

Furthermore, you can contact me using my email which can be found into my [profile](https://github.com/iamraf).

## License

Project is under [GPL-3.0 License](https://github.com/iamraf/IEE-Apps/blob/master/LICENSE).
