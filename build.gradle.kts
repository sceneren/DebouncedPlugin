buildscript {

    repositories {
        mavenLocal()
        mavenCentral()
        google()
        maven("https://oss.sonatype.org/content/repositories/public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
    }
}

task("clean", Delete::class) {
    delete(rootProject.buildDir)
}