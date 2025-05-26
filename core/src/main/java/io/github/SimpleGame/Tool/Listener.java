package io.github.SimpleGame.Tool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Item.Weapon;
import io.github.SimpleGame.Resource.WorldManager;

public class Listener{
    public static boolean equip;
    public static boolean LightningMagic_Flag;
    public static boolean attack_Flag;
    public static Body attack_Body;
    public static Body attacked_Body;

    public static void Bound(World world, Player player){
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();

//                System.out.println("Collision detected!");
//                System.out.println("BodyA: " + (bodyA != null ? bodyA.getUserData() : "null"));
//                System.out.println("BodyB: " + (bodyB != null ? bodyB.getUserData() : "null"));
//
//                if (bodyA == null || bodyB == null) {
//                    System.out.println("One of the bodies is null!"); // 调试信息
//                    return;
//                }

                if ((bodyA.getUserData() != null && bodyA.getUserData().equals("player") &&
                    bodyB.getUserData() != null && bodyB.getUserData().equals("weapon")) ||
                    (bodyA.getUserData() != null && bodyA.getUserData().equals("weapon") &&
                        bodyB.getUserData() != null && bodyB.getUserData().equals("player"))) {
                    Flag_equip();
                }

                if ((bodyA.getUserData() != null && bodyA.getUserData().equals("player") &&
                    bodyB.getUserData() != null && bodyB.getUserData().equals("Lightning")) ||
                    (bodyA.getUserData() != null && bodyA.getUserData().equals("Lightning") &&
                        bodyB.getUserData() != null && bodyB.getUserData().equals("player"))) {
                    Flag_LightningMagic();
                }

                if ((bodyA.getUserData() != null && bodyA.getUserData().equals("player") &&
                    bodyB.getUserData() != null && bodyB.getUserData().equals("enemy")) ||
                    (bodyA.getUserData() != null && bodyA.getUserData().equals("enemy") &&
                        bodyB.getUserData() != null && bodyB.getUserData().equals("enemy"))) {
                    //这个时候已经发生了碰撞，攻击造成伤害应该要是在这里发生
                    Flag_attack();
                }
            }
            @Override
            public void endContact(Contact contact) {}

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}
        });
    }
    private static void Flag_equip() {equip=true;}
    private static void Flag_LightningMagic() {LightningMagic_Flag=true;}
    private static void  Flag_attack(){attack_Flag=true;};
}
