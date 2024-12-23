package me.julionxn.modpackbundler.app;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public abstract class BaseItem {

    private static final Color DARK_GRAY_COLOR = new Color(0.2274509803921569f, 0.2784313725490196f, 0.3137254901960784f, 1f);
    private static final Color LIGHT_GRAY_COLOR =  new Color(0.2980392156862745f, 0.3607843137254902f, 0.403921568627451f, 1f);
    private static final Color ACCENT_COLOR = new Color(0.7450980392156863f, 0.192156862745098f, 0.2666666666666667f, 1f);
    protected final StackPane stackPane;
    protected final Rectangle rectangle;

    public BaseItem(String name){
        this.rectangle = getRectangle();
        this.stackPane = getStackPane(name);
    }

    private StackPane getStackPane(String name) {
        Label label = getLabel(name);
        StackPane stackPane = new StackPane(rectangle, label);
        stackPane.setOnMouseClicked(this::onClick);
        stackPane.setOnMouseEntered(event -> setHovered(true));
        stackPane.setOnMouseExited(event -> setHovered(false));
        return stackPane;
    }

    protected abstract void onClick(MouseEvent event);

    private Rectangle getRectangle(){
        Rectangle rect = new Rectangle();
        rect.setWidth(60);
        rect.setHeight(90);
        rect.setFill(DARK_GRAY_COLOR);
        return rect;
    }

    private Label getLabel(String text){
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        return label;
    }

    public void setActive(boolean active){
        if (active){
            rectangle.setStroke(ACCENT_COLOR);
            rectangle.setStrokeWidth(2);
        } else {
            rectangle.setStroke(Color.TRANSPARENT);
            rectangle.setStrokeWidth(0);
        }
    }

    public void setHovered(boolean hovered){
        if (hovered){
            rectangle.setFill(LIGHT_GRAY_COLOR);
        } else {
            rectangle.setFill(DARK_GRAY_COLOR);
        }
    }


}
