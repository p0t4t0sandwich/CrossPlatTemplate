/**
 * Copyright (c) 2025 SomeNameHere - person@example.com
 * The project is Licensed under <a href="https://github.com/Example/TestMod/blob/dev/LICENSE">MIT</a>
 */
package com.example.templatemod.bungeecord;

import com.example.templatemod.Constants;
import com.example.templatemod.vanilla.CommonClass;

import net.md_5.bungee.api.plugin.Plugin;

public class TemplateModBungeeCord extends Plugin {
    @Override
    public void onEnable() {
        Constants.logger().info("Hello BungeeCord world!");
        CommonClass.init();
    }

    @Override
    public void onDisable() {}
}
