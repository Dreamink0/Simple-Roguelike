package io.github.SimpleGame.Item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Config;
import io.github.SimpleGame.Tool.Listener;

public class WeaponRender {
    private WeaponHitBox weaponHitBox;
    private Texture texture;
    private float scale;
    public WeaponRender(WeaponHitBox weaponHitBox){
        this.weaponHitBox = weaponHitBox;
        this.texture =  weaponHitBox.getTexture();
        this.scale = weaponHitBox.getScale();
    }
    public void render(SpriteBatch batch, SpriteBatch UIbatch, Player player){
        if(distance(player)){
            player.setIsequipped(true);
        }
        if(player.isIsequipped()){
            weaponHitBox.attachToPlayer(player);
            weaponHitBox.updatePosition(player,player.getX(), player.getY());
            if(isNotAttack(player)){
                drawtoPlayer(batch,player);
                Uidraw(UIbatch,player);
            }
        }else{
            draw(batch,player);
        }
        if(dst(player)){
            Tips.E( batch,player.getX(),player.getY());
        }
    }
    public float getX(){return weaponHitBox.getX();}
    public float getY(){return weaponHitBox.getY();}
    public boolean distance(Player player){
        return
            ((Math.abs(player.getX())-getX())<=2)&&((Math.abs(player.getY())-getY())<=2)&&
            Gdx.input.isKeyJustPressed(Input.Keys.E)&&
            Listener.equip;
    }
    public boolean dst(Player player){
        return
             (Math.sqrt(Math.pow(player.getX() - getX(), 2) + Math.pow(player.getY() - getY(), 2)) <= 2) && Listener.equip;
    }
    public boolean isNotAttack(Player player){
        return !Gdx.input.isKeyPressed(Input.Keys.J)&&!player.getPlayerController().isAttacking();
    }
    public void drawtoPlayer(SpriteBatch batch,Player player){
        batch.begin();
        batch.draw(
            texture,
            (player.getX() - (float) texture.getWidth() /2 * scale)-0.1f,
            (player.getY() - (float) texture.getHeight() /2 * scale)-0.4f,
            (float) texture.getWidth() /2 * scale,
            (float) texture.getHeight() /2 * scale,
            texture.getWidth() * scale,
            texture.getHeight() * scale,
            0.08f, 0.08f,
            0,  //旋转角度
            0, 0,  //纹理坐标
            texture.getWidth(),
            texture.getHeight(),
            !player.getPlayerController().isFlipped,
            true
        );
        batch.end();
    }
    public void draw(SpriteBatch batch,Player player){
        batch.begin();
        // 获取武器位置并直接绘制在合适的位置
        float weaponX = weaponHitBox.getX(); // 直接获取武器的坐标
        float weaponY = weaponHitBox.getY();
        batch.draw(
            texture,
            weaponX - (texture.getWidth() * scale / 2),  // 居中对齐
            weaponY - (texture.getHeight() * scale / 2),
            texture.getWidth() / 2 * scale,
            texture.getHeight() / 2 * scale,
            texture.getWidth() * scale,
            texture.getHeight() * scale,
            0.08f, 0.08f,  // 缩放比例
            0,             // 旋转角度
            0, 0,          // 起始纹理坐标
            texture.getWidth(),
            texture.getHeight(),
            false,         // 翻转（根据需求调整）
            true           // 自动释放资源
        );
        batch.end();
    }
    public void Uidraw(SpriteBatch UIbatch,Player player){
        UIbatch.begin();
        UIbatch.draw(
            texture,
            Config.WORLD_WIDTH/2+5f,Config.WORLD_WIDTH/2-9.5f,
            (float) texture.getWidth() /2*scale*0.1f, (float) texture.getHeight() /2*scale*0.1f);
        UIbatch.end();
    }
    public void dispose(){
        texture.dispose();
        weaponHitBox.dispose();
    }
}
