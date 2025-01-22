package org.depaul.logic.events;

import org.depaul.gui.GuiController;
import org.depaul.logic.board.Board;
import org.depaul.logic.board.SimpleBoard;
import org.depaul.logic.bricks.Brick;
import org.depaul.logic.data.ViewData;

import java.util.concurrent.ThreadLocalRandom;

public class GameController implements InputEventListener {

    private final Board board = new SimpleBoard(25, 10);
    private final GuiController viewGuiController;

    private boolean holdingBrick = false;
    private Brick heldBrick = null;
    private boolean canUseHold = true;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
    }

    @Override
    public ViewData onRandomMoveEvent(MoveEvent event) {
        boolean movable = board.moveBrickRandom();
        if (!movable) {
            board.mergeBrickToBackground();
            board.getScore().add(5);
            canUseHold = true;
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        } else {
            if (event.getEventSource() == EventSource.USER) {
                switch (event.getEventType()) {
                    case LEFT:
                        board.moveBrickLeft();
                        break;
                    case RIGHT:
                        board.moveBrickRight();
                        break;
                    case DOWN:
                        board.moveBrickDown();
                        break;
                    case UP:
                        board.rotateBrick();
                        break;
                    case DROP:
                        board.instantDropBrick();
                        int yPos = 20;
                        int bonusScore = yPos;
                        viewGuiController.addBonusScore(bonusScore);
                        board.getScore().add(bonusScore);
                        break;
                    case HOLD:
                        holdBrick();
                        break;
                }
            }
        }
        return board.getViewData();
    }

    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event){
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event){
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event){
        board.rotateBrick();
        return board.getViewData();
    }

    private void holdBrick()
    {
        if(!canUseHold) return;

        if(!holdingBrick)
        {
            heldBrick = board.getBrickRotator().getBrick();
            board.createNewBrick();
            holdingBrick = true;
        }
        else
        {
            Brick stored = board.getBrickRotator().getBrick();
            board.getBrickRotator().setBrick(heldBrick);
            heldBrick = stored;
            canUseHold = false;
        }
    }
}
