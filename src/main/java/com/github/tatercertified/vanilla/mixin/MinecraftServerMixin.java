/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/FairFight/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin;

import com.github.tatercertified.vanilla.FairFight;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow
    public abstract int getTickCount();

    @Inject(method = "tickServer", at = @At("TAIL"))
    private void fairfight$tickFakePlayers(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        Iterator<ServerPlayer> iterator2 = FairFight.COMBAT_LOG_LIST.values().iterator();
        while (iterator2.hasNext()) {
            ServerPlayer player = iterator2.next();

            // Apply gravity manually
            if (!player.onGround()) {
                Vec3 vel = player.getDeltaMovement();
                player.setDeltaMovement(vel.x, vel.y - 0.08, vel.z);
            }
            player.move(MoverType.SELF, player.getDeltaMovement());
            player.needsSync = true;

            ((ServerGamePacketListenerImplInvokerMixin) player.connection).invokeTickPlayer();

            if (this.getTickCount() % 20 == 0) {
                player.getCombatTracker().recheckStatus();
            }

            if (!((CombatTrackerAccessor) (player.getCombatTracker())).isInCombat()) {
                player.level().getServer().getPlayerList().remove(player);
                player.level()
                        .getServer()
                        .getPlayerList()
                        .broadcastSystemMessage(
                                Component.translatable(
                                                "multiplayer.player.left", player.getDisplayName())
                                        .withStyle(ChatFormatting.YELLOW),
                                false);
                iterator2.remove();
            }
        }
    }
}
