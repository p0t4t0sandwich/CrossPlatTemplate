import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask

plugins {
    id("java")
    id("maven-publish")
    id("idea")
    id("eclipse")
    alias(libs.plugins.blossom)
    alias(libs.plugins.spotless)
    alias(libs.plugins.unimined)
}

base {
    archivesName = modName
}

java.toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
java.sourceCompatibility = JavaVersion.toVersion(javaVersion)
java.targetCompatibility = JavaVersion.toVersion(javaVersion)

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

spotless {
    format("misc") {
        target("*.gradle", ".gitattributes", ".gitignore")
        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
    }
    java {
        target("src/**/*.java", "src/**/*.java.peb")
        toggleOffOn()
        importOrder()
        removeUnusedImports()
        cleanthat()
        googleJavaFormat("1.24.0")
            .aosp()
            .formatJavadoc(true)
            .reorderImports(true)
        formatAnnotations()
        licenseHeader("""/**
 * Copyright (c) 2025 $author - person@example.com
 * The project is Licensed under <a href="https://github.com/Example/TestMod/blob/dev/LICENSE">MIT</a>
 */
""")
    }
}

sourceSets {
    main.get().blossom.javaSources {
        property("mod_id", modId)
        property("mod_name", modName)
        property("version", version.toString())
        property("license", license)
        property("author", author)
        property("description", description)
        property("homepage_url", homepageUrl)
    }
    create("api")
    create("bungeecord") {
        compileClasspath += main.get().output
        runtimeClasspath += main.get().output
    }
    create("fabric") {
        compileClasspath += main.get().output
        runtimeClasspath += main.get().output
    }
    create("forge") {
        compileClasspath += main.get().output
        runtimeClasspath += main.get().output
    }
    create("neoforge") {
        compileClasspath += main.get().output
        runtimeClasspath += main.get().output
    }
    create("paper") {
        compileClasspath += main.get().output
        runtimeClasspath += main.get().output
    }
    create("sponge") {
        compileClasspath += main.get().output
        runtimeClasspath += main.get().output
    }
    create("velocity") {
        compileClasspath += main.get().output
        runtimeClasspath += main.get().output
    }
}

configurations {
    val mainCompileOnly by creating
    named("compileOnly") {
        extendsFrom(getByName("bungeecordCompileOnly"))
        extendsFrom(getByName("fabricCompileOnly"))
        extendsFrom(getByName("forgeCompileOnly"))
        extendsFrom(getByName("neoforgeCompileOnly"))
        extendsFrom(getByName("paperCompileOnly"))
        extendsFrom(getByName("spongeCompileOnly"))
        extendsFrom(getByName("velocityCompileOnly"))
    }
    val modImplementation by creating
    named("modImplementation") {
        extendsFrom(getByName("fabricImplementation"))
    }
}

repositories {
    mavenCentral()
    unimined.fabricMaven()
    unimined.minecraftForgeMaven()
    unimined.neoForgedMaven()
    unimined.parchmentMaven()
    unimined.spongeMaven()
    maven("https://repo.papermc.io/repository/maven-public/")
}

// ------------------------------------------- Vanilla -------------------------------------------
unimined.minecraft {
    version(minecraftVersion)
    mappings {
        parchment(parchmentMinecraft, parchmentVersion)
        mojmap()
        devFallbackNamespace("official")
    }
    defaultRemapJar = false
}

tasks.jar {
    archiveClassifier.set("vanilla")
}

// ------------------------------------------- API -------------------------------------------
tasks.register<Jar>("apiJar") {
    archiveClassifier.set("api")
    from(sourceSets.getByName("api").output)
}

// ------------------------------------------- BungeeCord -------------------------------------------
unimined.minecraft(sourceSets.getByName("bungeecord")) {
    combineWith(sourceSets.main.get())
    combineWith(sourceSets.getByName("api"))
    defaultRemapJar = true
}

// ------------------------------------------- Fabric -------------------------------------------
unimined.minecraft(sourceSets.getByName("fabric")) {
    combineWith(sourceSets.main.get())
    fabric {
        loader(fabricLoaderVersion)
    }
    defaultRemapJar = true
}

tasks.named<RemapJarTask>("remapFabricJar") {
    mixinRemap {
        disableRefmap()
    }
}

// ------------------------------------------- Forge -------------------------------------------
unimined.minecraft(sourceSets.getByName("forge")) {
    combineWith(sourceSets.main.get())
    minecraftForge {
        loader(forgeVersion)
    }
    defaultRemapJar = true
}

// ------------------------------------------- NeoForge -------------------------------------------
unimined.minecraft(sourceSets.getByName("neoforge")) {
    combineWith(sourceSets.main.get())
    neoForge {
        loader(neoForgeVersion)
    }
    defaultRemapJar = true
}

// ------------------------------------------- Paper -------------------------------------------
unimined.minecraft(sourceSets.getByName("paper")) {
    combineWith(sourceSets.main.get())
    accessTransformer {
        // https://github.com/PaperMC/Paper/blob/main/build-data/paper.at
        accessTransformer("src/paper/paper.at")
    }
    defaultRemapJar = true
}

// ------------------------------------------- Sponge -------------------------------------------
unimined.minecraft(sourceSets.getByName("sponge")) {
    combineWith(sourceSets.main.get())
    defaultRemapJar = true
}

// ------------------------------------------- Velocity -------------------------------------------
unimined.minecraft(sourceSets.getByName("velocity")) {
    combineWith(sourceSets.main.get())
    defaultRemapJar = true
}

// ------------------------------------------- Common -------------------------------------------
dependencies {
    implementation(libs.annotations)
    implementation(libs.mixin)
    "bungeecordCompileOnly"("net.md-5:bungeecord-api:$bungeecordVersion")
    listOf(
        "fabric-api-base",
        "fabric-command-api-v2",
        "fabric-lifecycle-events-v1"
    ).forEach {
        "fabricModImplementation"(fabricApi.fabricModule(it, fabricVersion))
    }
    "paperCompileOnly"("io.papermc.paper:paper-api:$minecraftVersion-$paperVersion")
    "paperCompileOnly"(libs.ignite.api)
    "paperCompileOnly"(libs.mixin)
    "spongeCompileOnly"("org.spongepowered:spongeapi:$spongeVersion")
    "spongeCompileOnly"(libs.mixin)
    "velocityCompileOnly"("com.velocitypowered:velocity-api:$velocityVersion")
}

tasks.withType<ProcessResources> {
    filesMatching(listOf(
        "bungee.yml",
        "fabric.mod.json",
        "pack.mcmeta",
        "META-INF/mods.toml",
        "META-INF/neoforge.mods.toml",
        "plugin.yml",
        "paper-plugin.yml",
        "ignite.mod.json",
        "META-INF/sponge_plugins.json",
        "velocity-plugin.json"
    )) {
        expand(project.properties)
    }
}

tasks.build.get().dependsOn("spotlessApply")
