import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val basicVersion = "1.5.21"
    id("org.springframework.boot") version "2.5.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version basicVersion
    kotlin("plugin.spring") version basicVersion
    kotlin("plugin.jpa") version basicVersion
    kotlin("kapt") version basicVersion

    idea
}

configurations {
    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}

val profile = if(project.hasProperty("profile"))
    project.property("profile").toString() else "local"

sourceSets {
    main {
        resources {
            srcDirs(listOf("src/main/resources", "src/main/resources-$profile"))
        }
    }
    test {
        resources{
            srcDirs(listOf("src/test/kotlin/resources"))
        }
    }
}

group = "team.sopo"
version = "0.0.27"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

apply(plugin = "kotlin-jpa")

allOpen{
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

extra["springCloudVersion"] = "2020.0.3"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.cloud", "spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
    implementation("org.springframework.security.oauth.boot", "spring-security-oauth2-autoconfigure", "2.5.2")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("io.github.openfeign", "feign-httpclient")

    //Spring Doc
    implementation("org.springdoc", "springdoc-openapi-ui", "1.5.4")
    implementation("org.springdoc", "springdoc-openapi-security", "1.5.4")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.mockito","mockito-inline")

    // kapt로 dependency를 지정해 준다.
    // kotlin 코드가 아니라면 kapt 대신 annotationProcessor를 사용한다.
    api("com.querydsl:querydsl-jpa:4.4.0")
    kapt("com.querydsl:querydsl-apt:4.4.0:jpa") // ":jpa 꼭 붙여줘야 한다!!"

    runtimeOnly("org.mariadb.jdbc", "mariadb-java-client")

    //Httpclient
    implementation("org.apache.httpcomponents", "httpclient", "4.5.9")

    //Log4j2
    implementation("org.apache.logging.log4j","log4j-slf4j-impl","2.14.1")
    implementation("org.apache.logging.log4j","log4j-core","2.14.1")
    implementation("org.apache.logging.log4j","log4j-jul","2.14.1")
    implementation("org.apache.logging.log4j","log4j-layout-template-json","2.14.1")

    //Jackson Module Kotlin
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.12.4")
    implementation("com.fasterxml.jackson.core", "jackson-databind", "2.12.4")

    //Gson
    implementation("com.google.code.gson", "gson", "2.8.6")

    //jjwt
    implementation("io.jsonwebtoken", "jjwt", "0.9.1")

    implementation("com.nimbusds", "nimbus-jose-jwt", "8.20.1")

    // retrofit
    implementation( "com.squareup.retrofit2", "retrofit", "2.9.0")
    implementation( "com.squareup.retrofit2", "converter-jackson", "2.9.0")
    implementation( "com.squareup.retrofit2", "converter-gson", "2.9.0")
    implementation( "com.squareup.retrofit2", "adapter-rxjava2", "2.9.0")
    implementation( "com.squareup.okhttp3", "logging-interceptor", "4.9.0")
    implementation( "com.squareup.okhttp3", "okhttp", "4.9.0")
    implementation( "org.apache.commons", "commons-math3", "3.6.1")

    // mapstruct
    implementation("org.mapstruct","mapstruct", "1.4.2.Final")
    implementation("org.mapstruct","mapstruct-jdk8", "1.4.2.Final")
    kapt("org.mapstruct","mapstruct-processor", "1.4.2.Final")
    api("com.github.pozo:mapstruct-kotlin:1.3.1.2")
    kapt("com.github.pozo:mapstruct-kotlin-processor:1.3.1.2")

    implementation ("com.googlecode.json-simple","json-simple","1.1")
    implementation ("org.codehaus.jettison","jettison","1.4.1")

    implementation ("com.google.firebase","firebase-admin","6.8.1")
    implementation("com.fasterxml.uuid:java-uuid-generator:3.1.4")

    implementation ("org.flywaydb:flyway-core")
}

idea {
    module {
        val kaptMain = file("build/generated/source/kapt/main")
        sourceDirs.add(kaptMain)
        generatedSourceDirs.add(kaptMain)
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootJar{
    base.archivesBaseName = "${project.name}-$profile"
    launchScript()
}

tasks.bootRun {
    args("--spring.profiles.active=$profile")
}