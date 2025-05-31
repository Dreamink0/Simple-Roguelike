package io.github.SimpleGame.Item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Character.Player.Player;

import static io.github.SimpleGame.Config.PIXELS_PER_METER;

public class WeaponHitBox {
    private Texture texture;
    private Rectangle BoundingBox;
    private Body body;
    private World world;
    private Body attachedBody;
    private boolean isAttached = false;
    private float scale;
    public WeaponHitBox(Texture texture,World world){
        this.texture = texture;
        this.world = world;
    }

    public void create(float x, float y,float scale){
        this.scale =  scale;
        float scaledWidth = texture.getWidth()/PIXELS_PER_METER;
        float scaledHeight = texture.getHeight()/PIXELS_PER_METER;
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
    public void attachToPlayer(Player player) {
        if (!isAttached) {
            if (body != null) {
                world.destroyBody(body);
                body = null;
            }
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.KinematicBody;
            bodyDef.position.set(player.getBody().getPosition().x/PIXELS_PER_METER,
                player.getBody().getPosition().y/PIXELS_PER_METER);
            body = world.createBody(bodyDef);
            isAttached=true;
            this.attachedBody = world.createBody(bodyDef);
            this.attachedBody.setUserData("weapon");

            PolygonShape shape = new PolygonShape();
            float scaledWidth = texture.getWidth() / PIXELS_PER_METER;
            float scaledHeight = texture.getHeight() / PIXELS_PER_METER;
            shape.setAsBox(0*scaledWidth, 0*scaledHeight);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.isSensor = true;
            this.attachedBody.createFixture(fixtureDef).setUserData("weapon");
            shape.dispose();

            isAttached = true;
        }
    }
    public void updatePosition(Player player,float offsetX, float offsetY){
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
    public float getX() {return body.getPosition().x;}
    public float getY() {return body.getPosition().y;}
    public Texture getTexture(){return texture;}
    public float getScale(){return scale;}

    public Rectangle getBoundingBox() {return BoundingBox;}
    public void dispose() {
        if (body != null) {
            world.destroyBody(body);
            body = null;
        }
        if (attachedBody != null) {
            world.destroyBody(attachedBody);
            attachedBody = null;
        }
    }
}
