package io.github.SimpleGame.Resource;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class MapManager{
    private final TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;
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
            // AI说要如果wallsLayer 是 Tile Layer
            // createWalls()方法就不会处理它，因为getObjects()只返回对象层的对象。
            if (wallsLayer instanceof TiledMapTileLayer tileLayer) {
                //加入边界检测,先获得地图尺寸多少像素
                int TileWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
                int TileHeight = tiledMap.getProperties().get("tileheight", Integer.class);
                for (int x = 0; x < tileLayer.getWidth(); x++) {
                    for (int y = 0; y < tileLayer.getHeight(); y++) {
                        TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                        if (cell != null && cell.getTile() != null) {
                            //计算Tile的在世界的坐标
                            float worldX = x * TileWidth * scale;
                            float worldY = y * TileHeight * scale;
                            //创建碰撞体
                            createWallBody(
                                worldX + (TileWidth * scale) / 2f,
                                worldY + (TileHeight * scale) / 2f,
                                TileWidth * scale,
                                TileHeight * scale
                            );
                        }
                    }
                }
            }
        }
    }

    private void createWallBody(float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);
        body.setUserData("wall");

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0f; // 静态刚体密度应为 0
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
           mapRenderer = null;
        }

        for (Body body : wallBodies) {
            world.destroyBody(body);
        }
        wallBodies.clear();
    }
}
