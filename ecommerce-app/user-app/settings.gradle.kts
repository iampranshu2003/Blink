pluginManagement {
    repositories {
        google() // Google's repository for Android plugins and libraries
        mavenCentral() // Maven Central for general Java/Kotlin dependencies
        gradlePluginPortal() // Gradle's official plugin repository
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google() // Standard Android libraries
        mavenCentral() // Central repository for Kotlin/Java libraries
        maven { url = uri("https://jitpack.io") } // For GitHub-hosted libraries (optional)

        // PhonePe Intent SDK repository
        maven {
            url = uri("https://phonepe.mycloudrepo.io/public/repositories/phonepe-intentsdk-android")
        }
    }
}

// Project name and module inclusion
rootProject.name = "My Application"
include(":app")
