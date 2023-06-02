buildscript {

    repositories {
//        mavenLocal()
        google()
        mavenCentral()
//        maven("https://oss.sonatype.org/content/repositories/public/")
//        maven("https://oss.sonatype.org/content/repositories/snapshots/")

//        maven {
//            url = uri("http://172.16.3.249:8081/repository/maven-public/")
//            isAllowInsecureProtocol = true
//        }

    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0")
    }
}

task("clean", Delete::class) {
    delete(rootProject.buildDir)
}