pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        //https://github.com/mukeshsolanki/MarkdownView-Android
        maven(url = "https://jitpack.io")
        google()
        mavenCentral()
    }
}

rootProject.name = "RandomMonkey"
include(":app")
