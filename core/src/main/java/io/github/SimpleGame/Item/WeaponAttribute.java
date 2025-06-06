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
    private static float playerDamage;
    private static float playerRange;
    private static float playerAttackSpeed;
    private static WeaponEffects effect;
    public WeaponAttribute(){}

    public WeaponAttribute(float damage, float range, float attackSpeed) {
        this.damage = damage;
        this.range = range;
        this.attackSpeed = attackSpeed;
    }

    public WeaponAttribute readData(int ID, int WeaponID, Player player){
        WeaponAttribute Attribute = new WeaponAttribute();
        if (ID == 0) {
            if (WeaponID == 0) Attribute= new WeaponAttribute(3, 1, 0f);
            if (WeaponID == 1) Attribute= new WeaponAttribute(3, 1, 0f);
            if (WeaponID == 2) Attribute= new WeaponAttribute(3, 1, 0f);
            if (WeaponID == 23) Attribute= new WeaponAttribute(5, 2, 0f);
        }
        this.damage = Attribute.getDamage();
        this.range = Attribute.getRange();
        this.attackSpeed = Attribute.getAttackSpeed();
        return Attribute;
    }
    public void setData(Player player){
        playerDamage = player.getAttributeHandler().getDamage();
        playerRange = player.getAttributeHandler().getAttackrange();
        playerAttackSpeed = player.attackCooldown;
        player.getAttributeHandler().setDamage(playerDamage+getDamage());
        player.getAttributeHandler().setAttackrange(playerRange+getRange());
        player.attackCooldown = playerAttackSpeed+getAttackSpeed();
    }
    public void resetData(Player player){
        final float damage = playerDamage;
        final float range = playerRange;
        final float attackSpeed = playerAttackSpeed;
        player.getAttributeHandler().setDamage(damage);
        player.getAttributeHandler().setAttackrange(range);
        player.attackCooldown = attackSpeed;
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
