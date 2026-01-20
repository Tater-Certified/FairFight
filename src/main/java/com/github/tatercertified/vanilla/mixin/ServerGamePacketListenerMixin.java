/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/FairFight/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin;

import com.github.tatercertified.vanilla.FairFight;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerMixin {
    @Shadow public ServerPlayer player;

    @WrapOperation(
            method = "removePlayerFromWorld",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/server/players/PlayerList;remove(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void lifesteal$removePlayerFromWorld(
            PlayerList instance, ServerPlayer serverPlayer, Operation<Void> original) {
        if (!((CombatTrackerAccessor) (serverPlayer.getCombatTracker())).isInCombat()) {
            original.call(instance, serverPlayer);
        } else {
            FairFight.COMBAT_LOG_LIST.put(serverPlayer.getUUID(), serverPlayer);
        }
    }

    @WrapOperation(
            method = "removePlayerFromWorld",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    private void lifesteal$removePlayerFromWorld(
            PlayerList instance, Component component, boolean bl, Operation<Void> original) {
        if (!((CombatTrackerAccessor) (this.player.getCombatTracker())).isInCombat()) {
            original.call(instance, component, bl);
        } else {
            FairFight.COMBAT_LOG_LIST.put(this.player.getUUID(), this.player);
        }
    }
}
