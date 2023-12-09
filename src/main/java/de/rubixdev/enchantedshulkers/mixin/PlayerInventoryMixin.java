package de.rubixdev.enchantedshulkers.mixin;

import de.rubixdev.enchantedshulkers.enchantment.SiphonEnchantment;
import de.rubixdev.enchantedshulkers.enchantment.VacuumEnchantment;
import de.rubixdev.enchantedshulkers.enchantment.VoidEnchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Shadow
    @Final
    public PlayerEntity player;

    @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    public void insertStack(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;
        if (SiphonEnchantment.onItemPickup(serverPlayer, stack) && stack.isEmpty()) {
            cir.setReturnValue(true);
        } else if (VacuumEnchantment.onItemPickup(serverPlayer, stack) && stack.isEmpty()) {
            cir.setReturnValue(true);
        } else if (VoidEnchantment.onItemPickup(serverPlayer, stack) && stack.isEmpty()) {
            cir.setReturnValue(true);
        }
    }
}
