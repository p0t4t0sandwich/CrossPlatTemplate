/**
 * Copyright (c) 2025 SomeNameHere - person@example.com
 * The project is Licensed under <a href="https://github.com/Example/TestMod/blob/dev/LICENSE">MIT</a>
 */
package com.example.templatemod.velocity;

import com.example.templatemod.Constants;
import com.example.templatemod.common.CommonClass;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;

import org.slf4j.Logger;

@Plugin(
        id = Constants.MOD_ID,
        name = Constants.MOD_NAME,
        version = Constants.VERSION,
        authors = Constants.AUTHOR,
        description = Constants.DESCRIPTION,
        url = Constants.URL)
public class TemplateModVelocity {
    private final PluginContainer plugin;
    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public TemplateModVelocity(PluginContainer plugin, ProxyServer server, Logger logger) {
        this.plugin = plugin;
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        Constants.logger().info("Hello Velocity world!");
        CommonClass.init();
    }
}
