/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/FairFight/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

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
                && !((CombatTrackerAccessor) (serverPlayer.getCombatTracker())).isInCombat()) {
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
}
