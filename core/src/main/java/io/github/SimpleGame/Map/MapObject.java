package io.github.SimpleGame.Map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class MapObject {
    private TextureMapObject mapObject;
    private Sprite sprite;
    private Vector2 position;
    private float scale;
    private Body body;
    private World world;
    private boolean isStatic;
    private boolean isSensor;

    public MapObject(TextureMapObject mapObject, float scale, World world, boolean isStatic) {
        this.mapObject = mapObject;
        this.scale = scale;
        this.world = world;
        this.isStatic = isStatic;
        this.position = new Vector2(mapObject.getX(), mapObject.getY());
        
        // 从对象属性中获取是否为传感器
        Boolean sensor = mapObject.getProperties().get("sensor", Boolean.class);
        this.isSensor = sensor != null && sensor;
        
        if (mapObject.getTextureRegion() != null) {
            this.sprite = new Sprite(mapObject.getTextureRegion());
            float width = mapObject.getProperties().get("width", Float.class);
            float height = mapObject.getProperties().get("height", Float.class);
            this.sprite.setSize(width * scale, height * scale);
            
            // 创建物理体
            createBody(width, height);
        }
    }

    private void createBody(float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = isStatic ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(
            (position.x + width/2) * scale,
            (position.y + height/2) * scale
        );

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
            (width/2) * scale,
            (height/2) * scale
        );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;
        fixtureDef.isSensor = isSensor; // 设置为传感器

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void render(SpriteBatch batch) {
        if (sprite != null) {
            Vector2 bodyPos = body.getPosition();
            sprite.setPosition(
                bodyPos.x - sprite.getWidth()/2,
                bodyPos.y - sprite.getHeight()/2
            );
            sprite.setRotation((float) Math.toDegrees(body.getAngle()));
            sprite.draw(batch);
        }
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public float getWidth() {
        return mapObject.getProperties().get("width", Float.class) * scale;
    }

    public float getHeight() {
        return mapObject.getProperties().get("height", Float.class) * scale;
    }

    public Body getBody() {
        return body;
    }

    public boolean isSensor() {
        return isSensor;
    }

    public void dispose() {
        if (body != null) {
            world.destroyBody(body);
        }
    }
} 