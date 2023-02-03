package common.gameboard;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;

public class GameBoardCell implements Serializable
{
    private static final long serialVersionUID = -1818635003623489510L;

    @JsonbProperty(value = "row")
    private int row;

    @JsonbProperty(value = "column")
    private int column;

    public GameBoardCell() {
    }

    public GameBoardCell(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }
    @SuppressWarnings("unused") //used by json convertor
    public void setColumn(int column) {
        this.column = column;
    }

}
