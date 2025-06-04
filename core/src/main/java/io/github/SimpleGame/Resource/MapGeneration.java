package io.github.SimpleGame.Resource;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.ObjectMap;

public class MapGeneration {
    private static final int ROOM_WIDTH = 20;
    private static final int ROOM_HEIGHT = 12;
    private static final int DUNGEON_SIZE = 3;
    private static final int TILE_SIZE = 24;

    // 上(0)、下(1)、左(2)、右(3)
    private static final int DOOR_UP = 0;
    private static final int DOOR_DOWN = 1;
    private static final int DOOR_LEFT = 2;
    private static final int DOOR_RIGHT = 3;

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
        dungeonMap = new TiledMap();

        MapProperties properties = dungeonMap.getProperties();
        properties.put("tilewidth", TILE_SIZE);
        properties.put("tileheight", TILE_SIZE);
        properties.put("width", DUNGEON_SIZE * ROOM_WIDTH);
        properties.put("height", DUNGEON_SIZE * ROOM_HEIGHT);

        TiledMapTileLayer groundLayer = new TiledMapTileLayer(DUNGEON_SIZE * ROOM_WIDTH, DUNGEON_SIZE * ROOM_HEIGHT, TILE_SIZE, TILE_SIZE);
        groundLayer.setName("Layer 1");
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(DUNGEON_SIZE * ROOM_WIDTH, DUNGEON_SIZE * ROOM_HEIGHT, TILE_SIZE, TILE_SIZE);
        wallLayer.setName("Wall");
        wallLayer.setOpacity(0f);

        String[][] roomLayout = generateConnectedRoomLayout();

        printRoomLayout(roomLayout);

        if (!validateMapConnections(roomLayout)) {
            System.err.println("ValidateMapConnectionsFailed");
        }

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

        layout[centerX][centerY] = getValidCenterRoom(centerX, centerY);
        visited[centerX][centerY] = true;

