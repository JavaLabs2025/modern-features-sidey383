plugins {
    java
    application
}

group = "org.lab"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("org.lab.Startup")
    applicationDefaultJvmArgs = listOf(
        "--enable-preview",
        "-Dslf4j.provider=ch.qos.logback.classic.spi.LogbackServiceProvider"
    )
}

java {
    version = JavaVersion.VERSION_25
}

dependencies {

    implementation(libs.javalin)
    implementation(libs.javalin.openapi.plugin)
    implementation(libs.javalin.redoc.plugin)
    implementation(libs.javalin.swagger.plugin)
    implementation(libs.javalin.openapi.annotation)

    implementation(libs.bcrypt)

    implementation(libs.jdbi.core)
    implementation(libs.jdbi.jackson2)
    implementation(libs.jdbi.postgres)
    implementation(libs.jdbi.sqlobject)
    implementation(libs.hikaricp)

    implementation(libs.flyway.core)
    implementation(libs.flyway.database.postgresql)
    implementation(libs.postgresql)


    implementation(libs.slf4j)

    implementation(libs.lombok)
    implementation(libs.jwt)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    annotationProcessor(libs.lombok.mapstruct.binding)

    testImplementation(libs.junit.api)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.launcher)

    testImplementation("org.testcontainers:postgresql:1.17.1")
    testImplementation("org.testcontainers:testcontainers:1.17.1")
    testImplementation("org.testcontainers:junit-jupiter:1.19.0")
    testImplementation("io.rest-assured:rest-assured:5.5.0")
    testImplementation(libs.assertj)

    testAnnotationProcessor(libs.lombok)

}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
}

tasks.withType<Test> {
    jvmArgs(
        "--enable-preview"
    )
    useJUnitPlatform()
}

tasks.withType<JavaExec> {
    jvmArgs(
        "--enable-preview",
        "-Dslf4j.provider=ch.qos.logback.classic.spi.LogbackServiceProvider"
    )
}
