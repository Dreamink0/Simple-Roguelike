package io.github.SimpleGame.Item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Resource.Game;

public class WeaponAttribute {
    private String name;
    private float damage;
    private float attackSpeed;
    private float range;
    private float speed;
    private float hp;
    private float mp;
    private float def;
    private static WeaponEffects effect;
    public WeaponAttribute(){}

    public WeaponAttribute(float damage, float range, float attackSpeed) {
        this.damage = damage;
        this.range = range;
        this.attackSpeed = attackSpeed;
    }

    public static WeaponAttribute readData(int ID, int WeaponID, Player player){
        WeaponAttribute Attribute = new WeaponAttribute();
        if (ID == 0) {
            if (WeaponID == 0) Attribute= new WeaponAttribute(3, 1, 0);
            if (WeaponID == 1) Attribute= new WeaponAttribute(3, 1, 0);
        }
        return Attribute;
    }

    public float getDamage() {
        return damage;
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }

    public float getRange() {
        return range;
    }

    public WeaponEffects getEffect() {
        return effect;
    }
}
