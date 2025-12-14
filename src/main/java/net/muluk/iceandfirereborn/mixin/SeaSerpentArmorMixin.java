package net.muluk.iceandfirereborn.mixin;

import com.github.alexthe666.iceandfire.item.ItemSeaSerpentArmor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemSeaSerpentArmor.class)
public class SeaSerpentArmorMixin {

    @Unique
    private int test_1_20_1_Forge$strengthLevel = 0;

    @Inject(method = "onArmorTick", at = @At("HEAD"), cancellable = true, remap = false)
    private void overrideSeaSerpentEffects(ItemStack stack, Level world, Player player, CallbackInfo ci) {
        ci.cancel();

        // Count how many Sea Serpent armor pieces the player is wearing
        int count = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR &&
                    player.getItemBySlot(slot).getItem() instanceof ItemSeaSerpentArmor) {
                count++;
            }
        }

        // Apply effects only if the full armor set is worn and the player is in water or rain
        if (count == 4 && player.isInWaterOrRain()) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 50, test_1_20_1_Forge$strengthLevel, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 50, 0, false, false));
        }
        // Remove effects if less than the full armor set is worn
        else if (count < 4) {
            player.removeEffect(MobEffects.DAMAGE_BOOST);
            player.removeEffect(MobEffects.WATER_BREATHING);
        }
    }
}
