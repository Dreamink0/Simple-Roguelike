package io.github.SimpleGame.Character.Enemy.TRY;

public abstract class Behavior {
    public enum Status{
        Failure, Success,
        Running, Aborted,
        Invalid
    }
    public boolean isTerminated() { return status == Status.Aborted; }
    public boolean isSuccess() { return status == Status.Success; }
    public boolean isFailure() { return status == Status.Failure; }
    public boolean isRunning() { return status == Status.Running; }

    protected Status status; //运行状态

    public Behavior(){
        status = Status.Invalid;
    }

    //当进入该节点时才会执行的函数
    protected void OnInitialize(){};

    //该节点的运行逻辑
    protected abstract Status OnUpdate();

    //当退出该节点时才会执行的函数
    protected void OnTerminate(){}

    //运行该节点,该函数会一直运行直到返回成功或者失败
    public Status Tick(){
        if(!isRunning()) OnInitialize();
        status = OnUpdate();
        if(!isRunning()) OnTerminate();
        return status;
    }

    // 添加子节点
    public void AddChild(Behavior child) {}

    //重置该节点
    public void Reset(){
        status = Status.Invalid;
    }

    //强制打断该节点
    public void Abort(){
        OnTerminate();
        status = Status.Aborted;
    }

}
