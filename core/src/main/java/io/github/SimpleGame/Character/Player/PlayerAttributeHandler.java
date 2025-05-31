package io.github.SimpleGame.Character.Player;

public interface PlayerAttributeHandler {
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
    float getAttackrange();
    void setAttackrange(float Aattackrange);

    void update(PlayerTextureHandler textureHandler);
}
