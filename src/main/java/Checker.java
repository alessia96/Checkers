public class Checker
{
    private boolean isBlack;
    private boolean isCaptured;
    private int currentRow;
    private int currentCol;

    public Checker(boolean isBlack, int row, int column)
    {
        this.isBlack = isBlack;
        currentRow = row;
        currentCol = column;
    }

    public boolean isBlack()
    {
        return isBlack;
    }

    public int getRow()
    {
        return currentRow;
    }

    public int getCol()
    {
        return currentCol;
    }

    public void setCaptured()
    {
        isCaptured = true;
    }

    public boolean isCaptured()
    {
        return isCaptured;
    }
}
