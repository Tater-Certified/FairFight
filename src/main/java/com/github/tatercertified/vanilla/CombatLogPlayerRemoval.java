/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/FairFight/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla;

import net.minecraft.server.level.ServerPlayer;

import java.util.Iterator;

public interface CombatLogPlayerRemoval {
    void removePlayer(Iterator<ServerPlayer> players, ServerPlayer current);
}
