package io.github.SimpleGame.Resource;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

public class MapGeneration {
    private static final String[] MAP_TEMPLATES = {
        "0000", "0001", "0010", "0011",
        "0100", "0101", "0110", "0111",
        "1000", "1001", "1010", "1011",
        "1100", "1101", "1110", "1111"
    };
    private final Random random;
    private final ResourceManager resourceManager;

    public MapGeneration() {
        this.random = new Random();
        this.resourceManager = ResourceManager.getInstance();
    }

    public TiledMap generateRandomMap() {
        // 随机选择一个模板地图
        String templateMap = MAP_TEMPLATES[random.nextInt(MAP_TEMPLATES.length)];
        
        // 从ResourceManager获取基础地图
        TiledMap baseMap = resourceManager.getBaseMap(templateMap);
        if (baseMap == null) {
            throw new RuntimeException("Failed to load base map: " + templateMap);
        }
        
        // 获取地图层
        TiledMapTileLayer groundLayer = (TiledMapTileLayer) baseMap.getLayers().get("图块层 1");
        TiledMapTileLayer wallLayer = (TiledMapTileLayer) baseMap.getLayers().get("Wall");
        
        if (groundLayer != null && wallLayer != null) {
            // 随机修改一些地面和墙壁
            modifyMapRandomly(groundLayer, wallLayer);
        }
        
        return baseMap;
    }

    private void modifyMapRandomly(TiledMapTileLayer groundLayer, TiledMapTileLayer wallLayer) {
        int width = groundLayer.getWidth();
        int height = groundLayer.getHeight();
        
        // 随机修改一些地面和墙壁
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                // 20%的概率修改地面
                if (random.nextFloat() < 0.2f) {
                    TiledMapTileLayer.Cell groundCell = groundLayer.getCell(x, y);
                    if (groundCell != null && groundCell.getTile() != null) {
                        // 获取当前tile的纹理区域
                        TextureRegion currentTexture = groundCell.getTile().getTextureRegion();
                        // 创建新的tile，使用相同的纹理区域
                        StaticTiledMapTile newTile = new StaticTiledMapTile(currentTexture);
                        groundCell.setTile(newTile);
                    }
                }
                
                // 10%的概率修改墙壁
                if (random.nextFloat() < 0.1f) {
                    TiledMapTileLayer.Cell wallCell = wallLayer.getCell(x, y);
                    if (wallCell != null) {
                        // 随机决定是否移除墙壁
                        if (random.nextBoolean()) {
                            wallLayer.setCell(x, y, null);
                        }
                    }
                }
            }
        }
    }

    public TiledMap generateMapWithSeed(long seed) {
        random.setSeed(seed);
        return generateRandomMap();
    }
} 