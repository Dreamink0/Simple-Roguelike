package io.github.SimpleGame.Character.Enemy;

public class EnemyAttribute implements EnemyAttributeHandler{
    private float HP,Damage,attackRange,chaseRange,Speed;
    public EnemyAttribute(float HP,float Damage,float attackRange,float chaseRange,float Speed)
    {
        this.HP=HP;
        this.Damage=Damage;
        this.attackRange=attackRange;
        this.chaseRange=chaseRange;
        this.Speed=Speed;
    }

    @Override
    public void setHP(float HP) {
        this.HP=HP;
    }

    @Override
    public float getHP() {
        return HP;
    }

    @Override
    public void setDamage(float Damage) {
        this.Damage=Damage;
    }

    @Override
    public float getDamage() {
        return Damage;
    }

    public float getAttackRange() {
        return attackRange;
    }

    public float getChaseRange() {
        return chaseRange;
    }

    public float getSpeed() {
        return Speed;
    }
}
