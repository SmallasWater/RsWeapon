package weapon.items;


import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;
import weapon.RsWeapon;
import weapon.players.effects.BaseEffect;
import weapon.players.effects.MineCraftEffect;
import weapon.players.effects.PlayerEffect;
import weapon.utils.RsWeaponSkill;
import weapon.utils.Skill;

import java.io.File;
import java.util.*;


public class GemStone extends BaseItem{

    private static final String TAG_NAME = "RsWeapon_Stone";

    private static final String EFFECT = "效果";

    private static final String COLD = "冷却(s)";

    public static final  String WEAPON = "武器";

    public static final  String ARMOR = "盔甲";

    private boolean canShow = false;

    private String nameTag;



    private int xLevel;

    private String xAttribute;

    private int xpf;

    private LinkedList<String> xItem;

    private int min;

    private int max;

    private int armor;

    private double kick;

    private int health;

    private int cg;

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

    @Override
    public String getType() {
        return type;
    }

    public void setCg(int cg) {
        this.cg = cg;
    }

    public int getCg() {
        return cg;
    }

    private void setType(String type) {
        this.type = type;
    }

    public String getNameTag() {
        return nameTag;
    }

    public void setNameTag(String nameTag) {
        this.nameTag = nameTag;
    }

    public void setCanShow(boolean canShow) {
        this.canShow = canShow;
    }

    public boolean isCanShow() {
        return canShow;
    }

    private GemStone(String name){
        this.name = name;
    }

    public static GemStone toGemStone(String name){
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
        String xAttribute = "";
        int xpf = 0;
        if(x.containsKey("限制镶嵌属性")){
            xAttribute =  (String) x.get("限制镶嵌属性");
        }
        if(x.containsKey("限制镶嵌评级")){
            xpf =  Integer.parseInt(x.get("限制镶嵌评级").toString()) ;
        }

        LinkedList<String> xItem = GemStone.lists((List) x.get("装备"));
        double kick = config.getDouble("增加抗击退");
        int health = config.getInt("增加血量");
        LinkedList<BaseEffect> effects = new LinkedList<>();
        LinkedList<BaseEffect> damageEffect = new LinkedList<>();
        Object skillObject = config.get("技能");
        if(skillObject instanceof Map){
            Map skill = (Map) skillObject;
            for(Object skillName:skill.keySet()){
                if(skillName instanceof String){
                    Skill skills = RsWeaponSkill.getSkill((String) skillName);
                    if(skills != null){
                        Map f = (Map) skill.get(skills.getName());
                        if(skills.getType().equals(Skill.PASSIVE)){
                            if(f.containsKey(EFFECT) && (int) f.get(EFFECT) > 0){
                                effects.add(new PlayerEffect(skills.getName(),(int)f.get(EFFECT),(int)f.get(COLD)));
                            }
                        }else if(skills.getType().equals(Skill.ACTIVE)){
                            if(f.containsKey(EFFECT) &&(int)f.get(EFFECT) > 0){
                                damageEffect.add(new PlayerEffect(skills.getName(),(int)f.get(EFFECT),(int)f.get(COLD)));
                            }
                        }
                    }
                }
            }
        }else{
            /* 保留旧版....*/
            for(Skill skillName:RsWeaponSkill.getSkillList()){
                Map f = (Map) config.get(skillName.getName());
                if(f != null){
                    if(skillName.getType().equals(Skill.PASSIVE)){
                        if(f.containsKey(EFFECT) && (int) f.get(EFFECT) > 0){
                            effects.add(new PlayerEffect(skillName.getName(),(int)f.get(EFFECT),(int)f.get(COLD)));
                        }
                    }else if(skillName.getType().equals(Skill.ACTIVE)){
                        if(f.containsKey(EFFECT) && (int)f.get(EFFECT) > 0){
                            damageEffect.add(new PlayerEffect(skillName.getName(),(int)f.get(EFFECT),(int)f.get(COLD)));
                        }
                    }
                }
            }
        }
        Map eff = (Map) config.get("药水");
        Object obj = eff.get("己方");
        if(obj instanceof Map){
            Map me = (Map) obj;
            if(me.size() > 0){
                effects.addAll(GemStone.effects(me));
            }
        }
        obj = eff.get("敌方");
        if(obj instanceof Map){
            Map damage = (Map) obj;
            if(damage.size() > 0){
                damageEffect.addAll(GemStone.effects(damage));
            }
        }
        int level = config.getInt("宝石品阶");
        Object enchantObject = config.get("宝石附魔");
        if(enchantObject instanceof Map){
            int enchantId = (int) ((Map)enchantObject).get("id");
            int enchantLevel = (int) ((Map)enchantObject).get("level");
            if(enchantLevel > 0){
                Enchantment aura = Enchantment.getEnchantment(enchantId).setLevel(enchantLevel);
                item.addEnchantment(aura);
            }
        }else if(enchantObject instanceof List){
            List<Map> enchant = config.getMapList("宝石附魔");
            ArrayList<Enchantment> enchants = BaseItem.getEnchant(enchant);
            for (Enchantment aura : enchants){
                item.addEnchantment(aura);
            }
        }

        GemStone stone = new GemStone(item,level,xLevel,xItem,min,max,toDamage,armor,dKick ,kick,health,effects,damageEffect);
        stone.setMessage(message);
        Object up = config.get("稀有度");
        int levelUp = 0;
        if(up != null){
            if(up instanceof String){
                if("x".equals(up.toString().toLowerCase())){
                    levelUp = new Random().nextInt(RsWeapon.rarity.size());
                    stone.setCanUp(true);
                }
            }else if(up instanceof Integer){
                levelUp = (int) up;
            }
        }
        stone.setLevelUp(levelUp);
        stone.setCg(config.getInt("镶嵌成功率",100));
        stone.setType(type);
        stone.setName(name);
        stone.setXpf(xpf);
        stone.setxAttribute(xAttribute);
        stone.setNameTag(config.getString("名称",name));
        stone.setLevelUp(config.getInt("稀有度",0));
        stone.setCanShow(config.getBoolean("是否在创造背包显示",false));
        return stone;
    }

