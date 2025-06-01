/**
 * Copyright (c) 2025 SomeNameHere - person@example.com
 * The project is Licensed under <a href="https://github.com/Example/TestMod/blob/dev/LICENSE">MIT</a>
 */
package com.example.templatemod.neoforge;

import com.example.templatemod.Constants;
import com.example.templatemod.common.CommonClass;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class TemplateModNeoForge {
    public TemplateModNeoForge(IEventBus eventBus) {
        Constants.logger().info("Hello NeoForge world!");
        CommonClass.init();
    }
}
