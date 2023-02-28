package common.gameboard;

import javax.json.bind.annotation.JsonbProperty;

public class BattleshipBoardCell extends GameBoardCell
{
    private static final long serialVersionUID = -8455859597263353611L;

    public enum CellState
    {
        BLANK, //no moves for current cell
        HIT, //current cell was targeted by opponent move and is a part of (damaged) ship
        MISS //current cell was targeted by opponent move and is not a part of ship
    }
    
    @JsonbProperty(value = "cellState")
    private BattleshipBoardCell.CellState state;
    
    @SuppressWarnings("unused") //used by json convertor
    public BattleshipBoardCell() {}

    public BattleshipBoardCell(int row, int column) {
        super(row, column);
        this.state = CellState.BLANK;
    }
    
    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object obj) 
    {
        if(obj instanceof BattleshipBoardCell)
            if(((BattleshipBoardCell) obj).getColumn() == this.getColumn() && ((BattleshipBoardCell) obj).getRow() == this.getRow())
                return true;
        return super.equals(obj);
    }

    @Override
    public String toString(){
        return "{row: "+ getRow() +", column: " + getColumn() + "}";
    }

}
