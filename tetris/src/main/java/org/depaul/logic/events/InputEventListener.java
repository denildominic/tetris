package org.depaul.logic.events;

import org.depaul.logic.data.ViewData;

public interface InputEventListener {

    ViewData onRandomMoveEvent(MoveEvent event);

    void createNewGame();

    ViewData onLeftEvent(MoveEvent event);

    ViewData onRightEvent(MoveEvent event);

    ViewData onRotateEvent(MoveEvent event);


}
