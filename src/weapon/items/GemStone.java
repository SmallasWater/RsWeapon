package weapon.items;


import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;
import weapon.RsWeapon;
import weapon.players.effects.BaseEffect;
import weapon.players.effects.MineCraftEffect;
import weapon.players.effects.PlayerEffect;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class GemStone extends BaseItem{

    private static final String TAG_NAME = "RsWeapon_Stone";

    private LinkedList<String> weaponEffect = new LinkedList<String>(){
        {
            add(PlayerEffect.ICE);
            add(PlayerEffect.FRAME);
            add(PlayerEffect.FRAME);
            add(PlayerEffect.ADD_HEALTH);
            add(PlayerEffect.LIGHTNING);
        }
    };

    private String name;

    private String message;

    private int xLevel;

    private LinkedList<String> xItem;

    private int min;

    private int max;

    private String type;

    private int armor;

    private double kick;

    private int health;

    private int level;

    private int toDamage;

    private double dKick;

    private LinkedList<BaseEffect> effect;

    private LinkedList<BaseEffect> damages;


    private GemStone(Item item,int level,int xLevel,LinkedList<String> xItem,int min,int max,int toDamage,int armor,double dKick,double kick, int health, LinkedList<BaseEffect> effect,LinkedList<BaseEffect> damages){
        this.item = item;
        this.xItem = xItem;
        this.xLevel = xLevel;
        this.min = min;
        this.max = max;
        this.armor = armor;
        this.kick = kick;
        this.health = health;
        this.effect = effect;
        this.damages = damages;
        this.level = level;
        this.dKick = dKick;
        this.toDamage = toDamage;

    }

    private void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    private GemStone(String name){
        this.name = name;
    }

    private static GemStone toGemStone(String name){
        Config config = new Config(RsWeapon.getInstance().getGemFile()+"/"+name+".yml");
        String id = config.getString("宝石外形");
        String type = config.getString("类型 ");
        Item item = BaseItem.toItemByMap(id);
        Map damages = (Map) config.get("增加攻击力");
        int min = 0,max = 0;
        if(damages.containsKey("min")){
            min = (int) damages.get("min");
        }
        if(damages.containsKey("max")){
            max = (int) damages.get("max");
        }
        int armor = config.getInt("增加护甲值");
        String message = config.getString("介绍");
        double dKick = config.getDouble("增加抗击退");
        int toDamage = config.getInt("反伤");
        Map x = (Map) config.get("镶嵌限制");
        int xLevel = (int) x.get("品阶");
        LinkedList<String> xItem = GemStone.lists((List) x.get("装备"));
        double kick = config.getDouble("增加抗击退");
        int health = config.getInt("增加血量");
        LinkedList<BaseEffect> effects = new LinkedList<>();
        LinkedList<BaseEffect> damageEffect = new LinkedList<>();

        Map f = (Map) config.get(PlayerEffect.SHIELD);
        if(f.containsKey("抵抗伤害(%)") && (int) f.get("抵抗伤害(%)") > 0){
            effects.add(new PlayerEffect(PlayerEffect.SHIELD,(int)f.get("抵抗伤害(%)"),(int)f.get("冷却(s)")));
        }

        Map addHealth = (Map) config.get(PlayerEffect.ADD_HEALTH);
        if(addHealth.containsKey("吸收伤害(%)") &&(int)addHealth.get("吸收伤害(%)") > 0){
            damageEffect.add(new PlayerEffect(PlayerEffect.ADD_HEALTH,(int)addHealth.get("吸收伤害(%)"),(int)addHealth.get("冷却(s)")));
        }

        Map eff = (Map) config.get("药水");
        Map me = (Map) eff.get("己方");
        effects.addAll(GemStone.effects(me));
        Map f1 = (Map) config.get(PlayerEffect.ICE);
        if(f1.containsKey("持续时间(s)") && (int)f1.get("持续时间(s)") > 0){
            damageEffect.add(new PlayerEffect(PlayerEffect.ICE,(int)f1.get("持续时间(s)"),(int)f1.get("冷却(s)")));
        }

        Map f2 = (Map) config.get(PlayerEffect.FRAME);
        if(f2.containsKey("持续时间(s)") &&(int)f2.get("持续时间(s)") > 0){
            damageEffect.add(new PlayerEffect(PlayerEffect.FRAME,(int)f2.get("持续时间(s)"),(int)f2.get("冷却(s)")));
        }
        Map f3 = (Map) config.get(PlayerEffect.LIGHTNING);
        if(f3.containsKey("伤害") && (int)f3.get("伤害") > 0){
            damageEffect.add(new PlayerEffect(PlayerEffect.LIGHTNING,(int)f3.get("伤害"),(int)f3.get("冷却(s)")));
        }
        Map damage = (Map) eff.get("敌方");
        damageEffect.addAll(GemStone.effects(damage));

        int level = config.getInt("宝石品阶");
        Map enchant = (Map) config.get("宝石附魔");
        item.addEnchantment(BaseItem.getEnchant(enchant));

        GemStone stone = new GemStone(item,level,xLevel,xItem,min,max,toDamage,armor,dKick ,kick,health,effects,damageEffect);
        stone.setMessage(message);
        stone.setType(type);
        stone.setName(name);
        return stone;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private static LinkedList<String> lists(List list){
        LinkedList<String> linkedList = new LinkedList<>();
        for(Object o:list){
            if(o instanceof String){
                linkedList.add((String) o);
            }
        }
        return linkedList;
    }

    public double getDKick() {
        return dKick;
    }

    public int getToDamage() {
        return toDamage;
    }

    private static LinkedList<BaseEffect> effects(Map map){
        LinkedList<BaseEffect> effects = new LinkedList<>();
        for(Object s:map.keySet()){
                Map maps = (Map) map.get(s);
                Effect effect = Effect.getEffect((Integer) s);
                effect.setAmplifier((int) maps.get("等级"));
                effect.setDuration((int) maps.get("持续(s)") * 20);
                effects.add(new MineCraftEffect(effect,(int) maps.get("冷却(s)")));

        }
        return effects;
    }


    public static void generateGemStone(String name,String id){
        if(!GemStone.inArray(name)){
            RsWeapon.getInstance().saveResource("stone.yml","/GemStone/"+name+".yml",false);
            Config config = new Config(RsWeapon.getInstance().getGemFile()+"/"+name+".yml",Config.YAML);
            config.set("宝石外形",id);
            config.save();

            RsWeapon.GemStones.put(name,toGemStone(name));
        }
    }




    public static GemStone getInstance(String name){
        if(RsWeapon.GemStones.containsKey(name)){
            return RsWeapon.GemStones.get(name);
        }else{
            if(GemStone.inArray(name)){
                return toGemStone(name);
            }
            return null;
        }

    }

    private static String getItemName(Item item){
        CompoundTag tag = item.getNamedTag();
        return tag.getString(TAG_NAME+"name");
    }

    public static GemStone getInstance(Item item){
        if(GemStone.isGemStone(item)){
            return getInstance(getItemName(item));
        }
        return null;
    }

    public static boolean isGemStone(Item item){
        if(item.hasCompoundTag()){
            CompoundTag tag = item.getNamedTag();
            if(tag.contains(BaseItem.TAG_NAME)){
                return tag.getString(BaseItem.TAG_NAME).equals(TAG_NAME);
            }
        }
        return false;
    }

    @Override
    public boolean canUpData() {
        return false;
    }

    public static String getGemStoneName(Item item){
        if(GemStone.isGemStone(item)){
            if(item.hasCompoundTag()){
                CompoundTag tag = item.getNamedTag();
                return tag.getString(TAG_NAME+"name");
            }
        }
        return null;
    }


    public static boolean inArray(String name) {
        File gem = new File(RsWeapon.getInstance().getGemFile()+"/"+name+".yml");
        return gem.exists();
    }

    @Override
    public CompoundTag getCompoundTag() {
        CompoundTag tag = super.getCompoundTag();
        tag.putString(BaseItem.TAG_NAME,TAG_NAME);
        tag.putString(TAG_NAME+"name",name);
        return tag;
    }

    @Override
    public Item toItem() {
        Item item = this.item;
        item.setCompoundTag(getCompoundTag());
        item.setCustomName(this.name);
        item.setLore(lore());
        item.addEnchantment(this.item.getEnchantments());
        return item;
    }

    public String[] lore(){
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§r§l§f═§7╞════════════╡§f═");
        lore.add("§r§l§f◈品阶:     ◈"+RsWeapon.levels.get(level).getName());
        lore.add("§r§l§f◈可镶嵌:   ◈"+xItem);
        lore.add("§r§l§f◈限制品阶: ◈"+RsWeapon.levels.get(xLevel).getName());
        lore.add("§r§l§f═§7╞════════════╡§f═");
        lore.add("§r§l"+message);
        lore.add("§r§l§f═§7╞════════════╡§f═");
        if(xItem.contains("武器")){
            lore.add("§r§l§f†镶嵌武器†");
            lore.add("   §r§l§f✎攻击力 ✎+§a"+min+"§f - §a+"+max);
            lore.add("   §r§l§f☈击退    ☈+ §6"+kick);
        }
        if(xItem.contains("盔甲")){
            lore.add("§r§l§f†镶嵌盔甲†");
            lore.add("   §r§l§f♥血量    ♥+§d"+health);
            lore.add("   §r§l§f☂护甲    ☂+§e"+armor);
            lore.add("   §r§l§f☽韧性     ☽+§4"+kick);
            lore.add("   §r§l§f☀反伤    ☀+§2"+toDamage);
        }
        lore.add("§r§l§f═§7╞════════════╡§f═");
        if(xItem.contains("武器")){
            lore.add("§r§l§f技能: §e(武器)");
            if(getWeaponEffect().size() > 0){
                lore.add(this.skillGetString(getWeaponEffect(),"§2[被动]").toString());
            }
            if(getWeaponDamages().size() > 0){
                lore.add(this.skillGetString(getWeaponDamages(),"§6[主动]").toString());
            }
            if(getWeaponEffect().size() == 0 && getWeaponDamages().size() == 0){
                lore.add("§c无");
            }
        }
        if(xItem.contains("盔甲")){
            lore.add("§r§f技能: §b(盔甲)");
            if(getArmorEffect().size() > 0){
                lore.add(this.skillGetString(getArmorEffect(),"§2[被动]").toString());
            }
            if(getArmorDamages().size() > 0){
                lore.add(this.skillGetString(getArmorDamages(),"§c[主动]").toString());
            }
            if(getArmorDamages().size() == 0 && getArmorEffect().size() == 0){
                lore.add("§c无");
            }
        }
        return lore.toArray(new String[0]);
    }

    public int getMin() {
        return min;
    }

    public String getName() {
        return name;
    }

    public int getArmor() {
        return armor;
    }

    public int getLevel() {
        return level;
    }

    public int getMax() {
        return max;
    }

    public double getKick() {
        return kick;
    }

    public int getxLevel() {
        return xLevel;
    }

    public LinkedList<String> getxItem() {
        return xItem;
    }

    public int getHealth() {
        return health;
    }

    private LinkedList<BaseEffect> getDamages() {
        return damages;
    }

    private LinkedList<BaseEffect> getEffect() {
        return effect;
    }


    public LinkedList<BaseEffect> getWeaponDamages(){
        return baseEffects(damages,true,true);
    }

    public LinkedList<BaseEffect> getWeaponEffect(){
        return baseEffects(effect,true,false);
    }

    public LinkedList<BaseEffect> getArmorDamages(){
        return baseEffects(damages,false,true);
    }

    public LinkedList<BaseEffect> getArmorEffect(){
        return baseEffects(effect,false,false);
    }


    private LinkedList<BaseEffect> baseEffects(LinkedList<BaseEffect> baseEffects, boolean isWeapon, boolean isDamage){
        LinkedList<BaseEffect> effects = new LinkedList<>();
        if(baseEffects.size() > 0){
            for (BaseEffect effect:baseEffects){
                if(isWeapon){
                    if(isDamage){
                        if(effect instanceof PlayerEffect){
                            if(!weaponEffect.contains(((PlayerEffect) effect).getBufferName())){
                                continue;
                            }
                        }
                        effects.add(effect);
                    }else{
                        if(effect instanceof PlayerEffect){
                            continue;
                        }
                        effects.add(effect);
                    }
                }else{
                    if(!isDamage){
                        if(effect instanceof PlayerEffect){
                            if(weaponEffect.contains(((PlayerEffect) effect).getBufferName())){
                                continue;
                            }
                        }
                        effects.add(effect);
                    }
                }
            }
        }
        return effects;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof GemStone){
            return ((GemStone) obj).getName().equals(name);
        }
        return false;
    }
}
