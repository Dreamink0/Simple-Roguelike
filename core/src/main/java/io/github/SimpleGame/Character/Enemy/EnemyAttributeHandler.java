package io.github.SimpleGame.Character.Enemy;

import io.github.SimpleGame.Character.Player.PlayerTextureHandler;

public interface EnemyAttributeHandler {
    void setHP(float HP);
    float getHP();
    void setMaxHP(float maxHP);
    float getMaxHP();
    void setMP(float MP);
    float getMP();
    void setMaxMP(float maxMP);
    float getMaxMP();
    void setDEF(float MP);
    float getDEF();
    void setMaxDEF(float maxDEF);
    float getMaxDEF();
    void setDamage(float Damage);
    float getDamage();
    void update(PlayerTextureHandler textureHandler);
}
