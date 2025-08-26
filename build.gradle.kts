import xyz.srnyx.gradlegalaxy.utility.setupLazyLibrary


plugins {
    application
    id("xyz.srnyx.gradle-galaxy") version "1.3.4"
    id("com.gradleup.shadow") version "8.3.8"
    id("dev.reformator.stacktracedecoroutinator") version "2.5.6"
}

setupLazyLibrary("botcommands-v3-SNAPSHOT", "5.6.1", "xyz.srnyx", "2.0.0", "General purpose bot for srnyx")
