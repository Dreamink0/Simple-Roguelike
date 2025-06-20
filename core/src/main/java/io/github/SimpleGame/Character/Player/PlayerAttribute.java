package io.github.SimpleGame.Character.Player;

import com.badlogic.gdx.Gdx;

public class PlayerAttribute implements PlayerAttributeHandler{
    private float HP;
    private float maxHP;
    private float MP;
    private float maxMP;
    private float Damage;
    private PlayerTextureHandler textureHandler;
    private float Attackrange=4.5f;
    public PlayerAttribute(float HP, float MP,float Damage)
    {
        this.HP = HP;
        this.maxHP = HP;
        this.MP = MP;
        this.maxMP = MP;
        this.Damage = Damage;
    }
    @Override
    public void setHP(float HP) {
        this.HP = HP;
        if(textureHandler != null){
            textureHandler.get();
        }
    }
    @Override
    public float getHP() {return HP/maxHP;}
    @Override
    public void setMP(float MP) {
        if(MP<=0)MP=0;
        this.MP = MP;
        if(textureHandler != null){
            textureHandler.get();
        }
    }
    @Override
    public float getMP() {return MP/maxMP;}
    @Override
    public void setDamage(float Damage) {this.Damage = Damage;}
    @Override
    public float getDamage() {return Damage;}
    @Override
    public float getAttackrange() {
        return Attackrange;
    }
    @Override
    public void setAttackrange(float Aattackrange) {
        this.Attackrange = Aattackrange;
    }
    @Override
    public void setMaxHP(float maxHP) {}
    //带max都是玩家当前最大生命值;
    @Override
    public float getMaxHP() {
        return HP;
    }
    @Override
    public void setMaxMP(float maxMP) {}
    @Override
    public float getMaxMP() {
        return MP;
    }
    public void setTextureHandler(PlayerTextureHandler textureHandler) {this.textureHandler = textureHandler;}
    @Override
    public void update(PlayerTextureHandler textureHandler) {
        boolean wasTextureRequested = false;
        if (getHP() < 1.0f && HP > 0) {
            HP += 0.01f * Gdx.graphics.getDeltaTime()*100; // 按一定速率恢复HP
            if (HP > maxHP) HP = maxHP;
            setHP(HP);
            wasTextureRequested = true;
        }
        if (getMP() < 1.0f) {
            MP += 0.01f * Gdx.graphics.getDeltaTime()*100; // 按一定速率恢复MP
            if (MP > maxMP) MP = maxMP;
            setMP(MP);
            wasTextureRequested = true;
        }
        if (wasTextureRequested && textureHandler != null) {
            textureHandler.get();
        }
    }
}
