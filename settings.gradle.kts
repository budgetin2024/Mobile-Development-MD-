pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) // Mengizinkan tambahan repositori di module
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // Tambahkan JitPack jika dibutuhkan
    }
}

rootProject.name = "budgee"
include(":app")
