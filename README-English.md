<a href="https://github.com/SmallasWater/RsWeapon/releases/latest" alt="Latest release">
    <img src="https://img.shields.io/github/v/release/SmallasWater/RsWeapon?include_prereleases" alt="Latest release">
</a>
<a href="https://github.com/SmallasWater/RsWeapon/blob/master/README.md" alt="chinese">
    <img src="https://img.shields.io/badge/language-chinese-1" alt="chinese">
</a>

# RsWeapon

![logo](https://github.com/SmallasWater/RsWeapon/blob/master/resources/logo.png)
------
##Function
-[x] high damage items
-[x] custom item enchant
-[x] armor system
-[x] gemstone mosaic system
-[x] reinforcement system
-[x] docking RPG rating system
-[x] skill system
##Usage
>/ we help main command (OP)
>/ click Help gem inlay master command (player)
>Strengthen your weapon or armor
>/ up update weapon armor gem
##Notice to developers
Custom skill increase
```java
/** 
*注册技能
*/
RsWeaponSkill.addSkill(new Skill("狂暴","增加 %i% %伤害 冷却 %cold%",Skill.ACTIVE,"武器"));

/**
*Add skill to gem
*@ ParM item gem item
*/

 GemStone gem = GemStone.getInstance(item);
 gem.addSkill(RsWeaponSkill.getSkill("狂暴"),10,20);
```

