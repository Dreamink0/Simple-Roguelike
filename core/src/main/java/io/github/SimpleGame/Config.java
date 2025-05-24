package io.github.SimpleGame;

public class Config {
    // 世界配置
    public static final float WORLD_WIDTH = 20f;
    public static final float WORLD_HEIGHT = 15f;
    public static final float PIXELS_PER_METER = 64f;
    public static final float TIME_STEP = 1/60f;
    public static final float PLAYER_SCALE = 2f;

    // 资源路径
    public static final String MAP_PATH = "Maps/TestMap.tmx";
    public static final String PLAYER_ATLAS_PATH = "Sprites/BasePlayer/BasePlayer.atlas";
    public static final String POWER_ATLAS_PATH = "Magic/ElectricA.atlas";
    public static final String PLAYERATTACK_ATLAS_PATH = "Sprites/BasePlayer/raw/attack/Attack.atlas";
    public static final String LIGHTNING_MAGIC_PATH="Magic/Lightning/Lightning.atlas";
    public static final String LIGHTNING_MAGIC_ICON_PATH="Magic/Lightning/ICON.png";
    public static final String LIGHTNING_MAGIC_ICON2_PATH="Magic/Lightning/ICON2.png";
    public static final String THUNDER_STRIKE_PATH="Magic/Lightning/Thunderstrike.atlas";
    public static final String TRALALEROTRLALA_PATH="Enemy/tralalo_telala-sheet.png";
    // 动画配置
    public static final float IDLE_ANIMATION_FRAME_DURATION = 0.1f;
    public static final float FIRE_ANIMATION_FRAME_DURATION = 0.033f;
}
