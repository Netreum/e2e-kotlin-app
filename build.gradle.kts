
plugins {
    kotlin("jvm") version "2.1.0"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-netty:3.1.0")
    implementation("io.ktor:ktor-server-core:3.1.0")
    implementation("io.ktor:ktor-server-status-pages:3.1.0")
    implementation("io.ktor:ktor-server-content-negotiation:3.1.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.1.0")
    implementation("io.ktor:ktor-server-auth:3.1.0")
    implementation("io.ktor:ktor-server-auth-jwt:3.1.0")
    implementation("com.auth0:java-jwt:4.4.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("io.rest-assured:rest-assured:5.3.0")
}

application {
    mainClass.set("com.example.app.ApplicationKt")
}

tasks.test {
    useJUnitPlatform()
}
