package net.muluk.iceandfirereborn.mixin;

import com.github.alexthe666.iceandfire.datagen.tags.IafItemTags;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.github.alexthe666.iceandfire.entity.ai.HippogryphAIMate;
import com.github.alexthe666.iceandfire.entity.ai.HippogryphAITarget;
import com.github.alexthe666.iceandfire.entity.ai.HippogryphAITargetItems;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.muluk.iceandfirereborn.misc.HippogryphAIWanderPatched;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityHippogryph.class)
public abstract class EntityHippogryphMixin extends TamableAnimal {
    protected EntityHippogryphMixin(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"), cancellable = true)
    private void patchGoals(CallbackInfo ci) {
        ci.cancel();
        EntityHippogryph hippogryph = (EntityHippogryph) (Object) this;
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, LivingEntity.class, 6.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new HippogryphAIMate(hippogryph, 1.0D));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.0D, Ingredient.of(IafItemTags.TEMPT_HIPPOGRYPH), false));
        this.goalSelector.addGoal(8, new HippogryphAIWanderPatched(hippogryph, 1.0D));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new HippogryphAITargetItems<>(this, false));
        this.targetSelector.addGoal(5, new HippogryphAITarget<>(hippogryph, LivingEntity.class, false, entity -> !(entity instanceof AbstractHorse) && DragonUtils.isAlive(entity)));
        this.targetSelector.addGoal(5, new HippogryphAITarget<>(hippogryph, Player.class, 350, false, entity -> entity instanceof Player player && !player.isCreative()));
    }
}