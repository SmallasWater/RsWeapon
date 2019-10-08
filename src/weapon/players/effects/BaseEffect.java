package weapon.players.effects;


/**
 * @author 若水
 * 本类为玩家受到的状态*/
public abstract class BaseEffect implements Cloneable{

    int cold;

    int time;

    /**
     * 获取冷却时间
     * @return  时间(s)
     * */
    abstract public int getCold();


    /**
     * 获取持续时间
     * @return  时间(s)
     * */
    abstract public int getTime();


    /**
     * 设置持续时间
     * @param   time 时间(s)
     * */
    abstract public void setTime(int time);

    /**
     * 设置冷却时间
     * @param  cold  时间(s)
     * */
    abstract public void setCold(int cold);



    @Override
    public boolean equals(Object obj) {return false;}
}
