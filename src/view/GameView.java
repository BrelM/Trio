package Trio.src.view;

import Trio.src.controller.GameController;
import Trio.src.model.GameModel;

public interface GameView {

    void setController(GameController controller);

    void refresh(GameModel model);

    void showGameOver(GameModel model);
}
