package org.depaul.logic.board;

import org.depaul.logic.bricks.Brick;
import org.depaul.logic.bricks.BrickGenerator;
import org.depaul.logic.bricks.RandomBrickGenerator;
import org.depaul.logic.data.Score;
import org.depaul.logic.data.ViewData;
import org.depaul.logic.rotator.BrickRotator;
import org.depaul.logic.util.Operations;

import java.awt.*;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    @Override
    public boolean moveBrickRandom(){
//        current matrix state
        int[][] currentMatrix = Operations.copyMatrix(currentGameMatrix);

        int[][] currentBrickMatrix = brickRotator.getCurrentShapeMatrix();

        // Define a new point, moving one step down on the Y-axis
        Point p = new Point(currentOffset);
        p.translate(0, 1); // Move down by one step

        // Check for conflicts at the new position
        boolean conflict = Operations.intersectMatrix(currentMatrix, currentBrickMatrix, (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false; // Cannot move
        } else {
            currentOffset = p; // Update position
            return true;
        }
    }


    @Override
    public void moveBrickLeft() {
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        if (!Operations.intersectMatrix(currentGameMatrix, brickRotator.getCurrentShapeMatrix(), (int) p.getX(), (int) p.getY())) {
            currentOffset = p;
        }
    }

    @Override
    public void moveBrickRight() {
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        if (!Operations.intersectMatrix(currentGameMatrix, brickRotator.getCurrentShapeMatrix(), (int) p.getX(), (int) p.getY())) {
            currentOffset = p;
        }
    }

    @Override
    public void moveBrickDown() {
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        if (!Operations.intersectMatrix(currentGameMatrix, brickRotator.getCurrentShapeMatrix(), (int) p.getX(), (int) p.getY())) {
            currentOffset = p;
        }
    }

    public void instantDropBrick() {
        while (moveBrickRandom());

    }

    @Override
    public void rotateBrick() {
        int[][] nextShape = brickRotator.getNextShapeMatrix();
        int originalX = (int) currentOffset.getX();
        int originalY = (int) currentOffset.getY();

        if (!Operations.intersectMatrix(currentGameMatrix, nextShape, originalX, originalY)) {
            brickRotator.rotate();
            return;
        }

        Point[] adjustment = {
                new Point(-1, 0),
                new Point(1, 0),
                new Point(0, -1)
        };

        for (Point p : adjustment) {
            int adjustedX = originalX + p.x;
            int adjustedY = originalY + p.y;

            if (!Operations.intersectMatrix(currentGameMatrix, nextShape, adjustedX, adjustedY)) {
                brickRotator.rotate();
                currentOffset.translate((int )p.getX(), (int) p.getY());
                return;
            }
        }

    }


    public void dropMatrix() {
        int targetRow = currentGameMatrix.length - 1;

        for (int i = currentGameMatrix.length - 1; i >= 0; i--) {
            if (!Arrays.stream(currentGameMatrix[i]).allMatch(value -> value == 0)) {
                if (i != targetRow) {
                    currentGameMatrix[targetRow] = currentGameMatrix[i];
                    currentGameMatrix[i] = new int[currentGameMatrix[i].length]; // Clear the moved row
                }
                targetRow--;
            }
        }
        this.getScore().add(50);
    }




    public boolean detectAndClearFullRows() {

        Set<Integer> fullRows = new java.util.HashSet<>(); ;
        boolean rowCleared = false;

        for (int y = 0; y < currentGameMatrix.length; y++) {
            int row[] = currentGameMatrix[y];
            if(Arrays.stream(row).allMatch(value -> value != 0)){
                if (fullRows.contains(y) == false) {
                    fullRows.add(y);
                }
            }
        }

        if(fullRows.size() >0){
            for(int rowIndex: fullRows){
                Arrays.fill(currentGameMatrix[rowIndex], 0);
            }
            rowCleared = true;
        }

        return rowCleared;
    }

    @Override
    public BrickRotator getBrickRotator()
    {
        return brickRotator;
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(3, 0);
        return Operations.intersectMatrix(currentGameMatrix, brickRotator.getCurrentShapeMatrix(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        return new ViewData(brickRotator.getCurrentShapeMatrix(), (int) currentOffset.getX(), (int) currentOffset.getY(), brickGenerator.getNextBrick().getBrickMatrixList().get(0));
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = Operations.mergeMatrix(currentGameMatrix, brickRotator.getCurrentShapeMatrix(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if(this.detectAndClearFullRows()){
            this.dropMatrix();
        }
    }

    @Override
    public Score getScore() {
        return score;
    }


    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        createNewBrick();
    }
}
