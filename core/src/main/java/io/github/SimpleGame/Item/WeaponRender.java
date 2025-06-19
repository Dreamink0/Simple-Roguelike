package io.github.SimpleGame.Item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Config;

import java.util.ArrayList;

public class WeaponRender {
    private final WeaponHitBox weaponHitBox;
    private final Texture texture;
    private final float scale;
    private boolean isClosest = false;
    private boolean isEquip = false;
    private static int count = 0;
    private final ArrayList<WeaponRender> equippedWeapons; // 改为非静态成员
    public static final float UI_SPACING = 1.5f; //武器之间的间距

    // 新增攻击动画相关字段
    private boolean isAttacking = false;
    private float attackProgress = 0f;
    private static final float ATTACK_DURATION = 0.3f; // 攻击动画持续时间
    private final int weaponID;

    // 使用构造参数传入列表，实现统一管理
    public WeaponRender(WeaponHitBox weaponHitBox, ArrayList<WeaponRender> globalEquippedWeapons,int weaponID){
        this.weaponHitBox = weaponHitBox;
        this.texture = weaponHitBox.getTexture();
        this.scale = weaponHitBox.getScale();
        this.equippedWeapons = globalEquippedWeapons;
        this.weaponID = weaponID;
    }

    public void addEquippedWeapon() {
        if (!equippedWeapons.contains(this)) {
            equippedWeapons.add(this);
        }
    }

    public void removeEquippedWeapon() {
        equippedWeapons.remove(this);
    }

    public int getWeaponIndex() {
        return equippedWeapons.indexOf(this);
    }

    public void render(SpriteBatch batch, SpriteBatch UIbatch, Player player){
        if(distance(player)&&count<1){
            player.setIsequipped(true);
            addEquippedWeapon();
        }
        if(player.checkDeath()){
            player.setIsequipped(false);
            isEquip = false;
            removeEquippedWeapon();
            count = 0;
            if (!equippedWeapons.isEmpty()) {
                equippedWeapons.clear();
            }
        }
        if(player.isIsequipped() && isNotAttack(player)&&isEquip){
            drawtoPlayer(batch, player);
        }

        if(isClosest&&count<1){
            player.setIsequipped(true);
            weaponHitBox.attachToPlayer(player);
            weaponHitBox.updatePosition(player, player.getX(), player.getY());
            ++count;
            isEquip = true;
            addEquippedWeapon();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.Q) && !equippedWeapons.isEmpty() && !player.getPlayerController().isAttacking()){
            WeaponRender weaponToDrop = equippedWeapons.get(0); // 丢弃第一个武器
            weaponToDrop.removeEquippedWeapon();
            weaponToDrop.weaponHitBox.detachFromPlayer(player);
            --count;
            weaponToDrop.isEquip = false;
            if(equippedWeapons.isEmpty()) {
                player.setIsequipped(false);
            }
        }

        // 检测攻击输入并设置攻击状态
        if (Gdx.input.isKeyPressed(Input.Keys.J) && player.getPlayerController() != null &&
            player.attackCooldownTimer <= 0 && !isAttacking) {
            isAttacking = true;
            attackProgress = 0f;
            player.attackCooldownTimer = player.attackCooldown;
        }

        for (WeaponRender weapon : equippedWeapons) {
            if (weapon.isEquip()) {
                if (weapon.isAttacking) {
                    if(weapon.weaponID!=12) {
                        weapon.animateAttack(batch, player);
                    }
                } else {
                    weapon.drawtoPlayer(batch, player);
                }
                if(weapon.weaponID==12&&!isAttacking){
                    weapon.drawtoPlayer(batch, player);
                }
            } else {
                weapon.draw(batch);
            }
        }

        if (count >= 1) {
            Uidraw(UIbatch);
        }

        if (!isEquip) {
            draw(batch);
        }

