/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/FairFight/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin;

import com.github.tatercertified.vanilla.CombatLogger;
import com.github.tatercertified.vanilla.FairFight;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.gamerules.GameRules;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(
            method = "tick",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/damagesource/CombatTracker;recheckStatus()V",
                            shift = At.Shift.AFTER))
    private void lifesteal$checkIfDead(CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof ServerPlayer serverPlayer
                && serverPlayer.hasDisconnected()
                && !CombatLogger.isInCombat(serverPlayer)) {
            serverPlayer.level().getServer().getPlayerList().remove(serverPlayer);
            serverPlayer
                    .level()
                    .getServer()
                    .getPlayerList()
                    .broadcastSystemMessage(
                            Component.translatable(
                                            "multiplayer.player.left",
                                            serverPlayer.getDisplayName())
                                    .withStyle(ChatFormatting.YELLOW),
                            false);
        }
    }

    @Inject(
            method = "tick",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/damagesource/CombatTracker;recheckStatus()V",
                            shift = At.Shift.AFTER))
    private void fairfight$showCombatTime(CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof ServerPlayer serverPlayer) {
            GameRules gameRules = serverPlayer.level().getGameRules();
            if (CombatLogger.isInCombat(serverPlayer)
                    && gameRules.get(FairFight.COMBAT_TIME_SHOWN)) {
                int seconds = CombatLogger.getCombatSecondsLeft(serverPlayer);
                if (seconds > 0) {
                    serverPlayer.sendSystemMessage(
                            Component.literal("You are in combat for " + seconds + " more seconds"),
                            true);
                }
            }
        }
    }

    @Inject(
            method = "knockback",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V",
                            shift = At.Shift.AFTER))
    private void fairfight$simulateKnockback(CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof ServerPlayer serverPlayer
                && serverPlayer.hasDisconnected()) {
            serverPlayer.move(MoverType.SELF, serverPlayer.getDeltaMovement());
        }
    }
}
