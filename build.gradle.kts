version = "1.3.0"

plugins {
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral() // org.spongepowered:configurate-yaml, io.github.freya022:BotCommands
    maven("https://jitpack.io") // io.github.freya022:BotCommands
    maven("https://m2.dv8tion.net/releases") // com.sedmelluq:lavaplayer
}

dependencies {
    implementation("net.dv8tion", "JDA", "5.0.0-beta.4")
    implementation("org.spongepowered", "configurate-yaml", "4.1.2")
    implementation("io.github.freya022", "BotCommands", "2.8.4")
    implementation("com.sedmelluq", "lavaplayer", "1.3.77")
    implementation("ch.qos.logback", "logback-classic", "1.4.5")
}

application.mainClass.set("xyz.srnyx.srnyxbot.SrnyxBot")

tasks {
    // Remove '-all' from the JAR file name and clean up the build folder
    shadowJar {
        archiveClassifier.set("")
    }

    // Make 'gradle build' run 'gradle shadowJar'
    build {
        dependsOn("shadowJar")
    }

    // Text encoding
    compileJava {
        options.encoding = "UTF-8"
        options.compilerArgs.plusAssign("-parameters")
    }

    // Disable unnecessary tasks
    classes { enabled = false }
    jar { enabled = false }
    distTar { enabled = false }
    distZip { enabled = false }
    shadowDistTar { enabled = false }
    shadowDistZip { enabled = false }
    compileTestJava { enabled = false }
    processTestResources { enabled = false }
    testClasses { enabled = false }
    test { enabled = false }
    check { enabled = false }
}
