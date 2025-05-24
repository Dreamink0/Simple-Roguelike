package io.github.SimpleGame.Character.Enemy.TRY;

public class BehaviorTree {
    private Behavior root;
    public boolean HaveRoot(){return root != null;};
    public BehaviorTree(Behavior root){
        this.root = root;
    }
    public void Tick(){
        root.Tick();
    }
    public void SetRoot(Behavior root){
        this.root = root;
    }

}
