package weapon.items;

import cn.nukkit.item.Item;
import weapon.items.suits.Suit;

import java.util.Arrays;
import java.util.LinkedList;

public class UpDataStone {

    private String name;

    private Item item;

    private String math;

    private LinkedList<Suit> suits;

    public UpDataStone(String name,Item item,String math,LinkedList<Suit>  suits){
        this.name = name;
        this.item = item;
        this.math = math;
        this.suits = suits;
    }

//    public Item getItem() {
//
//
//    }

    public String getName() {
        return name;
    }

    public String getMath() {
        return math;
    }

    public LinkedList<Suit> getSuits() {
        return suits;
    }
}
