import xyz.srnyx.gradlegalaxy.utility.lazyLibrary
import xyz.srnyx.gradlegalaxy.utility.setupJda


plugins {
    application
    id("xyz.srnyx.gradle-galaxy") version "1.3.3"
    id("com.gradleup.shadow") version "8.3.8"
    id("dev.reformator.stacktracedecoroutinator") version "2.5.6"
}

lazyLibrary("botcommands-v3-SNAPSHOT")
setupJda("5.6.1", "xyz.srnyx", "2.0.0", "General purpose bot for srnyx")

// Fix some tasks
tasks["distZip"].dependsOn("shadowJar")
tasks["distTar"].dependsOn("shadowJar")
tasks["startScripts"].dependsOn("shadowJar")
tasks["startShadowScripts"].dependsOn("jar")
