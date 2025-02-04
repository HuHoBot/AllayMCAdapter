import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.allaymc.javaplugintemplate"
description = "Java plugin template for allay server"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
    maven("https://repo.opencollab.dev/maven-releases/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
}

dependencies {
    compileOnly(group = "org.allaymc.allay", name = "api", version = "master-SNAPSHOT")
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.34")

    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.34")
}

tasks.shadowJar {
    archiveClassifier = "shaded"
}

tasks.register<Copy>("runServer") {
    outputs.upToDateWhen { false }
    dependsOn("shadowJar")
    val launcherRepo = "https://raw.githubusercontent.com/AllayMC/AllayLauncher/refs/heads/main/scripts"
    val cmdWin = "Invoke-Expression (Invoke-WebRequest -Uri \"${launcherRepo}/install_windows.ps1\").Content"
    val cmdLinux = "wget -qO- ${launcherRepo}/install_linux.sh | bash"
    val cwd = layout.buildDirectory.file("launcher").get().asFile.apply { mkdirs() }

    val shadowJar = tasks.named("shadowJar", ShadowJar::class).get()
    from(shadowJar.archiveFile.get().asFile)
    into(cwd.resolve("plugins").apply { mkdirs() })

    val isWindows = System.getProperty("os.name").startsWith("Windows")
    fun launch() = exec {
        workingDir = cwd
        if (isWindows) commandLine("powershell", "-Command", cmdWin)
        else commandLine("sh", "-c", cmdLinux)
    }

    // https://github.com/gradle/gradle/issues/18716  // kill it manually by click X...
    doLast { launch() }
}
