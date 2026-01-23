/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/FairFight/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRule;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FairFight {
    public static final Map<UUID, ServerPlayer> COMBAT_LOG_LIST = new HashMap<>();
    public static final String MOD_ID = "fairfight";

    public static GameRule<Integer> IN_COMBAT_TIME;
    public static GameRule<Boolean> COMBAT_TIME_SHOWN;
    public static GameRule<Boolean> COMBAT_LOGGER_PLAYERS_ONLY;
    public static GameRule<Boolean> DISABLE_ELYTRA_IN_COMBAT;
}
