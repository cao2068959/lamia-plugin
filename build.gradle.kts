plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.3"
}

group = "org.chy"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
}



// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.3.6")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf("com.intellij.java"))
}

tasks {

    patchPluginXml {
        sinceBuild.set("222")
        untilBuild.set("243.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}


dependencies{
    implementation("io.github.cao2068959", "lamia-compile","2.2.0" )
}

