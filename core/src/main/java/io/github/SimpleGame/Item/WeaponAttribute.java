package io.github.SimpleGame.Item;

import io.github.SimpleGame.Character.Player.Player;

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
            if (WeaponID == 0) Attribute= new WeaponAttribute(3, 1, 0f);
            if (WeaponID == 1) Attribute= new WeaponAttribute(3, 1, 0f);
            if (WeaponID == 2) Attribute= new WeaponAttribute(3, 1, 0f);
            if (WeaponID == 23) Attribute= new WeaponAttribute(5, 2, 0f);
        }
        return Attribute;
    }
    public void setData(Player player){
        float Damage = player.getAttributeHandler().getDamage();
        float Range = player.getAttributeHandler().getAttackrange();
        float Speed =  player.attackCooldown;
        player.getAttributeHandler().setDamage(Damage+getDamage());
        player.getAttributeHandler().setAttackrange(Range+getRange());
        player.attackCooldown = Speed+getAttackSpeed();
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
