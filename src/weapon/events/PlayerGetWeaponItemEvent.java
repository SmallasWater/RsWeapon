package weapon.events;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import cn.nukkit.item.Item;
import weapon.items.BaseItem;

public class PlayerGetWeaponItemEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public static HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    private BaseItem item;
    private boolean isMaster;

    public PlayerGetWeaponItemEvent(Player player, BaseItem item, boolean isMaster){
        this.item = item;
        this.player = player;
        this.isMaster = isMaster;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean master) {
        isMaster = master;
    }

    public BaseItem getItem() {
        return item;
    }
}
