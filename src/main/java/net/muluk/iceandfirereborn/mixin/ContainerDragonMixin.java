package net.muluk.iceandfirereborn.mixin;

import com.github.alexthe666.iceandfire.inventory.ContainerDragon;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.muluk.iceandfirereborn.item.ModItems;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContainerDragon.class)
public abstract class ContainerDragonMixin extends AbstractContainerMenu {

    @Mutable @Final @Shadow(remap = false) private Container dragonInventory;

    protected ContainerDragonMixin(MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
    }

    @Redirect(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;)V", remap = false, at = @At(value = "NEW", target = "(I)Lnet/minecraft/world/SimpleContainer;", ordinal = 0, remap = false))
    private static SimpleContainer redirectContainerSize(int size) {
        return new SimpleContainer(6);
    }

    @Inject(method = "<init>(ILnet/minecraft/world/Container;Lnet/minecraft/world/entity/player/Inventory;Lcom/github/alexthe666/iceandfire/entity/EntityDragonBase;)V", at = @At("TAIL"), remap = false)
    private void addSaddleSlot(int id, Container dragonInventoryIn, Inventory playerInventory, com.github.alexthe666.iceandfire.entity.EntityDragonBase dragonIn, CallbackInfo ci) {
        this.addSlot(new Slot(dragonInventoryIn, 5, 153, 54) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return super.mayPlace(stack) && stack.getItem() == ModItems.DRAGON_SADDLE.get();
            }
        });
    }

    /**
     * @author Muluk
     * @reason Adjusted for 6 slots, fixed buggy logic, no general slots for dragon
     */
    @Overwrite(remap = false)
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.dragonInventory.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, this.dragonInventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (this.getSlot(0).mayPlace(itemstack1) && !this.getSlot(0).hasItem()) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.getSlot(1).mayPlace(itemstack1) && !this.getSlot(1).hasItem()) {
                    if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.getSlot(2).mayPlace(itemstack1) && !this.getSlot(2).hasItem()) {
                    if (!this.moveItemStackTo(itemstack1, 2, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.getSlot(3).mayPlace(itemstack1) && !this.getSlot(3).hasItem()) {
                    if (!this.moveItemStackTo(itemstack1, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.getSlot(4).mayPlace(itemstack1) && !this.getSlot(4).hasItem()) {
                    if (!this.moveItemStackTo(itemstack1, 4, 5, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.getSlot(5).mayPlace(itemstack1) && !this.getSlot(5).hasItem()) {
                    if (!this.moveItemStackTo(itemstack1, 5, 6, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // No general slots, so if not placed, fail
                if (itemstack1.getCount() == itemstack.getCount()) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }
}