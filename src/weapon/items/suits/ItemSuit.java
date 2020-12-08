package weapon.items.suits;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ItemSuit {

    /**套装名称*/
    public String name;

    /**激活效果*/
    private LinkedList<Suit> suits;

    private LinkedList<String> itemNames;

    public ItemSuit(String name, List<String> items,LinkedList<Suit> suits){
        this.name = name;
        this.itemNames = new LinkedList<>(items);
        this.suits = suits;
    }

    public String getName() {
        return name;
    }

    public LinkedList<String> getItemNames() {
        return itemNames;
    }

    public LinkedList<Suit> getSuits() {
        return suits;
    }

    @Override
    public String toString() {
        return "ItemSuit(name:"+name+",itemNames:"+itemNames+",suits:"+suits+")";
    }
}
