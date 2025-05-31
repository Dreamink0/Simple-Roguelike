package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.TimeUtils;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Resource.SoundManager;

import static io.github.SimpleGame.Resource.Game.world;

public class EnemyState implements EnemyStateHandler{
    private Body enemyBody;
    public Enemy.State currentState;
    private Player player;
    private float stateTime=0;
    private float x,y=0;
    private EnemyPhysic enemyPhysic;
    private EnemyAttribute enemyAttribute;
    public float attackTimer = 0f;
    public float attackCooldown = 1.5f;
    public float hurtTimer = 0f;
    public float hurtAnimationDuration = 0.45f;
    public boolean hurtflag=false;
    public long startTime = 0;
    public EnemyState(Body enemyBody, Enemy.State currentState,Player player,EnemyPhysic enemyPhysic,EnemyAttribute enemyAttribute) {
        this.enemyBody = enemyBody;
        this.currentState = currentState;
        this.player = player;
        this.enemyPhysic = enemyPhysic;
        this.enemyAttribute = enemyAttribute;
    }
    @Override
    public void update(SpriteBatch batch) {
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(),5);;
        stateTime += deltaTime;
        double distance = calculateDistance(player);
        if(enemyAttribute.getHP()>0) {
            if (enemyBody != null) {
                x = enemyBody.getPosition().x;
                y = enemyBody.getPosition().y;
            }
            if (currentState==Enemy.State.ATTACK) {
                enemyPhysic.setCollisionBoxSize(
                    enemyPhysic.getOriginalWidth() * 2f,
                    enemyPhysic.getOriginalHeight() * 1.5f);
            }else{
                enemyPhysic.setCollisionBoxSize(
                    enemyPhysic.getOriginalWidth(),
                    enemyPhysic.getOriginalHeight());
            }
            //活着逻辑
            if (hurtflag && (TimeUtils.nanoTime() - startTime) / 1e9f < hurtAnimationDuration) {
                currentState = Enemy.State.HURT;
            } else if (distance <= enemyAttribute.getAttackRange()) {
                currentState = Enemy.State.ATTACK;
                attack(deltaTime);
            } else if (distance <= enemyAttribute.getChaseRange()) {
                currentState = Enemy.State.CHASE;
                chase(deltaTime);
            } else {
                currentState = Enemy.State.IDLE;
                idle(deltaTime);
            }
            if(distance <=player.getAttributeHandler().getAttackrange()&&player.getPlayerController().isAttacking()&&hurtTimer<=0){
                currentState =  Enemy.State.HURT;
                hurt(deltaTime);
                hurtflag = true;
                startTime = TimeUtils.nanoTime();
            }
            hurtTimer -= deltaTime;
        }else{
            currentState = Enemy.State.DIE;
        }
    }

    @Override
    public void idle(float deltaTime) {}

    @Override
    public void chase(float deltaTime) {
        if(enemyAttribute.getHP()<=0){
            currentState = Enemy.State.DIE;
            return;
        }
        float Speed = enemyAttribute.getSpeed();
        float deltaX = player.getX() - enemyBody.getPosition().x; ;
        float deltaY = player.getY() - enemyBody.getPosition().y;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance > 0.5f) {
            float forceX = (deltaX / distance) * Speed * 2f;
            float forceY = (deltaY / distance) * Speed * 2f;
            float randomFactor = 0.2f;
            forceX += (float) (Math.random() - 0.5) * randomFactor;
            forceY += (float) (Math.random() - 0.5) * randomFactor;
            Vector2 direction = new Vector2(forceX, forceY).nor();
            enemyBody.setLinearVelocity(
                direction.x * Speed * 2f,
                direction.y * Speed * 2f
            );
        }
    }

    @Override
    public void attack(float deltaTime) {
        if(enemyAttribute.getHP()<=0){
            currentState = Enemy.State.DIE;
            return;
        }
        float distance = calculateDistance(player);
        float Damage = enemyAttribute.getDamage();
        float Attackrange = enemyAttribute.getAttackRange();
        boolean isAttacking = player.getPlayerController().isAttacking();

        if ( distance <= Attackrange && !isAttacking && attackTimer <= 0) {
            player.getAttributeHandler().setHP(player.getAttributeHandler().getMaxHP() - Damage);
            attackTimer = attackCooldown;
        }
        attackTimer -= deltaTime;
    }
    @Override
    public void hurt(float deltaTime) {
        if (enemyAttribute.getHP() <= 0) {
            currentState = Enemy.State.DIE;
            return;
        }
        float knockbackForce = (float) Math.pow(2f,4f); //击退力度，可调整
        float deltaX = enemyBody.getPosition().x - player.getX();
        float deltaY = enemyBody.getPosition().y - player.getY();
        if (player.getPlayerController().isAttacking() && calculateDistance(player) <= player.getAttributeHandler().getAttackrange()) {
            enemyAttribute.setHP(enemyAttribute.getHP() - player.getAttributeHandler().getDamage());
            System.out.println("HP:" + enemyAttribute.getHP());
            Vector2 knockbackDirection = new Vector2(deltaX, deltaY).nor();
            enemyBody.applyLinearImpulse(knockbackDirection.scl(knockbackForce), enemyBody.getWorldCenter(), true);
        }
    }
    @Override
    public void die(Body enemyBody) {
    }

    @Override
    public float calculateDistance(Player player) {
        if (enemyBody == null) return Float.MAX_VALUE;
        return enemyBody.getPosition().dst(player.getX(), player.getY());
    }

    public Body getEnemyBody() {
        return enemyBody;
    }
    public void freeBody(){
        if(enemyBody!=null){
            world.destroyBody(enemyBody);
        }
        enemyBody=null;
    }

    public void dispose(){
        if(enemyBody!=null){
            world.destroyBody(enemyBody);
        }
        enemyBody=null;
        player=null;
        enemyAttribute=null;
        enemyPhysic=null;
        currentState=null;
    }
}
