package client;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Connect4Disk extends Circle {
    private final Circle preview; //preview of the disk will be shown before the disk dropped or may be dropped to potential cell
    private final int row;
    private final int column;

    public Connect4Disk(double radius, int row, int column, SimpleObjectProperty<Color> playerColorProperty)
    {
        super(radius);
        this.row = row;
        this.column = column;

        DropShadow diskShadow = new DropShadow();
        diskShadow.setColor(Color.LIGHTGRAY);
        setEffect(diskShadow);
        fillProperty().bind(playerColorProperty);

        this.preview = new Circle(radius);
        this.preview.setFill(Color.TRANSPARENT);
        this.preview.setOpacity(.4); //make disk preview a little transparent
    }

    public Circle getPreview() {
        return preview;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

}