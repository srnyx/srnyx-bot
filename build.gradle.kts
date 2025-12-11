import xyz.srnyx.gradlegalaxy.data.config.DependencyConfig
import xyz.srnyx.gradlegalaxy.data.config.JavaSetupConfig
import xyz.srnyx.gradlegalaxy.utility.setupLazyLibrary


plugins {
    application
    id("xyz.srnyx.gradle-galaxy") version "2.0.2"
    id("com.gradleup.shadow") version "8.3.9"
}

setupLazyLibrary(
    javaSetupConfig = JavaSetupConfig(
        group = "xyz.srnyx",
        version = "2.0.0",
        description = "General purpose bot for srnyx"),
    jdaConfig = DependencyConfig(version = "6.1.3"),
    lazyLibraryConfig = DependencyConfig(version = "botcommands-v3-SNAPSHOT"))
