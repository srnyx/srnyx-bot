description = "srnyx-Bot"
version = "1.1.0"

repositories {
    maven("https://m2.dv8tion.net/releases")
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-alpha.12")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
}

plugins {
    application
    id("com.github.johnrengelman.shadow") version("7.1.2")
}

application {
    mainClass.set("xyz.srnyx.srnyx_bot.Main")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
}