plugins {
    java
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.jabis.refund"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.1")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")
    implementation("com.nimbusds:nimbus-jose-jwt:9.37.3")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    implementation("com.univocity:univocity-parsers:2.9.1")
    implementation("com.zaxxer:HikariCP:5.1.0")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("com.h2database:h2:2.1.214")
    runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.104.Final")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.mock-server:mockserver-netty:5.15.0")
    testImplementation("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.mock-server:mockserver-client-java:5.15.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
