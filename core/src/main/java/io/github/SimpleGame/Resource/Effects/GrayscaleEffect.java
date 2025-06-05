package io.github.SimpleGame.Resource.Effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Resource.ShaderManager;
import java.util.HashMap;
import java.util.Map;

/**
 * 灰度效果 - 主要用于死亡效果
 */
public class GrayscaleEffect extends BaseEffect {
    
    public GrayscaleEffect(ShaderManager shaderManager) {
        super(shaderManager);
        this.fadeDuration = 2.0f; // 默认2秒渐变
    }
    
    @Override
    public void apply(SpriteBatch batch) {
        if (hasEffect() && shaderManager != null) {
            Map<String, Object> uniforms = new HashMap<>();
            uniforms.put("u_grayscaleAmount", strength);
            shaderManager.applyShader(batch, ShaderManager.ShaderType.GRAYSCALE, uniforms);
        }
    }
}
