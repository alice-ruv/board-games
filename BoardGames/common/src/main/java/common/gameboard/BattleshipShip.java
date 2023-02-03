package common.gameboard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BattleshipShip implements Serializable
{
    private static final long serialVersionUID = -7888490760742167238L;

    public enum ShipSize
    {   //ship size is measured by the number of cells it takes on the game board
        SIZE_2(2),
        SIZE_3(3),
        SIZE_4(4),
        SIZE_5(5);

        private final int value;

        ShipSize(final int newValue) {
            value = newValue;
        }

        public int getValue() { return value; }
    }
    private ShipSize size;

    public enum ShipSide {HORIZONTAL,VERTICAL}
    private ShipSide side;

    private List<BattleshipBoardCell> cellsInShip;
    @SuppressWarnings("unused") //used when serializing
    public BattleshipShip(){}

    public BattleshipShip(ShipSize size, int row, int column, ShipSide side, BattleshipBoardCell[][] cells)
    {
        this.size = size;
        this.side = side;
        this.cellsInShip = new ArrayList<>();
        this.cellsInShip.add(cells[row][column]);

       if (this.side == ShipSide.HORIZONTAL)
       {
           for(int i = column + 1; i < this.size.value + column; i++)
           {
               this.cellsInShip.add(cells[row][i]);
           }
       }
       else //this.side == ShipSide.VERTICAL
       {
           for(int i = row + 1; i < this.size.value + row; i++)
           {
               this.cellsInShip.add(cells[i][column]);
           }
       }
    }

    public ShipSize getSize() {
        return size;
    }

    public void setSize(ShipSize size) {
        this.size = size;
    }

    public ShipSide getSide() {
        return side;
    }

    public void setSide(ShipSide side) {
        this.side = side;
    }

    public List<BattleshipBoardCell> getCellsInShip()
    {
        return cellsInShip;
    }

    public void setCellsInShip(List<BattleshipBoardCell> cellsInShip) {
        this.cellsInShip = cellsInShip;
    }


    public boolean isDestroyed()
    {   //Ship is destroyed when the number of "HIT" cells in ship equals to the size of the ship (in other words,
        //when all cells of the ship are "HIT" cells).
        return cellsInShip.stream().filter(cell -> cell.getState() == BattleshipBoardCell.CellState.HIT).count() == size.value;
    }

}


