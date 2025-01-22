package org.depaul.logic.rotator;

import org.depaul.logic.bricks.Brick;

public class BrickRotator {

    private Brick brick;
    private int currentShapeIndex = 0;
    private int nextShapeIndex;

    public int[][] getNextShapeMatrix() {
        int nextShapeIndex = (currentShapeIndex + 1) % brick.getBrickMatrixList().size();
        return brick.getBrickMatrixList().get(nextShapeIndex);
    }

    public int[][] getCurrentShapeMatrix() {
        return brick.getBrickMatrixList().get(currentShapeIndex);
    }

    public int getCurrentShapeIndex() {
        return currentShapeIndex;
    }

    public int getNextShapeIndex() {
        return nextShapeIndex;
    }

    public void setCurrentShapeIndex(int currentShapeIndex) {
        this.currentShapeIndex = currentShapeIndex;
    }

    public void rotate(){
        currentShapeIndex = (++currentShapeIndex) % brick.getBrickMatrixList().size();
    }


    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShapeIndex = 0;
    }

    public Brick getBrick()
    {
        return brick;
    }


    public boolean canRotate(int[][] grid, int x, int y) {
        int[][] nextShape = getNextShapeMatrix();

        for (int row = 0; row < nextShape.length; row++) {
            for (int col = 0; col < nextShape[row].length; col++) {
                if (nextShape[row][col] != 0) {  // Check only occupied cells
                    int newX = x + col;
                    int newY = y + row;

                    if (newX < 0 || newX >= grid[0].length || newY < 0 || newY >= grid.length) {
                        return false;
                    }

                    if (grid[newY][newX] != 0) {
                        return false;
                    }
                }
            }
        }

        return true;
    }




}
