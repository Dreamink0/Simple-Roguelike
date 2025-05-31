package io.github.SimpleGame.Resource;

import java.util.LinkedList;
import java.util.Queue;
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
    
    // 房间类型编码常量 - 四位字符串分别代表：上(0)、右(1)、下(2)、左(3)
    private static final int DOOR_UP = 0;    // 上门：roomType.charAt(0)
    private static final int DOOR_RIGHT = 1; // 右门：roomType.charAt(1)  
    private static final int DOOR_DOWN = 2;  // 下门：roomType.charAt(2)
    private static final int DOOR_LEFT = 3;  // 左门：roomType.charAt(3)

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
        
        // 验证连接是否正确
        if (!validateMapConnections(roomLayout)) {
            System.err.println("警告：地图连接验证失败，但仍将继续生成");
        }
        
        // 可选：打印房间布局用于调试
        // printRoomLayout(roomLayout);

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
            if (y + 1 < DUNGEON_SIZE && !visited[x][y + 1] && currentRoom.charAt(DOOR_UP) == '1') {
                // 上方向 - 新房间需要下门来连接当前房间的上门
                layout[x][y + 1] = getRandomRoomWithDoor(DOOR_DOWN);
                visited[x][y + 1] = true;
                queue.add(new int[]{x, y + 1});
            }
            if (x + 1 < DUNGEON_SIZE && !visited[x + 1][y] && currentRoom.charAt(DOOR_RIGHT) == '1') {
                // 右方向 - 新房间需要左门来连接当前房间的右门
                layout[x + 1][y] = getRandomRoomWithDoor(DOOR_LEFT);
                visited[x + 1][y] = true;
                queue.add(new int[]{x + 1, y});
            }
            if (y - 1 >= 0 && !visited[x][y - 1] && currentRoom.charAt(DOOR_DOWN) == '1') {
                // 下方向 - 新房间需要上门来连接当前房间的下门
                layout[x][y - 1] = getRandomRoomWithDoor(DOOR_UP);
                visited[x][y - 1] = true;
                queue.add(new int[]{x, y - 1});
            }
            if (x - 1 >= 0 && !visited[x - 1][y] && currentRoom.charAt(DOOR_LEFT) == '1') {
                // 左方向 - 新房间需要右门来连接当前房间的左门
                layout[x - 1][y] = getRandomRoomWithDoor(DOOR_RIGHT);
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

        int nearestX = nearest[0];
        int nearestY = nearest[1];
        
        // 确定连接方向
        int dx = nearestX - x;
        int dy = nearestY - y;

        // 选择适当的房间类型并确保双向连接
        String roomType;
        if (dx == 1) {  // 目标房间在右侧，新房间需要右门
            String[] rightRooms = {"0010", "0110", "1010", "1110"};
            roomType = rightRooms[random.nextInt(rightRooms.length)];
            // 确保目标房间有左门
            layout[nearestX][nearestY] = ensureDoorAtPosition(layout[nearestX][nearestY], DOOR_LEFT);
        } else if (dx == -1) {  // 目标房间在左侧，新房间需要左门
            String[] leftRooms = {"1000", "1001", "1100", "1101"};
            roomType = leftRooms[random.nextInt(leftRooms.length)];
            // 确保目标房间有右门
            layout[nearestX][nearestY] = ensureDoorAtPosition(layout[nearestX][nearestY], DOOR_RIGHT);
        } else if (dy == 1) {  // 目标房间在上方，新房间需要上门
            String[] upRooms = {"0001", "0101", "1001", "1101"};
            roomType = upRooms[random.nextInt(upRooms.length)];
            // 确保目标房间有下门
            layout[nearestX][nearestY] = ensureDoorAtPosition(layout[nearestX][nearestY], DOOR_DOWN);
        } else if (dy == -1) {  // 目标房间在下方，新房间需要下门
            String[] downRooms = {"0100", "0110", "1100", "1110"};
            roomType = downRooms[random.nextInt(downRooms.length)];
            // 确保目标房间有上门
            layout[nearestX][nearestY] = ensureDoorAtPosition(layout[nearestX][nearestY], DOOR_UP);
        } else {
            // 不相邻的情况，不应该发生
            return;
        }

        layout[x][y] = roomType;
        visited[x][y] = true;
    }

    private int[] findNearestVisitedRoom(boolean[][] visited, int x, int y) {
        // 只检查相邻房间（曼哈顿距离为1），确保连接合理
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // 上右下左
        
        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (nx >= 0 && nx < DUNGEON_SIZE && ny >= 0 && ny < DUNGEON_SIZE && visited[nx][ny]) {
                return new int[]{nx, ny};
            }
        }
        
        // 如果没有相邻的已访问房间，找最近的
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

    /**
     * 确保房间在指定位置有门
     * @param roomType 原房间类型
     * @param doorPosition 门的位置（0=上，1=右，2=下，3=左）
     * @return 修改后的房间类型
     */
    private String ensureDoorAtPosition(String roomType, int doorPosition) {
        char[] chars = roomType.toCharArray();
        chars[doorPosition] = '1';  // 确保该位置有门
        return new String(chars);
    }

    /**
     * 获取满足条件的房间类型（确保指定方向有门）
     * @param doorDirection 需要有门的方向（0=上，1=右，2=下，3=左）
     * @return 随机选择的满足条件的房间类型
     */
    private String getRandomRoomWithDoor(int doorDirection) {
        String[] allRoomTypes = {
            "0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111",
            "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111"
        };
        
        // 筛选出在指定方向有门的房间类型
        java.util.List<String> validRooms = new java.util.ArrayList<>();
        for (String roomType : allRoomTypes) {
            if (roomType.charAt(doorDirection) == '1') {
                validRooms.add(roomType);
            }
        }
        
        if (validRooms.isEmpty()) {
            return "0000"; // 默认房间
        }
        
        return validRooms.get(random.nextInt(validRooms.size()));
    }

    public TiledMap generateMapWithSeed(long seed) {
        random.setSeed(seed);
        return generateRandomMap();
    }

    /**
     * 验证地图连接是否正确
     * @param layout 房间布局
     * @return 是否所有连接都正确
     */
    private boolean validateMapConnections(String[][] layout) {
        for (int x = 0; x < DUNGEON_SIZE; x++) {
            for (int y = 0; y < DUNGEON_SIZE; y++) {
                String currentRoom = layout[x][y];
                if (currentRoom == null) continue;

                // 检查上方连接
                if (y + 1 < DUNGEON_SIZE && layout[x][y + 1] != null) {
                    boolean currentHasUp = currentRoom.charAt(DOOR_UP) == '1';
                    boolean nextHasDown = layout[x][y + 1].charAt(DOOR_DOWN) == '1';
                    if (currentHasUp != nextHasDown) {
                        System.err.printf("连接不一致: (%d,%d) 上门=%s, (%d,%d) 下门=%s%n", 
                            x, y, currentHasUp, x, y + 1, nextHasDown);
                        return false;
                    }
                }

                // 检查右方连接
                if (x + 1 < DUNGEON_SIZE && layout[x + 1][y] != null) {
                    boolean currentHasRight = currentRoom.charAt(DOOR_RIGHT) == '1';
                    boolean nextHasLeft = layout[x + 1][y].charAt(DOOR_LEFT) == '1';
                    if (currentHasRight != nextHasLeft) {
                        System.err.printf("连接不一致: (%d,%d) 右门=%s, (%d,%d) 左门=%s%n", 
                            x, y, currentHasRight, x + 1, y, nextHasLeft);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 打印房间布局（用于调试）
     */
    private void printRoomLayout(String[][] layout) {
        System.out.println("生成的房间布局:");
        for (int y = DUNGEON_SIZE - 1; y >= 0; y--) { // 从上到下打印
            for (int x = 0; x < DUNGEON_SIZE; x++) {
                String room = layout[x][y];
                if (room != null) {
                    System.out.printf("%s ", room);
                } else {
                    System.out.print("null ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
