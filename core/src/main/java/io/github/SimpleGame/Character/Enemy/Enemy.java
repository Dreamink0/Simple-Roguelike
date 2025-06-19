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
    protected Player player;
    protected World world;
    protected boolean isDead = false;
    protected boolean hasDied = false;
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
    public void setHP(Magic magic){attribute.setHP(attribute.getHP()-magic.getDamage());}
    public void setHP(float Damage) {
        if (isDead) return;
        attribute.setHP(attribute.getHP() - Damage);
        if (attribute.getHP() <= 0) {
            isDead = true;
            if (!hasDied) {
                hasDied = true; // 仅首次死亡时触发计数
            }
        }
    }
    public boolean isDead() {
        return isDead;
    }
    public boolean shouldRemove() {
        return isDead && getAnimation().getAnimationTools()[4].isAnimationFinished();
    }
    public boolean hasDied() {
        return hasDied;
    }
    public String getClassName(){return animation.getClassName();}
    public float getHP(){return attribute.getHP();}
    public void setState(State newState) {
        if (this.enemyState == null) {
            this.enemyState = new EnemyState(
                this.enemyPhysic != null ? this.enemyPhysic.getEnemyBody() : null,
                newState,
                player,
                this.enemyPhysic,
                this.attribute
            );
        } else {
            this.enemyState.currentState = newState;
        }
    }
    public EnemyAnimation getAnimation() {return animation;}
    public EnemyState getEnemyState() {return enemyState;}
    public float getX() {return enemyPhysic.getX();}
    public float getY() {return enemyPhysic.getY();}
}
