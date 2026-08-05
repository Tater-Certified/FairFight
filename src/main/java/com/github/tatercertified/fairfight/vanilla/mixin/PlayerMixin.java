/**
 * Copyright (c) 2026 QPCrummer
 * This project is Licensed under <a href="https://github.com/Tater-Certified/FairFight/blob/main/LICENSE">MIT</a>
 */
package com.github.tatercertified.fairfight.vanilla.mixin;

import com.github.tatercertified.fairfight.vanilla.CombatLogger;
import com.github.tatercertified.fairfight.vanilla.FairFight;
import com.github.tatercertified.fairfight.vanilla.PlayerInvulnerable;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Shadow
    public abstract void sendOverlayMessage(Component message);

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

    // You cannot kill players if invulnerable
    @WrapMethod(method = "attack")
    private void fairfight$preventAttackingPlayers(Entity entity, Operation<Void> original) {
        if (((Player) (Object) this) instanceof PlayerInvulnerable playerInvulnerable
                && playerInvulnerable.isInvulnerableToPlayers()
                && entity instanceof ServerPlayer) {
            this.sendOverlayMessage(Component.literal("You cannot kill players since you are invulnerable"));
        } else {
            original.call(entity);
        }
    }
}
