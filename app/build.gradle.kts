plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.agrimart"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.agrimart"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    sourceSets {
        getByName("main") {
            res {
                srcDirs("src\\main\\res", "src\\main\\res\\animator")
            }
        }
    }
    buildFeatures{
        viewBinding=true;
        dataBinding=true;
        buildConfig = true
    }
}
dependencies {
    implementation("com.github.wdsqjq:AndRatingBar:1.0.6")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.yalantis:ucrop:2.2.6")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.22")
    implementation("androidx.viewpager:viewpager:1.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.9.1")
    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("com.google.android.material:material:1.12.0")
    implementation("com.google.firebase:firebase-firestore:25.1.1")
    implementation("com.google.firebase:firebase-auth:23.1.0")
    implementation("com.google.firebase:firebase-storage:21.0.1")
    implementation("com.google.firebase:firebase-messaging:24.0.3")
    implementation("com.google.firebase:firebase-inappmessaging-display:21.0.1")
    implementation("androidx.navigation:navigation-fragment:2.8.3")
    implementation("androidx.navigation:navigation-ui:2.8.3")
    //ZaloPay
    implementation(fileTree(mapOf(
        "dir" to "D:\\Data\\Android\\DoAn\\ZaloPay",
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.github.dhaval2404:imagepicker:2.1")
//  implementation ("io.github.ParkSangGwon:tedimagepicker:1.6.1")
    // ViewModel
//    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.5")
    implementation ("com.facebook.android:facebook-login:latest.release")
    implementation("androidx.browser:browser:1.8.0")

    implementation("com.squareup.okhttp3:okhttp:3.14.1")
    implementation(files("libs/merchant-1.0.25.aar"))

    implementation ("com.squareup.retrofit2:converter-jackson:2.10.0")
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.17.2")

}
