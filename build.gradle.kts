import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.5"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"
}

group = "ru.apexman"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${properties["jacksonVersion"]}")
	implementation("org.jetbrains.kotlin:kotlin-reflect:${properties["jetBrainsKotlinVersion"]}")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${properties["jetBrainsKotlinVersion"]}")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${properties["jetBrainsKotlinCoroutinesCoreVersion"]}")
	implementation("org.apache.commons:commons-lang3:${properties["apacheCommonLangVersion"]}")
//
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.webjars:bootstrap:4.0.0-2")
	implementation("org.webjars:webjars-locator:0.30")
//
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named<Jar>("jar") {
	enabled = false
}

tasks.jar {
	manifest {
		attributes("Main-Class" to "ru.apexman.statmoonbotlistener.StatMoonBotListenerApplication")
	}
	from(sourceSets.main.get().output)
	dependsOn(configurations.runtimeClasspath)
	from({
		configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
	})
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
