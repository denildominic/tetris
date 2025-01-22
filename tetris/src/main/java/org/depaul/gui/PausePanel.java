package org.depaul.gui;

import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;


public class PausePanel extends BorderPane
{
    public PausePanel() { this("Paused");  }

    public PausePanel(String text) {
        final Label pauseLabel = new Label(text);
        pauseLabel.getStyleClass().add("pauseStyle");
        setCenter(pauseLabel);
    }

}
