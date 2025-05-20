package io.github.SimpleGame.Character.Player;

import com.badlogic.gdx.graphics.Texture;

public class PlayerAttribute extends Player implements PlayerAttributeHandler{
    public float HP;
    public float MP;
    public float DEF;
    private float Damage;
    public final float MP_RECOVERY_RATE = 5f; //每秒恢复量
    public static final float RECOVERY_DELAY = 1.0f;//恢复延迟
    public long recoveryStartTime = 0;//恢复开始时间
    public PlayerAttribute(float HP, float MP, float DEF,float Damage) {
        this.HP = HP;
        this.MP = MP;
        this.DEF = DEF;
        this.Damage = Damage;
        setAttribute(HP, MP, DEF,Damage);
    }
    @Override
    public void setAttribute(float HP, float MP, float DEF,float Damage){

    }
}
