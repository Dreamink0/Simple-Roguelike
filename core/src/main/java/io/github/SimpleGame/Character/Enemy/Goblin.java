package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Tool.AnimationTool;
import io.github.SimpleGame.Tool.Listener;

public class Goblin extends Enemy{
    private State currentState;
    private Player player;
    protected Body enemyBody;
    private GoblinAI AI;
    float statetime;
    public Goblin(){}
    public Goblin(World world, Player player, float x, float y)
    {
        this.player = player;
        this.currentState = State.PATROL;
        this.AI = new GoblinAI(world,player,x,y);
    }
    public void render(SpriteBatch batch,Player player,World world){
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
        statetime += deltaTime;
        AI.update(statetime, batch);
        if (Listener.attack_Flag||(AI.calculateDistance(player)<4f||AI.calculateDistance(player)<=3)) {
            if (AI.calculateDistance(player) <=4f && player.getPlayerController().isAttacking()) {
                AI.HP -= 5f;
                if (AI.HP <= 0) {
                    AI.HP = 0;
                }
            } else if (AI.calculateDistance(player) <= 5f&&!player.getPlayerController().isAttacking()) {
                player.getAttributeHandler().setHP(player.getAttributeHandler().getMaxHP() - 0.05f);
            }
            System.out.println("HP:" + AI.HP);
        }
        Listener.attack_Flag = false;
    }
    public void dispose() {
        AI.dispose();
    }
}
class GoblinAnimation extends Goblin implements EnemyAnimationHandler{
    private Texture PatrolTexture;
    private Texture ChaseTexture;
    private Texture HurtTexture;
    private Texture AttackTexture;
    private Texture DieTexture;
    private AnimationTool[] animationTools;
    protected float X;
    protected float Y;
    public GoblinAnimation(float x,float y){
        this.X=x;
        this.Y=y;
        animationTools  = new AnimationTool[5];
        load();
    }
    @Override
    public void load() {
        assetManager.load("Enemy/goblin/goblin scout - silhouette all animations-idle.png", Texture.class);
        assetManager.load("Enemy/goblin/goblin scout - silhouette all animations-run.png",Texture.class);
        assetManager.load("Enemy/goblin/goblin scout - silhouette all animations-hit.png",Texture.class);
        assetManager.load("Enemy/goblin/goblin scout - silhouette all animations-hurt.png",Texture.class);
        assetManager.load("Enemy/goblin/goblin scout - silhouette all animations-death 1.png",Texture.class);
        assetManager.finishLoading();
        PatrolTexture = assetManager.get("Enemy/goblin/goblin scout - silhouette all animations-idle.png", Texture.class);
        ChaseTexture = assetManager.get("Enemy/goblin/goblin scout - silhouette all animations-run.png",Texture.class);
        AttackTexture = assetManager.get("Enemy/goblin/goblin scout - silhouette all animations-hit.png",Texture.class);
        HurtTexture = assetManager.get("Enemy/goblin/goblin scout - silhouette all animations-hurt.png",Texture.class);
        DieTexture = assetManager.get("Enemy/goblin/goblin scout - silhouette all animations-death 1.png",Texture.class);
        animationTools[0] = new AnimationTool();
        animationTools[0].create("Patrol",PatrolTexture,1,8,0.15f);
        animationTools[1] = new AnimationTool();
        animationTools[1].create("Chase",ChaseTexture,1,8,0.15f);
        animationTools[2] = new AnimationTool();
        animationTools[2].create("Attack",AttackTexture,1,3,0.05f);
        animationTools[3] = new AnimationTool();
        animationTools[3].create("Die",DieTexture,1,12,0.15f);
        animationTools[4] = new AnimationTool();
        animationTools[4].create("Hurt",HurtTexture,1,3,1f);
    }
    @Override
    public void dispose() {
        for (AnimationTool animationTool : animationTools) {
            animationTool.dispose();
        }
        animationTools = null;
        assetManager.unload("Enemy/goblin/goblin scout - silhouette all animations-idle.png");
        assetManager.unload("Enemy/goblin/goblin scout - silhouette all animations-run.png");
        assetManager.unload("Enemy/goblin/goblin scout - silhouette all animations-hit.png");
        assetManager.unload("Enemy/goblin/goblin scout - silhouette all animations-death 1.png");
    }

    public Texture getPatrolTexture() {
        return PatrolTexture;
    }

