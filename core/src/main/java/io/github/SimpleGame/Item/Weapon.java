package io.github.SimpleGame.Item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Config;
import io.github.SimpleGame.Tool.Listener;

import java.util.Random;

import static io.github.SimpleGame.Config.PIXELS_PER_METER;

public class Weapon {
    private TextureRegion textureRegion;
    private Texture texture;
    private Rectangle BoundingBox;
    private Body body;
    private float scale;
    private AssetManager assetManager;
    private  float offsetX;
    private  float offsetY;
    private  World world;
    private Body attachedBody;
    private boolean isAttached = false;
    public Weapon(World world, float x, float y, float scale) {
        this.world = world;
        assetManager = new AssetManager();
        Random random = new Random();
        int temp = random.nextInt(76);
        assetManager.load("Items/Equipments/raw/Equipment_" + temp + ".png", Texture.class);
        assetManager.finishLoading();
        this.texture = assetManager.get("Items/Equipments/raw/Equipment_" + temp + ".png", Texture.class);
        this.textureRegion = new TextureRegion(texture);
        this.scale = scale;

        float scaledWidth = textureRegion.getRegionWidth()/PIXELS_PER_METER;
        float scaledHeight = textureRegion.getRegionHeight()/PIXELS_PER_METER;

        this.BoundingBox = new Rectangle(x-scaledWidth, y-scaledHeight, scaledWidth, scaledHeight);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(
            (x + 2*scaledWidth),
            (y + 2*scaledHeight)
        );
        this.body = world.createBody(bodyDef);
        this.body.setUserData("weapon");
        PolygonShape shape = new PolygonShape();

        shape.setAsBox(2*scaledWidth, 2*scaledHeight);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        this.body.createFixture(fixtureDef).setUserData("weapon");
        shape.dispose();
    }
    public void render(SpriteBatch batch,SpriteBatch UIbatch,Player player) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.E)&&Listener.equip&&(Math.abs(player.getX())-getX())<=2&&(Math.abs(player.getY())-getY())<=2){
            player.setIsequipped(true);
        }
        if (player.isIsequipped()) {
            attachToPlayer(player,player.getX(),player.getY());
            updatePosition(player,player.getX(), player.getY(), offsetX, offsetY);
            batch.begin();
            if(!Gdx.input.isKeyPressed(Input.Keys.J)&& !player.getPlayerController().isAttacking()){
                batch.draw(
                    textureRegion.getTexture(),
                    (player.getX() - (float) textureRegion.getRegionWidth() /2 * scale)-0.1f,
                    (player.getY() - (float) textureRegion.getRegionHeight() /2 * scale)-0.4f,
                    (float) textureRegion.getRegionWidth() /2 * scale,
                    (float) textureRegion.getRegionHeight() /2 * scale,
                    textureRegion.getRegionWidth() * scale,
                    textureRegion.getRegionHeight() * scale,
                    0.13f, 0.13f,
                    0,  // 旋转角度
                    0, 0,  // 纹理坐标
                    textureRegion.getRegionWidth(),
                    textureRegion.getRegionHeight(),
                    player.getPlayerController().isFlipped,
                    true
                );
            }
            batch.end();
            UIbatch.begin();
            UIbatch.draw(textureRegion.getTexture(),
                Config.WORLD_WIDTH/2+5f,Config.WORLD_WIDTH/2-9.5f,
                textureRegion.getRegionWidth()/2*scale*0.1f,textureRegion.getRegionHeight()/2*scale*0.1f);
          UIbatch.end();
        }else{
            batch.begin();
                updatePosition(player,player.getX(), player.getY(), offsetX, offsetY);
                Vector2 pos = body.getPosition();
                batch.draw(
                    textureRegion,
                    BoundingBox.x, BoundingBox.y,
                    0, 0,
                    textureRegion.getRegionWidth() * scale,
                    textureRegion.getRegionHeight() * scale,
                    0.1f, 0.1f, 0
                );
            batch.end();
        }
    }
    public float getX() {return body.getPosition().x;}
    public float getY() {return body.getPosition().y;}
    public void attachToPlayer(Player player, float offsetX, float offsetY) {
        if (!isAttached) {
            if (body != null) {
                world.destroyBody(body);
                body = null;
            }
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.KinematicBody;
            bodyDef.position.set(player.getBody().getPosition().x + offsetX/PIXELS_PER_METER,
                player.getBody().getPosition().y + offsetY/PIXELS_PER_METER);
            body = world.createBody(bodyDef);
            isAttached=true;
            this.attachedBody = world.createBody(bodyDef);
            this.attachedBody.setUserData("weapon");

            PolygonShape shape = new PolygonShape();
            float scaledWidth = textureRegion.getRegionWidth() / PIXELS_PER_METER;
            float scaledHeight = textureRegion.getRegionHeight() / PIXELS_PER_METER;
            shape.setAsBox(0*scaledWidth, 0*scaledHeight);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.isSensor = true;
            this.attachedBody.createFixture(fixtureDef).setUserData("weapon");
            shape.dispose();

            isAttached = true;
        }
    }
    public void updatePosition(Player player,float x, float y, float offsetX, float offsetY){
        if (isAttached && attachedBody != null && player.getPlayerController() != null) {
            //获取玩家位置并更新武器位置
            Body playerBody = player.getPlayerController().getBody(); // 需要从Player类获取控制器
            if (playerBody != null) {
                Vector2 playerPos = playerBody.getPosition();
                attachedBody.setTransform(
                    playerPos.x + offsetX/PIXELS_PER_METER,
                    playerPos.y + offsetY/PIXELS_PER_METER,
                    0
                );
            }
        }
    }
}
