package client;

import common.gameboard.BattleshipBoardCell;
import common.gameboard.BattleshipShip;
import common.gameboard.BattleshipUserBoard;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static common.gameboard.BattleshipBoard.COLUMNS;
import static common.gameboard.BattleshipBoard.ROWS;

public class BattleshipUserBoardHandler
{
    private final double x, y;    //coordinates of the upper-left point of user board
    private final double w, h; //width and height of user board

    private BattleshipUserBoard userBoard;

    private BattleshipBoardCell previewCell;

    public boolean isOpponentBoard;
    private static final double OPACITY_VAL = 0.5;
    private static final int MAX_FONT_SIZE = 24;

    public BattleshipUserBoardHandler(double x, double y, double w, double h, BattleshipUserBoard userBoard, boolean isOpponentBoard)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.userBoard = userBoard;
        this.isOpponentBoard = isOpponentBoard;
    }

    public BattleshipUserBoard getUserBoard() {
        return userBoard;
    }

    public void setUserBoard(BattleshipUserBoard userBoard) {
        this.userBoard = userBoard;
    }

    public BattleshipShip getShip(BattleshipBoardCell cell)
    {
        return userBoard.getShip(cell);
    }

    public boolean isShipCollides(BattleshipShip ship)
    {
        return isShipCollidesOutside(ship) || isShipCollidesInside(ship);
    }

    //checks collision for every cell in the given ship
    public boolean isShipCollidesInside(BattleshipShip currentShip)
    {
        for(BattleshipBoardCell cell : currentShip.getCellsInShip())
            if(cell.getColumn() >= COLUMNS || cell.getRow() >= ROWS)
                return true;
        for(BattleshipShip ship : userBoard.getShips())
        {
            if(ship.equals(currentShip)) //we are using object equals, because we are using the same ship instance from the user board
                continue;
            for(BattleshipBoardCell cell : currentShip.getCellsInShip())
                if(ship.getCellsInShip().contains(cell))
                    return true;
        }
        return false;
    }

    //checks collision for the cells surrounding the given ship
    public boolean isShipCollidesOutside(BattleshipShip ship)
    {
        for(BattleshipBoardCell cell : ship.getCellsInShip())
        {
            if(
                //check collision in cell in current row (left and right from the ship)
                checkCollisionInCell(ship, new BattleshipBoardCell(cell.getRow(), cell.getColumn() + 1)) ||
                checkCollisionInCell(ship, new BattleshipBoardCell(cell.getRow(), cell.getColumn() - 1)) ||
                //check collision in cell in current column (up and down from the ship)
                checkCollisionInCell(ship, new BattleshipBoardCell(cell.getRow() + 1, cell.getColumn())) ||
                checkCollisionInCell(ship, new BattleshipBoardCell(cell.getRow() - 1, cell.getColumn()) )||
                //check collision in cell diagonally from the ship
                checkCollisionInCell(ship, new BattleshipBoardCell(cell.getRow() + 1, cell.getColumn() + 1)) ||
                checkCollisionInCell(ship, new BattleshipBoardCell(cell.getRow() - 1, cell.getColumn() - 1)) ||
                checkCollisionInCell(ship, new BattleshipBoardCell(cell.getRow() + 1, cell.getColumn() - 1)) ||
                checkCollisionInCell(ship, new BattleshipBoardCell(cell.getRow() - 1, cell.getColumn() + 1)))
            {
                return true;
            }
        }
        return false;
    }

    public boolean checkCollisionInCell(BattleshipShip currentShip, BattleshipBoardCell cell)
    {
        if(cell == null)
            return false;
        if(cell.getColumn() >= COLUMNS || cell.getRow() >= ROWS)
            return false;
        for(BattleshipShip ship : userBoard.getShips())
        {
            if(ship.equals(currentShip)) //we are using object equals, because it's the same ship instance from user board
                continue;
            if(ship.getCellsInShip().contains(cell))
                return true;
        }
        return false;
    }

    public void shoot(BattleshipBoardCell cell)
    {
        userBoard.shoot(cell);
    }


    public boolean isOnBoard(Point2D.Double point)
    {
        return getX() <= point.getX() && getX() + getCellWidth() * COLUMNS >= point.getX() &&
                getY() <= point.getY() && getY() + getCellHeight() * ROWS >= point.getY();
    }

    public boolean isOnBoard(int row, int column)
    {
        return userBoard.isOnBoard(row, column);
    }



    public BattleshipBoardCell getNearestCell(Point2D.Double point) //calls only when the point inside the game board
    {
        int row = (int)((point.getY() - getY()) / getCellHeight());
        int column = (int)((point.getX() - getX()) / getCellWidth());

        return isOnBoard(row, column) ? userBoard.getCells()[row][column] : null;
    }

    public double getCellWidth(){
        return w/ COLUMNS;
    }
    public double getCellHeight(){
        return h/ ROWS;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setPreviewCell(BattleshipBoardCell previewCell) {
        this.previewCell = previewCell;
    }

    public void rotateShip(BattleshipShip ship)
    {
        List<BattleshipBoardCell> targetShipCells = new ArrayList<>();
        List<BattleshipBoardCell> originalCells = ship.getCellsInShip();
        //The ship rotates in compare to the anchor cell (the upper-left cell of the ship), which retains at the same position in rotation.
        BattleshipBoardCell anchorCell = originalCells.get(0);
        targetShipCells.add(anchorCell);
        //for all cells in ship except the anchor cell
        for (int i = 1; i < ship.getSize().getValue(); i++)
        {
            int targetRow = (ship.getSide() == BattleshipShip.ShipSide.HORIZONTAL) ? anchorCell.getRow() + i :
                    anchorCell.getRow();
            int targetColumn = (ship.getSide() == BattleshipShip.ShipSide.HORIZONTAL) ? anchorCell.getColumn() :
                    anchorCell.getColumn() + i;
            if (!isOnBoard(targetRow,targetColumn))
            {   //if the target point is outside the board we cancel the move
                return;
            }
            targetShipCells.add(userBoard.getCells()[targetRow][targetColumn]);
        }
        ship.setCellsInShip(targetShipCells);
        if (isShipCollides(ship))
        {   //if the rotated ship collides with other ships or not placed within the board bounds, we rotateShip the ship back.
            ship.setCellsInShip(originalCells);
            return;
        }

        ship.setSide((ship.getSide() == BattleshipShip.ShipSide.HORIZONTAL ?
                BattleshipShip.ShipSide.VERTICAL : BattleshipShip.ShipSide.HORIZONTAL));

    }

    public boolean moveShip(BattleshipBoardCell sourceCell, BattleshipBoardCell targetCell, BattleshipShip ship)
    {
        if (targetCell == null)
        {
            return false;
        }
        //the target cell is the cell that we were are on with the mouse. in move ship we check nearest cell on board that we are on
        int diffRow = targetCell.getRow() - sourceCell.getRow();
        int diffColumn = targetCell.getColumn() - sourceCell.getColumn();

        if (diffRow == 0 && diffColumn == 0)
        {
            return false;
        }

        List<BattleshipBoardCell> targetShipCells = new ArrayList<>();

        for (BattleshipBoardCell cell : ship.getCellsInShip())
        {
            int targetRow = cell.getRow() + diffRow;
            int targetColumn = cell.getColumn() + diffColumn;

            if (!isOnBoard(targetRow,targetColumn))
            {   //if the target point is outside the board we cancel the move
                return false;
            }

            targetShipCells.add(userBoard.getCells()[targetRow][targetColumn]);
        }
        ship.setCellsInShip(targetShipCells);
        return true;
    }

    public boolean moveShip(BattleshipBoardCell sourceCell, Point2D.Double targetPoint, BattleshipShip ship)
    {
        if (!isOnBoard(targetPoint))
        {
            return false;
        }
        BattleshipBoardCell targetCell = getNearestCell(targetPoint);
        return moveShip(sourceCell, targetCell, ship);
    }

    public void draw(GraphicsContext context)
    {
        drawBoard(context);
        drawCoordinates(context);
        drawShips(context);
        drawDestroyedShip(context);
        drawShootPreview(context);
        drawShoot(context);
    }

    public void drawBoard(GraphicsContext context)
    {
        context.save();
        context.translate(x, y);
        context.beginPath();

        //draw vertical lines of the board
        for (int i = 0; i <= COLUMNS; i++)
        {
            context.moveTo(i * w / COLUMNS, 0);
            context.lineTo(i * w / COLUMNS, h);
        }

        //draw horizontal lines of the board
        for (int i = 0; i <= ROWS; i++)
        {
            context.moveTo(0, i * h / ROWS);
            context.lineTo(w, i * h / ROWS);
        }

        context.closePath();
        context.stroke();
        context.restore();
    }


    public void drawCoordinates(GraphicsContext context)
    {
        double fontSize = getCellHeight() > MAX_FONT_SIZE ? MAX_FONT_SIZE : getCellHeight();
        context.setFont(Font.font("Arial", fontSize));
        context.setFill(Color.BLACK);
        int offset = 5;

        //place horizontal coordinates
        for (int i = 1; i <= COLUMNS; i++)
        {
            Text t = new Text(String.valueOf(i));
            t.setFont(context.getFont());
            //X position of the beginning (the most left position) of current column
            double x = getX() + (i - 1) * getCellWidth();
            //set x position so the text will be aligned in the center of current column margins, according to cell width and text width
            x += getCellWidth() / 2 - t.getLayoutBounds().getWidth() / 2;
            context.fillText(t.getText(), x, getY() - offset, getCellWidth());
        }

        //place vertical coordinates
        char c = 'A';
        for (int i = 1; i <= ROWS; i++, c++)
        {
            Text t = new Text(String.valueOf(c));
            t.setFont(context.getFont());
            //Y position of the beginning (the upper position) of current row
            double y = getY() + fontSize + (i - 1) * getCellHeight();
            //set y position so the text will be aligned in the center of current row margins, according to cell width and text width
            y += getCellHeight() / 2 - fontSize / 2;
            context.fillText(t.getText(), getX() - t.getLayoutBounds().getWidth() - offset, y);
        }
    }

    public void drawShips(GraphicsContext context)
    {
        if(isOpponentBoard)
        {   //Ships will be displayed only on current user board (and not on the opponent board, unless ship is destroyed)
            return;
        }

        for (BattleshipShip ship : userBoard.getShips())
            drawShip(context, ship);
    }

    public void drawDestroyedShip(GraphicsContext context)
    {
        for (BattleshipShip ship : userBoard.getShips())
        {
            if(ship.isDestroyed())
            {
                drawShip(context, ship);
            }
        }
    }

    private void drawShip(GraphicsContext ctx, BattleshipShip ship)
    {
        double x = getX() + getCellWidth() * ship.getCellsInShip().get(0).getColumn(); //x coordinate of the upper-left point of the ship
        double y = getY() + getCellHeight() * ship.getCellsInShip().get(0).getRow(); //y coordinate of the upper-left point of the ship
        double w = getCellWidth();  //ship width = short size of the ship
        double h = getCellHeight() * ship.getSize().getValue() ; //ship height = long side of the ship

        Image shipImage = new Image("ship.png");

        if(ship.getSide() == BattleshipShip.ShipSide.HORIZONTAL)
        {
            ctx.save();
            //The image is drawn at (x,y) rotated by angle pivoted around the point (x + w/2, y + getCellHeight()/2)
            ctx.translate(x+w/2,y + getCellHeight()/2);
            //Because the ship in given image is vertical, we will rotateShip the image
            ctx.rotate(-90);
            ctx.drawImage(shipImage,-w/2,-getCellHeight()/2, w, h);
        }
        else
        {   //Draw VERTICAL ship. Because the ship in given image is vertical, no adjustments need here.
            ctx.drawImage(shipImage, x, y, w, h);
        }
        ctx.restore();
    }

    public void drawShoot(GraphicsContext context)
    {
        context.save();
        context.translate(x, y);
        //for every cell in List which containing all elements from BattleshipBoardCell[][]
        for (BattleshipBoardCell cell : Arrays.stream(userBoard.getCells()).flatMap(Arrays::stream).collect(Collectors.toList()))
        {
            if(cell.getState() != BattleshipBoardCell.CellState.BLANK)
            {
                context.setFill(cell.getState() == BattleshipBoardCell.CellState.HIT ? Color.RED : Color.DEEPSKYBLUE);
                context.setGlobalAlpha(OPACITY_VAL);
                context.fillRect(getCellWidth() * cell.getColumn(), getCellHeight() * cell.getRow(), getCellWidth(), getCellHeight());
            }
        }
        context.restore();
    }

    private void drawShootPreview(GraphicsContext context)
    {
        if (previewCell == null || previewCell.getState() != BattleshipBoardCell.CellState.BLANK)
        {
            return;
        }
        context.save();
        context.translate(x, y);

        context.setFill(Color.GREY);
        context.setGlobalAlpha(OPACITY_VAL);

        context.fillArc(getCellWidth() * previewCell.getColumn() + getCellWidth() / 4 ,
                getCellHeight() * previewCell.getRow() + getCellHeight() / 4 ,
                getCellWidth() / 2, getCellHeight() / 2, 0, 360, ArcType.ROUND);

        context.restore();
    }

}


