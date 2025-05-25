package io.github.SimpleGame.Character.Enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Tool.AnimationTool;
public class Monster1 extends Enemy{
    private State currentState;
    private Player player;
    protected Body enemyBody;
    private monster1AI AI;
    float statetime;
    public Monster1(){}
    public Monster1(World world, Player player, float x, float y)
    {
        this.player = player;
        this.currentState = State.PATROL;
        this.AI = new monster1AI(world,player,x,y);
    }
    public void render(SpriteBatch batch,Player player,World world){
        float deltaTime =Math.min(Gdx.graphics.getDeltaTime(),0.25f);
        statetime+=deltaTime;
        AI.update(statetime,batch);
    }
}
class monster1Animation extends Monster1 implements EnemyAnimationHandler{
    private Texture PatrolTexture;
    private Texture ChaseTexture;
    private Texture AttackTexture;
    private Texture DieTexture;
    private AnimationTool[] animationTools;
    protected float X;
    protected float Y;
    public monster1Animation(float x,float y){
        this.X=x;
        this.Y=y;
        animationTools  = new AnimationTool[4];
        load();
    }
    @Override
    public void load() {
        assetManager.load("Enemy/NightBorne-Normal-sheet.png", Texture.class);
        assetManager.load("Enemy/NightBorne-Running-sheet.png",Texture.class);
        assetManager.load("Enemy/NightBorne-Attack-sheet.png",Texture.class);
        assetManager.finishLoading();
        PatrolTexture = assetManager.get("Enemy/NightBorne-Normal-sheet.png", Texture.class);
        ChaseTexture = assetManager.get("Enemy/NightBorne-Running-sheet.png",Texture.class);
        AttackTexture = assetManager.get("Enemy/NightBorne-Attack-sheet.png",Texture.class);
        animationTools[0] = new AnimationTool();
        animationTools[0].create("Patrol",PatrolTexture,1,9,0.15f);
        animationTools[1] = new AnimationTool();
        animationTools[1].create("Chase",ChaseTexture,1,6,0.15f);
        animationTools[2] = new AnimationTool();
        animationTools[2].create("Attack",AttackTexture,1,12,0.05f);
    }
    @Override
    public void dispose() {

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
class monster1AI extends Monster1 implements EnemyStateHandler {
    private double distance;
    private float X;
    private float Y;
    private World world;
    private Player player;
    private float HP;
    private float Damage;
    private float Speed = 0.75f;
    private float AttackRange;
    private float AttackSpeed;
    private SpriteBatch batch;
    private monster1Animation animations;
    private AnimationTool currentAnimation;
    private float stateTime = 0f;
    private float originalWidth = 1.0f;
    private float originalHeight = 1.0f;
    private boolean isAttackBoxExtended = false;
    public enum State {
        PATROL,
        CHASE,
        ATTACK,
        DIE
    }

    private State currentState = State.PATROL;

    public monster1AI(World world, Player player, float x, float y) {
        this.world = world;
        this.player = player;
        this.X = x;
        this.Y = y;
        this.animations = new monster1Animation(x, y);
        // 初始化碰撞体
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
        fixtureDef.density = 5;
        fixtureDef.friction = 10;
        fixtureDef.restitution = 0f;
        enemyBody.createFixture(fixtureDef);
        shape.dispose();

        enemyBody.setUserData("monster1");
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
        newFixtureDef.density = 5;
        newFixtureDef.friction = 10;
        newFixtureDef.restitution = 0f;

        enemyBody.createFixture(newFixtureDef);
        newShape.dispose();
    }
    @Override
    public void update(float deltaTime, SpriteBatch batch) {
        stateTime += deltaTime;

        if (enemyBody != null) {
            X = enemyBody.getPosition().x;
            Y = enemyBody.getPosition().y;
        }
        distance = calculateDistance(player);

        if (distance < 5f) {
            currentState = State.ATTACK;
            attack(deltaTime);

            if (!isAttackBoxExtended) {
                setCollisionBoxSize(originalWidth * 3f, originalHeight * 1.5f);
                isAttackBoxExtended = true;
            }
        } else if (distance <= 20 && distance > 5f) {
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
        render(batch, stateTime);
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
            float forceX = (deltaX / distance) * Speed * 20;
            float forceY = (deltaY / distance) * Speed * 20;

            enemyBody.applyForceToCenter(forceX, forceY, true);
        }
    }

    @Override
    public void chase(float deltaTime) {
        float deltaX = player.getX() - X;
        float deltaY = player.getY() - Y;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance > 0.5f) {
            float forceX = (deltaX / distance) * Speed * 30;
            float forceY = (deltaY / distance) * Speed * 30;

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
    public void die() {
        if (enemyBody != null) {
            world.destroyBody(enemyBody);
            enemyBody = null;
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
            default:
                currentAnimation = animationTools[0];
        }
        if (currentAnimation != null) {
            if (currentState == State.ATTACK) {
                animationTools[2].render(batch, X, Y, 0.1f, false, flip);
            } else if (currentState == State.CHASE) {
                animationTools[1].render(batch, X, Y, 0.1f, true, flip);
            } else {
                currentAnimation.render(batch, X, Y, 0.1f, true, flip);
            }
        }
    }
}
