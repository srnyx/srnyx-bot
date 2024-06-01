import xyz.srnyx.gradlegalaxy.enums.Repository
import xyz.srnyx.gradlegalaxy.enums.repository
import xyz.srnyx.gradlegalaxy.utility.addCompilerArgs
import xyz.srnyx.gradlegalaxy.utility.setupJava


plugins {
    application
    id("xyz.srnyx.gradle-galaxy") version "1.1.3"
    id("io.github.goooler.shadow") version "8.1.7"
}

setupJava("xyz.srnyx", "1.3.0")
application.mainClass.set("xyz.srnyx.srnyxbot.SrnyxBot")
addCompilerArgs("-parameters")

repository(Repository.MAVEN_CENTRAL, Repository.JITPACK)
dependencies {
    implementation("net.dv8tion", "JDA", "5.0.0-beta.24")
    implementation("xyz.srnyx", "lazy-library", "42a89280cc")
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
