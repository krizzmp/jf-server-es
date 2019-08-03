import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.3.30"
}

group = "server"
version = "0.0.1"

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://dl.bintray.com/arrow-kt/arrow-kt/") }
    maven { url = uri("https://oss.jfrog.org/artifactory/oss-snapshot-local/") }
}
val arrow_version = "0.9.0"
dependencies {
    compile( "io.arrow-kt:arrow-core-data:$arrow_version")
    compile ("io.arrow-kt:arrow-core-extensions:$arrow_version")
    compile ("io.arrow-kt:arrow-syntax:$arrow_version")
    compile ("io.arrow-kt:arrow-typeclasses:$arrow_version")
    compile ("io.arrow-kt:arrow-extras-data:$arrow_version")
    compile ("io.arrow-kt:arrow-extras-extensions:$arrow_version")
    
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    compile("io.ktor:ktor-server-netty:$ktor_version")
    compile("ch.qos.logback:logback-classic:$logback_version")
    compile("io.ktor:ktor-server-core:$ktor_version")
    compile("io.ktor:ktor-locations:$ktor_version")
    compile("io.ktor:ktor-gson:$ktor_version")
    testCompile("io.ktor:ktor-server-tests:$ktor_version")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")
