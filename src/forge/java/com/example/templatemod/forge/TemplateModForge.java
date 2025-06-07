/**
* Copyright (c) 2025 SomeNameHere - person@example.com
* The project is Licensed under <a href="https://github.com/Example/TestMod/blob/dev/LICENSE">MIT</a>
*/
package com.example.templatemod.forge;

import com.example.templatemod.Constants;
import com.example.templatemod.common.CommonClass;

import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class TemplateModForge {
    public TemplateModForge() {
        Constants.logger().info("Hello Forge world!");
        CommonClass.init();
    }
}
