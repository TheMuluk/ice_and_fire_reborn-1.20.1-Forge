package net.muluk.iceandfirereborn.mixin;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.muluk.iceandfirereborn.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityDragonBase.class)
abstract class EntityDragonBaseMixin extends TamableAnimal implements IAnimatedEntity {

    @Shadow(remap = false)
    public SimpleContainer dragonInventory;

    @Shadow(remap = false)
    protected abstract void updateContainerEquipment();

    protected EntityDragonBaseMixin(EntityType<? extends TamableAnimal> pType, net.minecraft.world.level.Level pLevel) {
        super(pType, pLevel);
    }

    /**
     * @author muluk
     * @reason Increase inventory size to 6 for extra slot (saddle)
     */
    @Inject(method = "getContainerSize", at = @At("RETURN"), cancellable = true, remap = false)
    private void increaseInventorySize(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(6);
    }

    /**
     * Persist the dragon-inventory to NBT. Inject at the end of the entity save method.
     */
    @Inject(
            method = {
                    "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"
            },
            at = @At("TAIL"),
            require = 0
    )
    private void saveDragonInventory(CompoundTag compound, CallbackInfo ci) {
        if (this.dragonInventory != null) {
            ListTag list = new ListTag();
            for (int i = 0; i < this.dragonInventory.getContainerSize(); i++) {
                ItemStack stack = this.dragonInventory.getItem(i);
                if (!stack.isEmpty()) {
                    CompoundTag itemTag = new CompoundTag();
                    itemTag.putByte("Slot", (byte) i);
                    stack.save(itemTag);
                    list.add(itemTag);
                }
            }
            compound.put("DragonItems", list);
        }
    }

    /**
     * Load the dragon-inventory from NBT. Inject at the end of the entity load method.
     */
    @Inject(
            method = {
                    "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"
            },
            at = @At("TAIL"),
            require = 0
    )
    private void loadDragonInventory(CompoundTag compound, CallbackInfo ci) {
        String key = "DragonItems";
        if (compound.contains(key, Tag.TAG_LIST)) {
            ListTag list = compound.getList(key, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag itemTag = list.getCompound(i);
                int slot = itemTag.getByte("Slot") & 255;
                if (slot < this.dragonInventory.getContainerSize()) {
                    this.dragonInventory.setItem(slot, ItemStack.of(itemTag));
                }
            }
        }
        this.updateContainerEquipment();
    }

    /**
     * Check if the dragon has a saddle in the inventory.
     */
    @Unique
    boolean iceAndFire_Reborn_1_20_1_Forge$hasSaddle() {
        ItemStack saddle = this.dragonInventory.getItem(5);
        return !saddle.isEmpty() && saddle.getItem() == ModItems.DRAGON_SADDLE.get();
    }

    /**
     * Require saddle for riding if dragon stage > 2
     */
    @Inject(
            method = "canAddPassenger(Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("RETURN"),
            cancellable = true,
            require = 0
    )
    private void requireSaddleForRiding(
            Entity passenger,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!cir.getReturnValue()) return;

        if (passenger instanceof Player
                && this.getDragonStage() > 2
                && !iceAndFire_Reborn_1_20_1_Forge$hasSaddle()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void handleSaddleInteraction(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getMainHandItem();

        if (!this.isOwnedBy(player) || this.getDragonStage() <= 2) {
            return;
        }

        if (this.isOwnedBy(player) && stack.isEmpty() && this.getDragonStage() > 2 && !player.isShiftKeyDown()) {
            if (!level().isClientSide) {
                if (iceAndFire_Reborn_1_20_1_Forge$hasSaddle()) {
                    if (!player.isPassenger() && !this.isBaby()) {
                        player.startRiding(this, true);
                        cir.setReturnValue(InteractionResult.SUCCESS);
                        cir.cancel();
                    }
                } else {
                    player.displayClientMessage(Component.translatable("entity.iceandfirereborn.dragon.no_saddle"), true);
                    cir.setReturnValue(InteractionResult.FAIL);
                    cir.cancel();
                }
            }
        }
    }

    @Shadow(remap = false)
    public abstract int getDragonStage();
}