        if (dst(player)) {
            Tips.E(batch, player.getX(), player.getY());
        }
    }

    // 更新攻击动画进度
    public void animateAttack(SpriteBatch batch, Player player) {
        attackProgress += Gdx.graphics.getDeltaTime();
        if (attackProgress >= ATTACK_DURATION) {
            isAttacking = false;
            if (player.getPlayerController() != null) {
                player.attackCooldownTimer = 0;
            }
        }

        batch.begin();
        int index = getWeaponIndex();
        boolean isFlipped = player.getPlayerController().isFlipped;
        float baseAngle = isFlipped ? 90 : 180;
        float attackAngle = 0;

        if (attackProgress < ATTACK_DURATION) {
            // 使用缓动函数让动画更自然
            float t = attackProgress / ATTACK_DURATION;
            attackAngle = (float) (baseAngle - 120 * (Math.sin(Math.PI * t)));
        } else {
            attackAngle = baseAngle;
        }

        // 添加基于角色移动的摆动效果
        boolean isMoving = player.getPlayerController().isMoving();
        float offsetX = isFlipped ? -1.45f : 1.45f;
        float offsetY = 0.2f;

        if (isMoving) {
            float walkOffset = (float) Math.sin(System.currentTimeMillis() * 0.005) * 0.05f;
            offsetY += walkOffset;
        }

        // 绘制武器
        batch.draw(
            texture,
            (player.getX() - (float) texture.getWidth() / 2 * scale) + offsetX,
            (player.getY() - (float) texture.getHeight() / 2 * scale) + offsetY - 0.5f,
            (float) texture.getWidth() / 2 * scale,
            (float) texture.getHeight() / 2 * scale,
            texture.getWidth() * scale,
            texture.getHeight() * scale,
            0.05f, 0.05f,
            attackAngle, // 使用计算出的动画角度代替固定角度
            0, 0,
            texture.getWidth(),
            texture.getHeight(),
            !isFlipped,
            !isFlipped
        );
        batch.end();
    }
    public float getX(){ return weaponHitBox.getX(); }
    public float getY(){ return weaponHitBox.getY(); }
    public boolean distance(Player player){
        float distance = (float) Math.sqrt(Math.pow(player.getX() - getX(), 2) + Math.pow(player.getY() - getY(), 2));
        isClosest = distance <= 2 && Gdx.input.isKeyJustPressed(Input.Keys.E);
        return isClosest;
    }

    public boolean dst(Player player){
        return (Math.sqrt(Math.pow(player.getX() - getX(), 2) + Math.pow(player.getY() - getY(), 2)) <= 2);
    }

    public boolean isNotAttack(Player player){
        return !Gdx.input.isKeyPressed(Input.Keys.J) && !player.getPlayerController().isAttacking();
    }

    public void drawtoPlayer(SpriteBatch batch, Player player) {
        batch.begin();
        int index = getWeaponIndex();
        boolean isMoving = player.getPlayerController().isMoving();
        boolean isFlipped = player.getPlayerController().isFlipped;

        float offsetX = isFlipped ? -1.45f :1.45f;
        float offsetY = 0.2f;

        if (isMoving) {
            float walkOffset = (float) Math.sin(System.currentTimeMillis() * 0.005) * 0.05f;
            offsetY += walkOffset;
        }

        batch.draw(
            texture,
            (player.getX() - (float) texture.getWidth() / 2 * scale) + offsetX,
            (player.getY() - (float) texture.getHeight() / 2 * scale) + offsetY - 0.5f,
            (float) texture.getWidth() / 2 * scale,
            (float) texture.getHeight() / 2 * scale,
            texture.getWidth() * scale,
            texture.getHeight() * scale,
            0.05f, 0.05f,
            0,
            0, 0,
            texture.getWidth(),
            texture.getHeight(),
            !isFlipped,
            false
        );
        batch.end();
    }

    public void draw(SpriteBatch batch){
        batch.begin();
        float weaponX = weaponHitBox.getX();
        float weaponY = weaponHitBox.getY();
        batch.draw(
            texture,
            weaponX - (texture.getWidth() * scale / 2),
            weaponY - (texture.getHeight() * scale / 2),
            (float) texture.getWidth() / 2 * scale,
            (float) texture.getHeight() / 2 * scale,
            texture.getWidth() * scale,
            texture.getHeight() * scale,
            0.08f, 0.08f,
            0,
            0, 0,
            texture.getWidth(),
            texture.getHeight(),
            true,
            true
        );
        batch.end();
    }

    public void Uidraw(SpriteBatch UIbatch){
        int index = getWeaponIndex();
        if (index >= 0) {
            float uiX = Config.WORLD_WIDTH / 2 + 5f - (index * UI_SPACING);
            float uiY = Config.WORLD_HEIGHT / 2 - 9f;
            UIbatch.begin();
            UIbatch.draw(
                texture,
                uiX, uiY,
                (float) texture.getWidth() / 2 * scale * 0.1f,
                (float) texture.getHeight() / 2 * scale * 0.1f
            );
            UIbatch.end();
        }
    }

    public boolean isEquip() {
        return isEquip;
    }

    public void setEquip(boolean equip) {
        isEquip = equip;
    }

    public void dispose(){
        // 纹理由资源管理器统一释放，避免重复释放
        weaponHitBox.dispose();
        equippedWeapons.clear();
    }
}