        // BFS确保连通性
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{centerX, centerY});

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];
            String currentRoom = layout[x][y];

            // 检查四个方向
            if (y + 1 < DUNGEON_SIZE && !visited[x][y + 1] && currentRoom.charAt(DOOR_UP) == '1') {
                layout[x][y + 1] = getRandomRoomWithDoor(DOOR_DOWN, x, y + 1);
                visited[x][y + 1] = true;
                queue.add(new int[]{x, y + 1});
            }
            if (x + 1 < DUNGEON_SIZE && !visited[x + 1][y] && currentRoom.charAt(DOOR_RIGHT) == '1') {
                layout[x + 1][y] = getRandomRoomWithDoor(DOOR_LEFT, x + 1, y);
                visited[x + 1][y] = true;
                queue.add(new int[]{x + 1, y});
            }
            if (y - 1 >= 0 && !visited[x][y - 1] && currentRoom.charAt(DOOR_DOWN) == '1') {
                layout[x][y - 1] = getRandomRoomWithDoor(DOOR_UP, x, y - 1);
                visited[x][y - 1] = true;
                queue.add(new int[]{x, y - 1});
            }
            if (x - 1 >= 0 && !visited[x - 1][y] && currentRoom.charAt(DOOR_LEFT) == '1') {
                layout[x - 1][y] = getRandomRoomWithDoor(DOOR_RIGHT, x - 1, y);
                visited[x - 1][y] = true;
                queue.add(new int[]{x - 1, y});
            }
        }

        // 确保所有房间都被访问
        for (int x = 0; x < DUNGEON_SIZE; x++) {
            for (int y = 0; y < DUNGEON_SIZE; y++) {
                if (!visited[x][y]) {
                    connectToNearestRoom(layout, visited, x, y);
                }
            }
        }

        // 确保边界房间完全封闭
        ensureBoundaryRoomsClosedWithFix(layout);

        return layout;
    }

    private void connectToNearestRoom(String[][] layout, boolean[][] visited, int x, int y) {
        int[] nearest = findNearestVisitedRoom(visited, x, y);
        if (nearest == null) return;

        int nearestX = nearest[0];
        int nearestY = nearest[1];

        // 确定连接方向
        int dx = nearestX - x;
        int dy = nearestY - y;

        String roomType;
        if (dx == 1) {
            String[] rightRooms = {"0001", "0011", "0101", "0111", "1001", "1011", "1101", "1111"};
            roomType = rightRooms[random.nextInt(rightRooms.length)];
            layout[nearestX][nearestY] = ensureDoorAtPosition(layout[nearestX][nearestY], DOOR_LEFT);
        } else if (dx == -1) {
            String[] leftRooms = {"0010", "0011", "0110", "0111", "1010", "1011", "1110", "1111"};
            roomType = leftRooms[random.nextInt(leftRooms.length)];
            layout[nearestX][nearestY] = ensureDoorAtPosition(layout[nearestX][nearestY], DOOR_RIGHT);
        } else if (dy == 1) {
            String[] upRooms = {"1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"};
            roomType = upRooms[random.nextInt(upRooms.length)];
            layout[nearestX][nearestY] = ensureDoorAtPosition(layout[nearestX][nearestY], DOOR_DOWN);
        } else if (dy == -1) {
            String[] downRooms = {"0100", "0101", "0110", "0111", "1100", "1101", "1110", "1111"};
            roomType = downRooms[random.nextInt(downRooms.length)];
            layout[nearestX][nearestY] = ensureDoorAtPosition(layout[nearestX][nearestY], DOOR_UP);
        } else {
            // 不相邻
            return;
        }

        layout[x][y] = roomType;
        visited[x][y] = true;
    }

    private int[] findNearestVisitedRoom(boolean[][] visited, int x, int y) {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (nx >= 0 && nx < DUNGEON_SIZE && ny >= 0 && ny < DUNGEON_SIZE && visited[nx][ny]) {
                return new int[]{nx, ny};
            }
        }

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

    private String ensureDoorAtPosition(String roomType, int doorPosition) {
        char[] chars = roomType.toCharArray();
        chars[doorPosition] = '1';
        return new String(chars);
    }

    private String getRandomRoomWithDoor(int doorDirection, int x, int y) {
        String[] allRoomTypes = {
            "0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111",
            "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111"
        };

        java.util.List<String> validRooms = new java.util.ArrayList<>();
        for (String roomType : allRoomTypes) {
            if (roomType.charAt(doorDirection) == '1' && isValidRoomForPosition(roomType, x, y)) {
                validRooms.add(roomType);
            }
        }

        if (validRooms.isEmpty()) {
            return generateClosedRoomForPosition(x, y);
        }

        return validRooms.get(random.nextInt(validRooms.size()));
    }

    public TiledMap generateMapWithSeed(long seed) {
        random.setSeed(seed);
        return generateRandomMap();
    }

    private boolean validateMapConnections(String[][] layout) {
        for (int x = 0; x < DUNGEON_SIZE; x++) {
            for (int y = 0; y < DUNGEON_SIZE; y++) {
                String currentRoom = layout[x][y];
                if (currentRoom == null) continue;

                if (y + 1 < DUNGEON_SIZE && layout[x][y + 1] != null) {
                    boolean currentHasUp = currentRoom.charAt(DOOR_UP) == '1';
                    boolean nextHasDown = layout[x][y + 1].charAt(DOOR_DOWN) == '1';
                    if (currentHasUp != nextHasDown) {
                        return false;
                    }
                }

                if (x + 1 < DUNGEON_SIZE && layout[x + 1][y] != null) {
                    boolean currentHasRight = currentRoom.charAt(DOOR_RIGHT) == '1';
                    boolean nextHasLeft = layout[x + 1][y].charAt(DOOR_LEFT) == '1';
                    if (currentHasRight != nextHasLeft) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void printRoomLayout(String[][] layout) {
        System.out.println("Rooms:");
        for (int y = DUNGEON_SIZE - 1; y >= 0; y--) {
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

    private boolean isValidRoomForPosition(String roomType, int x, int y) {
        if (y == DUNGEON_SIZE - 1 && roomType.charAt(DOOR_UP) == '1') {
            return false;
        }

        if (x == DUNGEON_SIZE - 1 && roomType.charAt(DOOR_RIGHT) == '1') {
            return false;
        }

        if (y == 0 && roomType.charAt(DOOR_DOWN) == '1') {
            return false;
        }

        if (x == 0 && roomType.charAt(DOOR_LEFT) == '1') {
            return false;
        }

        return true;
    }

    private String generateClosedRoomForPosition(int x, int y) {
        return "0000";
    }

    private String getValidCenterRoom(int x, int y) {
        String[] centerRoomTypes = {"0111", "1011", "1101", "1110", "1111"};

        // 筛选出符合边界限制的房间类型
        java.util.List<String> validCenterRooms = new java.util.ArrayList<>();
        for (String roomType : centerRoomTypes) {
            if (isValidRoomForPosition(roomType, x, y)) {
                validCenterRooms.add(roomType);
            }
        }

        // 如果没有合适的多出口房间，选择基础房间
        if (validCenterRooms.isEmpty()) {
            return generateClosedRoomForPosition(x, y);
        }

        return validCenterRooms.get(random.nextInt(validCenterRooms.size()));
    }

    private void ensureBoundaryRoomsClosedWithFix(String[][] layout) {
        System.out.println("Fixup Room Layout");
        for (int x = 0; x < DUNGEON_SIZE; x++) {
            for (int y = 0; y < DUNGEON_SIZE; y++) {
                if (layout[x][y] != null) {
                    char[] roomChars = layout[x][y].toCharArray();
                    String originalRoom = layout[x][y];
                    boolean modified = false;

                    if (y == DUNGEON_SIZE - 1 && roomChars[DOOR_UP] == '1') {
                        roomChars[DOOR_UP] = '0';
                        modified = true;
                    }

                    if (x == DUNGEON_SIZE - 1 && roomChars[DOOR_RIGHT] == '1') {
                        roomChars[DOOR_RIGHT] = '0';
                        modified = true;
                    }

                    if (y == 0 && roomChars[DOOR_DOWN] == '1') {
                        roomChars[DOOR_DOWN] = '0';
                        modified = true;
                    }

                    if (x == 0 && roomChars[DOOR_LEFT] == '1') {
                        roomChars[DOOR_LEFT] = '0';
                        modified = true;
                    }

                    if (modified) {
                        layout[x][y] = new String(roomChars);
                        System.out.printf("Side (%d,%d): %s -> %s%n", x, y, originalRoom, layout[x][y]);

                        fixAdjacentRoomConnections(layout, x, y);
                    }
                }
            }
        }
        System.out.println("Completed.");
    }

    private void fixAdjacentRoomConnections(String[][] layout, int x, int y) {
        String currentRoom = layout[x][y];
        if (currentRoom == null) return;

        if (y + 1 < DUNGEON_SIZE && layout[x][y + 1] != null) {
            boolean currentHasUp = currentRoom.charAt(DOOR_UP) == '1';
            char[] adjacentChars = layout[x][y + 1].toCharArray();
            if (currentHasUp) {
                // 确保相邻房间有对应的下门
                adjacentChars[DOOR_DOWN] = '1';
            } else {
                // 移除相邻房间的下门
                adjacentChars[DOOR_DOWN] = '0';
            }
            layout[x][y + 1] = new String(adjacentChars);
        }

        if (x + 1 < DUNGEON_SIZE && layout[x + 1][y] != null) {
            boolean currentHasRight = currentRoom.charAt(DOOR_RIGHT) == '1';
            char[] adjacentChars = layout[x + 1][y].toCharArray();
            if (currentHasRight) {
                // 确保相邻房间有对应的左门
                adjacentChars[DOOR_LEFT] = '1';
            } else {
                // 移除相邻房间的左门
                adjacentChars[DOOR_LEFT] = '0';
            }
            layout[x + 1][y] = new String(adjacentChars);
        }

        if (y - 1 >= 0 && layout[x][y - 1] != null) {
            boolean currentHasDown = currentRoom.charAt(DOOR_DOWN) == '1';
            char[] adjacentChars = layout[x][y - 1].toCharArray();
            if (currentHasDown) {
                // 确保相邻房间有对应的上门
                adjacentChars[DOOR_UP] = '1';
            } else {
                // 移除相邻房间的上门
                adjacentChars[DOOR_UP] = '0';
            }
            layout[x][y - 1] = new String(adjacentChars);
        }

        if (x - 1 >= 0 && layout[x - 1][y] != null) {
            boolean currentHasLeft = currentRoom.charAt(DOOR_LEFT) == '1';
            char[] adjacentChars = layout[x - 1][y].toCharArray();
            if (currentHasLeft) {
                // 确保相邻房间有对应的右门
                adjacentChars[DOOR_RIGHT] = '1';
            } else {
                // 移除相邻房间的右门
                adjacentChars[DOOR_RIGHT] = '0';
            }
            layout[x - 1][y] = new String(adjacentChars);
        }
    }
}
