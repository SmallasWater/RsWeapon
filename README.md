# RsWeapon

![logo](https://github.com/SmallasWater/RsWeapon/blob/master/resources/logo.png)
------
## 功能
- [x] 高伤害的物品

- [x] 自定义物品附魔

- [x] 盔甲系统

- [x] 宝石镶嵌系统

- [x] 强化系统

- [x] 对接 RPG等级系统

- [x] 技能系统

## 用法
>  /we help 主命令(OP) 
>  /click help 镶嵌宝石主命令 (Player) 
>  /强化 强化手中的武器或者盔甲 
>  /up 更新手中的 武器 盔甲 宝石 
  
## 开发者须知
自定义增加技能
```java
/** 
*注册技能
*/
RsWeaponSkill.addSkill(new Skill("狂暴","增加 %i% %伤害 冷却 %cold%",Skill.ACTIVE,"武器"));

/**
* 给宝石增加技能
* @parm item 宝石物品
*/
 GemStone gem = GemStone.getInstance(item);
 gem.addSkill(RsWeaponSkill.getSkill("狂暴"),10,20);
```

