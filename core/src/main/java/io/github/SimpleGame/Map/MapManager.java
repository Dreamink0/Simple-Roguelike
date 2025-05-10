package io.github.SimpleGame.Map;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class MapManager {
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private List<MapObject> mapObjects;
    private float scale;
    private World world;

    public MapManager(TiledMap tiledMap, float scale, World world) {
        this.tiledMap = tiledMap;
        this.scale = scale;
        this.world = world;
        this.mapObjects = new ArrayList<>();
        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, scale);
        loadMapObjects();
    }

    private void loadMapObjects() {
        for (MapLayer layer : tiledMap.getLayers()) {
            if (layer.getName().equals("Obj")) {
                for (com.badlogic.gdx.maps.MapObject object : layer.getObjects()) {
                    if (object instanceof TextureMapObject) {
                        Boolean isStatic = object.getProperties().get("static", Boolean.class);
                        if (isStatic == null) {
                            isStatic = false;
                        }
                        mapObjects.add(new MapObject((TextureMapObject) object, scale, world, isStatic));
                    }
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        mapRenderer.render();
        
        batch.begin();
        for (MapObject mapObject : mapObjects) {
            mapObject.render(batch);
        }
        batch.end();
    }

    public void setView(OrthographicCamera camera) {
        mapRenderer.setView(camera);
    }

    public void dispose() {
        for (MapObject mapObject : mapObjects) {
            mapObject.dispose();
        }
        tiledMap.dispose();
        mapRenderer.dispose();
    }
} 