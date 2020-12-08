package weapon.floatingtext;

import cn.nukkit.entity.Entity;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.Random;

public class ShowHealthText {

    public static void send(Entity target, String text){
        int yaw = new Random().nextInt(360) + 1;
        double r = yaw / 180;
        int pitch = new Random().nextInt(90) + 85;
        double g = pitch / 180;
        CompoundTag tag = new CompoundTag();
        tag.putList(new ListTag<DoubleTag>("Pos")
                .add(new DoubleTag("",target.x))
                .add(new DoubleTag("",target.y + 1.5))
                .add(new DoubleTag("",target.z)));
        tag.putList(new ListTag<DoubleTag>("Motion")
                .add(new DoubleTag("", -Math.sin(r * Math.PI) * Math.cos(g * Math.PI)))
                .add(new DoubleTag("",0))
                .add(new DoubleTag("",Math.cos(r * Math.PI) * Math.cos(g * Math.PI))));
        tag.putList(new ListTag<FloatTag>("Rotation")
                .add(new FloatTag("",0))
                .add(new FloatTag("",0)));
        TextEntity entity = new TextEntity(target,tag);
        entity.setNameTag(text);
        entity.spawnToAll();
    }
}
