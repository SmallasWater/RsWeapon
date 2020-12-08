package weapon.utils;

import java.util.LinkedList;

public class RsWeaponSkill {

    private static LinkedList<Skill> skillList = new LinkedList<>();

    public static void addSkill(Skill skill){
        if (!skillList.contains(skill)) {
            skillList.add(skill);
        }else{
           skillList.remove(skill);
           skillList.add(skill);
        }
    }

    public static LinkedList<Skill> getSkillList() {
        return skillList;
    }

    public static Skill getSkill(String name){
        for (Skill skill:skillList){
            if(skill.getName().equals(name)){
                return skill;
            }
        }
        return null;
    }

    public static void removeSkill(String name){
        for (Skill skill:skillList){
            if(skill.getName().equals(name)){
                skillList.remove(skill);
            }
        }
    }






}
