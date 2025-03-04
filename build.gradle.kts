//plugins {
//    kotlin("jvm") version "1.9.25" apply false
//    kotlin("plugin.spring") version "1.9.25" apply false
//    kotlin("plugin.jpa") version "1.9.25" apply false
//    id("org.springframework.boot") version "3.4.3" apply false
//    id("io.spring.dependency-management") version "1.1.7" apply false
//}
//
//allprojects {
//    group = "org.bagirov"
//    version = "0.0.1-SNAPSHOT"
//
//    repositories {
//        mavenCentral()
//    }
//}
//
//subprojects {
//    apply(plugin = "org.springframework.boot")
//    apply(plugin = "io.spring.dependency-management")
//
//    dependencyManagement {
//        imports {
//            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
//        }
//    }
//
//    tasks.withType<Test> {
//        useJUnitPlatform()
//    }
//}
