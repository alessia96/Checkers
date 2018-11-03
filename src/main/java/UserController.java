import javafx.scene.control.Button;

public class UserController
{
    private Cell sourceCell;
    private Cell targetCell;

    public void sourceSelectEvt(Cell sourceCell)
    {
        this.sourceCell = sourceCell;
    }

    public void targetSelectEvt(Cell targetCell)
    {
        this.targetCell = targetCell;
        makeMove();
    }

    private void makeMove()
    {
        targetCell.occupyCell(sourceCell.getChecker());
        sourceCell.emptyCell();
    }
}