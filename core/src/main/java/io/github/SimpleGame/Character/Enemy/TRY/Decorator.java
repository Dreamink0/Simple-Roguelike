package io.github.SimpleGame.Character.Enemy.TRY;

public abstract class Decorator extends Behavior{
    protected Behavior child;
    @Override
    public void AddChild(Behavior child)
    {
        this.child = child;
    }
}
