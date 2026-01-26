/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/FairFight/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin;

import com.github.tatercertified.vanilla.CombatLogger;
import com.github.tatercertified.vanilla.FairFight;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.UUID;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    private final HashMap<UUID, Object[]> transferredCombatData = new HashMap<>();

    @Inject(
            method = "disconnectAllPlayersWithProfile",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z"),
            cancellable = true)
    private void fairfight$checkForFakePlayers(
            UUID uuid, CallbackInfoReturnable<Boolean> cir, @Local ServerPlayer serverPlayer) {
        if (serverPlayer.hasDisconnected()) {
            FairFight.COMBAT_LOG_LIST.remove(uuid);
            transferredCombatData.put(
                    uuid, ((CombatLogger) serverPlayer.getCombatTracker()).getCombatTrackerInfo());
            serverPlayer.level().getServer().getPlayerList().remove(serverPlayer);
            cir.setReturnValue(false);
        }
    }

    @WrapOperation(
            method = "placeNewPlayer",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V",
                            ordinal = 0))
    private void fairfight$cancelLoginMessage(
            PlayerList instance,
            Component message,
            boolean bypassHiddenChat,
            Operation<Void> original,
            @Local(argsOnly = true) ServerPlayer serverPlayer) {
        if (transferredCombatData.containsKey(serverPlayer.getUUID())) {
            ((CombatLogger) serverPlayer.getCombatTracker())
                    .copyCombatStatus(transferredCombatData.remove(serverPlayer.getUUID()));
        } else {
            original.call(instance, message, bypassHiddenChat);
        }
    }
}
