package io.github.SimpleGame.Resource;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import io.github.SimpleGame.Resource.Effects.*;
import java.util.HashMap;
import java.util.Map;
public class EffectManager {
    private static EffectManager instance;
    private ShaderManager shaderManager;

    // 效果类型枚举
    public enum EffectType {
        DEATH,          // 死亡效果（黑白）
        HURT,           // 受伤效果（红色闪烁）
        POISON,         // 中毒效果（绿色）
    }

    // 效果实例映射
    private final Map<EffectType, BaseEffect> effects = new HashMap<>();

    // 效果优先级（数值越高优先级越高）
    private final Map<EffectType, Integer> effectPriorities = new HashMap<>();

    private EffectManager() {
        initializePriorities();
    }

    public static EffectManager getInstance() {
        if (instance == null) {
            instance = new EffectManager();
        }
        return instance;
    }

    /**
     * 初始化效果优先级
     */
    private void initializePriorities() {
        effectPriorities.put(EffectType.DEATH, 100);        // 最高优先级
        effectPriorities.put(EffectType.HURT, 80);
        effectPriorities.put(EffectType.POISON, 50);
    }

    /**
     * 初始化效果管理器
     * @param shaderManager Shader管理器实例
     */
    public void initialize(ShaderManager shaderManager) {
        this.shaderManager = shaderManager;
        createEffects();
        Gdx.app.log("EffectManager", "Effect manager initialized with new effect system");
    }

    /**
     * 创建效果实例
     */
    private void createEffects() {
        if (shaderManager == null) return;

        // 创建死亡效果（灰度）
        effects.put(EffectType.DEATH, new GrayscaleEffect(shaderManager));

        // 创建受伤效果（红色叠加）
        ColorOverlayEffect hurtEffect = new ColorOverlayEffect(shaderManager);
        hurtEffect.setHurtEffect();
        effects.put(EffectType.HURT, hurtEffect);

        // 创建中毒效果（绿色叠加）
        ColorOverlayEffect poisonEffect = new ColorOverlayEffect(shaderManager);
        poisonEffect.setPoisonEffect();
        effects.put(EffectType.POISON, poisonEffect);
    }

    /**
     * 激活指定效果
     * @param effectType 效果类型
     */
    public void activateEffect(EffectType effectType) {
        activateEffect(effectType, 1.0f);
    }

    /**
     * 激活指定效果
     * @param effectType 效果类型
     * @param strength 效果强度 (0.0-1.0)
     */
    public void activateEffect(EffectType effectType, float strength) {
        BaseEffect effect = effects.get(effectType);
        if (effect != null) {
            effect.activate(strength);
            Gdx.app.log("EffectManager", effectType.name() + " effect activated with strength: " + strength);
        }
    }

    /**
     * 关闭指定效果
     * @param effectType 效果类型
     */
    public void deactivateEffect(EffectType effectType) {
        BaseEffect effect = effects.get(effectType);
        if (effect != null) {
            effect.deactivate();
            Gdx.app.log("EffectManager", effectType.name() + " effect deactivated");
        }
    }

    /**
     * 立即激活效果（无渐变）
     * @param effectType 效果类型
     * @param strength 效果强度
     */
    public void instantActivateEffect(EffectType effectType, float strength) {
        BaseEffect effect = effects.get(effectType);
        if (effect != null) {
            effect.instantActivate(strength);
            Gdx.app.log("EffectManager", effectType.name() + " effect instantly activated");
        }
    }

    /**
     * 立即关闭效果（无渐变）
     * @param effectType 效果类型
     */
    public void instantDeactivateEffect(EffectType effectType) {
        BaseEffect effect = effects.get(effectType);
        if (effect != null) {
            effect.instantDeactivate();
            Gdx.app.log("EffectManager", effectType.name() + " effect instantly deactivated");
        }
    }

    /**
     * 立即关闭所有效果
     */
    public void clearAllEffects() {
        for (BaseEffect effect : effects.values()) {
            if (effect != null) {
                effect.instantDeactivate();
            }
        }
//        Gdx.app.log("EffectManager", "All effects cleared");
    }

    /**
     * 设置效果的渐变时间
     * @param effectType 效果类型
     * @param duration 渐变时间（秒）
     */
    public void setFadeDuration(EffectType effectType, float duration) {
        BaseEffect effect = effects.get(effectType);
        if (effect != null) {
            effect.setFadeDuration(duration);
        }
    }

