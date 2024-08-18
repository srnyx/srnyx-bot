import xyz.srnyx.gradlegalaxy.enums.Repository
import xyz.srnyx.gradlegalaxy.enums.repository
import xyz.srnyx.gradlegalaxy.utility.addCompilerArgs
import xyz.srnyx.gradlegalaxy.utility.setupJava


plugins {
    application
    id("xyz.srnyx.gradle-galaxy") version "1.2.3"
    id("com.gradleup.shadow") version "8.3.0"
}

setupJava("xyz.srnyx", "2.0.0")
application.mainClass.set("xyz.srnyx.srnyxbot.SrnyxBot")
addCompilerArgs("-parameters")

repository(Repository.MAVEN_CENTRAL, Repository.JITPACK)
dependencies {
    implementation("net.dv8tion", "JDA", "5.0.2")
    implementation("xyz.srnyx", "lazy-library", "aed33d3728")
    compileOnly("io.github.freya022", "BotCommands", "2.10.3") // For documentation
}

// Fix some tasks
tasks {
    distZip {
        dependsOn("shadowJar")
    }
    distTar {
        dependsOn("shadowJar")
    }
    startScripts {
        dependsOn("shadowJar")
    }
    startShadowScripts {
        dependsOn("jar")
    }
}
