/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/FairFight/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin;

import com.github.tatercertified.vanilla.CombatLogger;
import com.github.tatercertified.vanilla.FairFight;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CombatTracker.class)
public class CombatTrackerMixin implements CombatLogger {

    @Shadow @Final private LivingEntity mob;

    @Shadow private boolean inCombat;

    @Shadow private int lastDamageTime;
    private boolean inPlayerCombat;
    private int lastPlayerDamageTime;

    @ModifyConstant(method = "recheckStatus", constant = @Constant(intValue = 300))
    private int fairfight$variableCombatTime(int constant) {
        Level level = mob.level();
        if (!level.isClientSide()) {
            return ((ServerLevel) (level)).getGameRules().get(FairFight.IN_COMBAT_TIME);
        }
        return constant;
    }

    @Inject(
            method = "recordDamage",
            at =
                    @At(
                            value = "FIELD",
                            target =
                                    "Lnet/minecraft/world/damagesource/CombatTracker;combatStartTime:I",
                            opcode = Opcodes.PUTFIELD))
    private void fairfight$recordDamage(DamageSource source, float damage, CallbackInfo ci) {
        if (source.getEntity() instanceof Player) {
            inPlayerCombat = true;
        }
    }

    @Inject(
            method = "recordDamage",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private void fairfight$setLastDamageTime(DamageSource source, float damage, CallbackInfo ci) {
        if (source.getEntity() instanceof Player) {
            lastPlayerDamageTime = this.mob.tickCount;
        }
    }

    @Inject(
            method = "recheckStatus",
            at =
                    @At(
                            value = "FIELD",
                            target =
                                    "Lnet/minecraft/world/damagesource/CombatTracker;takingDamage:Z",
                            ordinal = 0,
                            opcode = Opcodes.GETFIELD))
    private void fairfight$recheckStatus(CallbackInfo ci, @Local(ordinal = 0) int i) {
        if (this.inPlayerCombat && this.mob.tickCount - this.lastPlayerDamageTime > i) {
            this.inPlayerCombat = false;
        }
    }

    @Override
    public boolean isInPlayerCombat() {
        return this.inPlayerCombat;
    }

    @Override
    public int getPlayerCombatSecondsLeft() {
        if (this.mob.level().isClientSide()) {
            return 0;
        } else {
            int combatDuration = this.mob.tickCount - this.lastPlayerDamageTime;
            return Mth.ceil(
                    (((ServerLevel) this.mob.level()).getGameRules().get(FairFight.IN_COMBAT_TIME)
                                    - combatDuration)
                            / 20.0F);
        }
    }

    @Override
    public boolean isInCombat() {
        return this.inCombat;
    }

    @Override
    public int getCombatSecondsLeft() {
        if (this.mob.level().isClientSide()) {
            return 0;
        } else {
            int combatDuration = this.mob.tickCount - this.lastDamageTime;
            return Mth.ceil(
                    (((ServerLevel) this.mob.level()).getGameRules().get(FairFight.IN_COMBAT_TIME)
                                    - combatDuration)
                            / 20.0F);
        }
    }

    @Override
    public void copyCombatStatus(Object[] originalInfo) {
        this.inPlayerCombat = (boolean) originalInfo[0];
        int lastPlayerDamageTime = (int) originalInfo[1];
        this.inCombat = (boolean) originalInfo[2];
        int lastDamageTime = (int) originalInfo[3];
        int mobTickCount = (int) originalInfo[4];

        // Calculate proper lastPlayerDamageTime
        int playerDamagerDurationTicks = mobTickCount - lastPlayerDamageTime;
        int damageDurationTicks = mobTickCount - lastDamageTime;
        this.lastPlayerDamageTime = this.mob.tickCount - playerDamagerDurationTicks;
        this.lastDamageTime = this.mob.tickCount - damageDurationTicks;
    }

    @Override
    public Object[] getCombatTrackerInfo() {
        return new Object[] {
            this.inPlayerCombat,
            this.lastPlayerDamageTime,
            this.inCombat,
            this.lastDamageTime,
            this.mob.tickCount
        };
    }
}
