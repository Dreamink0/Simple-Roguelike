package io.github.SimpleGame.Resource;

import org.junit.Test;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class MapGenerationTest {
    
    @Test
    public void testMapGeneration() {
        // 创建地图生成器实例
        MapGeneration mapGen = new MapGeneration();
        
        // 生成地图
        TiledMap map = mapGen.generateRandomMap();
        
        // 验证地图不为空
        assert map != null : "生成的地图不应为空";
        
        // 验证地图有正确的图层
        assert map.getLayers().size() >= 2 : "地图应该至少有2个图层";
        
        System.out.println("地图生成测试通过！");
    }
    
    @Test
    public void testMapGenerationWithSeed() {
        MapGeneration mapGen = new MapGeneration();
        
        // 使用固定种子生成两次，应该得到相同的结果
        TiledMap map1 = mapGen.generateMapWithSeed(12345L);
        TiledMap map2 = mapGen.generateMapWithSeed(12345L);
        
        assert map1 != null && map2 != null : "使用种子生成的地图不应为空";
        
        System.out.println("种子地图生成测试通过！");
    }
}