    public Texture getChaseTexture() {
        return ChaseTexture;
    }

    public Texture getAttackTexture() {
        return AttackTexture;
    }

    public Texture getDieTexture() {
        return DieTexture;
    }

    public AnimationTool[] getAnimationTools() {
        return animationTools;
    }
}
class GoblinAI extends Goblin implements EnemyStateHandler {
    private double distance;
    private float X;
    private float Y;
    private World world;
    private Player player;
    public float HP=100;
    private float Damage=0.1f;
    private float Speed = 10f;
    private float AttackRange;
    private GoblinAnimation animations;
    private AnimationTool currentAnimation;
    private float stateTime = 0f;
    private float originalWidth = 0.5f;
    private float originalHeight = 1f;
    private boolean isAttackBoxExtended = false;
    private boolean hasPlayedDeathAnimation = false;
    private boolean canAttack = false;
    private float deathAnimationTimer = 0f;
    private final float DEATH_ANIMATION_DURATION = 1000f; // 根据死亡动画总时长调整
    public enum State {
        PATROL,
        CHASE,
        HURT,
        ATTACK,
        DIE
    }

    private State currentState = State.PATROL;

    public GoblinAI(World world, Player player, float x, float y) {
        this.world = world;
        this.player = player;
        this.X = x;
        this.Y = y;
        this.animations = new GoblinAnimation(x, y);
        createBody();
    }

    private void createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(X, Y);
        bodyDef.fixedRotation = true;

        enemyBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(originalWidth, originalHeight);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 10;
        fixtureDef.friction = 200;
        fixtureDef.restitution = 0f;
        enemyBody.createFixture(fixtureDef);
        shape.dispose();

