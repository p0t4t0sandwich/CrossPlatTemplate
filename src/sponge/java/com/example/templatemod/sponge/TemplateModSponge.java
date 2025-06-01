/**
 * Copyright (c) 2025 SomeNameHere - person@example.com
 * The project is Licensed under <a href="https://github.com/Example/TestMod/blob/dev/LICENSE">MIT</a>
 */
package com.example.templatemod.sponge;

import com.example.templatemod.Constants;
import com.google.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin(Constants.MOD_ID)
public class TemplateModSponge {
    private final Logger logger;
    private final PluginContainer pluginContainer;

    @Inject
    public TemplateModSponge(Logger logger, PluginContainer pluginContainer) {
        this.logger = logger;
        this.pluginContainer = pluginContainer;
    }

    @Listener
    public void onServerStart(final StartedEngineEvent<Server> event) {
        this.logger.info("Successfully running ExamplePlugin!!!");
    }
}
