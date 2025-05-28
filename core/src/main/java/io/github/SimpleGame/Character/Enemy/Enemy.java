package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Config;

public abstract class Enemy {
    //在这个类里面处理的是所有敌人的材质，状态，属性，攻击，移动，
    //攻击范围，检测范围，坐标，接口，状态机，动画，
    protected AssetManager assetManager=new AssetManager();;
    protected Enemy() {}
    //状态
    public enum State {
        PATROL, CHASE,
        ATTACK, DIE
    }
    protected State currentState;
    //目标
    protected Player player;
    protected Body enemybody;
    //敌人检测范围
    protected float HP;
    protected float Damage;
    protected float Distance;
    public Enemy(World world,Player player,float x,float y){
        this.player = player;
        this.currentState = State.PATROL; //初始状态：巡逻
        //其他逻辑实现自己写
    }

    public Player getPlayer() {
        return player;
    }

    public float getDamage() {
        return Damage;
    }

    public float getHP() {
        return HP;
    }

    public float getDistance() {
        return Distance;
    }

    public void setHP(float HP) {
        this.HP = HP;
    }

    public Body  getEnemyBody() {
        return enemybody;
    }
}
