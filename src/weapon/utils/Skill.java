package weapon.utils;

import java.util.Arrays;

public class Skill {

    private String name;
    private String message;
    private String type;
    private String[] canUse;

    public static final String PASSIVE = "被动";

    public static final String ACTIVE = "主动";


    public Skill(String name,String message,String type,String... canUse){
        this.name = name;
        this.message = message;
        this.canUse = canUse;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String[] getCanUse() {
        return canUse;
    }

    public boolean equalsUse(String name){
        return Arrays.asList(canUse).contains(name);
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Skill){
            return (((Skill) obj).getName().equals(name));
        }
        return false;
    }

    @Override
    public String toString() {
        return "name: "+name+"message: "+message;
    }
}