    /**
     * 更新所有效果
     * @param deltaTime 时间增量
     */
    public void update(float deltaTime) {
        for (BaseEffect effect : effects.values()) {
            if (effect != null) {
                effect.update(deltaTime);
            }
        }
    }

    /**
     * 应用效果到渲染批次（按优先级应用最高优先级的激活效果）
     * @param batch 要应用效果的SpriteBatch
     */
    public void applyEffect(SpriteBatch batch) {
        BaseEffect highestPriorityEffect = getHighestPriorityActiveEffect();
        if (highestPriorityEffect != null) {
            highestPriorityEffect.apply(batch);
        }
    }

    /**
     * 获取最高优先级的激活效果
     */
    private BaseEffect getHighestPriorityActiveEffect() {
        BaseEffect highestEffect = null;
        int highestPriority = -1;

        for (Map.Entry<EffectType, BaseEffect> entry : effects.entrySet()) {
            BaseEffect effect = entry.getValue();
            if (effect != null && effect.hasEffect()) {
                int priority = effectPriorities.getOrDefault(entry.getKey(), 0);
                if (priority > highestPriority) {
                    highestPriority = priority;
                    highestEffect = effect;
                }
            }
        }

        return highestEffect;
    }

    /**
     * 移除效果
     * @param batch 要移除效果的SpriteBatch
     */
    public void removeEffect(SpriteBatch batch) {
        if (shaderManager != null) {
            shaderManager.removeShader(batch);
        }
    }

    /**
     * 检查是否有任何效果激活
     */
    public boolean hasAnyActiveEffect() {
        for (BaseEffect effect : effects.values()) {
            if (effect != null && effect.hasEffect()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查指定效果是否激活
     * @param effectType 效果类型
     */
    public boolean isEffectActive(EffectType effectType) {
        BaseEffect effect = effects.get(effectType);
        return effect != null && effect.isActive();
    }

    /**
     * 获取指定效果的强度
     * @param effectType 效果类型
     */
    public float getEffectStrength(EffectType effectType) {
        BaseEffect effect = effects.get(effectType);
        return effect != null ? effect.getStrength() : 0.0f;
    }

    /**
     * 获取总体效果强度（最高优先级效果的强度）
     */
    public float getTotalEffectStrength() {
        BaseEffect highestEffect = getHighestPriorityActiveEffect();
        return highestEffect != null ? highestEffect.getStrength() : 0.0f;
    }

    /**
     * 切换效果状态
     * @param effectType 效果类型
     */
    public void toggleEffect(EffectType effectType) {
        if (isEffectActive(effectType)) {
            deactivateEffect(effectType);
        } else {
            activateEffect(effectType);
        }
    }

    /**
     * 获取效果管理器状态信息
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("EffectManager Status (New System):\n");
        for (Map.Entry<EffectType, BaseEffect> entry : effects.entrySet()) {
            BaseEffect effect = entry.getValue();
            if (effect != null) {
                sb.append(String.format("%s: %s (%.2f)\n",
                    entry.getKey().name(),
                    effect.isActive() ? "ACTIVE" : "INACTIVE",
                    effect.getStrength()));
            }
        }
        return sb.toString();
    }

    // 便捷方法
    public void triggerDeathEffect() {
        activateEffect(EffectType.DEATH);
    }

    public void triggerReviveEffect() {
        deactivateEffect(EffectType.DEATH);
    }

    public void instantDeathEffect() {
        instantActivateEffect(EffectType.DEATH, 1.0f);
    }

    public void instantReviveEffect() {
        instantDeactivateEffect(EffectType.DEATH);
    }

    public boolean isDead() {
        return isEffectActive(EffectType.DEATH);
    }

    /**
     * 获取指定类型的效果实例（用于高级自定义）
     * @param effectType 效果类型
     */
    public BaseEffect getEffect(EffectType effectType) {
        return effects.get(effectType);
    }

    /**
     * 设置效果优先级
     * @param effectType 效果类型
     * @param priority 优先级值
     */
    public void setEffectPriority(EffectType effectType, int priority) {
        effectPriorities.put(effectType, priority);
    }

    /**
     * 释放资源
     */
    public void dispose() {
        clearAllEffects();
        effects.clear();
        Gdx.app.log("EffectManager", "Effect manager disposed");
    }
}
