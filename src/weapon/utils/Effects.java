package weapon.utils;



import cn.nukkit.level.Position;
import cn.nukkit.level.particle.FlameParticle;
import cn.nukkit.level.particle.HappyVillagerParticle;
import cn.nukkit.math.Vector3;

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


}
