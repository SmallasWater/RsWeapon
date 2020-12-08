package weapon.utils;



import cn.nukkit.entity.Entity;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.DustParticle;
import cn.nukkit.level.particle.FlameParticle;
import cn.nukkit.level.particle.HappyVillagerParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.ArrayList;

public class Effects {
    /** 减伤 */
    public static void addRelief(Position position){
        ArrayList<Double[]> a = new ArrayList<>();
        ArrayList<Double[]> pos = new ArrayList<>();
        for(int i = 0;i <= 90;i += 9){
            double x = 1.5 * Math.cos(Math.toRadians(i));
            double y = 1.5 * Math.sin(Math.toRadians(i));
            a.add(new Double[]{x,+y});
            a.add(new Double[]{x,-y});
        }

        for(Double[] b : a){
            for(int i = 0;i <= 90;i += 9){
                double x = b[0] * Math.cos(Math.toRadians(i));
                double z= b[0] * Math.sin(Math.toRadians(i));
                pos.add(new Double[]{x,b[1],z});
                pos.add(new Double[]{-z,b[1],x});
                pos.add(new Double[]{-x,b[1],-z});
                pos.add(new Double[]{z,b[1],-x});
            }
        }
        for (Double[] xyz:pos){
            position.level.addParticle(new FlameParticle(new Vector3(xyz[0]+ position.x,xyz[1]+ position.y+1,xyz[2]+position.z)));
        }
    }
    /** 回血 */
    public static void addHealth(Position position){
        position.level.addParticle(new HappyVillagerParticle(new Vector3(position.x,position.y+2,position.z)));
    }

    /** 会心一击*/
    public static void addMaxDamage(Position position){
        Level level = position.level;
        level.addSound(position, Sound.MOB_GHAST_CHARGE);
        double x = position.x;
        double y = position.y;
        double z = position.z;
        double posA,posB,posC;
        posC = y;
        for(int i = 1;i <= 100;i++){
            posA = x + 3 * Math.cos(i * 3.14 / 36);
            posB = z + 3 * Math.sin(i * 3.14 / 36);
            level.addParticle(new DustParticle(new Vector3(posA,posC,posB),155,0,112));
            posC += 0.015;

        }
        posC = y;
        for(int i = 1;i <= 100;i++){
            posA = x + 2 * Math.cos(i * 3.14 / 36);
            posB = z + 2 * Math.sin(i * 3.14 / 36);
            level.addParticle(new DustParticle(new Vector3(posA,posC,posB),200,0,0));
            posC += 0.015;
        }
        posC = y;
        for(int i = 1;i <= 100;i++){
            posA = x + 1 * Math.cos(i * 3.14 / 36);
            posB = x + 1 * Math.sin(i * 3.14 / 36);
            level.addParticle(new DustParticle(new Vector3(posA,posC,posB),255,0,0));
            posC += 0.015;
        }


    }

    /** 雷击 */
    public static void toLight(int damage, Entity entity){
        FullChunk chunk = entity.getLevel().getChunk((int)entity.getX() >> 4, (int)entity.getZ() >> 4);
        CompoundTag nbt = new CompoundTag();
        nbt.putList(new ListTag<DoubleTag>("Pos")
                .add(new DoubleTag("",entity.x))
                .add(new DoubleTag("", entity.y))
                .add(new DoubleTag("", entity.z)));
        nbt.putList(new ListTag<DoubleTag>("Motion")
                .add(new DoubleTag("", 0.0D))
                .add(new DoubleTag("", 0.0D))
                .add(new DoubleTag("", 0.0D))
        );
        nbt.putList(new ListTag<FloatTag>("Rotation")
                .add(new FloatTag("", 0.0F))
                .add(new FloatTag("", 0.0F))
        );
        EntityLightning lightning = new EntityLightning(chunk, nbt);
        lightning.attack((float) damage);
        lightning.setEffect(false);
        lightning.spawnToAll();
    }


}
