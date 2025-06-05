package io.github.SimpleGame.Resource.Effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Resource.ShaderManager;

/**
 * 效果基类 - 定义所有视觉效果的通用接口
 */
public abstract class BaseEffect {
    protected float strength = 0.0f;           // 效果强度 (0.0-1.0)
    protected float timer = 0.0f;              // 效果计时器
    protected float fadeDuration = 2.0f;       // 渐变持续时间
    protected boolean isActive = false;        // 是否激活
    protected boolean isInstant = false;       // 是否立即生效
    
    protected ShaderManager shaderManager;
    
    public BaseEffect(ShaderManager shaderManager) {
        this.shaderManager = shaderManager;
    }
    
    /**
     * 激活效果
     * @param strength 效果强度
     */
    public void activate(float strength) {
        this.isActive = true;
        this.timer = 0.0f;
        if (isInstant) {
            this.strength = Math.max(0.0f, Math.min(1.0f, strength));
        }
    }
    
    /**
     * 关闭效果
     */
    public void deactivate() {
        this.isActive = false;
        this.timer = 0.0f;
        if (isInstant) {
            this.strength = 0.0f;
        }
    }
    
    /**
     * 立即激活效果（无渐变）
     * @param strength 效果强度
     */
    public void instantActivate(float strength) {
        this.isActive = true;
        this.strength = Math.max(0.0f, Math.min(1.0f, strength));
    }
    
    /**
     * 立即关闭效果（无渐变）
     */
    public void instantDeactivate() {
        this.isActive = false;
        this.strength = 0.0f;
    }
    
    /**
     * 更新效果状态
     * @param deltaTime 时间增量
     */
    public void update(float deltaTime) {
        if (isInstant) return;
        
        timer += deltaTime;
        float progress = Math.min(1.0f, timer / fadeDuration);
        
        if (isActive) {
            // 渐变到激活状态
            strength = progress;
        } else {
            // 渐变到关闭状态
            if (strength > 0.0f) {
                strength = 1.0f - progress;
                if (strength <= 0.0f) {
                    strength = 0.0f;
                }
            }
        }
    }
    
    /**
     * 应用效果到渲染批次
     * @param batch 渲染批次
     */
    public abstract void apply(SpriteBatch batch);
    
    /**
     * 移除效果
     * @param batch 渲染批次
     */
    public void remove(SpriteBatch batch) {
        if (shaderManager != null) {
            shaderManager.removeShader(batch);
        }
    }
    
    // Getters and Setters
    public float getStrength() { return strength; }
    public boolean isActive() { return isActive; }
    public boolean hasEffect() { return strength > 0.0f; }
    
    public void setFadeDuration(float duration) {
        this.fadeDuration = Math.max(0.1f, duration);
    }
    
    public void setInstant(boolean instant) {
        this.isInstant = instant;
    }
    
    public float getFadeDuration() { return fadeDuration; }
}