    private void setXpf(int xpf) {
        this.xpf = xpf;
    }

    public int getXpf() {
        return xpf;
    }

    private void setxAttribute(String xAttribute) {
        this.xAttribute = xAttribute;
    }

    public String getxAttribute() {
        return xAttribute;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setMessage(String message) {
        this.message = message;
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
                Effect effect = Effect.getEffect(Integer.parseInt(s.toString()));
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

    static String getItemName(Item item){
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
        item.setCustomName(this.nameTag);
        item.setLore(lore());
        item.addEnchantment(this.item.getEnchantments());
        return item;
    }

    @Override
    public boolean isGemStone() {
        return true;
    }

    @Override
    public String[] lore(){
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§r§2稀有度:  §r"+RsWeapon.getInstance().getLevelUpByString(levelUp).getName());
        lore.add("§r§f═§7╞════════════╡§f═");
        lore.add("§r§6◈§f宝石品阶     §6◈"+RsWeapon.levels.get(level).getName());
        lore.add("§r§6◈§f可镶嵌       §6◈"+(xItem.size() > 0?xItem:"§c不可镶嵌"));
        lore.add("§r§6◈§f限制品阶     §6◈"+RsWeapon.levels.get(xLevel).getName());
        lore.add("§r§6◈§d成功率       §6◈"+cg+"％");
        lore.add("§r§f═§7╞════════════╡§f═");
        lore.add("§r"+message.trim());
        lore.add("§r§f═§7╞════════════╡§f═");
        if(xItem.contains("武器")){
            lore.add("§r§f†镶嵌武器†");
            lore.add("   §r§6◈§f攻击§6◈   +§a"+min+"§f - §a+"+max);
            lore.add("   §r§6◈§f击退§6◈   +§a"+kick);
        }
        if(xItem.contains("盔甲")){
            lore.add("§r§f†镶嵌盔甲†");
            lore.add("   §r§6◈§f血量§6◈    +§d"+health);
            lore.add("   §r§6◈§f护甲§6◈    +§e"+armor);
            lore.add("   §r§6◈§f韧性§6◈    +§4"+kick);
            lore.add("   §r§6◈§f反伤§6◈    +§2"+toDamage);
        }
        lore.add("§r§f═§7╞════════════╡§f═");
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

    @Override
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

    public void addSkill(Skill skill,int time,int cold){
        if(skill.equalsUse(WEAPON)){
            if(skill.getType().equals(Skill.ACTIVE)){
                damages.add(new PlayerEffect(skill.getName(),time,cold));
            }else{
                effect.add(new PlayerEffect(skill.getName(),time,cold));
            }
        }else if(skill.equalsUse(ARMOR)){
            if(!skill.getType().equals(Skill.ACTIVE)){
                effect.add(new PlayerEffect(skill.getName(),time,cold));
            }
        }
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
                        canAdd(effects, effect, WEAPON);
                    }else{
                        if(effect instanceof PlayerEffect){
                            continue;
                        }
                        effects.add(effect);
                    }
                }else{
                    if(!isDamage){
                        canAdd(effects, effect, ARMOR);
                    }
                }
            }
        }
        return effects;
    }

    private void canAdd(LinkedList<BaseEffect> effects, BaseEffect effect, String weapon) {
        if(effect instanceof PlayerEffect){
            Skill skill = RsWeaponSkill.getSkill(effect.getBufferName());
            if(skill != null){
                if(!skill.equalsUse(weapon)){
                    return;
                }
            }else{
                return;
            }
        }
        effects.add(effect);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof GemStone){
            return ((GemStone) obj).getName().equals(name);
        }
        return false;
    }
}
