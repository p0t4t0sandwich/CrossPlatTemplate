import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask
import java.time.Instant

plugins {
    id("java")
    id("maven-publish")
    id("idea")
    id("eclipse")
    alias(libs.plugins.blossom)
    alias(libs.plugins.shadow)
    alias(libs.plugins.spotless)
    alias(libs.plugins.unimined)
}

base {
    archivesName = modName
}

java.toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
java.sourceCompatibility = JavaVersion.toVersion(javaVersion)
java.targetCompatibility = JavaVersion.toVersion(javaVersion)

spotless {
    format("misc") {
        target("*.gradle.kts", ".gitattributes", ".gitignore")
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
        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
        licenseHeader("""/**
* Copyright (c) 2025 $author
* The project is Licensed under <a href="https://github.com/Example/TestMod/blob/dev/LICENSE">MIT</a>
*/
""")
    }
}

val api: SourceSet by sourceSets.creating
val common: SourceSet by sourceSets.creating {
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
val bungeecord: SourceSet by sourceSets.creating
val fabric: SourceSet by sourceSets.creating
val forge: SourceSet by sourceSets.creating
val neoforge: SourceSet by sourceSets.creating
val paper: SourceSet by sourceSets.creating
val spigot: SourceSet by sourceSets.creating
val sponge: SourceSet by sourceSets.creating
val velocity: SourceSet by sourceSets.creating
listOf(bungeecord, spigot, velocity).forEach {
    listOf(api, common).forEach { sourceSet ->
        it.compileClasspath += sourceSet.output
        it.runtimeClasspath += sourceSet.output
    }
}

val mainCompileOnly: Configuration by configurations.creating
configurations.compileOnly.get().extendsFrom(mainCompileOnly)
val apiCompileOnly: Configuration by configurations.getting
val commonCompileOnly: Configuration by configurations.getting
val bungeecordCompileOnly: Configuration by configurations.getting
val fabricCompileOnly: Configuration by configurations.getting
val forgeCompileOnly: Configuration by configurations.getting
val neoforgeCompileOnly: Configuration by configurations.getting
val paperCompileOnly: Configuration by configurations.getting {
    extendsFrom(mainCompileOnly)
}
val spigotCompileOnly: Configuration by configurations.getting
val spongeCompileOnly: Configuration by configurations.getting {
    extendsFrom(mainCompileOnly)
}
val velocityCompileOnly: Configuration by configurations.getting
listOf(bungeecordCompileOnly, fabricCompileOnly, forgeCompileOnly, neoforgeCompileOnly,
    paperCompileOnly, spigotCompileOnly, spongeCompileOnly, velocityCompileOnly).forEach {
    it.extendsFrom(apiCompileOnly)
    it.extendsFrom(commonCompileOnly)
}
val modImplementation: Configuration by configurations.creating
val fabricModImplementation: Configuration by configurations.creating {
    extendsFrom(modImplementation)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<RemapJarTask> {
    mixinRemap {
        enableBaseMixin()
        disableRefmap()
    }
}

repositories {
    // maven("https://maven.neuralnexus.dev/mirror")
    mavenCentral()
    unimined.fabricMaven()
    unimined.minecraftForgeMaven()
    unimined.neoForgedMaven()
    unimined.parchmentMaven()
    unimined.spongeMaven()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

unimined.minecraft {
    combineWith(api)
    combineWith(common)
    version(minecraftVersion)
    mappings {
        parchment(parchmentMinecraft, parchmentVersion)
        mojmap()
        devFallbackNamespace("official")
    }
    defaultRemapJar = false
}

tasks.register<Jar>("apiJar") {
    archiveClassifier.set("api")
    from(api.output)
}

tasks.register<Jar>("commonJar") {
    archiveClassifier.set("common")
    from(common.output)
}

tasks.register<Jar>("bungeecordJar") {
    archiveClassifier.set("bungeecord")
    from(bungeecord.output)
}

unimined.minecraft(fabric) {
    combineWith(sourceSets.main.get())
    fabric {
        loader(fabricLoaderVersion)
    }
    defaultRemapJar = true
}

tasks.register<ShadowJar>("relocateFabricJar") {
    dependsOn("remapFabricJar")
    from(tasks.getByName<RemapJarTask>("remapFabricJar").asJar.archiveFile.get().asFile)
    archiveClassifier.set("fabric-relocated")
    dependencies {
        exclude("com/example/templatemod/mixin/vanilla/**")
    }
    relocate("com.example.templatemod.vanilla", "com.example.templatemod.y_intmdry")
}

unimined.minecraft(forge) {
    combineWith(sourceSets.main.get())
    minecraftForge {
        loader(forgeVersion)
    }
    defaultRemapJar = true
}

unimined.minecraft(neoforge) {
    combineWith(sourceSets.main.get())
    neoForge {
        loader(neoForgeVersion)
    }
    defaultRemapJar = true
}

unimined.minecraft(paper) {
    combineWith(sourceSets.main.get())
    combineWith(spigot)
    accessTransformer {
        // https://github.com/PaperMC/Paper/blob/main/build-data/paper.at
        accessTransformer("${rootProject.projectDir}/src/paper/paper.at")
    }
    defaultRemapJar = true
}

unimined.minecraft(sponge) {
    combineWith(sourceSets.main.get())
    defaultRemapJar = true
}

tasks.register<Jar>("spigotJar") {
    archiveClassifier.set("spigot")
    from(spigot)
}

tasks.register<Jar>("velocityJar") {
    archiveClassifier.set("velocity")
    from(velocity.output)
}

dependencies {
    mainCompileOnly(libs.annotations)
    mainCompileOnly(libs.mixin)
    commonCompileOnly(libs.slf4j)
    bungeecordCompileOnly("net.md-5:bungeecord-api:$bungeecordVersion")
    listOf("api-base", "command-api-v2", "lifecycle-events-v1", "networking-api-v1").forEach {
        fabricModImplementation(fabricApi.fabricModule("fabric-$it", fabricVersion))
    }
    paperCompileOnly("io.papermc.paper:paper-api:$minecraftVersion-$paperVersion")
    paperCompileOnly(libs.ignite.api)
    spigotCompileOnly("org.spigotmc:spigot-api:$minecraftVersion-$spigotVersion")
    spongeCompileOnly("org.spongepowered:spongeapi:$spongeVersion")
    velocityCompileOnly("com.velocitypowered:velocity-api:$velocityVersion")
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

tasks.jar {
    from(
        bungeecord.output,
        fabric.output,
        forge.output,
        neoforge.output,
        paper.output,
        sponge.output,
        spigot.output,
        velocity.output
    )
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
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
                "MixinConfigs" to "$modId.mixins.vanilla.json,$modId.mixins.forge.json"
            )
        )
    }
    from(listOf("README.md", "LICENSE")) {
        into("META-INF")
    }
}
tasks.build.get().dependsOn("spotlessApply")
