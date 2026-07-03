/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/FairFight/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin;

import com.github.tatercertified.vanilla.CombatLogger;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerMixin {
    @Shadow public ServerPlayer player;

    @Inject(method = {"handleChatCommand", "handleSignedChatCommand"}, at = @At("HEAD"), cancellable = true)
    private void fairfight$blockCommandsInPvp(CallbackInfo ci) {
        if (this.player.level().getGameRules().get(FairFight.DISABLE_COMMANDS_IN_PVP)
                && ((CombatLogger) this.player.getCombatTracker()).isInPlayerCombat()) {
            this.player.sendSystemMessage(
                    Component.literal("You cannot use commands in PvP combat."));
            ci.cancel();
        }
    }

    @WrapOperation(
            method = "removePlayerFromWorld",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/server/players/PlayerList;remove(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void fairfight$removePlayerFromWorld(
            PlayerList instance, ServerPlayer player, Operation<Void> original) {
        if (!CombatLogger.isInCombat(player)) {
            original.call(instance, player);
        } else {
            FairFight.COMBAT_LOG_LIST.put(player.getUUID(), player);
        }
    }

    @WrapOperation(
            method = "removePlayerFromWorld",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    private void fairfight$removePlayerFromWorld(
            PlayerList instance, Component message, boolean overlay, Operation<Void> original) {
        if (!CombatLogger.isInCombat(this.player)) {
            original.call(instance, message, overlay);
        } else {
            FairFight.COMBAT_LOG_LIST.put(this.player.getUUID(), this.player);
        }
    }
}
