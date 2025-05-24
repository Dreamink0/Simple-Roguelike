package io.github.SimpleGame.Character.Enemy.TRY;

public class Selector extends Sequence{
    @Override
    protected Status OnUpdate() {
        while (true) {
            var s = currentChild.get(currentChildIndex).Tick();
            if(s != Status.Failure){
                return s;
            }
            currentChildIndex++;
            if (currentChildIndex >= currentChild.size()) {
                return Status.Failure;
            }
        }
      }
}
