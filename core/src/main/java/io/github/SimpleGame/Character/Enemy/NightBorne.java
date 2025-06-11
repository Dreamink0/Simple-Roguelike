package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Resource.HitboxManager;
import io.github.SimpleGame.Resource.SoundManager;
import io.github.SimpleGame.Tool.AnimationTool;

import java.util.Random;

import static io.github.SimpleGame.Resource.Game.world;

public class NightBorne extends Enemy{
    private final NightBorneState nightBorneState;
    private final NightBorneAnimation nightBorneAnimation;
    public  NightBorne (World world, Player player, float x, float y) {
        super(world, player, x, y);
        nightBorneAnimation = new NightBorneAnimation();
        nightBorneAnimation.load("NightBorne");
        nightBorneAnimation.load();
        enemyPhysic = new EnemyPhysic(x, y, 1.1f, 1.2f);
        Body enemyBody = enemyPhysic.createBody(enemyPhysic.getEnemyBody());
        enemyBody.setUserData(this);
        attribute = new EnemyAttribute(200, 5, 4, 30, 5);
        nightBorneState = new NightBorneState(enemyBody, State.IDLE, player, enemyPhysic, attribute);
    }

    @Override
    public void render(SpriteBatch batch, Player player) {
        nightBorneAnimation.render(batch,nightBorneState,player);
        nightBorneState.update(batch);
    }

    @Override
    public void dispose() {
        if( nightBorneAnimation != null){
            nightBorneAnimation.dispose();
        }
    }
}
class NightBorneState extends EnemyState{
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
    public float hurtAnimationDuration = 0.25f;
    public boolean hurtflag=false;
    public long startTime = 0;

    public NightBorneState(Body enemyBody, Enemy.State currentState, Player player, EnemyPhysic enemyPhysic, EnemyAttribute enemyAttribute) {
        super(enemyBody, currentState, player, enemyPhysic, enemyAttribute);
        this.enemyBody = enemyBody;
        this.player = player;
        this.enemyPhysic = enemyPhysic;
        this.enemyAttribute = enemyAttribute;
        this.currentState = Enemy.State.IDLE;
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
                    enemyPhysic.getOriginalWidth() * 1.2f,
                    enemyPhysic.getOriginalHeight() * 1.2f);
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
                SoundManager.playSound("enemyHit");
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
    public void idle(float deltaTime) {
        if(enemyAttribute.getHP() <= 0){
            currentState = Enemy.State.DIE;
            return;
        }
        if (enemyBody != null) {
            enemyBody.setLinearVelocity(0, 0);
        }
    }

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
        if(distance>=20f){
            Random random = new Random();
            int flagRandom =  random.nextInt(1)-random.nextInt(2);
            int randomInt = random.nextInt(5);
            Vector2 vector = new Vector2(player.getX()+randomInt*flagRandom,player.getY()+randomInt*flagRandom);
            enemyBody.setTransform(vector,0);
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

        if (distance <= Attackrange && !isAttacking && attackTimer <= 0) {
            if (stateTime % attackCooldown >= attackCooldown/4) {
                player.getAttributeHandler().setHP(player.getAttributeHandler().getMaxHP() - Damage);
                attackTimer = attackCooldown;
            }
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
        if (player.getPlayerController().isAttacking() && calculateDistance(player) <= player.getAttributeHandler().getAttackrange()+1) {
            if (hurtTimer <= 0) {
                enemyAttribute.setHP(enemyAttribute.getHP() - player.getAttributeHandler().getDamage());
                System.out.println("HP:" + enemyAttribute.getHP());
                Vector2 knockbackDirection = new Vector2(deltaX, deltaY).nor();
                enemyBody.applyLinearImpulse(knockbackDirection.scl(knockbackForce), enemyBody.getWorldCenter(), true);
                hurtTimer = hurtAnimationDuration;
            }
        }
    }
    @Override
    public void die(Body enemyBody) {
        if(enemyBody!=null){
            x=enemyBody.getPosition().x;
            y=enemyBody.getPosition().y;
            world.destroyBody(enemyBody);
            enemyPhysic.dispose();
        }
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

    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }
}
class NightBorneAnimation extends EnemyAnimation{
    private HitboxManager hitbox;
    private AnimationTool animation;
    private Texture texture;
    private Body body;
    private float attackTimer=0;
    private float attackCooldown=0.15f;
    private float duration=0;
    public void load() {
        hitbox=new HitboxManager();
        texture = new Texture("Magic/Gravity/Gravity-Sheet.png");
        animation = new AnimationTool();
        animation.create("Gravity", texture, 5, 4, 0.08f);
    }

