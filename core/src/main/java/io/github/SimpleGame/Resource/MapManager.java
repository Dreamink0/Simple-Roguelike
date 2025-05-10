package io.github.SimpleGame.Resource;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class MapManager {
    private final TiledMap tiledMap;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final World world;
    private final float scale;
    private final Array<Body> wallBodies;

    public MapManager(TiledMap tiledMap, float scale, World world) {
        this.tiledMap = tiledMap;
        this.scale = scale;
        this.world = world;
        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, scale);
        this.wallBodies = new Array<>();

        createWalls();
    }

    private void createWalls() {
        MapLayer wallsLayer = tiledMap.getLayers().get("Wall");
        if (wallsLayer != null) {
            for (MapObject object : wallsLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    RectangleMapObject rectangleObject = (RectangleMapObject) object;
                    Boolean isWall = rectangleObject.getProperties().get("Wall", Boolean.class);
                    if (isWall != null && isWall) {
                        createWallBody(rectangleObject.getRectangle());
                    }
                }
            }
        }
    }

    private void createWallBody(Rectangle rectangle) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(
                (rectangle.x + rectangle.width / 2) * scale,
                (rectangle.y + rectangle.height / 2) * scale
        );

        Body body = world.createBody(bodyDef);
        body.setUserData("wall");

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(
                (rectangle.width / 2) * scale,
                (rectangle.height / 2) * scale
        );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.0f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.0f;

        body.createFixture(fixtureDef);
        wallBodies.add(body);
        shape.dispose();
    }

    public void setView(OrthographicCamera camera) {
        mapRenderer.setView(camera);
    }

    public void render(SpriteBatch batch) {
        mapRenderer.render();
    }

    public void dispose() {
        if (mapRenderer != null) {
            mapRenderer.dispose();
        }
        for (Body body : wallBodies) {
            world.destroyBody(body);
        }
        wallBodies.clear();
    }
}
