import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

version = "1.21.8"

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("com.gradleup.shadow") version "8.3.6"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/") // worldguard
}

configurations {
    create("resolvableImplementation") {
        isCanBeResolved = true
        isCanBeConsumed = true
    }
}

dependencies {
    // Paper dev bundle — Mojang mapped, otomatik reobfuscation
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")

    // Kotlin stdlib (sunucu tarafından sağlanır, compileOnly)
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib")
    compileOnly("org.jetbrains.kotlin:kotlin-reflect")

    // TOML parsing kütüphanesi (jar'a gömülür)
    compileOnly("org.tomlj:tomlj:1.1.1")
    configurations["resolvableImplementation"]("org.tomlj:tomlj:1.1.1")

    // WorldGuard (opsiyonel soft dependency)
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

// NMS kaynak kodunu ana kaynak setine ekle
sourceSets {
    main {
        java.srcDir("src/nms/v1_21")
    }
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("")
        archiveBaseName.set("xc-1.21.8")
        configurations = mutableListOf(
            project.configurations.named("resolvableImplementation").get()
        )
    }
    build { dependsOn(shadowJar) }
    assemble { dependsOn(project.tasks.named("reobfJar")) }
}
