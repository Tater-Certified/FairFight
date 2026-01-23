/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/FairFight/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRules;

public interface CombatLogger {
    boolean isInPlayerCombat();

    int getPlayerCombatSecondsLeft();

    boolean isInCombat();

    int getCombatSecondsLeft();

    static boolean isInCombat(ServerPlayer player) {
        GameRules gameRules = player.level().getGameRules();
        if (gameRules.get(FairFight.COMBAT_LOGGER_PLAYERS_ONLY)) {
            return ((CombatLogger) player.getCombatTracker()).isInPlayerCombat();
        } else {
            return ((CombatLogger) player.getCombatTracker()).isInCombat();
        }
    }

    static int getCombatSecondsLeft(ServerPlayer player) {
        GameRules gameRules = player.level().getGameRules();
        if (gameRules.get(FairFight.COMBAT_LOGGER_PLAYERS_ONLY)) {
            return ((CombatLogger) player.getCombatTracker()).getPlayerCombatSecondsLeft();
        } else {
            return ((CombatLogger) player.getCombatTracker()).getCombatSecondsLeft();
        }
    }
}
