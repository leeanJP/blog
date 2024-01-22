plugins {
	java
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "com.it"
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
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client:3.2.1")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	testImplementation("org.projectlombok:lombok:1.18.28")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.h2database:h2")
	annotationProcessor("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	developmentOnly ("org.springframework.boot:spring-boot-devtools")
	implementation ("io.jsonwebtoken:jjwt:0.9.1") /*java jwt 라이브러리*/
	implementation ("javax.xml.bind:jaxb-api:2.3.1") /* xml문서와 java 객체간 매핑 자동화 */


}

tasks.withType<Test> {
	useJUnitPlatform()
}
