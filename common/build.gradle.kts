plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.android.library")
    id("io.realm.kotlin")
    id("org.jetbrains.kotlin.native.cocoapods")
    id("com.google.devtools.ksp")
    id("com.rickclephas.kmp.nativecoroutines")
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
}

android {
    compileSdk = AndroidSdk.compile
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = AndroidSdk.min
        targetSdk = AndroidSdk.target

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "dev.johnoreilly.bikeshare.common"
}


// CocoaPods requires the podspec to have a version.
version = "1.0"

kotlin {
    targets {
        val iosTarget: (String, org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget.() -> Unit) -> org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget = when {
            System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
            System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> ::iosSimulatorArm64 // available to KT 1.5.30
            else -> ::iosX64
        }
        iosTarget("iOS") {}

        macosX64("macOS")
        android()
        jvm()
    }


    cocoapods {
        // Configure fields required by CocoaPods.
        summary = "BikeShare common module"
        homepage = "homepage placeholder"
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                with(Deps.Ktor) {
                    implementation(clientCore)
                    implementation(clientJson)
                    implementation(clientLogging)
                    implementation(clientSerialization)
                    implementation(contentNegotiation)
                    implementation(json)
                }

                with(Deps.Kotlinx) {
                    implementation(coroutinesCore)
                    implementation(serializationCore)
                }

                // Realm
                implementation(Deps.realm)

                // koin
                with(Deps.Koin) {
                    api(core)
                    api(test)
                }
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(Deps.Ktor.clientAndroid)
                implementation(Deps.androidXLifecycleViewModel)
            }
        }

        val iOSMain by getting {
            dependencies {
                implementation(Deps.Ktor.clientIos)
            }
        }
        val iOSTest by getting {
        }

        val macOSMain by getting {
            dependencies {
                implementation(Deps.Ktor.clientIos)
            }
        }

        val mobileMain by creating {
            dependsOn(commonMain)
            androidMain.dependsOn(this)
            iOSMain.dependsOn(this)
            dependencies {
                implementation("com.rickclephas.kmm:kmm-viewmodel-core:${Versions.kmmViewModel}")
            }
        }


        val jvmMain by getting {
            dependencies {
                implementation(Deps.Ktor.clientJava)
                implementation(Deps.Ktor.slf4j)
            }
        }
    }
}

kotlin {
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        compilations.get("main").kotlinOptions.freeCompilerArgs += "-Xexport-kdoc"
    }
}

multiplatformSwiftPackage {
    packageName("BikeShareKit")
    swiftToolsVersion("5.3")
    targetPlatforms {
        iOS { v("13") }
        macOS{ v("10_15") }
    }
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
}


// workaround for https://youtrack.jetbrains.com/issue/KT-55751 - should be fixed in Kotlin 1.9
val myAttribute = Attribute.of("myOwnAttribute", String::class.java)

if (configurations.findByName("podDebugFrameworkIosFat") != null) {
    configurations.named("podDebugFrameworkIosFat").configure {
        attributes {
            // put a unique attribute
            attribute(myAttribute, "podDebugFrameworkIosFat")
        }

    }
}

if (configurations.findByName("podReleaseFrameworkIosFat") != null) {
    configurations.named("podReleaseFrameworkIosFat").configure {
        attributes {
            attribute(myAttribute, "podReleaseFrameworkIosFat")
        }
    }
}

if (configurations.findByName("podDebugFrameworkMacOS") != null) {
    configurations.named("podDebugFrameworkMacOS").configure {
        attributes {
            attribute(myAttribute, "podDebugFrameworkMacOS")
        }
    }
}

if (configurations.findByName("podReleaseFrameworkMacOS") != null) {
    configurations.named("podReleaseFrameworkMacOS").configure {
        attributes {
            attribute(myAttribute, "podReleaseFrameworkMacOS")
        }
    }
}

