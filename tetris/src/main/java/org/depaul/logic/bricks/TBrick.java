package org.depaul.logic.bricks;

import org.depaul.logic.util.Operations;

import java.util.ArrayList;
import java.util.List;

final class TBrick implements Brick {

    private final List<int[][]> brickMatrixList = new ArrayList<>();

    public TBrick() {
        brickMatrixList.add(new int[][]{
                {0, 0, 6, 0},
                {0, 6, 6, 6},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrixList.add(new int[][]{
                {0, 0, 6, 0},
                {0, 0, 6, 6},
                {0, 0, 6, 0},
                {0, 0, 0, 0}
        });
        brickMatrixList.add(new int[][]{
                {0, 0, 0, 0},
                {0, 6, 6, 6},
                {0, 0, 6, 0},
                {0, 0, 0, 0}
        });
        brickMatrixList.add(new int[][]{
                {0, 0, 6, 0},
                {0, 6, 6, 0},
                {0, 0, 6, 0},
                {0, 0, 0, 0}
        });
        
    }

    @Override
    public List<int[][]> getBrickMatrixList() {
        return Operations.deepCopyList(brickMatrixList);
    }
}
