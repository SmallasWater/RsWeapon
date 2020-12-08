package weapon.players.effects;


/**
 * @author 若水
 * 本类为玩家受到的状态*/
public abstract class BaseEffect implements Cloneable{

    int cold;

    int time;

    String bufferName;


    /**
     * 获取效果名称
     * @return  名称
     * */
    public String getBufferName() {
        return bufferName;
    }

    /**
     * 设置效果名称
     * @param  bufferName 名称
     * */
    public void setBufferName(String bufferName) {
        this.bufferName = bufferName;
    }



    /**
     * 获取冷却时间
     * @return  时间(s)
     * */
    public int getCold(){
        return cold;
    }


    /**
     * 获取持续时间
     * @return  时间(s)
     * */
    public int getTime(){
        return time;
    }


    /**
     * 设置持续时间
     * @param   time 时间(s)
     * */
    public void setTime(int time){
        this.time = time;
    }

    /**
     * 设置冷却时间
     * @param  cold  时间(s)
     * */
    public void setCold(int cold){
        this.cold = cold;
    }



    @Override
    public boolean equals(Object obj) {return false;}

    @Override
    public String toString() {
        return "Buff"+getClass().getName()+" load: "+getTime()+" time: "+getCold();
    }
}
