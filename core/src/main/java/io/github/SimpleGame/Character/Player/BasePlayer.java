package io.github.SimpleGame.Character.Player;

public abstract class BasePlayer {
   private float HP;
   private  float MP;
   private  float Shield;

    public BasePlayer(float HP, float shield, float MP) {
        this.HP = HP;
        Shield = shield;
        this.MP = MP;
    }

    public abstract void CalculateValues();//计算三个条的使用情况
    public abstract void updateValues();//更新
    public abstract void render();//显示三个条
    public abstract void dispose();
}
