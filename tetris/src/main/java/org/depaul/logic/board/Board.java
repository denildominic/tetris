package org.depaul.logic.board;

import org.depaul.logic.data.Score;
import org.depaul.logic.data.ViewData;
import org.depaul.logic.rotator.BrickRotator;

public interface Board {

    boolean moveBrickRandom();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    Score getScore();

    void newGame();

    void moveBrickLeft();

    void moveBrickRight();

    void moveBrickDown();

    void rotateBrick();

    void dropMatrix();
    void instantDropBrick();

    BrickRotator getBrickRotator();

    boolean detectAndClearFullRows();

}
