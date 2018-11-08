public class UserController
{
    private Cell sourceCell;
    private Cell targetCell;
    private boolean isUserTurn;

    public void sourceSelectEvt(Cell sourceCell)
    {
        this.sourceCell = sourceCell;
    }

    public void targetSelectEvt(Cell targetCell)
    {
        this.targetCell = targetCell;
    }

    public void makeMove()
    {
        targetCell.occupyCell(sourceCell.getChecker());
        sourceCell.emptyCell();
    }

    public Cell getSource() { return sourceCell; }

    public Cell getTarget() { return targetCell; }

    public void setUserTurn(boolean isUserTurn)
    {
        this.isUserTurn = isUserTurn;
    }

    public boolean isUserTurn()
    {
        return isUserTurn;
    }
}