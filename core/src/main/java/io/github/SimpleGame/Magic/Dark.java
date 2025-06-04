package io.github.SimpleGame.Magic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Item.Tips;
import io.github.SimpleGame.Tool.AnimationTool;

public class Dark extends Magic {
    // 最大效果刚体数
    private static final int MAX_EFFECT_BODIES = 15;
    // 刚体对象池
    public static final BodyPool effectBodyPool;
    // 静态初始化块，初始化对象池
    static {
        effectBodyPool = new BodyPool(MAX_EFFECT_BODIES);
    }
    /**
     * 构造雷电魔法实例
     *
     * @param world  物理世界
     * @param player 玩家
     * @param x      魔法初始x坐标
     * @param y      魔法初始y坐标
     */
    public Dark(World world, Player player, float x, float y) {
        super(world, player, x, y);
        this.x = x;
        this.y = y;
        Animations.loadAssets("DarkMagic");
        Hitboxes = new MagicHitbox(world, player, Animations);
        Hitboxes.createIconBody(x, y);
        Attributes = new MagicAttribute(3, 2, 1, 1.8f, 5);
        magicState = new MagicState(Attributes, Animations, Hitboxes);
        label = "Dark";
    }
    /**
     * 渲染魔法
     *
     * @param batch   渲染批次
     * @param UIbatch UI渲染批次
     * @param player  玩家
     */
    @Override
    public void render(SpriteBatch batch, SpriteBatch UIbatch, Player player) {
        // 获得魔法道具时的判定
        if (distance(player)) {
            flag = true;
            Hitboxes.destroyIconBody();
        }
        if (!flag) {
            Animations.render(batch, x, y, flag, index);
        } else {
            isObtain = true;
            equippedMagic.add(this);
            index = equippedMagic.indexOf(this);
            Animations.render(UIbatch, x, y, flag, index);
        }

        // 获得后释放魔法的判定
        if (isObtain && Gdx.input.isKeyJustPressed(Input.Keys.F) && (player.getAttributeHandler().getMaxMP() - Attributes.getMPcost() >= 0)) {
            if (!Active && cooldownTimer <= 0) {
                Active = true;
                isActivating = true;
                StartX = player.getX();
                StartY = player.getY();
                isFlip = player.getPlayerController().isFlipped();
                cooldownTimer = Attributes.getCooldown();
                player.getAttributeHandler().setMP(player.getAttributeHandler().getMaxMP() - Attributes.getMPcost());
            }
        }

        if (Active) {
            Animations.isActive = true;

            // 只在激活瞬间创建效果体
            if (isActivating) {
                time = System.currentTimeMillis();
                isActivating = false;

                // 创建效果体并加入对象池管理
                Body effectBody = effectBodyPool.acquire(StartX, StartY, 2f, 2f);
                Hitboxes.createEffectWithBody(effectBody);
            }

            long timeSinceActivation = (System.currentTimeMillis() - time);
            if (timeSinceActivation >= Attributes.getDuration() * 1000) {
                Active = false;
                time = 0;
                StartX = 0f;
                StartY = 0f;
                Body effectsBody = Hitboxes.getEffectsBody();
                if (effectsBody != null) {
                    effectBodyPool.release(effectsBody); // 将刚体放回对象池
                    Hitboxes.clearEffectBody(); // 清除引用
                }
            }

            DarkAnimation.render(batch, StartX, StartY, isFlip, Animations, Hitboxes, Active);
        }

        // 如果技能处于非激活状态且冷却结束重置状态
        if (!Active && cooldownTimer <= 0 && (player.getAttributeHandler().getMaxMP() - Attributes.getMPcost() >= 0)) {
            Animations.isActive = false;
        }

        // 更新冷却计时器
        if (cooldownTimer > 0 && !Active) {
            cooldownTimer -= Gdx.graphics.getDeltaTime();
            if (cooldownTimer < 0) cooldownTimer = 0;
        }

        if (dst(player) && !flag) {
            Tips.E(batch, player.getX(), player.getY());
        }

        // 伤害造成逻辑
        if (Active) {
            Body effectsBody = Hitboxes.getEffectsBody();
            if (effectsBody != null) {
                effectsBody.setUserData(this);
            }
        }
    }

    /**
     * 释放资源
     */
    public void dispose() {
        Animations.dispose(); // 释放动画资源
        if (Hitboxes != null) {
            Hitboxes.destroyEffectBody(); // 销毁效果体
            Hitboxes.destroyIconBody(); // 销毁图标体
        }
        if (effectBodyPool != null) {
            effectBodyPool.releaseAll();
        }
    }
    // 黑暗魔法动画类
    static class DarkAnimation {
        private static float stateTime = 0f;
        /**
         * 渲染雷电魔法动画
         *
         * @param batch     渲染批次
         * @param x         魔法x坐标
         * @param y         魔法y坐标
         * @param flip      是否翻转
         * @param animation 动画
         * @param Hitboxes  命中盒
         * @param Active    魔法是否激活
         */
        public static void render(SpriteBatch batch, float x, float y, Boolean flip, MagicAnimation animation, MagicHitbox Hitboxes, Boolean Active) {
            AnimationTool[] animations = animation.getAnimations();
            float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
            stateTime += deltaTime;
            batch.begin();
            int flag = 1;
            if (flip) {
                flag = -1;
            }
            float CurrentX = x + 10 * stateTime * flag;
            animations[0].render(batch, CurrentX, y, 0.1f, false, flip);
            batch.end();

            // 更新碰撞箱位置
            if (Hitboxes != null && Hitboxes.getEffectsBody() != null) {
                Body effectsBody = Hitboxes.getEffectsBody();
                // 更新效果体的位置，保持与渲染坐标同步
                effectsBody.setTransform(CurrentX, y, effectsBody.getAngle());
            }
            if (!Active) {
                stateTime = 0f;
            }
        }
    }
}
