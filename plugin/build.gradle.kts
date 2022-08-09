plugins {
    `kotlin-dsl`
    kotlin("kapt")
    kotlin("jvm")
    `maven-publish`
}

group = "com.github.sceneren"
// 描述
description = "检查快速点击"
// 版本号
version = "2.0.2"

repositories {
    mavenLocal()
    mavenCentral()
    google()
    maven("https://oss.sonatype.org/content/repositories/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}


dependencies {
    implementation(gradleApi())
    implementation("com.android.tools.build:gradle:7.2.1")
    compileOnly("commons-io:commons-io:2.6")
    compileOnly("commons-codec:commons-codec:1.15")
    compileOnly("org.ow2.asm:asm-commons:9.3")
    compileOnly("org.ow2.asm:asm-tree:9.3")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "DebouncedPlugin"
            from(components["java"])
        }
    }


    repositories {
        maven {
            // 生成的插件位置
            url = uri("../repo")
        }
    }
}