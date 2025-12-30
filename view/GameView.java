package Trio.view;

import Trio.controller.GameController;
import Trio.model.GameModel;

public interface GameView {

    void setController(GameController controller);

    void refresh(GameModel model);

    void showGameOver(GameModel model);
}
