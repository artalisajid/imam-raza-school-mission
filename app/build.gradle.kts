import java.util.Properties
val local = Properties().apply { val file = rootProject.file("local.properties"); if (file.exists()) file.inputStream().use(::load) }
fun config(key: String) = (local.getProperty(key) ?: System.getenv(key.uppercase().replace(".", "_")) ?: "").replace("\\", "\\\\").replace("\"", "\\\"")
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}
android {
    namespace = "org.imamraza.schoolmission"
    compileSdk = 36
    defaultConfig {
        applicationId = "org.imamraza.schoolmission"
        minSdk = 26
        targetSdk = 36
        versionCode = 4
        versionName = "0.3.0-internal"
        buildConfigField("String", "SUPABASE_URL", "\""+config("supabase.url")+"\"")
        buildConfigField("String", "SUPABASE_PUBLISHABLE_KEY", "\""+config("supabase.publishable_key")+"\"")
    }
    val uploadKeystore = System.getenv("IMAM_UPLOAD_KEYSTORE")
    val uploadPassword = System.getenv("IMAM_UPLOAD_PASSWORD")
    signingConfigs {
        if (!uploadKeystore.isNullOrBlank() && !uploadPassword.isNullOrBlank()) {
            create("upload") {
                storeFile = file(uploadKeystore)
                storePassword = uploadPassword
                keyAlias = "imam-raza-upload"
                keyPassword = uploadPassword
            }
        }
    }
    buildTypes {
        getByName("release") {
            if (!uploadKeystore.isNullOrBlank() && !uploadPassword.isNullOrBlank()) {
                signingConfig = signingConfigs.getByName("upload")
            }
            isMinifyEnabled = false
        }
    }
    buildFeatures { compose = true; buildConfig = true }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}
dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.08.00")
    implementation(composeBom)
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")
    implementation("com.android.billingclient:billing-ktx:8.0.0")
    implementation("androidx.browser:browser:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    testImplementation("junit:junit:4.13.2")
}
