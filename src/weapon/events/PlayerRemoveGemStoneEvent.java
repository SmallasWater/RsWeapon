package weapon.events;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import weapon.items.BaseItem;
import weapon.items.GemStone;

public class PlayerRemoveGemStoneEvent extends PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public static HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    private GemStone gem;

    private BaseItem item;

    public PlayerRemoveGemStoneEvent(Player player, GemStone stone, BaseItem item){
        this.gem = stone;
        this.item = item;
        this.player = player;
    }

    public BaseItem getItem() {
        return item;
    }

    public GemStone getGem() {
        return gem;
    }
}
