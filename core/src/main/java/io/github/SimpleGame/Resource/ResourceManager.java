package io.github.SimpleGame.Resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.physics.box2d.World;

import io.github.SimpleGame.Config;

public class ResourceManager {
    private static ResourceManager instance;
    private AssetManager assetManager;
    private TextureAtlas playerTextureAtlas;
    private TiledMap tiledMap;
    private MapManager mapManager;
    private Animation<TextureRegion> playerIdleAnimation;
    private Animation<TextureRegion> playerRunAnimation;
    private Sprite playerSprite;

    private ResourceManager() {
        assetManager = new AssetManager();
        // 注册TiledMap加载器
        assetManager.setLoader(TiledMap.class, new TmxMapLoader());
    }

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    public void loadResources() {
        try {
            // 使用 AssetManager 加载所有资源
            assetManager.load(Config.PLAYER_ATLAS_PATH, TextureAtlas.class);
            assetManager.load(Config.MAP_PATH, TiledMap.class);

            // 等待所有资源加载完成
            assetManager.finishLoading();

            // 获取加载的资源
            playerTextureAtlas = assetManager.get(Config.PLAYER_ATLAS_PATH, TextureAtlas.class);
            tiledMap = assetManager.get(Config.MAP_PATH, TiledMap.class);

            if (playerTextureAtlas == null || tiledMap == null) {
                throw new RuntimeException("Failed to load required resources");
            }

            // 设置所有纹理过滤模式为Nearest
            for (Texture texture : playerTextureAtlas.getTextures()) {
                texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            }

            playerIdleAnimation = new Animation<>(0.15f, playerTextureAtlas.findRegions("idle"), Animation.PlayMode.LOOP);
            playerRunAnimation = new Animation<>(0.08f, playerTextureAtlas.findRegions("run"), Animation.PlayMode.LOOP);

            playerSprite = new Sprite(playerTextureAtlas.findRegion("idle"));
            playerSprite.setSize(
                (2*playerSprite.getWidth() / Config.PIXELS_PER_METER) * Config.PLAYER_SCALE,
                (2*playerSprite.getHeight() / Config.PIXELS_PER_METER) * Config.PLAYER_SCALE
            );
        } catch (Exception e) {
            Gdx.app.error("ResourceManager", "Error loading resources: " + e.getMessage());
            throw new RuntimeException("Failed to load game resources", e);
        }
    }

    public TextureAtlas getPlayerTextureAtlas() {
        return playerTextureAtlas;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public Animation<TextureRegion> getPlayerIdleAnimation() {
        return playerIdleAnimation;
    }

    public Animation<TextureRegion> getPlayerRunAnimation() {
        return playerRunAnimation;
    }

    public Sprite getPlayerSprite() {
        return playerSprite;
    }

    public MapManager getMapManager(World world) {
        if (mapManager == null) {
            mapManager = new MapManager(tiledMap, Config.PIXELS_PER_METER/512, world);
        }
        return mapManager;
    }

    public void dispose() {
        if (assetManager != null) {
            assetManager.dispose();
        }
        if (tiledMap != null) {
            tiledMap.dispose();
        }
        if (playerTextureAtlas != null) {
            playerTextureAtlas.dispose();
        }
        if (mapManager != null) {
            mapManager.dispose();
        }
    }
}
