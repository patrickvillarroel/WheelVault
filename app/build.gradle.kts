import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint)
}

group = "io.github.patrickvillarroel"
version = "2.0.4"

dependencyLocking {
    lockAllConfigurations()
}

kotlin {
    compilerOptions {
        extraWarnings.set(true)
        allWarningsAsErrors.set(false) // Until kotlin 2.3.0 fix value is never read
        optIn.addAll(
            "kotlin.contracts.ExperimentalContracts",
            "kotlin.time.ExperimentalTime",
            "kotlin.uuid.ExperimentalUuidApi",
            "androidx.compose.material3.ExperimentalMaterial3Api",
            "androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
            "androidx.compose.animation.ExperimentalSharedTransitionApi",
        )
        jvmTarget.set(JvmTarget.JVM_11)
        jvmDefault.set(JvmDefaultMode.NO_COMPATIBILITY)
    }
}

android {
    namespace = "$group.wheel.vault"
    compileSdk = libs.versions.android.targetSdk.get().toInt()

    defaultConfig {
        applicationId = "$group.wheel.vault"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 7
        versionName = version.toString()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SUPABASE_URL", "\"${env.SUPABASE_URL.value}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${env.SUPABASE_ANON_KEY.value}\"")
        buildConfigField("String", "SUPABASE_WEB_KEY", "\"${env.SUPABASE_WEB_KEY.value}\"")
        // flavor (variant) only this going to have trading enabled
        buildConfigField("boolean", "ENABLE_TRADING", env.fetch("ENABLE_TRADING", "false"))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
        shaders = false
        resValues = false
    }
}

composeCompiler {
    reportsDestination.set(layout.buildDirectory.dir("reports/compose_reports"))
    metricsDestination.set(layout.buildDirectory.dir("reports/compose_metrics"))
}

dependencies {
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.kotlinx.serialization)
    implementation(libs.ktor.client.engine.cio)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui.text.google.fonts)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.androidx.startup)
    implementation(libs.koin.compose.viewmodel)

    implementation(libs.coil.compose)
    implementation(libs.coil.ktor3)

    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.compose.auth.ui)
    implementation(libs.supabase.compose.coil3)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.postgres)
    implementation(libs.supabase.storage)

    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.mlkit.vision)
    implementation(libs.text.recognition)

    implementation(libs.androidx.credentials)
    implementation(libs.android.identity.googleid)
    implementation(libs.androidx.credentials.play.services.auth)

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui.compose)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockk.android)
    testImplementation(libs.mockk.agent)

    debugImplementation(platform(libs.compose.bom))
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    ktlintRuleset(libs.ktlint.compose)
}

ktlint {
    version.set(libs.versions.ktlint.core.get())
    ignoreFailures.set(true)
}
