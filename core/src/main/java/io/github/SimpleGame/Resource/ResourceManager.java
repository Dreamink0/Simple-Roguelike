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
import com.badlogic.gdx.utils.ObjectMap;
import io.github.SimpleGame.Config;
import io.github.SimpleGame.Item.Weapon;

public class ResourceManager {
    private static ResourceManager instance;
    private final AssetManager assetManager;
    //Atlas
    private TextureAtlas playerTextureAtlas;
    private TextureAtlas playerAttackTextureAtlas;
    private TextureAtlas playerDeadTextureAtlas;
    //Map
    private TiledMap tiledMap;
    private ObjectMap<String, TiledMap> baseMaps;
    private MapManager mapManager;
    //Player
    private Sprite playerSprite;
    private Texture playerHP;
    private Texture playerMP;
    private Texture playerDEF;
    //Animation
    public Animation<TextureRegion> playerIdleAnimation;
    public Animation<TextureRegion> playerRunAnimation;
    public Animation<TextureRegion> playerAttackAnimation;
    public Animation<TextureRegion> playerDeadAnimation;
    //Weapon
    private Texture WeaponTexture;
    private Weapon weapon;
    private TextureAtlas playerTextureAtlas2;
    //Shader
    private ShaderManager shaderManager;
    //Effect
    private EffectManager effectManager;
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
            Load();
            Get();
            Set();
            // 初始化ShaderManager和EffectManager
            shaderManager = ShaderManager.getInstance();
            effectManager = EffectManager.getInstance();
            effectManager.initialize(shaderManager);
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

    public Animation<TextureRegion> getPlayerAttackAnimation() {return playerAttackAnimation;}

    public Sprite getPlayerSprite() {
        return playerSprite;
    }
    public MapManager getMapManager(World world) {
        if (mapManager == null) {
            mapManager = new MapManager(tiledMap, Config.PIXELS_PER_METER/512, world);
        }
        return mapManager;
    }

    public Animation<TextureRegion> getPlayerDeadAnimation() {
        return playerDeadAnimation;
    }

    public void dispose() {
        if (mapManager != null) {
            mapManager.dispose();
            mapManager = null;
        }
        if (shaderManager != null) {
            shaderManager.dispose();
            shaderManager = null;
        }
        if (assetManager != null) {
            assetManager.dispose();
        }
        if (tiledMap != null) {
            tiledMap.dispose();
        }
        if (playerTextureAtlas != null) {
            playerTextureAtlas.dispose();
        }
        if (baseMaps != null) {
            for (TiledMap map : baseMaps.values()) {
                map.dispose();
            }
            baseMaps.clear();
        }
    }

    public void Load(){
        //使用 AssetManager 加载所有资源
        assetManager.load("Sprites/BasePlayer/Player-Idle.atlas", TextureAtlas.class);
        assetManager.load(Config.MAP_PATH, TiledMap.class);
        assetManager.load("Sprites/BasePlayer/Player-attack.atlas",TextureAtlas.class);
        assetManager.load("Sprites/BasePlayer/Player-run.atlas", TextureAtlas.class);
        assetManager.load("Sprites/BasePlayer/Player-dead.atlas",TextureAtlas.class);
        // 加载所有基础地图
        String[] baseMapNames = {
            "0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111",
            "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111"
        };

        for (String mapName : baseMapNames) {
            assetManager.load("Maps/Base/" + mapName + ".tmx", TiledMap.class);
        }
        assetManager.finishLoading();
    }
    public void Get(){
        //获取加载的资源
        playerTextureAtlas = assetManager.get("Sprites/BasePlayer/Player-Idle.atlas", TextureAtlas.class);
        playerTextureAtlas2=assetManager.get("Sprites/BasePlayer/Player-run.atlas",TextureAtlas.class);
        tiledMap = assetManager.get(Config.MAP_PATH, TiledMap.class);
        playerAttackTextureAtlas = assetManager.get("Sprites/BasePlayer/Player-attack.atlas", TextureAtlas.class);
        playerDeadTextureAtlas = assetManager.get("Sprites/BasePlayer/Player-dead.atlas", TextureAtlas.class);
        // 获取所有基础地图
        baseMaps = new ObjectMap<>();
        String[] baseMapNames = {
            "0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111",
            "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111"
        };
        for (String mapName : baseMapNames) {
            baseMaps.put(mapName, assetManager.get("Maps/Base/" + mapName + ".tmx", TiledMap.class));
        }

        if (playerTextureAtlas == null || tiledMap == null) {
            throw new RuntimeException("Failed to load required resources");
        }
    }
    public void Set(){
        //设置所有纹理过滤模式为Nearest
        for (Texture texture : playerTextureAtlas2.getTextures()) {
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        for (Texture texture : playerTextureAtlas.getTextures()) {
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        for(Texture texture : playerAttackTextureAtlas.getTextures()) {
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        for(Texture texture : playerDeadTextureAtlas.getTextures()){
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        playerIdleAnimation = new Animation<>(0.15f, playerTextureAtlas.findRegions("idle"), Animation.PlayMode.LOOP);
        playerRunAnimation = new Animation<>(0.08f, playerTextureAtlas2.findRegions("run"), Animation.PlayMode.LOOP);
        playerSprite = new Sprite(playerTextureAtlas.findRegion("idle"));
        playerAttackAnimation = new Animation<>(0.09f, playerAttackTextureAtlas.findRegions("attack"), Animation.PlayMode.LOOP);
        playerDeadAnimation = new Animation<>(0.1f, playerDeadTextureAtlas.findRegions("dead"), Animation.PlayMode.NORMAL);
        playerSprite.setSize(
            (2 * playerSprite.getWidth() / Config.PIXELS_PER_METER) * Config.PLAYER_SCALE,
            (2 * playerSprite.getHeight() / Config.PIXELS_PER_METER) * Config.PLAYER_SCALE
        );
    }

    public TiledMap getBaseMap(String mapName) {
        return baseMaps.get(mapName);
    }

    /**
     * 获取ShaderManager实例
     */
    public ShaderManager getShaderManager() {
        return shaderManager;
    }

    /**
     * 获取EffectManager实例
     */
    public EffectManager getEffectManager() {
        return effectManager;
    }
}
