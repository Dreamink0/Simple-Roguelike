package io.github.SimpleGame;

public class Config {
    // 世界配置
    public static final float WORLD_WIDTH = 20f;
    public static final float WORLD_HEIGHT = 15f;
    public static final float PIXELS_PER_METER = 64f;
    public static final float TIME_STEP = 1/60f;
    public static final float PLAYER_SCALE = 8f;

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
    public static final String HP4_PATH="UI/HP/HP4.png";
    public static final String HP3_PATH="UI/HP/HP3.png";
    public static final String HP2_PATH="UI/HP/HP2.png";
    public static final String HP1_PATH="UI/HP/HP1.png";
    public static final String MP4_PATH="UI/MP/MP4.png";
    public static final String MP3_PATH="UI/MP/MP3.png";
    public static final String MP2_PATH="UI/MP/MP2.png";
    public static final String MP1_PATH="UI/MP/MP1.png";
    public static final String DEF4_PATH="UI/DEF/DEF4.png";
    public static final String DEF3_PATH="UI/DEF/DEF3.png";
    public static final String DEF2_PATH="UI/DEF/DEF2.png";
    public static final String DEF1_PATH="UI/DEF/DEF1.png";
    // 动画配置
    public static final float IDLE_ANIMATION_FRAME_DURATION = 0.1f;
    public static final float FIRE_ANIMATION_FRAME_DURATION = 0.033f;
    public static final short BIT_MAGIC = (short) 2f;
    public static final short BIT_ENEMY = (short) 2f;
    public static final boolean DEBUG_PHYSICS = true;
}
