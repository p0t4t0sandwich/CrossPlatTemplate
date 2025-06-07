/**
* Copyright (c) 2025 SomeNameHere - person@example.com
* The project is Licensed under <a href="https://github.com/Example/TestMod/blob/dev/LICENSE">MIT</a>
*/
package com.example.templatemod.paper;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.JarLibrary;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;

import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import java.nio.file.Path;

@SuppressWarnings("UnstableApiUsage")
public class TemplateModPaperLoader implements PluginLoader {
    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        classpathBuilder.addLibrary(new JarLibrary(Path.of("dependency.jar")));

        MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addDependency(
                new Dependency(new DefaultArtifact("com.example:example:version"), null));
        resolver.addRepository(
                new RemoteRepository.Builder(
                                "paper",
                                "default",
                                "https://repo.papermc.io/repository/maven-public/")
                        .build());

        classpathBuilder.addLibrary(resolver);
    }
}
