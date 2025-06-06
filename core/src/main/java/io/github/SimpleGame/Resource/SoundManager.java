package io.github.SimpleGame.Resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
    private static Sound[] playerHitSound;
    private static Sound deadSound;
    private static final AssetManager assetManager=new AssetManager();
    public static final void loadSounds() {
        playerHitSound =  new Sound[25];
       for(int i=1;i<=25;i++){
           assetManager.load("Sounds/"+i+"-2.wav", Sound.class);
       }
       assetManager.load("Sounds/dead.wav", Sound.class);
        assetManager.finishLoading();
       for(int i=0;i<25;i++){
           playerHitSound[i] = assetManager.get("Sounds/"+(i+1)+"-2.wav", Sound.class);
       }
       deadSound = assetManager.get("Sounds/dead.wav", Sound.class);
    }
    public static void playSound(String soundName) {
        loadSounds();
        if (soundName.equals("playerHit")) {
            playerHitSound[12].play(1f);
        }
        if(soundName.equals("enemyHit")){
            playerHitSound[16].play(1f);
            playerHitSound[24].play();
        }
        if(soundName.equals("dead")){
            deadSound.play(0.5f); // 1f 表示最大音量，可以根据需要调整此值（0.5f 为半音量）
        }
    }
    public static void stopSound(){
        for (Sound sound : playerHitSound) {
            sound.stop();
        }
    }
    public static void dispose() {
        if(playerHitSound!=null) {
            for (Sound sound : playerHitSound) {
                sound.dispose();
            }
        }
        assetManager.dispose();
        playerHitSound = null;
    }
}
