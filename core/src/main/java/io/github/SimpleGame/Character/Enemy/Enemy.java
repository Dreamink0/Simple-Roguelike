package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Magic.Magic;

public abstract class Enemy {
    //在这个类里面处理的是所有敌人的材质，状态，属性，攻击，移动，
    //攻击范围，检测范围，坐标，接口，状态机，动画，
    protected Enemy() {}


    //状态
    public enum State {IDLE, CHASE, ATTACK, HURT,DIE}
    protected State currentState;
    //属性
    protected Player player;
    protected World world;
    //接口
    protected EnemyAttribute attribute;
    protected EnemyState enemyState;
    protected EnemyAnimation animation;
    protected EnemyPhysic enemyPhysic;
    public Enemy(World world,Player player,float x,float y){
        this.player = player;
        this.currentState = State.IDLE;
        this.world = world;
        this.animation = new EnemyAnimation();
    }
    public abstract void render(SpriteBatch batch, Player player);
    public abstract void dispose();
    public void setHP(Magic magic){
        attribute.setHP(attribute.getHP()-magic.getDamage());
    }
    public float getHP(){
        return attribute.getHP();
    }

    public EnemyState getEnemyState() {
        return enemyState;
    }
}
