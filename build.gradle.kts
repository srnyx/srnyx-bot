import xyz.srnyx.gradlegalaxy.data.config.DependencyConfig
import xyz.srnyx.gradlegalaxy.data.config.JavaSetupConfig
import xyz.srnyx.gradlegalaxy.utility.setupLazyLibrary


plugins {
    application
    id("xyz.srnyx.gradle-galaxy") version "2.1.0"
    id("com.gradleup.shadow") version "8.3.9"
}

setupLazyLibrary(
    javaSetupConfig = JavaSetupConfig(
        group = "xyz.srnyx",
        version = "2.0.0",
        description = "General purpose bot for srnyx"),
    jdaConfig = DependencyConfig(version = "6.4.1"),
    lazyLibraryConfig = DependencyConfig(version = "977e285"))
