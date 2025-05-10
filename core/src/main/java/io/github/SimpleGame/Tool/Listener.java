package io.github.SimpleGame.Tool;

import com.badlogic.gdx.physics.box2d.*;
import io.github.SimpleGame.Character.Player.Player;
import io.github.SimpleGame.Item.Weapon;

public class Listener {
    public static void Bound(World world){
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();

                System.out.println("Collision detected!");
                System.out.println("BodyA: " + (bodyA != null ? bodyA.getUserData() : "null"));
                System.out.println("BodyB: " + (bodyB != null ? bodyB.getUserData() : "null"));

                if (bodyA == null || bodyB == null) {
                    System.out.println("One of the bodies is null!"); // 调试信息
                    return;
                }

                if ((bodyA.getUserData() != null && bodyA.getUserData().equals("player") &&
                    bodyB.getUserData() != null && bodyB.getUserData().equals("weapon")) ||
                    (bodyA.getUserData() != null && bodyA.getUserData().equals("weapon") &&
                        bodyB.getUserData() != null && bodyB.getUserData().equals("player"))) {

                    System.out.println("The player collided with the weapon!"); // 调试信息
                }

                if ((bodyA.getUserData() != null && bodyA.getUserData().equals("player") &&
                        bodyB.getUserData() != null && bodyB.getUserData().equals("wall")) ||
                        (bodyA.getUserData() != null && bodyA.getUserData().equals("wall") &&
                                bodyB.getUserData() != null && bodyB.getUserData().equals("player"))) {

                    System.out.println("The player collided with the wall!"); // 调试信息
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
    public static void Weapon_logic(Player player,Weapon weapon){
        System.out.println("拾取成功");
    }
}
