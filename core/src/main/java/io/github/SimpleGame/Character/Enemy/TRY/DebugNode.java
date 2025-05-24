package io.github.SimpleGame.Character.Enemy.TRY;

public class DebugNode extends Behavior{
    private String word;
    public DebugNode(String word)
    {
        this.word = word;
    }
    @Override
    protected Status OnUpdate()
    {
        System.out.println(word);
        return Status.Success;
    }
}
