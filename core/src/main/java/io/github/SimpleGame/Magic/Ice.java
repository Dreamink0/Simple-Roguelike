//package io.github.SimpleGame.Magic;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.physics.box2d.Body;
//import com.badlogic.gdx.physics.box2d.World;
//import io.github.SimpleGame.Character.Player.Player;
//import io.github.SimpleGame.Tool.AnimationTool;
//
//public class Ice extends Magic {
//    // 冰弹配置常量
//    private static final int POOL_CAPACITY = 15;
//    private static final float SPEED = 8.0f;
//    private static final float RANGE = 25f;
//    private static final float ANIMATION_SCALE = 0.01f;
//
//    // 对象池
//    private static final BodyPool bodyPool = new BodyPool(POOL_CAPACITY);
//
//    // 状态跟踪
//    private boolean isLaunched;
//    private float launchTime;
//    private float travelDistance;
//
//    // 弹道相关
//    private float direction;
//    private float startX, startY;
//
//    /**
//     * 构造冰魔法实例
//     *
//     * @param world  物理世界
//     * @param player 玩家
//     * @param x      初始x坐标
//     * @param y      初始y坐标
//     */
//    public Ice(World world, Player player, float x, float y) {
//        super(world, player, x, y);
//        initializeMagic();
//    }
//
//    private void initializeMagic() {
//        this.x = x;
//        this.y = y;
//        Animations.loadAssets("IceMagic");
//        Hitboxes = new MagicHitbox(world, player, Animations);
//        Attributes = new MagicAttribute(5, 3, 0, 1.2f, 4);
//        magicState = new MagicState(Attributes, Animations, Hitboxes);
//        label = "ICE";
//    }
//
//    @Override
//    public void render(SpriteBatch batch, SpriteBatch UIbatch, Player player) {
//        handleActivation(player);
//
//        if (isLaunched) {
//            updateProjectile();
//            updateAnimation(batch);
//            checkLifetime();
//        }
//
//        updateCooldown();
//    }
//
//    /**
//     * 处理魔法激活逻辑
//     */
//    private void handleActivation(Player player) {
//        if (!Active && canCast(player)) {
//            prepareLaunch(player);
//        }
//    }
//
//    /**
//     * 检查是否可以施放魔法
//     */
//    private boolean canCast(Player player) {
//        return player.getAttributeHandler().getMP() >= Attributes.getMPcost() &&
//               cooldownTimer <= 0 &&
//               Gdx.input.isKeyJustPressed(Input.Keys.J);
//    }
//
//    /**
//     * 准备发射冰弹
//     */
//    private void prepareLaunch(Player player) {
//        Active = true;
//        isLaunched = true;
//        launchTime = System.currentTimeMillis();
//        travelDistance = 0;
//
//        // 设置起始位置
//        startX = player.getX();
//        startY = player.getY();
//        direction = player.getPlayerController().isFlipped() ? -1 : 1;
//
//        // 扣除魔法值
//        player.getAttributeHandler().setMP(
//            player.getAttributeHandler().getMP() - Attributes.getMPcost()
//        );
//
//        // 创建效果体
//        Body effectBody = bodyPool.acquire(startX, startY, 0.5f, 0.5f);
//        Hitboxes.createEffectWithBody(effectBody);
//    }
//
//    /**
//     * 更新弹道运动
//     */
//    private void updateProjectile() {
//        if (!isLaunched || travelDistance >= RANGE) return;
//
//        Body effectsBody = Hitboxes.getEffectsBody();
//        if (effectsBody != null) {
//            float deltaTime = Gdx.graphics.getDeltaTime();
//            float newX = effectsBody.getPosition().x + SPEED * direction * deltaTime;
//
//            // 更新位置并计算飞行距离
//            effectsBody.setTransform(newX, startY, 0);
//            travelDistance += Math.abs(effectsBody.getPosition().x - newX);
//        }
//    }
//
//    /**
//     * 更新动画渲染
//     */
//    private void updateAnimation(SpriteBatch batch) {
//        AnimationTool[] animations = Animations.getAnimations();
//        if (animations == null || animations.length == 0) return;
//
//        Body effectsBody = Hitboxes.getEffectsBody();
//        if (effectsBody == null) return;
//
//        float currentX = effectsBody.getPosition().x;
//        float currentY = effectsBody.getPosition().y;
//
//        // 渲染动画
//        batch.begin();
//        animations[0].render(batch, currentX, currentY, ANIMATION_SCALE, true, direction < 0);
//        batch.end();
//
//        // 同步碰撞箱位置
//        Hitboxes.setEffectsBody(effectsBody);
//        effectsBody.setTransform(currentX, currentY, effectsBody.getAngle());
//    }
//
//    /**
//     * 检查冰弹生命周期状态
//     */
//    private void checkLifetime() {
//        long age = (long) (System.currentTimeMillis() - launchTime);
//        Body effectsBody = Hitboxes.getEffectsBody();
//
//        // 超出范围或时间过长则回收
//        if ((travelDistance >= RANGE || age > 1000) && effectsBody != null) {
//            bodyPool.release(effectsBody);
//            Hitboxes.clearEffectBody();
//            resetState();
//        }
//
//        // 动画结束时重置状态时间
//        if (Animations.getAnimations()[0].isAnimationFinished()) {
//            Animations.getAnimations()[0].resetStateTime();
//        }
//    }
//
//    /**
//     * 重置发射状态
//     */
//    private void resetState() {
//        isLaunched = false;
//        Active = false;
//        launchTime = 0;
//    }
//
//    /**
//     * 更新冷却计时器
//     */
//    private void updateCooldown() {
//        if (cooldownTimer > 0 && !Active) {
//            cooldownTimer -= Gdx.graphics.getDeltaTime();
//            if (cooldownTimer < 0) cooldownTimer = 0;
//        }
//    }
//
//    /**
//     * 释放资源
//     */
//    public void dispose() {
//        Animations.dispose();
//        if (Hitboxes != null) {
//            Hitboxes.dispose();
//        }
//        bodyPool.releaseAll();
//    }
//}
