plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    ktlint {
        verbose.set(true)
        android.set(true)
        filter {
            exclude("**/generated/**")
        }
    }
}

apply(plugin = "io.gitlab.arturbosch.detekt")
detekt {
    parallel = true
}
