import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask
import java.time.Instant

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
    create("api")
    create("common") {
        blossom.javaSources {
            property("mod_id", modId)
            property("mod_name", modName)
            property("version", version.toString())
            property("license", license)
            property("author", author)
            property("description", description)
            property("homepage_url", homepageUrl)
        }
    }
    create("bungeecord") {
        compileClasspath += getByName("api").output
        compileClasspath += getByName("common").output
        runtimeClasspath += getByName("api").output
        runtimeClasspath += getByName("common").output
    }
    create("fabric")
    create("forge")
    create("neoforge")
    create("paper")
    create("spigot") {
        compileClasspath += getByName("api").output
        compileClasspath += getByName("common").output
        runtimeClasspath += getByName("api").output
        runtimeClasspath += getByName("common").output
    }
    create("sponge")
    create("velocity") {
        compileClasspath += getByName("api").output
        compileClasspath += getByName("common").output
        runtimeClasspath += getByName("api").output
        runtimeClasspath += getByName("common").output
    }
}

configurations {
    val mainCompileOnly by creating
    named("compileOnly") {
        extendsFrom(getByName("apiCompileOnly"))
        extendsFrom(getByName("commonCompileOnly"))
        extendsFrom(getByName("bungeecordCompileOnly"))
        extendsFrom(getByName("fabricCompileOnly"))
        extendsFrom(getByName("forgeCompileOnly"))
        extendsFrom(getByName("neoforgeCompileOnly"))
        extendsFrom(getByName("paperCompileOnly"))
        extendsFrom(getByName("spigotCompileOnly"))
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
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

// ------------------------------------------- Vanilla -------------------------------------------
unimined.minecraft {
    combineWith(sourceSets.getByName("api"))
    combineWith(sourceSets.getByName("common"))
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

// ------------------------------------------- Common -------------------------------------------
tasks.register<Jar>("commonJar") {
    archiveClassifier.set("common")
    from(sourceSets.getByName("common").output)
}

// ------------------------------------------- BungeeCord -------------------------------------------
tasks.register<Jar>("bungeecordJar") {
    archiveClassifier.set("bungeecord")
    from(sourceSets.getByName("bungeecord").output)
    from(sourceSets.getByName("api").output)
    from(sourceSets.getByName("common").output)
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

// ------------------------------------------- Spigot -------------------------------------------
tasks.register<Jar>("spigotJar") {
    archiveClassifier.set("spigot")
    from(sourceSets.getByName("spigot").output)
    from(sourceSets.getByName("api").output)
    from(sourceSets.getByName("common").output)
}

// ------------------------------------------- Velocity -------------------------------------------
tasks.register<Jar>("velocityJar") {
    archiveClassifier.set("velocity")
    from(sourceSets.getByName("velocity").output)
    from(sourceSets.getByName("api").output)
    from(sourceSets.getByName("common").output)
}

// ------------------------------------------- Common -------------------------------------------
dependencies {
    implementation(libs.annotations)
    implementation(libs.mixin)
    "commonCompileOnly"("org.slf4j:slf4j-api:2.0.16")
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
    //"spigotCompileOnly"("org.spigotmc:spigot-api:$minecraftVersion-$spigotVersion")
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

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to modName,
                "Specification-Version" to version,
                "Specification-Vendor" to "SomeVendor",
                "Implementation-Version" to version,
                "Implementation-Vendor" to "SomeVendor",
                "Implementation-Timestamp" to Instant.now().toString(),
                "FMLCorePluginContainsFMLMod" to "true",
                "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker",
                "MixinConfigs" to "$modId.mixins.json,$modId.forge.mixins.json"
            )
        )
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(listOf("README.md", "LICENSE")) {
        into("META-INF")
    }
}

tasks.build.get().dependsOn(
    "apiJar",
    "commonJar",
    "bungeecordJar",
    //"spigotJar",
    "velocityJar",
    "spotlessApply",
)