    public void render(SpriteBatch batch, NightBorneState enemyState, Player player){
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 5);
        Enemy.State currentState = enemyState.currentState;
        AnimationTool currentAnimation;
        if (enemyState.getEnemyBody() != null) {
            x = enemyState.getEnemyBody().getPosition().x;
            y = enemyState.getEnemyBody().getPosition().y;
            flip = player.getX() < x;
        }
        if (currentState == Enemy.State.DIE) {
            // 如果是第一次进入死亡状态，重置计时器
            if (!hasPlayedDeathAnimation) {
                deathAnimationTimer = 0f;
                if (animationTools[4].isAnimationFinished()) {
                    animationTools[4].resetStateTime(); // 重置死亡动画时间
                }
            }
            hasPlayedDeathAnimation = true;
        }

        if (!(currentState == Enemy.State.DIE && deathAnimationTimer >= DEATH_ANIMATION_DURATION)) {
            currentAnimation = switch (currentState) {
                case CHASE -> animationTools[1];
                case ATTACK -> animationTools[2];
                case HURT -> animationTools[3];
                case DIE -> animationTools[4];
                default -> animationTools[0];
            };
            if (currentAnimation != null) {
                // 死亡动画单独处理
                if (currentState == Enemy.State.DIE) {
                    effects.render(batch, enemyState, player);
                    // 只有在死亡动画未完成时才渲染
                    if (deathAnimationTimer < DEATH_ANIMATION_DURATION) {
                        animationTools[4].render(batch, x, y, scale, false, flip);
                    }
                } else if (currentState == Enemy.State.ATTACK) {
                    animationTools[2].resetStateTime();
                    animationTools[2].render(batch, x, y, scale, true, flip);
                } else if (currentState == Enemy.State.CHASE) {
                    animationTools[1].resetStateTime();
                    animationTools[1].render(batch, x, y, scale, true, flip);
                } else if (currentState == Enemy.State.HURT) {
                    effects.render(batch, enemyState, player);
                    animationTools[3].render(batch, x, y, scale, true, flip);
                } else {
                    currentAnimation.render(batch, x, y, scale, true, flip);
                }
            }
        }
        if (currentState == Enemy.State.DIE && hasPlayedDeathAnimation) {
            duration+=deltaTime;
            if(duration<=5) {
                animation.render(batch, x, y, scale, true);
                body = hitbox.create(world, animation, player.getX(), player.getY(), 0.3f, 0.3f);
                body.setUserData(this);
                hitbox.update(x, y, body);
                // 检测玩家是否与当前碰撞箱接触
                boolean isPlayerColliding = false;
                if (body != null && player.getBody() != null) {
                    for (Fixture fixture : body.getFixtureList()) {
                        for (Contact contact : world.getContactList()) {
                            if (contact.isTouching()) {
                                Fixture otherFixture = contact.getFixtureA() == fixture ? contact.getFixtureB() : contact.getFixtureA();
                                if (otherFixture.getBody() == player.getBody()) {
                                    isPlayerColliding = true;
                                    break;
                                }
                            }
                        }
                        if (isPlayerColliding) break;
                    }
                }
                if (isPlayerColliding) {
                    // 添加伤害冷却，防止一帧内多次造成伤害
                    if (attackTimer <= 0) {
                        player.getAttributeHandler().setHP(player.getAttributeHandler().getMaxHP() - 5);
                        attackTimer = attackCooldown; // 重置攻击冷却
                    }
                    attackTimer -= deltaTime;
                }
            }else{
                if(body!=null){
                    body.setActive(false);
                    hitbox.free(body);
                }
            }
            deathAnimationTimer += deltaTime;
            if (enemyState.getEnemyBody() != null) {
                enemyState.freeBody();
            }
        }
    }
}
