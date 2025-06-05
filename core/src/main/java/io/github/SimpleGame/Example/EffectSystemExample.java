package io.github.SimpleGame.Example;

import io.github.SimpleGame.Resource.EffectManager;

/**
 * 效果系统使用示例
 * 展示如何在游戏中集成和使用各种视觉效果
 */
public class EffectSystemExample {

    private EffectManager effectManager;
    private float playerHealth = 100.0f;
    private float maxHealth = 100.0f;

    public void initialize() {
        // 获取效果管理器实例
        effectManager = EffectManager.getInstance();
    }

//    /**
//     * 玩家受到伤害时的效果处理
//     */
//    public void onPlayerDamaged(float damage) {
//        playerHealth = Math.max(0, playerHealth - damage);
//
//        // 触发受伤效果
//        effectManager.activateEffect(EffectManager.EffectType.HURT);
//
//        // 如果玩家死亡
//        if (playerHealth <= 0) {
//            onPlayerDeath();
//        }
//    }

    /**
     * 玩家死亡时的效果处理
     */
    public void onPlayerDeath() {
        // 清除所有其他效果
        effectManager.clearAllEffects();

        // 激活死亡效果
        effectManager.activateEffect(EffectManager.EffectType.DEATH);

        System.out.println("Player died! Death effect activated.");
    }

    /**
     * 玩家复活时的效果处理
     */
    public void onPlayerRevive() {
        playerHealth = maxHealth;

        // 关闭死亡效果
        effectManager.deactivateEffect(EffectManager.EffectType.DEATH);

        System.out.println("Player revived! Death effect deactivated.");
    }

    /**
     * 玩家中毒时的效果处理
     */
    public void onPlayerPoisoned() {
        effectManager.activateEffect(EffectManager.EffectType.POISON, 0.6f);
        System.out.println("Player poisoned! Poison effect activated.");
    }

    /**
     * 玩家解毒时的效果处理
     */
    public void onPlayerCuredPoison() {
        effectManager.deactivateEffect(EffectManager.EffectType.POISON);
        System.out.println("Player cured! Poison effect deactivated.");
    }

    /**
     * 游戏更新循环中的效果管理
     */
    public void update(float deltaTime) {
        // 更新效果状态
        effectManager.update(deltaTime);

        // 自动恢复受伤效果（受伤效果应该是短暂的）
        if (effectManager.isEffectActive(EffectManager.EffectType.HURT)) {
            // 受伤效果持续0.5秒后自动关闭
            // 这个逻辑可以根据具体需求调整
        }
    }


    /**
     * 获取当前效果状态信息
     */
    public void printEffectStatus() {
        System.out.println("=== Effect Status ===");
        System.out.println("Player Health: " + playerHealth + "/" + maxHealth);
        System.out.println(effectManager.getStatusInfo());
        System.out.println("Total Effect Strength: " + effectManager.getTotalEffectStrength());
    }
}
