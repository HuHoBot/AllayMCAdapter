import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "cn.huohuas001.huHoBot"
description = "HuHoBot Allay Adapter"
version = "0.0.7"

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

    implementation("org.java-websocket:Java-WebSocket:1.5.4")
    implementation("com.alibaba.fastjson2:fastjson2:2.0.52")
    implementation(group = "com.github.MineBuilders", name = "allaymc-kotlinx", version = "master-SNAPSHOT")

    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.34")
}

// 新增配置生成任务
tasks.register<Copy>("generateServerConfig") {
    group = "Build"
    description = "生成服务器配置类"

    from("src/main/templates/java")
    into("src/main/generated/java")

    // 文件名处理
    include("**/*.template")
    rename { fileName ->
        fileName.replace(".template", "")
    }

    filter { line ->
        line.replace("\${WS_SERVER_URL}",
            project.findProperty("wsServerUrl")?.toString() ?: "ws://127.0.0.1:8080"
        )
    }

    filteringCharset = "UTF-8"
}

// 将生成的代码目录加入源码集
sourceSets {
    main {
        java {
            srcDir("src/main/generated/java")
        }
    }
}

// 确保编译前先执行生成任务
tasks.compileJava {
    dependsOn("clean")
    dependsOn("generateServerConfig")
}

tasks.clean {
    delete("src/main/generated")
}

tasks.shadowJar {
    archiveClassifier = "AllayMC"
}

tasks.register<Copy>("runServer") {
    outputs.upToDateWhen { false }
    dependsOn("shadowJar")
    val launcherRepo = "https://raw.githubusercontent.com/AllayMC/AllayLauncher/refs/heads/main/scripts"
    val cmdWin = "Invoke-Expression (Invoke-WebRequest -Uri \"${launcherRepo}/install_windows.ps1\").Content"
    val cmdLinux = "wget -qO- ${launcherRepo}/install_linux.sh | bash"
    val cwd = layout.buildDirectory.file("run").get().asFile

    val shadowJar = tasks.named("shadowJar", ShadowJar::class).get()
    from(shadowJar.archiveFile.get().asFile)
    into(cwd.resolve("plugins").apply { mkdirs() })

    val isDownloaded = cwd.listFiles()!!.any { it.isFile && it.nameWithoutExtension == "allay" }
    val isWindows = System.getProperty("os.name").startsWith("Windows")
    fun launch() = exec {
        workingDir = cwd
        val cmd = if (isDownloaded) "./allay" else if (isWindows) cmdWin else cmdLinux
        if (isWindows) commandLine("powershell", "-Command", cmd)
        else commandLine("sh", "-c", cmd)
    }

    // https://github.com/gradle/gradle/issues/18716  // kill it manually by click X...
    doLast { launch() }
}
