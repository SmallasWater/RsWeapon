package weapon.items.suits;


import java.util.LinkedHashMap;


public class Suit {

    private int count;

    private LinkedHashMap<Adds,String> adds;

    public enum Adds{
        /**
         * 属性
         * */
        DAMAGE, HEALTH, ARMOR, TO_DAMAGE, KICK, DEL_KICK, EFFECT
    }



    public Suit(int count,LinkedHashMap<Adds,String> adds){
        this.count = count;
        this.adds = adds;
    }

    public int getCount() {
        return count;
    }





    public LinkedHashMap<Adds,String> getAll(){
        LinkedHashMap<Adds,String> addsIntegerLinkedHashMap = new LinkedHashMap<>();
        for(Adds adds:adds.keySet()){
            addsIntegerLinkedHashMap.put(adds,this.adds.get(adds));
        }
        return addsIntegerLinkedHashMap;
    }

    @Override
    public String toString() {
        return "Suit(count:"+count+",adds:"+adds+")";
    }
}
