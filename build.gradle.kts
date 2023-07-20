plugins {
    kotlin("jvm") version "1.6.10"
    id ("com.github.johnrengelman.shadow") version "5.2.0"
    id ("io.papermc.paperweight.userdev") version "1.3.8"
    id ("xyz.jpenilla.run-paper") version "1.0.6"
}

group "com.github.yeandyson"
version "0.1"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    maven ("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.20")
    paperDevBundle("1.20.1-R0.1-SNAPSHOT")
}

val shade = configurations.create("shade")
shade.extendsFrom(configurations.implementation.get())

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }

    jar {
        from(
            shade.map {
                if (it.isDirectory)
                    it
                else
                    zipTree(it)
            }
        )
    }

    runServer {
        minecraftVersion("1.20.1")
    }
}
