package io.github.SimpleGame.Character.Enemy.TRY;

public class Repeat extends Decorator{
    public int counter;
    public int limit;
    public Repeat(int limit){
        this.limit = limit;
    }
    @Override
    protected void OnInitialize() {
        counter = 0;
    }
    @Override
    protected Status OnUpdate() {
        while(true){
            child.Tick();
            if(child.isRunning()){
                return Status.Running;
            }
            if(child.isFailure()){
                return Status.Failure;
            }
            if(++counter>=limit){
                return Status.Success;
            }
        }
    }
}
