public class Checker
{
    public enum Colour { BLACK, WHITE }
    private Colour colour;
    private int currentRow;
    private int currentCol;
    private Board board;
    private boolean isKing;

    public Checker(Board board, Colour colour, int row, int column)
    {
        this.board = board;
        this.colour = colour;
        currentRow = row;
        currentCol = column;
    }

    public Colour getColour()
    {
        return colour;
    }

    public int getRow()
    {
        return currentRow;
    }

    public int getColumn()
    {
        return currentCol;
    }

    public Cell getCell()
    {
        return board.getCellAt(currentRow, currentCol);
    }

    public boolean isKing()
    {
        return isKing;
    }

    public void setKing()
    {
        isKing = true;
    }
}
