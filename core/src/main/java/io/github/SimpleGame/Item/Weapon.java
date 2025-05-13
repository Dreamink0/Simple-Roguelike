package io.github.SimpleGame.Item;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Resource.ResourceManager;

import java.util.Random;

import static io.github.SimpleGame.Config.PIXELS_PER_METER;

public class Weapon {
    private TextureRegion textureRegion;
    private Texture texture;
    private Rectangle BoundingBox;
    private Body body;
    private float scale;
    private AssetManager assetManager;
    public Weapon(World world, float x, float y, float scale) {
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

    public Weapon(World world, TextureRegion textureRegion, float x, float y, float scale) {
        this.textureRegion = textureRegion;
        this.scale = scale;

        float scaledWidth = textureRegion.getRegionWidth() * scale;
        float scaledHeight = textureRegion.getRegionHeight() * scale;

        this.BoundingBox = new Rectangle(x, y, scaledWidth, scaledHeight);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        bodyDef.position.set(
            (x + scaledWidth / 2),
            (y + scaledHeight / 2)
        );

        body = world.createBody(bodyDef);
        body.setUserData("weapon");

        PolygonShape shape = new PolygonShape();
        float Width = scaledWidth / 2;
        float Height = scaledHeight / 2;
        shape.setAsBox(Width, Height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData("weapon");
        System.out.println("Weapon Fixture created: " + fixtureDef);
        shape.dispose();
    }
    public Body getBody() {return body;}
    public Rectangle getBoundingBox() {return BoundingBox;}
    public void render(SpriteBatch batch) {
        batch.draw(
            textureRegion,
            BoundingBox.x, BoundingBox.y,
            0, 0,
            textureRegion.getRegionWidth() * scale,
            textureRegion.getRegionHeight() * scale,
            0.1f, 0.1f, 0
        );
    }

    public boolean canBePickedUp(Player player) {
        if (player.getEquippedWeapon() != null) {
            return false; // 玩家已经持有武器
        }
        Vector2 playerPos = player.getBody().getPosition();
        Vector2 weaponPos = this.body.getPosition();
        float distance = playerPos.dst(weaponPos);

        return distance < 2.0f; // 可拾取距离阈值
    }

    public TextureRegion getTextureRegion() {
        return  textureRegion;
    }

    public float getScale() {
        return scale;
    }
}
