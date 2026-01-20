/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/FairFight/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.sponge;

import com.github.tatercertified.vanilla.FairFight;
import com.google.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin(FairFight.MOD_ID)
public class FairFightSponge {

    @Inject
    public FairFightSponge(Logger logger, PluginContainer pluginContainer) {}
}
