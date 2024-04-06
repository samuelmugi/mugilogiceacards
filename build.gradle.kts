plugins {
	java
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "com.mugi.logicea"
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
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	// https://mvnrepository.com/artifact/com.mysql/mysql-connector-j
	implementation("com.mysql:mysql-connector-j:8.3.0")

	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	annotationProcessor("org.projectlombok:lombok")

//	implementation ("org.springframework.security:spring-security-web:6.2.1")
	// https://mvnrepository.com/artifact/org.springframework.security/spring-security-config
	implementation ("org.springframework.security:spring-security-config:6.2.1")
// https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-api
	implementation ("io.jsonwebtoken:jjwt-api:0.12.5")
// https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-impl
	implementation ("io.jsonwebtoken:jjwt-impl:0.12.5")
// https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-jackson
	implementation ("io.jsonwebtoken:jjwt-jackson:0.12.5")
// https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
	implementation ("org.apache.commons:commons-lang3:3.0")
// https://mvnrepository.com/artifact/jakarta.validation/jakarta.validation-api
	implementation ("jakarta.validation:jakarta.validation-api:3.1.0-M1")
// https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
	implementation ("org.apache.commons:commons-lang3:3.14.0")
// https://mvnrepository.com/artifact/org.hibernate/hibernate-validator
	implementation ("org.hibernate:hibernate-validator:8.0.1.Final")


	compileOnly ("org.projectlombok:lombok")
	annotationProcessor ("org.projectlombok:lombok")

	// MapStruct
// https://mvnrepository.com/artifact/org.mapstruct/mapstruct
	implementation ("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor ("org.mapstruct:mapstruct-processor:1.5.5.Final")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
