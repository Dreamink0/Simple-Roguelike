package io.github.SimpleGame.Character.Enemy.TRY;

public class Inverter extends Decorator{
    @Override
    protected Status OnUpdate() {
        Status status = child.Tick();
        if(status == Status.Success)
            return Status.Failure;
        else if(status == Status.Failure)
            return Status.Success;
        else
            return Status.Running;
    }
}
