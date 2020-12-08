package weapon.items.suits;

import java.util.LinkedHashMap;

public class PlayerSuit {

    private String suitName;

    private int count;

    private LinkedHashMap<Suit.Adds,String> suits;

    public PlayerSuit(String suitName,int count,LinkedHashMap<Suit.Adds,String> suits){
        this.count = count;
        this.suitName = suitName;
        this.suits = suits;
    }

    public int getCount() {
        return count;
    }

    public LinkedHashMap<Suit.Adds, String> getSuits() {
        return suits;
    }

    public String getMath(Suit.Adds adds){
        if(suits.containsKey(adds)) {
            return suits.get(adds);
        }
        return "0";
    }

    public String getSuitName() {
        return suitName;
    }

    @Override
    public String toString() {
        return suitName+" ("+getCount()+")";
    }
}
