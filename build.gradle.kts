import xyz.srnyx.gradlegalaxy.data.config.DependencyConfig
import xyz.srnyx.gradlegalaxy.data.config.JavaSetupConfig
import xyz.srnyx.gradlegalaxy.utility.setupLazyLibrary


plugins {
    application
    id("xyz.srnyx.gradle-galaxy") version "3.2.0"
    id("com.gradleup.shadow") version "9.5.1"
}

setupLazyLibrary(
    javaSetupConfig = JavaSetupConfig(
        group = "xyz.srnyx",
        version = "2.0.0",
        description = "General purpose bot for srnyx"),
    jdaConfig = DependencyConfig(version = "6.5.0"),
    lazyLibraryConfig = DependencyConfig(version = "8cb5ea4"))
