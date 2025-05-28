package io.github.SimpleGame.Resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
    private static Sound[] playerHitSound;
    private static AssetManager assetManager=new AssetManager();
    public static void loadSounds() {
        playerHitSound =  new Sound[25];
       for(int i=1;i<=25;i++){
           assetManager.load("Sounds/"+i+"-2.wav", Sound.class);
       }
        assetManager.finishLoading();
       for(int i=0;i<25;i++){
           playerHitSound[i] = assetManager.get("Sounds/"+(i+1)+"-2.wav", Sound.class);
       }

    }
    public static void playSound(String soundName) {
        loadSounds();
        if (soundName.equals("playerHit")) {
//            int randomIndex = (int) (Math.random() * 25); // 使用 Math.random()
            playerHitSound[12].play(1f); // 播放速度加快为1.5倍

        }
    }
    public static void stopSound(){
        for (Sound sound : playerHitSound) {
            sound.stop();
        }
    }
    public static void dispose() {
        for (Sound sound : playerHitSound) {
            sound.dispose();
        }
        assetManager.dispose();
        playerHitSound = null;
    }
}
