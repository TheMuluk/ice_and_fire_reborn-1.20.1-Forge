package net.muluk.iceandfirereborn.misc;

import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class HippogryphAIWanderPatched extends Goal {
    private final EntityHippogryph hippo;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private final double speed;
    private int executionChance;
    private boolean mustUpdate;

    public HippogryphAIWanderPatched(EntityHippogryph creatureIn, double speedIn) {
        this(creatureIn, speedIn, 20);
    }

    public HippogryphAIWanderPatched(EntityHippogryph creatureIn, double speedIn, int chance) {
        this.hippo = creatureIn;
        this.speed = speedIn;
        this.executionChance = chance;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        if (!this.hippo.canMove()) {
            return false;
        } else if (!this.hippo.isFlying() && !this.hippo.isHovering()) {
            if (!this.mustUpdate && this.hippo.getRandom().nextInt(this.executionChance) != 0) {
                return false;
            } else {
                Vec3 Vector3d = DefaultRandomPos.getPos(this.hippo, 10, 7);
                if (Vector3d == null) {
                    return false;
                } else {
                    this.xPosition = Vector3d.x;
                    this.yPosition = Vector3d.y+ this.hippo.getRandom().nextIntBetweenInclusive(-4, 2);
                    this.zPosition = Vector3d.z;
                    this.mustUpdate = false;
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    public boolean canContinueToUse() {
        return !this.hippo.getNavigation().isDone();
    }

    public void start() {
        this.hippo.getNavigation().moveTo(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }

    public void makeUpdate() {
        this.mustUpdate = true;
    }

    public void setExecutionChance(int newchance) {
        this.executionChance = newchance;
    }
}