package io.github.SimpleGame.Item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Config;

import java.util.ArrayList;

public class WeaponRender {
    private WeaponHitBox weaponHitBox;
    private Texture texture;
    private float scale;
    private boolean isClosest = false;
    private boolean isEquip = false;
    private int count=0;
    private static final ArrayList<WeaponRender> equippedWeapons = new ArrayList<>();
    private static final float UI_SPACING = 1.5f; //武器之间的间距

    public WeaponRender(WeaponHitBox weaponHitBox){
        this.weaponHitBox = weaponHitBox;
        this.texture =  weaponHitBox.getTexture();
        this.scale = weaponHitBox.getScale();
    }

    public static void addEquippedWeapon(WeaponRender weapon) {
        if (!equippedWeapons.contains(weapon)) {
            equippedWeapons.add(weapon);
        }
    }

    public static void removeEquippedWeapon(WeaponRender weapon) {
        equippedWeapons.remove(weapon);
    }

    public static int getWeaponIndex(WeaponRender weapon) {
        return equippedWeapons.indexOf(weapon);
    }
    public void render(SpriteBatch batch, SpriteBatch UIbatch, Player player){
        if(distance(player)){
            player.setIsequipped(true);
            addEquippedWeapon(this);
        }

        if(player.isIsequipped()&&isNotAttack(player)&&count>=1){
            drawtoPlayer(batch,player);
        }

        if(player.isIsequipped()&&isClosest){
            weaponHitBox.attachToPlayer(player);
            weaponHitBox.updatePosition(player,player.getX(), player.getY());
            ++count;
            isEquip = true;
        }

        if(count>=1){
            Uidraw(UIbatch);
        }

        if(!isEquip){
            draw(batch);
        }

        if(dst(player)){
            Tips.E(batch, player.getX(), player.getY());
        }
    }

    public float getX(){return weaponHitBox.getX();}
    public float getY(){return weaponHitBox.getY();}

    public boolean distance(Player player){
        float distance = (float) Math.sqrt(Math.pow(player.getX() - getX(), 2) + Math.pow(player.getY() - getY(), 2));
        isClosest = distance <= 2 && Gdx.input.isKeyJustPressed(Input.Keys.E);
        return isClosest;
    }

    public boolean dst(Player player){
        return (Math.sqrt(Math.pow(player.getX() - getX(), 2) + Math.pow(player.getY() - getY(), 2)) <= 2);
    }

    public boolean isNotAttack(Player player){
        return !Gdx.input.isKeyPressed(Input.Keys.J)&&!player.getPlayerController().isAttacking();
    }

    public void drawtoPlayer(SpriteBatch batch, Player player) {
        batch.begin();
        int index = getWeaponIndex(this);

        // 根据武器索引设置不同的持握位置和角度，增加立体感
        float rotationAngle = (index == 1) ? 150 : 30; // 主手武器稍微倾斜

        // 获取玩家移动方向以调整武器位置
        boolean isMoving = player.getPlayerController().isMoving();
        boolean isFlipped = player.getPlayerController().isFlipped;

        // 基础偏移量
        float offsetX = isFlipped ? -0.3f : 0.3f; // 根据翻转状态调整水平位置
        float offsetY = -0.2f;

        // 如果玩家在移动，添加动态偏移模拟立体感
        if (isMoving) {
            float walkOffset = (float) Math.sin(System.currentTimeMillis() * 0.005) * 0.05f;
            offsetY += walkOffset;
        }

        batch.draw(
            texture,
            (player.getX() - (float) texture.getWidth() / 2 * scale) + offsetX,
            (player.getY() - (float) texture.getHeight() / 2 * scale) + offsetY-0.5f,
            (float) texture.getWidth() / 2 * scale,
            (float) texture.getHeight() / 2 * scale,
            texture.getWidth() * scale,
            texture.getHeight() * scale,
            0.05f, 0.05f,
            rotationAngle,
            0, 0,
            texture.getWidth(),
            texture.getHeight(),
            isFlipped,
            true
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
            texture.getWidth() / 2 * scale,
            texture.getHeight() / 2 * scale,
            texture.getWidth() * scale,
            texture.getHeight() * scale,
            0.08f, 0.08f,
            0,
            0, 0,
            texture.getWidth(),
            texture.getHeight(),
            false,
            true
        );
        batch.end();
    }

    public void Uidraw(SpriteBatch UIbatch){
        int index = getWeaponIndex(this);
        if (index >= 0) {
            float uiX = Config.WORLD_WIDTH / 2 + 5f - (index * UI_SPACING);
            float uiY = Config.WORLD_HEIGHT / 2 - 7f;

            UIbatch.begin();
            UIbatch.draw(
                texture,
                uiX, uiY,
                (float) texture.getWidth() / 2 * scale * 0.075f,
                (float) texture.getHeight() / 2 * scale * 0.075f
            );
            UIbatch.end();
        }
    }

    public void dispose(){
        texture.dispose();
        weaponHitBox.dispose();
        equippedWeapons.clear();
    }
}
