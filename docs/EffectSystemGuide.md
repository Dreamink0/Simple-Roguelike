# 效果系统使用指南

## 概述

新的效果系统采用面向对象的设计，支持多种视觉效果，并提供了优先级管理和渐变动画功能。

## 主要改进

### 1. 模块化设计
- **ShaderManager**: 通用着色器管理器，支持多种着色器类型
- **BaseEffect**: 效果基类，定义通用接口
- **具体效果类**: `GrayscaleEffect`、`ColorOverlayEffect` 等
- **EffectManager**: 统一的效果管理器

### 2. 支持的效果类型
- `DEATH`: 死亡效果（灰度）
- `HURT`: 受伤效果（红色闪烁）
- `POISON`: 中毒效果（绿色）
- `FREEZE`: 冰冻效果（蓝色）
- `BURN`: 燃烧效果（红橙色）
- `UNDERWATER`: 水下效果（蓝绿色）
- `NIGHT_VISION`: 夜视效果（绿色）

### 3. 优先级系统
效果按优先级渲染，高优先级效果会覆盖低优先级效果：
- DEATH: 100（最高）
- HURT: 80
- FREEZE: 70
- BURN: 60
- POISON: 50
- UNDERWATER: 30
- NIGHT_VISION: 20（最低）

## 使用方法

### 基本初始化

```java
// 获取管理器实例
ShaderManager shaderManager = ShaderManager.getInstance();
EffectManager effectManager = EffectManager.getInstance();

// 初始化效果管理器
effectManager.initialize(shaderManager);
```

### 激活效果

```java
// 激活死亡效果（带渐变）
effectManager.activateEffect(EffectType.DEATH);

// 激活受伤效果，指定强度
effectManager.activateEffect(EffectType.HURT, 0.8f);

// 立即激活效果（无渐变）
effectManager.instantActivateEffect(EffectType.FREEZE, 1.0f);
```

### 关闭效果

```java
// 关闭效果（带渐变）
effectManager.deactivateEffect(EffectType.DEATH);

// 立即关闭效果（无渐变）
effectManager.instantDeactivateEffect(EffectType.HURT);

// 清除所有效果
effectManager.clearAllEffects();
```

### 在渲染循环中使用

```java
public void render(float deltaTime) {
    // 更新效果状态
    effectManager.update(deltaTime);
    
    batch.begin();
    
    // 应用效果
    effectManager.applyEffect(batch);
    
    // 渲染游戏对象
    renderGameObjects();
    
    // 移除效果
    effectManager.removeEffect(batch);
    
    batch.end();
}
```

### 自定义效果配置

```java
// 设置渐变时间
effectManager.setFadeDuration(EffectType.HURT, 0.3f); // 快速渐变

// 获取效果实例进行高级配置
ColorOverlayEffect hurtEffect = (ColorOverlayEffect) effectManager.getEffect(EffectType.HURT);
if (hurtEffect != null) {
    hurtEffect.setColor(1.0f, 0.2f, 0.2f, 0.7f); // 自定义红色
}

// 设置效果优先级
effectManager.setEffectPriority(EffectType.HURT, 90);
```

### 便捷方法

```java
// 死亡相关
effectManager.triggerDeathEffect();
effectManager.triggerReviveEffect();
effectManager.instantDeathEffect();
effectManager.instantReviveEffect();
boolean isDead = effectManager.isDead();

// 状态查询
boolean hasEffects = effectManager.hasAnyActiveEffect();
boolean isHurt = effectManager.isEffectActive(EffectType.HURT);
float strength = effectManager.getEffectStrength(EffectType.POISON);
String status = effectManager.getStatusInfo();
```

## 扩展新效果

### 1. 创建新的效果类

```java
public class BlurEffect extends BaseEffect {
    public BlurEffect(ShaderManager shaderManager) {
        super(shaderManager);
        this.fadeDuration = 1.5f;
    }
    
    @Override
    public void apply(SpriteBatch batch) {
        if (hasEffect() && shaderManager != null) {
            Map<String, Object> uniforms = new HashMap<>();
            uniforms.put("u_blurRadius", strength * 5.0f);
            shaderManager.applyShader(batch, ShaderManager.ShaderType.BLUR, uniforms);
        }
    }
}
```

### 2. 添加到效果类型枚举

```java
public enum EffectType {
    // ...existing types...
    BLUR            // 模糊效果
}
```

### 3. 在EffectManager中注册

```java
private void createEffects() {
    // ...existing effects...
    
    // 创建模糊效果
    effects.put(EffectType.BLUR, new BlurEffect(shaderManager));
}

private void initializePriorities() {
    // ...existing priorities...
    effectPriorities.put(EffectType.BLUR, 40);
}
```

## 性能优化建议

1. **着色器缓存**: ShaderManager自动缓存已加载的着色器
2. **按需加载**: 只有在使用时才加载对应的着色器文件
3. **优先级渲染**: 只渲染最高优先级的效果，避免多重渲染
4. **资源管理**: 记得在游戏结束时调用`dispose()`方法

## 故障排除

### 着色器文件缺失
如果着色器文件不存在，系统会自动回退到可用的着色器或跳过效果。

### 效果不显示
1. 检查是否正确初始化了ShaderManager
2. 确认着色器文件存在于`assets/Shaders/`目录
3. 检查效果强度是否大于0
4. 确认在渲染循环中调用了`update()`和`applyEffect()`

### 性能问题
1. 避免频繁切换效果
2. 使用立即激活/关闭来避免不必要的渐变
3. 合理设置效果优先级，避免低优先级效果的计算

## 兼容性

新的效果系统向后兼容原有的API，但建议迁移到新的方法以获得更好的功能和性能。