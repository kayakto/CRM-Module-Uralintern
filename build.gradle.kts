plugins {
    id("java")
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "org.bitebuilders"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    // Spring Data JDBC
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    // PostgreSQL driver
    implementation("org.postgresql:postgresql")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Тестовые зависимости
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.mockito")
    }
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
    // previous JVM options
    jvmArgs(
        "-Djdk.instrument.traceUsage=false",
        "-XX:+DisableAttachMechanism"
    )
}

tasks.jar {
    manifest.attributes["Main-Class"] = "org.bitebuilders.UralinternCRMApiApplication"
}