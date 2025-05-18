package io.github.SimpleGame.Resource;

import java.util.Random;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.ObjectMap;

public class MapGeneration {
    private static final int ROOM_WIDTH = 20;  // 每个房间的宽度
    private static final int ROOM_HEIGHT = 12; // 每个房间的高度
    private static final int DUNGEON_SIZE = 3; // 地牢大小（3x3的房间）
    private static final int TILE_SIZE = 24;   // 瓦片大小
    
    private final Random random;
    private final ResourceManager resourceManager;
    private final ObjectMap<String, TiledMap> roomTemplates;
    private TiledMap dungeonMap;

    public MapGeneration() {
        this.random = new Random();
        this.resourceManager = ResourceManager.getInstance();
        this.roomTemplates = new ObjectMap<>();
        loadRoomTemplates();
    }

    private void loadRoomTemplates() {
        // 加载所有房间模板
        String[] roomTypes = {
            "0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111",
            "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111"
        };

        for (String roomType : roomTypes) {
            TiledMap room = resourceManager.getBaseMap(roomType);
            if (room != null) {
                roomTemplates.put(roomType, room);
            }
        }
    }

    public TiledMap generateRandomMap() {
        // 创建新的地牢地图
        dungeonMap = new TiledMap();
        
        // 设置地图属性
        MapProperties properties = dungeonMap.getProperties();
        properties.put("tilewidth", TILE_SIZE);
        properties.put("tileheight", TILE_SIZE);
        properties.put("width", DUNGEON_SIZE * ROOM_WIDTH);
        properties.put("height", DUNGEON_SIZE * ROOM_HEIGHT);
        
        // 创建地牢的图层
        TiledMapTileLayer groundLayer = new TiledMapTileLayer(DUNGEON_SIZE * ROOM_WIDTH, DUNGEON_SIZE * ROOM_HEIGHT, TILE_SIZE, TILE_SIZE);
        groundLayer.setName("图块层 1");
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(DUNGEON_SIZE * ROOM_WIDTH, DUNGEON_SIZE * ROOM_HEIGHT, TILE_SIZE, TILE_SIZE);
        wallLayer.setName("Wall");

        // 生成房间布局
        String[][] roomLayout = generateRoomLayout();
        
        // 放置房间
        for (int x = 0; x < DUNGEON_SIZE; x++) {
            for (int y = 0; y < DUNGEON_SIZE; y++) {
                String roomType = roomLayout[x][y];
                if (roomType != null) {
                    placeRoom(roomType, x, y, groundLayer, wallLayer);
                }
            }
        }

        // 处理房间连接处的墙壁
        processRoomConnections(roomLayout, wallLayer);

        // 添加图层到地图
        dungeonMap.getLayers().add(groundLayer);
        dungeonMap.getLayers().add(wallLayer);

        return dungeonMap;
    }

    private void processRoomConnections(String[][] roomLayout, TiledMapTileLayer wallLayer) {
        for (int x = 0; x < DUNGEON_SIZE; x++) {
            for (int y = 0; y < DUNGEON_SIZE; y++) {
                String currentRoom = roomLayout[x][y];
                if (currentRoom == null) continue;

                // 处理水平连接
                if (x < DUNGEON_SIZE - 1 && currentRoom.charAt(1) == '1') {
                    int wallX = (x + 1) * ROOM_WIDTH - 1;
                    int wallY = y * ROOM_HEIGHT + ROOM_HEIGHT / 2;
                    wallLayer.setCell(wallX, wallY, null);
                }

                // 处理垂直连接
                if (y < DUNGEON_SIZE - 1 && currentRoom.charAt(0) == '1') {
                    int wallX = x * ROOM_WIDTH + ROOM_WIDTH / 2;
                    int wallY = (y + 1) * ROOM_HEIGHT - 1;
                    wallLayer.setCell(wallX, wallY, null);
                }
            }
        }
    }

    private String[][] generateRoomLayout() {
        String[][] layout = new String[DUNGEON_SIZE][DUNGEON_SIZE];
        
        // 从中心房间开始
        int centerX = DUNGEON_SIZE / 2;
        int centerY = DUNGEON_SIZE / 2;
        
        // 放置中心房间（随机选择一个有多个出口的房间）
        String[] centerRoomTypes = {"0111", "1011", "1101", "1110", "1111"};
        layout[centerX][centerY] = centerRoomTypes[random.nextInt(centerRoomTypes.length)];
        
        // 根据中心房间的出口生成相邻房间
        generateConnectedRooms(layout, centerX, centerY);
        
        return layout;
    }

    private void generateConnectedRooms(String[][] layout, int x, int y) {
        String currentRoom = layout[x][y];
        if (currentRoom == null) return;

        // 检查四个方向
        if (currentRoom.charAt(0) == '1' && y + 1 < DUNGEON_SIZE && layout[x][y + 1] == null) {
            // 上方向
            String[] upRooms = {"0001", "0101", "1001", "1101"};
            layout[x][y + 1] = upRooms[random.nextInt(upRooms.length)];
            generateConnectedRooms(layout, x, y + 1);
        }
        if (currentRoom.charAt(1) == '1' && x + 1 < DUNGEON_SIZE && layout[x + 1][y] == null) {
            // 右方向
            String[] rightRooms = {"0010", "0110", "1010", "1110"};
            layout[x + 1][y] = rightRooms[random.nextInt(rightRooms.length)];
            generateConnectedRooms(layout, x + 1, y);
        }
        if (currentRoom.charAt(2) == '1' && y - 1 >= 0 && layout[x][y - 1] == null) {
            // 下方向
            String[] downRooms = {"0100", "0110", "1100", "1110"};
            layout[x][y - 1] = downRooms[random.nextInt(downRooms.length)];
            generateConnectedRooms(layout, x, y - 1);
        }
        if (currentRoom.charAt(3) == '1' && x - 1 >= 0 && layout[x - 1][y] == null) {
            // 左方向
            String[] leftRooms = {"1000", "1001", "1100", "1101"};
            layout[x - 1][y] = leftRooms[random.nextInt(leftRooms.length)];
            generateConnectedRooms(layout, x - 1, y);
        }
    }

    private void placeRoom(String roomType, int dungeonX, int dungeonY, TiledMapTileLayer groundLayer, TiledMapTileLayer wallLayer) {
        TiledMap roomTemplate = roomTemplates.get(roomType);
        if (roomTemplate == null) return;

        TiledMapTileLayer roomGroundLayer = (TiledMapTileLayer) roomTemplate.getLayers().get("图块层 1");
        TiledMapTileLayer roomWallLayer = (TiledMapTileLayer) roomTemplate.getLayers().get("Wall");

        if (roomGroundLayer == null || roomWallLayer == null) return;

        // 计算房间在地牢中的位置
        int startX = dungeonX * ROOM_WIDTH;
        int startY = dungeonY * ROOM_HEIGHT;

        // 复制地面层和墙壁层
        for (int x = 0; x < ROOM_WIDTH; x++) {
            for (int y = 0; y < ROOM_HEIGHT; y++) {
                TiledMapTileLayer.Cell groundCell = roomGroundLayer.getCell(x, y);
                TiledMapTileLayer.Cell wallCell = roomWallLayer.getCell(x, y);

                if (groundCell != null) {
                    groundLayer.setCell(startX + x, startY + y, groundCell);
                }
                if (wallCell != null) {
                    wallLayer.setCell(startX + x, startY + y, wallCell);
                }
            }
        }
    }

    public TiledMap generateMapWithSeed(long seed) {
        random.setSeed(seed);
        return generateRandomMap();
    }
} 