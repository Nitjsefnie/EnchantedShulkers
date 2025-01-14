package de.rubixdev.enchantedshulkers.mixin;

import com.mojang.authlib.GameProfile;
import de.rubixdev.enchantedshulkers.enchantment.RefillEnchantment;
import de.rubixdev.enchantedshulkers.interfaces.HasClientMod;
import de.rubixdev.enchantedshulkers.interfaces.InventoryState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerSyncHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements InventoryState, HasClientMod {
    @Shadow
    @Final
    private ScreenHandlerSyncHandler screenHandlerSyncHandler;

    @Unique
    private int previousSlot = -1;

    @Unique
    private ItemStack previousMainStack = ItemStack.EMPTY;

    @Unique
    private ItemStack previousOffStack = ItemStack.EMPTY;

    @Unique
    private boolean hasOpenInventory = false;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    @Unique
    private boolean hasClientMod = false;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Override
    public void enchantedShulkers$setOpen() {
        hasOpenInventory = true;
    }

    @Override
    public void enchantedShulkers$setClosed() {
        hasOpenInventory = false;
    }

    @Override
    public void enchantedShulkers$setTrue() {
        this.hasClientMod = true;
        ((HasClientMod) this.screenHandlerSyncHandler).enchantedShulkers$setTrue();
    }

    //#if MC < 12002
    //$$ @Override
    //$$ public void enchantedShulkers$submit() {
    //$$     de.rubixdev.enchantedshulkers.Mod.LOGGER.info("Player " + this.getEntityName() + " has logged in with" + (this.hasClientMod ? "" : "out") + " the client-side mod.");
    //$$     ((HasClientMod) this.screenHandlerSyncHandler).enchantedShulkers$submit();
    //$$ }
    //#endif

    @SuppressWarnings("DataFlowIssue") // cast to ServerPlayerEntity isn't invalid
    @Inject(
            method = "playerTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;tick()V"))
    public void playerTick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        int currentSlot = player.getInventory().selectedSlot;
        ItemStack currentMainStack = player.getInventory().getMainHandStack();
        ItemStack currentOffStack = player.getInventory().getStack(PlayerInventory.OFF_HAND_SLOT);

        RefillEnchantment.onPlayerTick(
                (ServerPlayerEntity) (Object) this,
                hasOpenInventory,
                currentSlot,
                currentMainStack,
                currentOffStack,
                previousSlot,
                previousMainStack,
                previousOffStack);

        previousSlot = currentSlot;
        previousMainStack = currentMainStack.copy();
        previousOffStack = currentOffStack.copy();
    }
}
