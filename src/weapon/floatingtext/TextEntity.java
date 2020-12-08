package weapon.floatingtext;

import cn.nukkit.entity.Entity;
import cn.nukkit.nbt.tag.CompoundTag;


public class TextEntity extends Entity {

    @Override
    public int getNetworkId() {
        return 64;
    }

    public boolean close = false;



    TextEntity(Entity entity, CompoundTag nbt) {
        super(entity.chunk, nbt);
        this.motionX = entity.motionX;
        this.motionY = entity.motionY;
        this.motionZ = entity.motionZ;
        this.onGround = true;

    }


    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(1);
        this.setHealth(1);
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
    }


    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }
        this.timing.startTiming();

        int tickDiff = currentTick - this.lastUpdate;
        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }
        this.lastUpdate = currentTick;

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (this.isAlive()) {

            if (!this.isCollided) {
                this.motionY -= 0.03;
            }

            this.move(this.motionX, this.motionY, this.motionZ);

            if (!this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001) {
                double f = Math.sqrt((this.motionX * this.motionX) + (this.motionZ * this.motionZ));
                this.yaw = (Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);
                this.pitch = (Math.atan2(this.motionY, f) * 180 / Math.PI);
                hasUpdate = true;
            }

            this.updateMovement();
        }

        if (this.age > 60 || this.isCollided){
            this.kill();
            hasUpdate = false;
            close = true;
        }

        this.timing.stopTiming();
        close = true;
        return hasUpdate;
    }

}