        enemyBody.setUserData("enemy");
    }

    private void setCollisionBoxSize(float width, float height) {
        if (enemyBody == null) return;

        for (Fixture fixture : enemyBody.getFixtureList()) {
            enemyBody.destroyFixture(fixture);
        }

        PolygonShape newShape = new PolygonShape();
        newShape.setAsBox(width, height);

        FixtureDef newFixtureDef = new FixtureDef();
        newFixtureDef.shape = newShape;
        newFixtureDef.density = 10;
        newFixtureDef.friction = 200;
        newFixtureDef.restitution = 0f;

        enemyBody.createFixture(newFixtureDef);
        newShape.dispose();
    }

    @Override
    public void update(float deltaTime, SpriteBatch batch) {
        stateTime += deltaTime;

        if(HP > 0){
            if (enemyBody != null) {
                X = enemyBody.getPosition().x;
                Y = enemyBody.getPosition().y;
            }
            distance = calculateDistance(player);

            if (distance < 3f) {
                currentState = State.ATTACK;
                attack(deltaTime);

                if (!isAttackBoxExtended) {
                    setCollisionBoxSize(originalWidth * 1.5f, originalHeight * 1.5f);
                    isAttackBoxExtended = true;
                }
            } else if (distance <= 30 && distance > 3f) {
                currentState = State.CHASE;
                chase(deltaTime);

                if (isAttackBoxExtended) {
                    setCollisionBoxSize(originalWidth, originalHeight);
                    isAttackBoxExtended = false;
                }
            } else {
                currentState = State.PATROL;
                patrol(deltaTime);

                if (isAttackBoxExtended) {
                    setCollisionBoxSize(originalWidth, originalHeight);
                    isAttackBoxExtended = false;
                }
            }
        } else {
            currentState = State.DIE;
            // 如果是第一次进入死亡状态，重置计时器
            if (!hasPlayedDeathAnimation) {
                deathAnimationTimer = 0f;
                if (animations != null && animations.getAnimationTools()[3] != null) {
                    animations.getAnimationTools()[3].resetStateTime(); // 重置死亡动画时间
                }
           }
            hasPlayedDeathAnimation = true;
        }
        if (Listener.attack_Flag||(calculateDistance(player)<=4||calculateDistance(player)<=3)) {
            if (calculateDistance(player) <= 4 && player.getPlayerController().isAttacking()) {
                currentState = State.HURT;
            }
        }

        // 如果处于死亡状态且动画播放完毕，不再渲染
        if (!(currentState == State.DIE && hasPlayedDeathAnimation && deathAnimationTimer >= DEATH_ANIMATION_DURATION)) {
            render(batch, stateTime);
        }

        // 处理死亡动画计时
        if (currentState == State.DIE && hasPlayedDeathAnimation) {
            deathAnimationTimer += deltaTime;

            // 动画播放结束后释放资源
            if (deathAnimationTimer >= DEATH_ANIMATION_DURATION) {
                dispose(); // 释放动画资源
                if (enemyBody != null) {
                    world.destroyBody(enemyBody);
                    enemyBody = null;
                }
            }
        }
    }

    @Override
    public void patrol(float deltaTime) {
        float angle = (float) (Math.random() * Math.PI * 2);
        float targetX = (float) (Math.cos(angle) * 5 + player.getX());
        float targetY = (float) (Math.sin(angle) * 5 + player.getY());

        float deltaX = targetX - X;
        float deltaY = targetY - Y;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance > 0) {
            float forceX = (deltaX / distance) * Speed * 2f;
            float forceY = (deltaY / distance) * Speed * 2f;

            enemyBody.applyForceToCenter(forceX, forceY, true);
        }
    }

    @Override
    public void chase(float deltaTime) {
        float deltaX = player.getX() - X;
        float deltaY = player.getY() - Y;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance > 0.5f) {
            float forceX = (deltaX / distance) * Speed * 3f;
            float forceY = (deltaY / distance) * Speed * 2f;

            forceX += (float) (Math.random() - 0.5) * 0.2f;
            forceY += (float) (Math.random() - 0.5) * 0.2f;
            enemyBody.applyForceToCenter(forceX, forceY, true);
        }
    }

    @Override
    public void attack(float deltaTime) {
        if (animations != null && animations.getAnimationTools()[2] != null && !isAttacking) {
            isAttacking = true;
            attackTimer = 0f;
            animations.getAnimationTools()[2].resetStateTime();
        }
    }

    @Override
    public void die(Body enemyBody) {
        if (enemyBody != null) {
            world.destroyBody(enemyBody);
        }
    }

    @Override
    public float calculateDistance(Player player) {
        if (enemyBody == null) return Float.MAX_VALUE;
        return enemyBody.getPosition().dst(player.getX(), player.getY());
    }

    public void setAttackRange(float range) {
        this.AttackRange = range;
    }

    public boolean isAttacking = false;
    private float attackTimer = 0f;
    private float attackCooldown = 1.5f;  // 攻击冷却时间

    public void updateAttack(float deltaTime) {
        if (isAttacking) {
            attackTimer += deltaTime;
            if (attackTimer >= 0.5f) { //攻击动画一半时触发伤害
                System.out.println("Player takes damage!");
            }
            if (attackTimer >= 1.0f) {//动画结束
                isAttacking = false;
                attackTimer = 0f;
                //攻击结束后恢复碰撞箱大小
                if (isAttackBoxExtended) {
                    setCollisionBoxSize(originalWidth, originalHeight);
                    isAttackBoxExtended = false;
                }
            }
        }
    }

    public void render(SpriteBatch batch, float stateTime) {
        AnimationTool[] animationTools = animations.getAnimationTools();
        boolean flip = player.getX() < X && currentAnimation != null;
        switch (currentState) {
            case PATROL:
                currentAnimation = animationTools[0];
                break;
            case CHASE:
                currentAnimation = animationTools[1];
                break;
            case ATTACK:
                currentAnimation = animationTools[2];
                break;
            case DIE:
                currentAnimation = animationTools[3];
                break;
            case HURT:
                currentAnimation = animationTools[4];
                break;
            default:
                currentAnimation = animationTools[0];
        }
        if (currentAnimation != null) {
            // 死亡动画单独处理
            if (currentState == State.DIE) {
                // 只有在死亡动画未完成时才渲染
                if (deathAnimationTimer < DEATH_ANIMATION_DURATION) {
                    animationTools[3].render(batch, X, Y, 0.1f, false, flip);
                }
            }
            else if (currentState == State.ATTACK) {
                animationTools[2].render(batch, X, Y, 0.1f, false, flip);
            }
            else if (currentState == State.CHASE) {
                animationTools[1].render(batch, X, Y, 0.1f, true, flip);
            }
            else {
                currentAnimation.render(batch, X, Y, 0.1f, true, flip);
            }
        }
    }
    public void dispose() {
        if (animations != null) {
            animations.dispose();
            animations = null;
        }
        if (enemyBody != null && world != null) {
            world.destroyBody(enemyBody);
            enemyBody = null;
        }
        currentAnimation = null;
        player = null;
        world = null;
    }
}
