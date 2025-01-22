package org.depaul.gui;

import org.depaul.logic.data.ViewData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.depaul.logic.events.EventSource;
import org.depaul.logic.events.EventType;
import org.depaul.logic.events.InputEventListener;
import org.depaul.logic.events.MoveEvent;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    private MediaPlayer mediaPlayer;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Text scoreValue;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane nextBrick;

    @FXML
    private GridPane brickPanel;

    @FXML
    private ToggleButton pauseButton;

    @FXML
    private Group gameOverNotification;

    @FXML
    private Group pauseNotification;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("3X5.TTF").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();


        String musicFile = getClass().getClassLoader().getResource("tetris_theme_epic.mp3").toExternalForm();
        Media media = new Media(musicFile);
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();

        // Modify the setOnKeyPressed method
        gamePanel.setOnKeyPressed(keyEvent -> {
            if (!isPause.get() && !isGameOver.get()) {
                switch (keyEvent.getCode()) {
                    case SPACE, R -> {
                        randomMove(new MoveEvent(EventType.DROP, EventSource.USER));  // Hard drop
                        keyEvent.consume();
                    }
                    case DOWN, S -> {
                        moveDown();  // Soft drop
                        keyEvent.consume();
                    }
                    case LEFT, A -> {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    case RIGHT, D -> {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    case UP, W -> {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.UP, EventSource.USER)));
                        keyEvent.consume();
                    }
                    case C, SHIFT -> {
                        randomMove(new MoveEvent(EventType.HOLD, EventSource.USER));
                        keyEvent.consume();
                    }
                }
            }
        });

//        GAME OVER panel notification
        gameOverNotification.setVisible(false);

//        PAUSE button
        pauseButton.selectedProperty().bindBidirectional(isPause);
        pauseButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(isGameOver.get()) return;

            if (newValue) {
                timeLine.pause();
                mediaPlayer.pause();
                pauseButton.setText("Resume");
            } else {
                timeLine.play();
                mediaPlayer.play();
                pauseButton.setText("Pause");
            }
        });

//        SCORE: Setting the reflection style
        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
        scoreValue.setEffect(reflection);
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
//        displayMatrix is the GUI representation of the current state of the board currentGameMatrix
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

//        rectangles is the GUI representation of the current state of brick.
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.setLayoutX(160+gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);

        generateNextBrickPanel(brick.getNextBrickData());


        // Modify the Timeline to call moveDown instead of randomMove
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(500),
                ae -> autoMoveDown()  // Use autoMoveDown here
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }

    private Paint getFillColor(int i) {
        Paint returnPaint = switch (i) {
            case 0 -> Color.TRANSPARENT;
            case 1 -> Color.AQUA;
            case 2 -> Color.ROYALBLUE;
            case 3 -> Color.DARKORANGE;
            case 4 -> Color.GOLD;
            case 5 -> Color.LIMEGREEN;
            case 6 -> Color.SLATEBLUE;
            case 7 -> Color.RED;
            default -> Color.web("", 1);
        };
        return returnPaint;
    }

    // In the existing moveDown() method, add conditions for user-controlled drops
    private void moveDown() {
        if (!isPause.get() && !isGameOver.get()) {
            randomMove(new MoveEvent(EventType.DOWN, EventSource.USER));  // User-initiated move down
        }
    }

    // Create a new method for automatic downward movement
    private void autoMoveDown() {
        if (!isPause.get() && !isGameOver.get()) {
            randomMove(new MoveEvent(EventType.DOWN, EventSource.THREAD));  // Automated move down
        }
    }

    private void generateNextBrickPanel(int[][] nextBrickData) {
        nextBrick.getChildren().clear();
        for (int i = 0; i < nextBrickData.length; i++) {
            for (int j = 0; j < nextBrickData[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                setRectangleData(nextBrickData[i][j], rectangle);
                if (nextBrickData[i][j] != 0) {
                    nextBrick.add(rectangle, j, i);
                }
            }
        }
    }

    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            brickPanel.setLayoutX(160 + gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
            generateNextBrickPanel(brick.getNextBrickData());
        }
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    public void addBonusScore(int bonusScore){
        if (!isPause.getValue() && !isGameOver.getValue())
        {
            //  Bonus notification
            NotificationPanel notificationPanel = new NotificationPanel("+" + bonusScore);
            groupNotification.getChildren().add(notificationPanel);
            notificationPanel.showScore(groupNotification.getChildren());
        }
    }

    private void randomMove(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            ViewData viewData = eventListener.onRandomMoveEvent(event);
            refreshBrick(viewData);
        }
        gamePanel.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
        scoreValue.textProperty().bind(integerProperty.asString());
    }

    public void gameOver() {
        timeLine.stop();
        GameOverPanel gameOverPanel = new GameOverPanel("Game Over!");
        gameOverNotification.getChildren().add(gameOverPanel);
        gameOverNotification.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
    }

    public void newGame(ActionEvent actionEvent) {
        if(isPause.get()) return;

        timeLine.stop();
        gameOverNotification.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void pauseGame(ActionEvent actionEvent) {
        if(isGameOver.get()) return;

        gamePanel.requestFocus();
        // true when paused, false when playing
        PausePanel pausePanel = new PausePanel("Paused");
        pauseNotification.getChildren().add(pausePanel);
        pauseNotification.setVisible(pauseButton.isSelected());
    }
}
