plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
}

android {
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    namespace = "io.lumyuan.filescope"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.document.file)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.lumyuan"
            artifactId = "file-scope"
            version = "0.0.2"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}