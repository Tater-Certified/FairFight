/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/FairFight/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.vanilla.mixin;

import com.github.tatercertified.vanilla.CombatLogger;
import com.github.tatercertified.vanilla.FairFight;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(
            method = "tryToStartFallFlying",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/entity/player/Player;startFallFlying()V"),
            cancellable = true)
    private void fairfight$cancelElytraFlight(CallbackInfoReturnable<Boolean> cir) {
        if (((Player) (Object) this) instanceof ServerPlayer serverPlayer
                && serverPlayer.level().getGameRules().get(FairFight.DISABLE_ELYTRA_IN_COMBAT)
                && CombatLogger.isInCombat(serverPlayer)) {
            cir.setReturnValue(false);
        }
    }
}
