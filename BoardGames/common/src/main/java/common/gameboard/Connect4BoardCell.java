package common.gameboard;

import javax.json.bind.annotation.JsonbProperty;

public class Connect4BoardCell extends GameBoardCell
{

    private static final long serialVersionUID = -1628400555396344118L;

    public enum CellColor
    {
        TRANSPARENT, //no moves for current cell
        YELLOW,
        RED
    }
    
    @JsonbProperty(value = "cellColor")
    private CellColor color;
    
    @SuppressWarnings("unused") //used by json convertor
    public Connect4BoardCell() {}

    public Connect4BoardCell(int row, int column) {
        super(row, column);
        this.color = CellColor.TRANSPARENT;
    }

    public Connect4BoardCell(int row, int column, CellColor color) {
        super(row, column);
        this.color = color;
    }
    
    @SuppressWarnings("unused") //used by json convertor
    public void setColor(CellColor color) {
        this.color = color;
    }

    public CellColor getColor() {
        return color;
    }
}
