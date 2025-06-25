import xyz.srnyx.gradlegalaxy.utility.setupLazyLibrary


plugins {
    application
    id("xyz.srnyx.gradle-galaxy") version "1.3.3"
    id("com.gradleup.shadow") version "8.3.7"
}

setupLazyLibrary("49af85c895", "5.6.1", "xyz.srnyx", "2.0.0")

dependencies.compileOnly("io.github.freya022", "BotCommands", "2.10.4") // For documentation
