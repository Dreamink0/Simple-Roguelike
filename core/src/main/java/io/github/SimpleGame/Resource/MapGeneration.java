package io.github.SimpleGame.Resource;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.ObjectMap;

public class MapGeneration {
    public static final int ROOM_WIDTH = 20;  // 每个房间的宽度
    public static final int ROOM_HEIGHT = 12; // 每个房间的高度
    public static final int DUNGEON_SIZE = 3; // 地牢大小（3x3的房间）
    public static final int TILE_SIZE = 24;   // 瓦片大小

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
        wallLayer.setOpacity(0f);

        // 生成房间布局
        String[][] roomLayout = generateConnectedRoomLayout();

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

    private String[][] generateConnectedRoomLayout() {
        String[][] layout = new String[DUNGEON_SIZE][DUNGEON_SIZE];
        boolean[][] visited = new boolean[DUNGEON_SIZE][DUNGEON_SIZE];

        // 从中心房间开始
        int centerX = DUNGEON_SIZE / 2;
        int centerY = DUNGEON_SIZE / 2;

        // 放置中心房间（随机选择一个有多个出口的房间）
        String[] centerRoomTypes = {"0111", "1011", "1101", "1110", "1111"};
        layout[centerX][centerY] = centerRoomTypes[random.nextInt(centerRoomTypes.length)];
        visited[centerX][centerY] = true;

        // 使用广度优先搜索确保连通性
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{centerX, centerY});

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];
            String currentRoom = layout[x][y];

            // 检查四个方向
            if (y + 1 < DUNGEON_SIZE && !visited[x][y + 1] && currentRoom.charAt(0) == '1') {
                // 上方向
                String[] upRooms = {"0001", "0101", "1001", "1101"};
                layout[x][y + 1] = upRooms[random.nextInt(upRooms.length)];
                visited[x][y + 1] = true;
                queue.add(new int[]{x, y + 1});
            }
            if (x + 1 < DUNGEON_SIZE && !visited[x + 1][y] && currentRoom.charAt(1) == '1') {
                // 右方向
                String[] rightRooms = {"0010", "0110", "1010", "1110"};
                layout[x + 1][y] = rightRooms[random.nextInt(rightRooms.length)];
                visited[x + 1][y] = true;
                queue.add(new int[]{x + 1, y});
            }
            if (y - 1 >= 0 && !visited[x][y - 1] && currentRoom.charAt(2) == '1') {
                // 下方向
                String[] downRooms = {"0100", "0110", "1100", "1110"};
                layout[x][y - 1] = downRooms[random.nextInt(downRooms.length)];
                visited[x][y - 1] = true;
                queue.add(new int[]{x, y - 1});
            }
            if (x - 1 >= 0 && !visited[x - 1][y] && currentRoom.charAt(3) == '1') {
                // 左方向
                String[] leftRooms = {"1000", "1001", "1100", "1101"};
                layout[x - 1][y] = leftRooms[random.nextInt(leftRooms.length)];
                visited[x - 1][y] = true;
                queue.add(new int[]{x - 1, y});
            }
        }

        // 确保所有房间都被访问到
        for (int x = 0; x < DUNGEON_SIZE; x++) {
            for (int y = 0; y < DUNGEON_SIZE; y++) {
                if (!visited[x][y]) {
                    // 如果房间未被访问，将其连接到最近的已访问房间
                    connectToNearestRoom(layout, visited, x, y);
                }
            }
        }

        return layout;
    }

    private void connectToNearestRoom(String[][] layout, boolean[][] visited, int x, int y) {
        // 找到最近的已访问房间
        int[] nearest = findNearestVisitedRoom(visited, x, y);
        if (nearest == null) return;

        // 确定连接方向
        int dx = nearest[0] - x;
        int dy = nearest[1] - y;

        // 选择适当的房间类型
        String roomType;
        if (dx == 1) {
            String[] rightRooms = {"0010", "0110", "1010", "1110"};
            roomType = rightRooms[random.nextInt(rightRooms.length)];
        } else if (dx == -1) {
            String[] leftRooms = {"1000", "1001", "1100", "1101"};
            roomType = leftRooms[random.nextInt(leftRooms.length)];
        } else if (dy == 1) {
            String[] upRooms = {"0001", "0101", "1001", "1101"};
            roomType = upRooms[random.nextInt(upRooms.length)];
        } else {
            String[] downRooms = {"0100", "0110", "1100", "1110"};
            roomType = downRooms[random.nextInt(downRooms.length)];
        }

        layout[x][y] = roomType;
        visited[x][y] = true;
    }

    private int[] findNearestVisitedRoom(boolean[][] visited, int x, int y) {
        int minDistance = Integer.MAX_VALUE;
        int[] nearest = null;

        for (int i = 0; i < DUNGEON_SIZE; i++) {
            for (int j = 0; j < DUNGEON_SIZE; j++) {
                if (visited[i][j]) {
                    int distance = Math.abs(i - x) + Math.abs(j - y);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearest = new int[]{i, j};
                    }
                }
            }
        }

        return nearest;
    }

    private void processRoomConnections(String[][] roomLayout, TiledMapTileLayer wallLayer) {
        for (int x = 0; x < DUNGEON_SIZE; x++) {
            for (int y = 0; y < DUNGEON_SIZE; y++) {
                String currentRoom = roomLayout[x][y];
                if (currentRoom == null) continue;

                if (x < DUNGEON_SIZE - 1 && currentRoom.charAt(1) == '1') {
                    int wallX = (x + 1) * ROOM_WIDTH - 1;
                    int wallY = y * ROOM_HEIGHT + ROOM_HEIGHT / 2;
                    wallLayer.setCell(wallX, wallY, null);
                }

                if (y < DUNGEON_SIZE - 1 && currentRoom.charAt(0) == '1') {
                    int wallX = x * ROOM_WIDTH + ROOM_WIDTH / 2;
                    int wallY = (y + 1) * ROOM_HEIGHT - 1;
                    wallLayer.setCell(wallX, wallY, null);
                }
            }
        }
    }

    private void placeRoom(String roomType, int dungeonX, int dungeonY, TiledMapTileLayer groundLayer, TiledMapTileLayer wallLayer) {
        TiledMap roomTemplate = roomTemplates.get(roomType);
        if (roomTemplate == null) return;

        TiledMapTileLayer roomGroundLayer = (TiledMapTileLayer) roomTemplate.getLayers().get("图块层 1");
        TiledMapTileLayer roomWallLayer = (TiledMapTileLayer) roomTemplate.getLayers().get("Wall");

        if (roomGroundLayer == null || roomWallLayer == null) return;

        int startX = dungeonX * ROOM_WIDTH;
        int startY = dungeonY * ROOM_HEIGHT;

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
