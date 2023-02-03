package common.gameboard;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static common.gameboard.BattleshipBoard.COLUMNS;
import static common.gameboard.BattleshipBoard.ROWS;

//BattleshipUserBoard represents the game board that displayed to user (user board).
//There are two user boards which displayed to the user: the board of current player and the board of the opponent player.
public class BattleshipUserBoard implements Serializable
{
    private static final long serialVersionUID = 8767185659717215014L;

    @JsonbProperty(value = "ships")
    List<BattleshipShip> ships;

    @JsonbProperty(value = "cells")
    private BattleshipBoardCell[][] cells;

    public static BattleshipUserBoard createDefaultUserBoard()
    {   //Init user board cells
        BattleshipBoardCell[][] cells = new BattleshipBoardCell[ROWS][COLUMNS];
        for (int row = 0; row < ROWS; row++)
        {
            for (int col = 0; col < COLUMNS; col++)
            {
                cells[row][col] = new BattleshipBoardCell(row,col);
            }
        }

        //In the beginning of the game, all the ships are placed on the board, one after another horizontally from the upper-left cell, by default.
        List<BattleshipShip> ships = new ArrayList<>();
        ships.add(new BattleshipShip(BattleshipShip.ShipSize.SIZE_5, 0,0, BattleshipShip.ShipSide.HORIZONTAL, cells));
        ships.add(new BattleshipShip(BattleshipShip.ShipSize.SIZE_4, 2,0, BattleshipShip.ShipSide.HORIZONTAL, cells));
        ships.add(new BattleshipShip(BattleshipShip.ShipSize.SIZE_3, 4,0, BattleshipShip.ShipSide.HORIZONTAL, cells));
        ships.add(new BattleshipShip(BattleshipShip.ShipSize.SIZE_3, 6,0, BattleshipShip.ShipSide.HORIZONTAL, cells));
        ships.add(new BattleshipShip(BattleshipShip.ShipSize.SIZE_2, 8,0, BattleshipShip.ShipSide.HORIZONTAL, cells));
        return new BattleshipUserBoard(ships, cells);
    }
    @SuppressWarnings("unused") //used when serializing
    public BattleshipUserBoard() {}

    private BattleshipUserBoard(List<BattleshipShip> ships, BattleshipBoardCell[][] cells)
    {
        this.ships = ships;
        this.cells = cells;
    }

    public List<BattleshipShip> getShips() {
        return ships;
    }
    @SuppressWarnings("unused") //used when serializing
    public void setShips(List<BattleshipShip> ships) {
        this.ships = ships;
    }

    public BattleshipBoardCell[][] getCells() {
        return cells;
    }
    @SuppressWarnings("unused") //used when serializing
    public void setCells(BattleshipBoardCell[][] cells) {
        this.cells = cells;
    }


    //returns ship which contains a given cell, if exists
    public BattleshipShip getShip(BattleshipBoardCell cell)
    {
        Optional<BattleshipShip> shipContainer = ships.stream().filter(ship -> ship.getCellsInShip().contains(cell)).findFirst();
        return shipContainer.orElse(null);
    }

    public boolean isOnBoard(int row, int column)
    {
        return (row >= 0 && row < ROWS) && (column >= 0 && column < COLUMNS);
    }

    public void shoot(BattleshipBoardCell cell)
    {
        BattleshipShip ship = getShip(cell);
        if (ship == null)
        {   //no ship located in the cell that was targeted
            cell.setState(BattleshipBoardCell.CellState.MISS);
        }
        else
        {   //a ship located in the cell that was targeted
            cell.setState(BattleshipBoardCell.CellState.HIT);
            for (BattleshipBoardCell cellInShip : ship.getCellsInShip())
            {
                if (cellInShip.equals(cell))
                {
                    cellInShip.setState(BattleshipBoardCell.CellState.HIT);
                    break;
                }
            }
        }
    }

    public boolean isGameOver()
    {   //Game is over when all the ships are destroyed
        //returns true if the number of destroyed ships equals to the number of all the ships on the board
        return ships.stream().filter(BattleshipShip::isDestroyed).count() == ships.size();
    }


}
