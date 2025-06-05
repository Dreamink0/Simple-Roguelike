package io.github.SimpleGame.Resource.Effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Resource.ShaderManager;
import java.util.HashMap;
import java.util.Map;

/**
 * 颜色叠加效果 - 可用于受伤(红色)、中毒(绿色)、冰冻(蓝色)等效果
 */
public class ColorOverlayEffect extends BaseEffect {
    private float red = 1.0f;
    private float green = 0.0f;
    private float blue = 0.0f;
    private float alpha = 0.5f;
    
    public ColorOverlayEffect(ShaderManager shaderManager) {
        super(shaderManager);
        this.fadeDuration = 1.0f; // 默认1秒渐变
    }
    
    /**
     * 设置叠加颜色
     * @param r 红色分量 (0.0-1.0)
     * @param g 绿色分量 (0.0-1.0)
     * @param b 蓝色分量 (0.0-1.0)
     * @param a 透明度 (0.0-1.0)
     */
    public void setColor(float r, float g, float b, float a) {
        this.red = Math.max(0.0f, Math.min(1.0f, r));
        this.green = Math.max(0.0f, Math.min(1.0f, g));
        this.blue = Math.max(0.0f, Math.min(1.0f, b));
        this.alpha = Math.max(0.0f, Math.min(1.0f, a));
    }
    
    /**
     * 设置为受伤效果（红色）
     */
    public void setHurtEffect() {
        setColor(1.0f, 0.0f, 0.0f, 0.6f);
        setFadeDuration(0.5f);
    }
    
    /**
     * 设置为中毒效果（绿色）
     */
    public void setPoisonEffect() {
        setColor(0.0f, 1.0f, 0.0f, 0.4f);
        setFadeDuration(1.0f);
    }
    
    /**
     * 设置为冰冻效果（蓝色）
     */
    public void setFreezeEffect() {
        setColor(0.0f, 0.5f, 1.0f, 0.5f);
        setFadeDuration(1.5f);
    }
    
    /**
     * 设置为燃烧效果（红橙色）
     */
    public void setBurnEffect() {
        setColor(1.0f, 0.4f, 0.0f, 0.5f);
        setFadeDuration(0.8f);
    }
    
    @Override
    public void apply(SpriteBatch batch) {
        if (hasEffect() && shaderManager != null) {
            // 如果没有COLOR_OVERLAY着色器，回退到GRAYSCALE
            if (!shaderManager.isShaderLoaded(ShaderManager.ShaderType.COLOR_OVERLAY)) {
                // 使用灰度着色器作为备选
                Map<String, Object> uniforms = new HashMap<>();
                uniforms.put("u_grayscaleAmount", strength * 0.3f); // 降低强度
                shaderManager.applyShader(batch, ShaderManager.ShaderType.GRAYSCALE, uniforms);
            } else {
                Map<String, Object> uniforms = new HashMap<>();
                uniforms.put("u_overlayColor", new float[]{red, green, blue, alpha * strength});
                uniforms.put("u_strength", strength);
                shaderManager.applyShader(batch, ShaderManager.ShaderType.COLOR_OVERLAY, uniforms);
            }
        }
    }
    
    // Getters
    public float getRed() { return red; }
    public float getGreen() { return green; }
    public float getBlue() { return blue; }
    public float getAlpha() { return alpha; }
}
