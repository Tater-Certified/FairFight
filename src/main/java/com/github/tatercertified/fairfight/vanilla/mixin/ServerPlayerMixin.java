/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/FairFight/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.fairfight.vanilla.mixin;

import com.github.tatercertified.fairfight.vanilla.CombatLogger;
import com.github.tatercertified.fairfight.vanilla.FairFight;
import com.github.tatercertified.fairfight.vanilla.PlayerInvulnerable;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements PlayerInvulnerable {
    private long playerDamageInvulnerabilityTicks;

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Shadow
    public abstract @NonNull ServerLevel level();

    @Shadow
    public abstract boolean hasDisconnected();

    @Shadow
    public abstract void sendOverlayMessage(Component message);

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

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void fairfight$setInvulnerability(ServerPlayer oldPlayer, boolean restoreAll, CallbackInfo ci) {
        int secs = this.level().getServer().getGameRules().get(FairFight.RESPAWN_INVULNERABILITY_SECONDS);
        this.playerDamageInvulnerabilityTicks = secs * 20L;
        if (secs == 1) {
            this.sendOverlayMessage(Component.literal("You have 1 second of invulnerability."));
        } else {
            this.sendOverlayMessage(Component.literal("You have " + secs + " seconds of invulnerability."));
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void fairfight$decreaseInvulnerability(CallbackInfo ci) {
        if (this.playerDamageInvulnerabilityTicks > 0) {
            // Send this when on the last tick
            if (this.playerDamageInvulnerabilityTicks == 1) {
                this.sendOverlayMessage(Component.literal("You are now vulnerable to players").withColor(TextColor.RED));
            } else if (this.playerDamageInvulnerabilityTicks <= (20L * this.level().getServer().getGameRules().get(FairFight.BEGIN_INVULNERABILITY_COUNTDOWN_SECONDS)) && this.playerDamageInvulnerabilityTicks % 20 == 0) {
                // Start countdown
                int seconds = (int) (this.playerDamageInvulnerabilityTicks / 20);
                if (seconds == 1) {
                    this.sendOverlayMessage(Component.literal("1 second remaining of invulnerability"));
                } else {
                    this.sendOverlayMessage(Component.literal(seconds + " seconds remaining of invulnerability"));
                }
            }
            this.playerDamageInvulnerabilityTicks--;
        }
    }

    // You cannot be killed by players if invulnerable
    @Inject(method = "canHarmPlayer", at = @At("HEAD"), cancellable = true)
    private void fairfight$checkInvulnerability(Player target, CallbackInfoReturnable<Boolean> cir) {
        if (this.isInvulnerableToPlayers()) {
            target.sendOverlayMessage(Component.literal("This player has respawn invulnerability"));
            cir.setReturnValue(false);
        }
    }

    @Override
    public boolean isInvulnerableToPlayers() {
        return this.playerDamageInvulnerabilityTicks > 0;
    }
}
