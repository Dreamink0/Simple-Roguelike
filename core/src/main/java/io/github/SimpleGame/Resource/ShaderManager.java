package io.github.SimpleGame.Resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.HashMap;
import java.util.Map;

/**
 * 着色器管理器 - 负责管理各种着色器程序
 * 重构后支持多种着色器类型，提供更好的扩展性
 */
public class ShaderManager {
    private static ShaderManager instance;

    public enum ShaderType {
        GRAYSCALE("Shaders/grayscale.vert", "Shaders/grayscale.frag"),
        COLOR_OVERLAY("Shaders/color_overlay.vert", "Shaders/color_overlay.frag"),
        DISTORTION("Shaders/distortion.vert", "Shaders/distortion.frag"),
        BLUR("Shaders/blur.vert", "Shaders/blur.frag");

        private final String vertexPath;
        private final String fragmentPath;

        ShaderType(String vertexPath, String fragmentPath) {
            this.vertexPath = vertexPath;
            this.fragmentPath = fragmentPath;
        }

        public String getVertexPath() { return vertexPath; }
        public String getFragmentPath() { return fragmentPath; }
    }

    // Shader程序缓存
    private final Map<ShaderType, ShaderProgram> shaderCache = new HashMap<>();
    private ShaderProgram currentShader = null;

    private ShaderManager() {
        loadDefaultShaders();
    }

    public static ShaderManager getInstance() {
        if (instance == null) {
            instance = new ShaderManager();
        }
        return instance;
    }

    /**
     * 加载默认着色器
     */
    private void loadDefaultShaders() {
        // 只加载存在的着色器文件
        loadShader(ShaderType.GRAYSCALE);
        Gdx.app.log("ShaderManager", "Default shaders loaded");
    }

    /**
     * 加载指定类型的着色器
     * @param type 着色器类型
     */
    public boolean loadShader(ShaderType type) {
        if (shaderCache.containsKey(type)) {
            return true; // 已经加载
        }

        try {
            // 检查文件是否存在
            if (!Gdx.files.internal(type.getVertexPath()).exists() ||
                !Gdx.files.internal(type.getFragmentPath()).exists()) {
                Gdx.app.log("ShaderManager", "Shader files not found for type: " + type.name());
                return false;
            }

            String vertexShader = Gdx.files.internal(type.getVertexPath()).readString();
            String fragmentShader = Gdx.files.internal(type.getFragmentPath()).readString();

            ShaderProgram shaderProgram = new ShaderProgram(vertexShader, fragmentShader);

            if (!shaderProgram.isCompiled()) {
                Gdx.app.error("ShaderManager", type.name() + " shader compilation failed: " + shaderProgram.getLog());
                shaderProgram.dispose();
                return false;
            }

            shaderCache.put(type, shaderProgram);
            Gdx.app.log("ShaderManager", type.name() + " shader loaded successfully");
            return true;

        } catch (Exception e) {
            Gdx.app.error("ShaderManager", "Error loading shader " + type.name() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取指定类型的着色器程序
     * @param type 着色器类型
     * @return 着色器程序，如果不存在则返回null
     */
    public ShaderProgram getShader(ShaderType type) {
        ShaderProgram shader = shaderCache.get(type);
        if (shader == null && loadShader(type)) {
            shader = shaderCache.get(type);
        }
        return shader;
    }

    /**
     * 应用着色器到SpriteBatch
     * @param batch 渲染批次
     * @param type 着色器类型
     * @param uniforms 着色器参数（键值对）
     */
    public void applyShader(SpriteBatch batch, ShaderType type, Map<String, Object> uniforms) {
        ShaderProgram shader = getShader(type);
        if (shader != null) {
            batch.setShader(shader);
            currentShader = shader;

            // 设置uniform变量
            if (uniforms != null) {
                setUniforms(shader, uniforms);
            }
        } else {
            Gdx.app.error("ShaderManager", "Failed to apply shader: " + type.name());
        }
    }

    /**
     * 应用着色器（无参数）
     * @param batch 渲染批次
     * @param type 着色器类型
     */
    public void applyShader(SpriteBatch batch, ShaderType type) {
        applyShader(batch, type, null);
    }

    /**
     * 设置着色器uniform变量
     * @param shader 着色器程序
     * @param uniforms 变量映射
     */
    private void setUniforms(ShaderProgram shader, Map<String, Object> uniforms) {
        for (Map.Entry<String, Object> entry : uniforms.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Float) {
                shader.setUniformf(name, (Float) value);
            } else if (value instanceof Integer) {
                shader.setUniformi(name, (Integer) value);
            } else if (value instanceof float[]) {
                float[] arr = (float[]) value;
                if (arr.length == 2) {
                    shader.setUniformf(name, arr[0], arr[1]);
                } else if (arr.length == 3) {
                    shader.setUniformf(name, arr[0], arr[1], arr[2]);
                } else if (arr.length == 4) {
                    shader.setUniformf(name, arr[0], arr[1], arr[2], arr[3]);
                }
            }
        }
    }

    /**
     * 移除当前着色器
     * @param batch 渲染批次
     */
    public void removeShader(SpriteBatch batch) {
        batch.setShader(null);
        currentShader = null;
    }

    /**
     * 获取当前应用的着色器
     */
    public ShaderProgram getCurrentShader() {
        return currentShader;
    }

    /**
     * 检查着色器是否已加载
     * @param type 着色器类型
     */
    public boolean isShaderLoaded(ShaderType type) {
        return shaderCache.containsKey(type);
    }

    /**
     * 重新加载所有着色器
     */
    public void reloadShaders() {
        disposeShaders();
        shaderCache.clear();
        loadDefaultShaders();
        Gdx.app.log("ShaderManager", "All shaders reloaded");
    }

    /**
     * 释放指定着色器
     * @param type 着色器类型
     */
    public void disposeShader(ShaderType type) {
        ShaderProgram shader = shaderCache.remove(type);
        if (shader != null) {
            shader.dispose();
            Gdx.app.log("ShaderManager", type.name() + " shader disposed");
        }
    }

    /**
     * 释放所有着色器资源
     */
    private void disposeShaders() {
        for (ShaderProgram shader : shaderCache.values()) {
            if (shader != null) {
                shader.dispose();
            }
        }
    }

    /**
     * 释放所有资源
     */
    public void dispose() {
        disposeShaders();
        shaderCache.clear();
        currentShader = null;
        Gdx.app.log("ShaderManager", "Shader manager disposed");
    }
}
