import java.util.ArrayList;
import java.util.List;

public class Board
{
    private Cell[][] grid;
    private int whiteCheckers;
    private int blackCheckers;
    private Cell idle;

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

    public List<Checker> getCheckers(boolean isBlack)
    {
        List<Checker> checkers = new ArrayList<>();

        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                if (grid[row][col].isOccupied())
                {
                    if (isBlack && grid[row][col].getChecker().isBlack())
                    {
                        checkers.add(grid[row][col].getChecker());
                    }
                    else if (!isBlack && !grid[row][col].getChecker().isBlack())
                    {
                        checkers.add(grid[row][col].getChecker());
                    }
                }
            }
        }

        return checkers;
    }

    public Cell[][] getGrid()
    {
        return grid;
    }

    public void makeMove(Move move)
    {
        if (move.getSource().isBlack())
        {
            grid[move.getTarget().getRow()][move.getTarget().getColumn()].occupyCell(new Checker(this, true, move.getTarget().getRow(), move.getTarget().getColumn()));
        }
        else
        {
            grid[move.getTarget().getRow()][move.getTarget().getColumn()].occupyCell(new Checker(this, false, move.getTarget().getRow(), move.getTarget().getColumn()));
        }

        grid[move.getSource().getRow()][move.getSource().getColumn()] = new Cell(null, move.getSource().getRow(), move.getSource().getColumn(), true);

        if (isCheckerInBetween(move))
        {
            Checker checkerInBetween = idle.getChecker();
            grid[checkerInBetween.getRow()][checkerInBetween.getColumn()] = new Cell(null, checkerInBetween.getRow(), checkerInBetween.getColumn(), true);
        }

        System.out.println("made move");
    }

    public boolean isMoveValid(Move move)
    {
        if (isTargetWhite(move) || isSourceSameAsTarget(move) || isMovingBackwards(move)
            || isTargetOccupied(move) || !isValidJump(move))
        {
            return false;
        }
        return true;
    }

    private boolean isSourceSameAsTarget(Move move)
    {
        if (move.getSource().getCell().getRow() == move.getTarget().getRow() &&
            move.getSource().getCell().getColumn() == move.getTarget().getColumn())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean isTargetWhite(Move move)
    {
        return !move.getTarget().isBlack();
    }

    private boolean isMovingBackwards(Move move)
    {
        if (move.getSource().isBlack() &&
                (move.getSource().getRow() < move.getTarget().getRow()))
        {
            return false;
        }
        else if (!move.getSource().isBlack() &&
                (move.getSource().getRow() > move.getTarget().getRow()))
        {
            return false;
        }

        return true;
    }

    private boolean isTargetOccupied(Move move)
    {
        if (move.getTarget().isOccupied())
        {
            return true;
        }
        return false;
    }

    private boolean isValidJump(Move move)
    {
        if (((Math.abs(move.getSource().getRow() - move.getTarget().getRow()) == 2) && isCheckerInBetween(move)) ||
                (Math.abs(move.getSource().getRow() - move.getTarget().getRow()) == 1))
        {
            return true;
        }

        return false;
    }

    private boolean isCheckerInBetween(Move move)
    {
        boolean blackSource = move.getSource().isBlack();
        boolean whiteSource = !move.getSource().isBlack();
        boolean moveRight = move.getSource().getColumn() < move.getTarget().getColumn();
        boolean moveLeft = move.getSource().getColumn() > move.getTarget().getColumn();
        Cell botRight = null;
        Cell botLeft = null;
        Cell topRight = null;
        Cell topLeft = null;

        if (move.getSource().getRow() + 1 < grid.length && move.getSource().getColumn() + 1 < grid.length)
            botRight = getCellAt(move.getSource().getRow() + 1, move.getSource().getColumn() + 1);

        if (move.getSource().getRow() + 1 < grid.length && move.getSource().getColumn() - 1 >= 0)
            botLeft = getCellAt(move.getSource().getRow() + 1, move.getSource().getColumn() - 1);

        if (move.getSource().getRow() - 1 >= 0 && move.getSource().getColumn() + 1 < grid.length)
            topRight = getCellAt(move.getSource().getRow() - 1, move.getSource().getColumn() + 1);

        if (move.getSource().getRow() - 1 >= 0 && move.getSource().getColumn() - 1 >= 0)
            topLeft = getCellAt(move.getSource().getRow() - 1, move.getSource().getColumn() - 1);

        if (blackSource && moveRight && botRight.isOccupied() && !botRight.getChecker().isBlack())
        {
            idle = botRight;
            return true;
        }
        else if (blackSource && moveLeft && botLeft.isOccupied() && !botLeft.getChecker().isBlack())
        {
            idle = botLeft;
            return true;
        }
        else if (whiteSource && moveRight && topRight.isOccupied() && topRight.getChecker().isBlack())
        {
            idle = topRight;
            return true;
        }
        else if (whiteSource && moveLeft && topLeft.isOccupied() && topLeft.getChecker().isBlack())
        {
            idle = topLeft;
            return true;
        }
        System.out.println("no checker in between");
        return false;
    }

    public Cell getIdleCell()
    {
        return idle;
    }
    /*

    public boolean isCaptureAvailable()
    {
        return false;
    }

    public List<Checker> getMovableCheckers(CheckersGame.Player turn)
    {
        List<Checker> movableCheckers = new ArrayList<>();

        if (turn == CheckersGame.Player.HUMAN)
        {
            for (int row = 0; row < grid.length; row++)
            {
                for (int col = 0; col < grid.length; col++)
                {
                    if (grid[row][col].isOccupied() && grid[row][col].getChecker().isBlack())
                    {
                        if (canMove(grid[row][col].getChecker(), turn) || canCapture(grid[row][col].getChecker(), turn))
                        {
                            movableCheckers.add(grid[row][col].getChecker());
                        }
                    }
                }
            }
        }
        else if (turn == CheckersGame.Player.AI)
        {
            for (int row = 0; row < grid.length; row++)
            {
                for (int col = 0; col < grid.length; col++)
                {
                    if (grid[row][col].isOccupied() && !grid[row][col].getChecker().isBlack())
                    {
                        if (canMove(grid[row][col].getChecker(), turn) || canCapture(grid[row][col].getChecker(), turn))
                        {
                            movableCheckers.add(grid[row][col].getChecker());
                        }
                    }
                }
            }
        }

        return movableCheckers;
    }

    public boolean canMove(Checker checker, CheckersGame.Player turn)
    {
        int row = checker.getRow();
        int col = checker.getColumn();
        Cell topRight = null;
        Cell topLeft = null;
        Cell botRight = null;
        Cell botLeft = null;

        if (row - 1 >= 0 && col + 1 < grid.length)
        {
            topRight = getCellAt(row - 1, col + 1);
        }
        if (row - 1 >= 0 && col - 1 >= 0)
        {
            topLeft = getCellAt(row - 1, col - 1);
        }
        if (row + 1 < grid.length && col + 1 < grid.length)
        {
            botRight = getCellAt(row + 1, col + 1);
        }
        if (row + 1 < grid.length && col - 1 >= 0)
        {
            botLeft = getCellAt(row + 1, col - 1);
        }

        if (turn == CheckersGame.Player.HUMAN) // black
        {
            if ((botRight != null && !botRight.isOccupied()) || (botLeft != null && !botLeft.isOccupied()))
            {
                return true;
            }
        }
        else if (turn == CheckersGame.Player.AI)    // white
        {
            if ((topRight != null && !topRight.isOccupied()) || (topLeft != null && !topLeft.isOccupied()))
            {
                return true;
            }
        }

        return false;
    }

    public boolean canCapture(Checker checker, CheckersGame.Player turn)
    {
        int row = checker.getRow();
        int col = checker.getColumn();
        Cell topRight = null;
        Cell topLeft = null;
        Cell botRight = null;
        Cell botLeft = null;
        Cell jumpTopRight = null;
        Cell jumpTopLeft = null;
        Cell jumpBotRight = null;
        Cell jumpBotLeft = null;

        if (row - 1 >= 0 && col + 1 < grid.length)
        {
            topRight = getCellAt(row - 1, col + 1);
        }
        if (row - 1 >= 0 && col - 1 >= 0)
        {
            topLeft = getCellAt(row - 1, col - 1);
        }
        if (row + 1 < grid.length && col + 1 < grid.length)
        {
            botRight = getCellAt(row + 1, col + 1);
        }
        if (row + 1 < grid.length && col - 1 >= 0)
        {
            botLeft = getCellAt(row + 1, col - 1);
        }

        if (topRight != null && topRight.getRow() - 1 >= 0 && topRight.getColumn() + 1 < grid.length)
        {
            jumpTopRight = getCellAt(topRight.getRow() - 1, topRight.getColumn() + 1);
        }
        if (topLeft != null && topLeft.getRow() - 1 >= 0 && topLeft.getColumn() - 1 >= 0)
        {
            jumpTopLeft = getCellAt(topLeft.getRow() - 1, topLeft.getColumn() - 1);
        }
        if (botRight != null && botRight.getRow() + 1 < grid.length && botRight.getColumn() + 1 < grid.length)
        {
            jumpBotRight = getCellAt(botRight.getRow() + 1, botRight.getColumn() + 1);
        }
        if (botLeft != null && botLeft.getRow() + 1 < grid.length && botLeft.getColumn() - 1 >= 0)
        {
            jumpBotLeft = getCellAt(botLeft.getRow() + 1, botLeft.getColumn() - 1);
        }

        if (turn == CheckersGame.Player.HUMAN)  // black
        {
            // if cell in between is occupied by a white cell and next cell is empty
            if (botRight != null && botRight.isOccupied() && !botRight.getChecker().isBlack() && jumpBotRight != null && !jumpBotRight.isOccupied())
            {
                return true;
            }
            if (botLeft != null && botLeft.isOccupied() && !botLeft.getChecker().isBlack() && jumpBotLeft != null && !jumpBotLeft.isOccupied())
            {
                return true;
            }
        }
        else if (turn == CheckersGame.Player.AI)    // white
        {
            // if cell in between is occupied by a black cell and next cell is empty
            if (topRight != null && topRight.isOccupied() && topRight.getChecker().isBlack() && jumpTopRight != null && !jumpTopRight.isOccupied())
            {
                return true;
            }
            if (topLeft != null && topLeft.isOccupied() && topLeft.getChecker().isBlack() && jumpTopLeft != null && !jumpTopLeft.isOccupied())
            {
                return true;
            }
        }

        return false;
    }*/
}
