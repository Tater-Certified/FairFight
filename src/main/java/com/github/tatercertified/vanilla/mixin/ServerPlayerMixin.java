/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/FairFight/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin;

import com.github.tatercertified.vanilla.CombatLogger;
import com.github.tatercertified.vanilla.FairFight;
import com.mojang.authlib.GameProfile;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Shadow
    public abstract @NonNull ServerLevel level();

    @Shadow
    public abstract boolean hasDisconnected();

    @Inject(method = "die", at = @At("TAIL"))
    private void fairfight$onDeath(DamageSource damageSource, CallbackInfo ci) {
        if (this.hasDisconnected() && !CombatLogger.isInCombat((ServerPlayer) (Object) this)) {
            this.level()
                    .getServer()
                    .getPlayerList()
                    .broadcastSystemMessage(
                            Component.translatable("multiplayer.player.left", this.getDisplayName())
                                    .withStyle(ChatFormatting.YELLOW),
                            false);
            this.level().getServer().getPlayerList().remove((ServerPlayer) (Object) this);
            FairFight.COMBAT_LOG_LIST.remove(this.getUUID());
        }
    }
}
