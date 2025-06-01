/**
 * Copyright (c) 2025 SomeNameHere - person@example.com
 * The project is Licensed under <a href="https://github.com/Example/TestMod/blob/dev/LICENSE">MIT</a>
 */
package com.example.templatemod.fabric;

import com.example.templatemod.Constants;
import com.example.templatemod.common.CommonClass;

import net.fabricmc.api.ModInitializer;

public class TemplateModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Constants.logger().info("Hello Fabric world!");
        CommonClass.init();
    }
}
