/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/FairFight/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin;

import com.github.tatercertified.vanilla.FairFight;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CombatTracker.class)
public class CombatTrackerMixin {

    @Shadow @Final private LivingEntity mob;

    @ModifyConstant(method = "recheckStatus", constant = @Constant(intValue = 300))
    private int fairfight$variableCombatTime(int constant) {
        Level level = mob.level();
        if (!level.isClientSide()) {
            return ((ServerLevel) (level)).getGameRules().get(FairFight.IN_COMBAT_TIME);
        }
        return constant;
    }
}
