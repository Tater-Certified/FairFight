/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/FairFight/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin;

import com.github.tatercertified.vanilla.FairFight;

import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRules;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRules.class)
public abstract class GameRuleRegistrationMixin {

    @Shadow
    private static GameRule<Integer> registerInteger(
            String name, GameRuleCategory category, int defaultValue, int minValue) {
        return null;
    }

    @Shadow
    private static GameRule<Boolean> registerBoolean(
            String name, GameRuleCategory category, boolean defaultValue) {
        return null;
    }

    @Inject(
            method = "<clinit>",
            at =
                    @At(
                            value = "FIELD",
                            target =
                                    "Lnet/minecraft/world/level/gamerules/GameRules;FIRE_SPREAD_RADIUS_AROUND_PLAYER:Lnet/minecraft/world/level/gamerules/GameRule;",
                            opcode = Opcodes.PUTSTATIC))
    private static void fairfight$registerGameRules(CallbackInfo ci) {
        FairFight.IN_COMBAT_TIME =
                registerInteger("in_combat_ticks", GameRuleCategory.PLAYER, 300, 1);
        FairFight.COMBAT_TIME_SHOWN =
                registerBoolean("combat_time_shown", GameRuleCategory.PLAYER, true);
        FairFight.COMBAT_LOGGER_PLAYERS_ONLY =
                registerBoolean("combat_logger_players_only", GameRuleCategory.PLAYER, true);
        FairFight.DISABLE_ELYTRA_IN_COMBAT =
                registerBoolean("disable_elytra_in_combat", GameRuleCategory.PLAYER, false);
    }
}
