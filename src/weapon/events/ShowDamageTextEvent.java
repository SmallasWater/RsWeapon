package weapon.events;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Position;

public class ShowDamageTextEvent extends Event implements Cancellable {

    private Position position;

    private boolean canShow;

    private String text;
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public static HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public ShowDamageTextEvent(String text,boolean canShow,Position position){
        this.position = position;
        this.canShow = canShow;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Position getPosition() {
        return position;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCanShow(boolean canShow) {
        this.canShow = canShow;
    }

    public boolean isCanShow() {
        return canShow;
    }
}
