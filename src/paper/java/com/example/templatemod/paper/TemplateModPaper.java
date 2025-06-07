/**
* Copyright (c) 2025 SomeNameHere - person@example.com
* The project is Licensed under <a href="https://github.com/Example/TestMod/blob/dev/LICENSE">MIT</a>
*/
package com.example.templatemod.paper;

import com.example.templatemod.Constants;
import com.example.templatemod.common.CommonClass;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TemplateModPaper extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        Constants.logger().info("Hello Paper world!");
        CommonClass.init();

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer()
                .sendMessage(Component.text("Hello, " + event.getPlayer().getName() + "!"));
    }
}
