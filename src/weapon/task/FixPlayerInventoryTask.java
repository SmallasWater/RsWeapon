package weapon.task;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.Task;
import weapon.RsWeapon;
import weapon.floatingtext.TextEntity;
import weapon.items.Armor;
import weapon.items.BaseItem;
import weapon.items.GemStone;
import weapon.items.Weapon;

import java.util.Arrays;
import java.util.LinkedList;

public class FixPlayerInventoryTask extends Task {

    @Override
    public void onRun(int i) {
        Server.getInstance().getScheduler().scheduleAsyncTask(RsWeapon.getInstance(), new AsyncTask() {
            @Override
            public void onRun() {
                for (Player player: Server.getInstance().getOnlinePlayers().values()) {
                    LinkedList<Item> allItems = new LinkedList<>(player.getInventory().getContents().values());
                    allItems.addAll(Arrays.asList(player.getInventory().getArmorContents()));
                    for (Item item:allItems){
                        if(Weapon.isWeapon(item)){
                            if(!Weapon.inArray(Weapon.getWeaponName(item))){
                                player.sendMessage("§c检测到不存在武器--"+Weapon.getWeaponName(item)+"§c已移除");
                                Weapon weapon = Weapon.getInstance(item);
                                toAddStone(weapon,player);
                                player.getInventory().remove(item);

                            }
                        }
                        if(Armor.isArmor(item)){
                            if(!Armor.inArray(Armor.getArmorName(item))){
                                player.sendMessage("§c检测到不存在盔甲--"+Armor.getArmorName(item)+"§c已移除");
                                Armor armor = Armor.getInstance(item);
                                toAddStone(armor,player);
                                player.getInventory().remove(item);
                            }
                        }
                        if(GemStone.isGemStone(item)){
                            if(!GemStone.inArray(GemStone.getGemStoneName(item))){
                                player.sendMessage("§c检测到不存在宝石--"+GemStone.getGemStoneName(item)+"§c已移除");
                                player.getInventory().remove(item);
                            }
                        }
                    }
                }
            }
        });

        for (Level level : Server.getInstance().getLevels().values()) {
            for(Entity entity:level.getEntities()){
                if(entity instanceof TextEntity){
                    if(((TextEntity) entity).close){
                        entity.kill();
                    }
                }
            }

        }
    }
    private void toAddStone(BaseItem baseItem,Player player){
        try{
            if(baseItem != null){
                LinkedList<GemStone> stones = new LinkedList<>();
                if(baseItem instanceof Weapon){
                    stones = ((Weapon) baseItem).getGemStones();
                }else if(baseItem instanceof Armor){
                    stones = ((Armor) baseItem).getGemStones();
                }
                if(stones.size() > 0){
                    for (GemStone stone: stones) {
                        if(GemStone.inArray(stone.getName())){
                            Item stoneItem = stone.toItem();
                            if(player.getInventory().canAddItem(stoneItem)){
                                player.getInventory().addItem(stoneItem);
                                player.sendMessage("§a返还宝石: "+stone.getName());
                            }
                        }else{
                            player.sendMessage("§c永久损失宝石: "+stone.getName());
                        }
                    }
                }
            }
        }catch(Exception ignored){
        }

    }
}
