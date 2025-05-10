package io.github.SimpleGame.Item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

import static io.github.SimpleGame.Config.PIXELS_PER_METER;

public class Weapon {
    private TextureRegion textureRegion;
    private Rectangle BoundingBox;
    private Body body;
    private float scale;
    public Weapon(World world, TextureAtlas atlas,String PATH,float x, float y,float scale) {
        this.textureRegion = atlas.findRegion(PATH);
        this.scale = scale;

        float scaledWidth = textureRegion.getRegionWidth() * scale;
        float scaledHeight = textureRegion.getRegionHeight() * scale;

        this.BoundingBox = new Rectangle(x, y, scaledWidth, scaledHeight);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        bodyDef.position.set(
            (x + scaledWidth/2),
            (y + scaledHeight/2)
        );
        body = world.createBody(bodyDef);
        body.setUserData("weapon");
        PolygonShape shape = new PolygonShape();
        float Width = scaledWidth/2;
        float Height = scaledHeight/2;
        shape.setAsBox(Width, Height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData("weapon");
        System.out.println("Weapon Fixture created: " + fixtureDef);
        shape.dispose();
    }
    //武器更换
    public void setWeapon(World world, TextureAtlas atlas, String path,float scale) {
        for (Fixture fixture : body.getFixtureList()) {
            body.destroyFixture(fixture);
        }

        TextureRegion newTextureRegion = atlas.findRegion(path);
        if (newTextureRegion == null) {
            throw new IllegalArgumentException("Texture region not found: " + path);
        }

        this.textureRegion = newTextureRegion;
        this.scale = scale;

        float scaledWidth = textureRegion.getRegionWidth() * scale;
        float scaledHeight = textureRegion.getRegionHeight() * scale;

        this.BoundingBox.setSize(scaledWidth, scaledHeight);
        this.BoundingBox.setPosition(
            body.getPosition().x * PIXELS_PER_METER - scaledWidth / 2f,
            body.getPosition().y * PIXELS_PER_METER - scaledHeight / 2f
        );
        //碰撞
        PolygonShape shape = new PolygonShape();
        float halfWidth = scaledWidth / 2f / PIXELS_PER_METER;
        float halfHeight = scaledHeight / 2f / PIXELS_PER_METER;
        shape.setAsBox(halfWidth, halfHeight);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData("weapon");
        shape.dispose();
    }
    public Body getBody() {
        return body;
    }

    public  Rectangle getBoundingBox() {
        return BoundingBox;
    }

    public void render(SpriteBatch batch) {
        batch.draw(
            textureRegion,
            BoundingBox.x,
            BoundingBox.y,
            textureRegion.getRegionWidth() * scale / 2f, // 原点偏移
            textureRegion.getRegionHeight() * scale / 2f, // 原点偏移
            textureRegion.getRegionWidth() * scale, // 宽度
            textureRegion.getRegionHeight() * scale, // 高度
            1, 1, 0 // 缩放和旋转
        );
    }
}
