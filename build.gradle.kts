plugins {
    java
    id("org.springframework.boot") version "3.0.2"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation:3.0.2")
    implementation("org.springframework.boot:spring-boot-starter-web:3.0.2")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.0.2")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.0.2")
    implementation("org.flywaydb:flyway-core:9.14.1")

    runtimeOnly("org.postgresql:postgresql:42.5.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.0.2")
    testImplementation("com.h2database:h2")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.0")

    compileOnly("org.projectlombok:lombok:1.18.26")
    testCompileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")

}

tasks.withType<Test> {
    useJUnitPlatform()
}

testing {
    suites {
        val integrationTest by registering(JvmTestSuite::class) {
            sources {
                java {
                    setSrcDirs(listOf("src/integrationTest/java"))
                }
            }
            dependencies {
                implementation(project)
                implementation("org.springframework.boot:spring-boot-starter-test:3.0.2")
                implementation("org.springframework.boot:spring-boot-starter-web:3.0.2")
            }
        }
    }
}


