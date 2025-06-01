/**
 * Copyright (c) 2025 SomeNameHere - person@example.com
 * The project is Licensed under <a href="https://github.com/Example/TestMod/blob/dev/LICENSE">MIT</a>
 */
package com.example.templatemod.paper;

import com.example.templatemod.common.CommonClass;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TemplateModSpigot extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getLogger().info("Hello Paper world!");
        CommonClass.init();

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Hello, " + event.getPlayer().getName() + "!");
    }
}
