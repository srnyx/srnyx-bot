import xyz.srnyx.gradlegalaxy.data.DependencyConfig
import xyz.srnyx.gradlegalaxy.data.JavaSetupConfig
import xyz.srnyx.gradlegalaxy.utility.setupLazyLibrary


plugins {
    application
    id("xyz.srnyx.gradle-galaxy") version "2.0.0"
    id("com.gradleup.shadow") version "8.3.8"
    id("dev.reformator.stacktracedecoroutinator") version "2.5.6"
}

setupLazyLibrary(
    javaSetupConfig = JavaSetupConfig(
        group = "xyz.srnyx",
        version = "2.0.0",
        description = "General purpose bot for srnyx"),
    jdaConfig = DependencyConfig(version = "6.0.0-rc.5"),
    lazyLibraryConfig = DependencyConfig(version = "botcommands-v3-SNAPSHOT"))
