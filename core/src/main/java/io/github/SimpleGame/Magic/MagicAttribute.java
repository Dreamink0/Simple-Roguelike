package io.github.SimpleGame.Magic;

public class MagicAttribute {
    private float Damage;
    private float Speed;
    private float Duration;
    private float Cooldown;
    private float MPcost;
    public MagicAttribute(float Damage,float Speed,float Cooldown,float Duration,float MPcost){
        this.Damage = Damage;
        this.Speed = Speed;
        this.Cooldown = Cooldown;
        this.Duration = Duration;
        this.MPcost = MPcost;
    }

    public float getMPcost() {return MPcost;}
    public float getDamage(){return Damage;}
    public float getSpeed(){return Speed;}
     public float getDuration(){return Duration;}
    public float getCooldown() {return Cooldown;}
}
