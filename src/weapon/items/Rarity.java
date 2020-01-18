package weapon.items;

import java.util.*;

public class Rarity {

    public static List<Map> ras = new LinkedList<Map>() {
        {
            add(new LinkedHashMap<String, Object>(){
                {
                    put("名称","§o§7D");
                    put("随机范围","1 - 2");
                }
            });
            add(new LinkedHashMap<String, Object>(){
                {
                    put("名称","§o§aC");
                    put("随机范围","4 - 8");
                }
            });
            add(new LinkedHashMap<String, Object>(){
                {
                    put("名称","§o§bB");
                    put("随机范围","20% - 50%");
                }
            });
            add(new LinkedHashMap<String, Object>(){
                {
                    put("名称","§o§dA");
                    put("随机范围","100% - 200%");
                }
            });
            add(new LinkedHashMap<String, Object>(){
                {
                    put("名称","§o§eS");
                    put("随机范围","150% - 300%");
                }
            });
        }
    };

    private String name;

    private String round;

    public Rarity(String name,String round){
        this.name = name;
        this.round = round;
    }

    public String getName() {
        return name;
    }

    public int getRound(int r) {
        String[] s = round.split("-");
        String r1 = s[0].trim();
        String r2 = s[1].trim();
        int i1 = 0,i2 = 0;
        if(r1.matches("^([0-9.]+)[ ]*%$")){
            int a1 = Integer.parseInt(r1.split("%")[0]);
            a1 /= 100;
            if(a1 > 0){
                i1 = r * a1;
            }
        }else{
            i1 = Integer.parseInt(r1);
        }
        if(r2.matches("^([0-9.]+)[ ]*%$")){
            int a2 = Integer.parseInt(r2.split("%")[0]);
            a2 /= 100;
            if(a2 > 0){
                i2 = r * a2;
            }
        }else{
            i2 = Integer.parseInt(r2);
        }
        return new Random().nextInt(i2) + i1;
    }
}
