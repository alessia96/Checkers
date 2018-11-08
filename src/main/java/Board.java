public class Board
{
    private Cell[][] grid;
    private int whiteCheckers;
    private int blackCheckers;

    public Board()
    {
        grid = new Cell[8][8];
        fillWithCells();
        fillWithCheckers();
    }

    private void fillWithCells()
    {
        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                if ((col % 2 == 0 && row % 2 == 1) || (col % 2 == 1 && row % 2 == 0))
                {
                    grid[row][col] = new Cell(null, row, col, true);
                }
                else
                {
                    grid[row][col] = new Cell(null, row, col, false);
                }
            }
        }
    }

    private void fillWithCheckers()
    {
        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                if (((row == 0 || row == 2) && col % 2 == 1) || row == 1 && col % 2 == 0)
                {
                    grid[row][col].occupyCell(new Checker(this, true, row, col));     // fill up blacks
                    blackCheckers++;
                }
                else if (((row == 5 || row == 7) && col % 2 == 0) || row == 6 && col % 2 == 1)
                {
                    grid[row][col].occupyCell(new Checker(this, false, row, col));    // fill up whites
                    whiteCheckers++;
                }
            }
        }
    }

    public Cell getCellAt(int row, int col)
    {
        return grid[row][col];
    }

    public void decreaseCheckerCount(boolean isBlack)
    {
        if (isBlack)
        {
            blackCheckers--;
        }
        else
        {
            whiteCheckers--;
        }
    }

    public int getCheckerCount(boolean isBlack)
    {
        if (isBlack) { return blackCheckers; } else { return whiteCheckers; }
    }
}
