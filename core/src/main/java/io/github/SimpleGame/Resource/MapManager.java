package io.github.SimpleGame.Resource;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class MapManager {
    private final TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private final World world;
    private final float scale;
    private final Array<Body> wallBodies;
    private final Pool<BodyDef> bodyDefPool;
    private final Pool<PolygonShape> shapePool;
    private final Pool<FixtureDef> fixtureDefPool;
    public MapManager(TiledMap tiledMap, float scale, World world) {
        this.tiledMap = tiledMap;
        this.scale = scale;
        this.world = world;
        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, scale);
        this.wallBodies = new Array<>();

        // 初始化对象池
        this.bodyDefPool = new Pool<BodyDef>(100) {
            @Override
            protected BodyDef newObject() {
                return new BodyDef();
            }
        };
        this.shapePool = new Pool<PolygonShape>(100) {
            @Override
            protected PolygonShape newObject() {
                return new PolygonShape();
            }
        };
        this.fixtureDefPool = new Pool<FixtureDef>(100) {
            @Override
            protected FixtureDef newObject() {
                return new FixtureDef();
            }
        };

        createWalls();
    }

    private void createWalls() {
        MapLayer wallsLayer = tiledMap.getLayers().get("Wall");
        if (wallsLayer != null && wallsLayer instanceof TiledMapTileLayer tileLayer) {
            int tileWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
            int tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);

            // 批量处理瓦片
            for (int x = 0; x < tileLayer.getWidth(); x++) {
                for (int y = 0; y < tileLayer.getHeight(); y++) {
                    TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                    if (cell != null && cell.getTile() != null) {
                        float worldX = x * tileWidth * scale;
                        float worldY = y * tileHeight * scale;
                        createWallBody(
                            worldX + (tileWidth * scale) / 2f,
                            worldY + (tileHeight * scale) / 2f,
                            tileWidth * scale,
                            tileHeight * scale
                        );
                    }
                }
            }
        }
    }

    private void createWallBody(float x, float y, float width, float height) {
        BodyDef bodyDef = bodyDefPool.obtain();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);
        body.setUserData("wall");

        PolygonShape shape = shapePool.obtain();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = fixtureDefPool.obtain();
        fixtureDef.shape = shape;
        fixtureDef.density = 0f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.0f;

        body.createFixture(fixtureDef);
        wallBodies.add(body);

        // 释放对象回池
        shapePool.free(shape);
        fixtureDefPool.free(fixtureDef);
        bodyDefPool.free(bodyDef);
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
            mapRenderer = null;
        }

        if (tiledMap != null) {
            tiledMap.dispose();
        }

        for (Body body : wallBodies) {
            if (body != null) {
                world.destroyBody(body);
            }
        }
        wallBodies.clear();

        // 清理对象池
        bodyDefPool.clear();
        shapePool.clear();
        fixtureDefPool.clear();
    }
}
