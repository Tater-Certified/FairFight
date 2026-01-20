/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/FairFight/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin;

import com.github.tatercertified.vanilla.FairFight;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    private final Set<UUID> cancelLogin = new HashSet<>();

    @Inject(
            method = "disconnectAllPlayersWithProfile",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z"),
            cancellable = true)
    private void fairfight$checkForFakePlayers(
            UUID uuid, CallbackInfoReturnable<Boolean> cir, @Local ServerPlayer serverPlayer) {
        if (serverPlayer.hasDisconnected()) {
            FairFight.COMBAT_LOG_LIST.remove(uuid);
            serverPlayer.level().getServer().getPlayerList().remove(serverPlayer);
            cancelLogin.add(uuid);
            cir.setReturnValue(false);
        }
    }

    @WrapOperation(
            method = "placeNewPlayer",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;",
                            ordinal = 0))
    private MutableComponent fairfight$cancelLoginMessage(
            String string,
            Object[] objects,
            Operation<MutableComponent> original,
            @Local(argsOnly = true) ServerPlayer serverPlayer) {
        if (cancelLogin.contains(serverPlayer.getUUID())) {
            cancelLogin.remove(serverPlayer.getUUID());
            return Component.empty();
        } else {
            return original.call(string, objects);
        }
    }
}
