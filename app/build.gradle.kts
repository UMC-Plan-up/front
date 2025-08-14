plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.planup"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.planup"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.mpandroidchart)
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.viewpager2)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.kakao.sdk:v2-user:2.21.6")
    implementation("com.kakao.sdk:v2-share:2.21.6")
    implementation("com.kakao.sdk:v2-auth:2.21.6")
    /*gson 사용하기 위한 라이브러리 다운*/
//    implementation("com.google.code.gson:gson:2.8.7")
    implementation("com.google.code.gson:gson:2.13.1")
    //Retrofit 사용을 위한 lib 추가
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    //implementation(libs.kotlin.stdlib.v190)
    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:3.0.0")
    //Retrofit은 okhttp 기반으로 작성됨
//    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
//    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")

    implementation("com.kizitonwose.calendar:view:2.6.2")
    implementation("com.getkeepsafe.taptargetview:taptargetview:1.15.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    implementation("com.android.billingclient:billing-ktx:6.1.0")
    implementation("androidx.core:core-splashscreen:1.0.1")

}
