package common;

public class GameTypes
{
    public static final String CONNECT_4 = "Connect 4";
    public static final String BATTLESHIP = "Battleship";

    public static final String CONNECT_4_INSTRUCTIONS = "Each player will have a color (yellow or red), and the player with yellow disks will make a first turn." +
            "\nPlayers must alternate turns, and only one disc can be dropped in each turn. " +
            "\nOn your turn, drop one of your discs from the top into any of the seven slots. " +
            "\nThe game ends when there is a 4-in-a-line or a stalemate." +
            "\nThe goal is to be the first player to connect 4 of the same colored discs in a row (either vertically, horizontally, or diagonally).";

    public static final String BATTLESHIP_INSTRUCTIONS =  "Each player places 5 ships somewhere on their board." +
            "\nThe ships can only be placed vertically or horizontally. Diagonal placement is not allowed." +
            "\nNo part of a ship may hang off the edge of the board." +
            "\nShips may not overlap each other. " + "No ships may be placed on another ship." +
            "\n" + "Player's take turns guessing by calling out the coordinates of target cell." +
            "\nIf target cell is part of a ship (\"hit\") -  the cell will change color to red." +
            "\nIf target cell is not part of a ship (\"miss\") - the cell will change it's color to blue." +
            "\nIf a player shot all of the cells that ship contains (\"sink\") - the ship image will be displayed to the player." +
            "\nThe goal is to try and sink all of the other player's before they sink all of your ships.";
}
