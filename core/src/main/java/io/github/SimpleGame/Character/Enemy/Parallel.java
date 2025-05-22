package io.github.SimpleGame.Character.Enemy;


public class Parallel extends Composite{
    public Policy SuccessPolicy;
    public Policy FailurePolicy;
    public enum Policy{
        RequireOne,
        RequireAll,
    }
    public Parallel(Policy successPolicy,Policy failurePolicy){
        SuccessPolicy = successPolicy;
        FailurePolicy = failurePolicy;
    }
    @Override
    protected Status OnUpdate() {
        int successCount = 0;
        int failureCount = 0;
        int size = children.size();
        for (int i = 0; i < size; i++) {
            var bh = children.get(i);//从头开始遍历
            if (!bh.isTerminated()) bh.Tick();

            if (bh.isSuccess()) {++successCount;
                if (SuccessPolicy == Policy.RequireOne) return Status.Success;
            }
            if (bh.isFailure()) {++failureCount;
                if (FailurePolicy == Policy.RequireOne) return Status.Failure;
            }
        }
        if (SuccessPolicy == Policy.RequireAll && failureCount == size) return Status.Failure;
        if (FailurePolicy == Policy.RequireAll && successCount == size) return Status.Success;
        return Status.Running;
    }
    //结束函数，把所有子节点设为中断
    @Override
    protected void OnTerminate() {
        for (var b : children) {
            if (!b.isTerminated()) b.Abort();
        }
    }
}
