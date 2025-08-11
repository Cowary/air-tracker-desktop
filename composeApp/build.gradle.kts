import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.0"
    id("org.openapi.generator") version "7.14.0"
//    id("com.google.devtools.ksp") version "1.9.22-1.0.17"
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation("io.ktor:ktor-client-core:3.2.2")
            implementation("io.ktor:ktor-client-content-negotiation:3.2.2")
            // https://mvnrepository.com/artifact/io.ktor/ktor-serialization-kotlinx-json-jvm
            implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.2.2")
            // https://mvnrepository.com/artifact/io.ktor/ktor-client-cio
            implementation("io.ktor:ktor-client-cio:3.2.2")
            implementation("org.jetbrains.kotlin.plugin.serialization:org.jetbrains.kotlin.plugin.serialization.gradle.plugin:2.2.0")
            implementation("io.ktor:ktor-client-logging:3.2.2")

            implementation("cafe.adriel.voyager:voyager-navigator:1.1.0-beta03")
            implementation("cafe.adriel.voyager:voyager-transitions:1.1.0-beta03") // Для анимаций
            // Navigation Compose Multiplatform
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
//            implementation("com.google.accompanist:accompanist-imageresizer:0.32.0")
//            implementation("io.coil-kt.coil3:coil-compose:3.3.0")
//            implementation("io.coil-kt.coil3:coil-network-ktor2:3.3.0")
//            implementation("io.ktor:ktor-client-java:3.2.2")
//            implementation("io.ktor:ktor-utils-jvm:1.3.2-1.4-M2")
            implementation("io.coil-kt.coil3:coil-compose:3.3.0")
            implementation("io.coil-kt.coil3:coil-network-ktor3:3.3.0")
//            implementation("io.ktor:ktor-client-js:3.0.0-wasm2")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}


compose.desktop {
    application {
        mainClass = "org.cowary.MainKt"

        nativeDistributions {
//            includeAllModules = true
            targetFormats(TargetFormat.Rpm, TargetFormat.Exe)
            packageName = "air-tracker-desktop"
            packageVersion = "1.2.0"

        }
    }
}

//tasks.jar {
//    manifest {
//        attributes["Main-Class"] = "org.cowary.MainKt"
//    }
//    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
//}

// Настройка OpenAPI Generator
openApiGenerate {
    inputSpec.set("$projectDir/src/desktopMain/resources/art-tracker-back-api.json")
    generatorName.set("kotlin")
    outputDir.set("$buildDir/generated/openapi")
    configOptions.set(mapOf(
        "library" to "multiplatform",
//        "serializationLibrary" to "kotlinx_serialization",
        "useCoroutines" to "true",
        "enumPropertyNaming" to "UPPERCASE",
        "dateLibrary" to "string",
    ))
//
    globalProperties.set(mapOf(
        "modelTests" to "false",
        "apiTests" to "false"
    ))
//    apiPackage.set("org.openapi.example.api")
//    invokerPackage.set("org.openapi.example.invoker")
//    modelPackage.set("org.openapi.example.model")
//    configOptions.set(Map.[
//        dateLibrary: "java8"
//    ])
}

// Добавление сгенерированных источников в sourceSet
kotlin {
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("$buildDir/generated/openapi/src")
        }
    }
}

//// Зависимости задач
//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile<*>> {
//    dependsOn("openApiGenerate")
//}

//// Очистка перед генерацией
//tasks.named("clean") {
//    doFirst {
//        delete("$buildDir/generated/openapi")
//    }
//}

// KSP для генерации кода
//dependencies {
//    add("kspCommonMainMetadata", "io.github.raamcosta.compose-destinations:ksp:1.10.2")
//}