package org.depaul.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Collections;

public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;

    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new LBrick());
        brickList.add(new JBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());

        refillBag(); // Fill the first bag initially
    }

    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 1) {
            refillBag();
        }
        return nextBricks.poll();
    }

    @Override
    public Brick getNextBrick() {
        if(nextBricks.isEmpty()){
            refillBag();
        }
        return nextBricks.peek();
    }

    // Helper method to refill the bag with a shuffled set of bricks
    private void refillBag(){
        List<Brick> newBag = new ArrayList<>(brickList);
        Collections.shuffle(newBag); // Shuffle the list to randomize the order
        nextBricks.addAll(newBag);
    }
}
