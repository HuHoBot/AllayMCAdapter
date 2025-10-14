import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "cn.huohuas001.huHoBot"
description = "HuHoBot Allay Adapter"
version = "0.0.10"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
}

dependencies {
    compileOnly(group = "org.allaymc.allay", name = "api", version = "0.13.0")
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.34")

    implementation(group = "org.java-websocket", name = "Java-WebSocket", version = "1.5.4")
    implementation(group = "com.alibaba.fastjson2", name = "fastjson2", version = "2.0.52")
    implementation(group = "eu.okaeri", name = "okaeri-configs-yaml-snakeyaml", version = "5.0.13")

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
