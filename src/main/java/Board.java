import java.util.ArrayList;
import java.util.List;

// state representation
public class Board
{
    private CheckersGame match;
    private Cell[][] grid;
    private Checker checkerInBetween;
    private List<Cell> toJumpTo;
    private List<Checker> checkerThatCanCapture;
    private boolean capturingMove;

    public Board(CheckersGame match)
    {
        this.match = match;
        grid = new Cell[8][8];
        fillWithCells();
        fillWithCheckers();
        checkerThatCanCapture = new ArrayList<>();
        toJumpTo = new ArrayList<>();
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

    public void clearBoard()
    {
        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                grid[row][col].emptyCell();
            }
        }
    }

    public void fillWithExistingCheckers(List<Checker> checkers)
    {
        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                for (Checker c : checkers)
                {
                    if (c.getCell() == grid[row][col])
                    {
                        grid[row][col].occupyCell(c);
                    }
                }
            }
        }
    }

    public void fillWithCheckers()
    {
        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                if (((row == 0 || row == 2) && col % 2 == 1) || row == 1 && col % 2 == 0)
                {
                    grid[row][col].occupyCell(new Checker(this, Checker.Colour.BLACK, row, col, false));     // fill up blacks
                }
                else if (((row == 5 || row == 7) && col % 2 == 0) || row == 6 && col % 2 == 1)
                {
                    grid[row][col].occupyCell(new Checker(this, Checker.Colour.WHITE, row, col, false));    // fill up whites
                }
            }
        }
    }

    public Cell getCellAt(int row, int col)
    {
        return grid[row][col];
    }

    public List<Checker> getCheckers()
    {
        List<Checker> checkers = new ArrayList<>();

        for (int row = 0; row < grid.length; row++)
        {
            for (int col = 0; col < grid.length; col++)
            {
                if (grid[row][col].isOccupied())
                {
                    checkers.add(grid[row][col].getChecker());
                }
            }
        }

        return checkers;
    }

    public List<Checker> cloneHistory(List<Checker> source)
    {
        List<Checker> dest = new ArrayList<>();
        Checker temp;
        for(int i = 0; i < source.size(); i++)
        {
            temp = source.get(i);
            dest.add(temp);
        }
        return dest;
    }

    public List<Checker> getCheckers(boolean isBlack)
    {
        List<Checker> checkers = new ArrayList<>();

        for (int row = 0; row < grid.length; row++)
        {
            for (int col = 0; col < grid.length; col++)
            {
                if (grid[row][col].isOccupied())
                {
                    if (isBlack && grid[row][col].getChecker().getColour() == Checker.Colour.BLACK)
                    {
                        checkers.add(grid[row][col].getChecker());
                    }
                    else if (!isBlack && grid[row][col].getChecker().getColour() == Checker.Colour.WHITE)
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

    public Checker getCheckerInBetween()
    {
        return checkerInBetween;
    }

    // return true if a move is still available, false if it can give up turn to opponent
    public void makeMove(Move move, boolean isTest)
    {
        grid[move.getTarget().getRow()][move.getTarget().getColumn()].occupyCell(new Checker(this, move.getSource().getColour(), move.getTarget().getRow(), move.getTarget().getColumn(), move.getSource().isKing()));

        grid[move.getSource().getRow()][move.getSource().getColumn()].emptyCell();

        if ((move.getSource().getColour() == Checker.Colour.BLACK && move.getTarget().getRow() == grid.length - 1)
            || (move.getSource().getColour() == Checker.Colour.WHITE && move.getTarget().getRow() == 0))
        {
            crownKing(move.getTarget().getChecker());
        }

        if (capturingMove && checkerInBetween != null)
        {
            if (!isTest)
            {
                move.capturedChecker = checkerInBetween;
            }

            checkerInBetween.getCell().emptyCell();
            checkerInBetween = null;
            //capturingMove = false;
        }

        if (!isTest)
        {
            if (match.getTurn() == CheckersGame.Player.AI)
            {
                match.setTurn(CheckersGame.Player.HUMAN);
            }
            else
            {
                match.setTurn(CheckersGame.Player.AI);
            }
        }
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

    public boolean isSourceSameAsTarget(Move move)
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

    public boolean isTargetWhite(Move move)
    {
        return !move.getTarget().isBlack();
    }

    public boolean isMovingBackwards(Move move)
    {
        if (move.getSource().isKing()) return false;

        if (move.getSource().getColour() == Checker.Colour.BLACK &&
                (move.getSource().getRow() < move.getTarget().getRow()))
        {
            return false;
        }
        else if (move.getSource().getColour() == Checker.Colour.WHITE &&
                (move.getSource().getRow() > move.getTarget().getRow()))
        {
            return false;
        }
        return true;
    }

    public boolean isTargetOccupied(Move move)
    {
        if (move.getTarget().isOccupied())
        {
            return true;
        }
        return false;
    }

    public boolean isValidJump(Move move)
    {
        if (isCheckerInBetween(move))
        {
            for (Cell c : toJumpTo)
            {
                if (c.getRow() == move.getTarget().getRow() && c.getColumn() == move.getTarget().getColumn())
                {
                    capturingMove = true;
                    return true;
                }
            }

            return false;
        }
        else if (!toJumpTo.isEmpty()) return false;

        if (Math.abs(move.getSource().getRow() - move.getTarget().getRow()) == 1
                && Math.abs(move.getSource().getColumn() - move.getTarget().getColumn()) == 1 && !move.getTarget().isOccupied())
        {
            return true;
        }
        return false;
    }

    public boolean wasCapturingMove()
    {
        return capturingMove;
    }

    public void resetCapturingMove()
    {
        capturingMove = false;
    }

    public boolean isCheckerInBetween(Move move)
    {
        boolean blackSource = move.getSource().getColour() == Checker.Colour.BLACK;
        boolean whiteSource = move.getSource().getColour() == Checker.Colour.WHITE;
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

        if (blackSource && moveRight && botRight != null && botRight.isOccupied() && botRight.getChecker().getColour() == Checker.Colour.WHITE)
        {
            checkerInBetween = botRight.getChecker();
            return true;
        }
        else if (blackSource && moveLeft && botLeft != null && botLeft.isOccupied() && botLeft.getChecker().getColour() == Checker.Colour.WHITE)
        {
            checkerInBetween = botLeft.getChecker();
            return true;
        }
        else if (blackSource && move.getSource().isKing() && moveRight && topRight != null && topRight.isOccupied() && topRight.getChecker().getColour() == Checker.Colour.WHITE)
        {
            checkerInBetween = topRight.getChecker();
            return true;
        }
        else if (blackSource && move.getSource().isKing() && moveLeft && topLeft != null && topLeft.isOccupied() && topLeft.getChecker().getColour() == Checker.Colour.WHITE)
        {
            checkerInBetween = topLeft.getChecker();
            return true;
        }
        else if (whiteSource && moveRight && topRight != null && topRight.isOccupied() && topRight.getChecker().getColour() == Checker.Colour.BLACK)
        {
            checkerInBetween = topRight.getChecker();
            return true;
        }
        else if (whiteSource && moveLeft && topLeft != null && topLeft.isOccupied() && topLeft.getChecker().getColour() == Checker.Colour.BLACK)
        {
            checkerInBetween = topLeft.getChecker();
            return true;
        }
        else if (whiteSource && move.getSource().isKing() && moveRight && botRight != null && botRight.isOccupied() && botRight.getChecker().getColour() == Checker.Colour.BLACK)
        {
            checkerInBetween = botRight.getChecker();
            return true;
        }
        else if (whiteSource && move.getSource().isKing() && moveLeft && botLeft != null && botLeft.isOccupied() && botLeft.getChecker().getColour() == Checker.Colour.BLACK)
        {
            checkerInBetween = botLeft.getChecker();
            return true;
        }
        return false;
    }

    public List<Checker> getMovableCheckers(CheckersGame.Player turn)
    {
        List<Checker> movableCheckers = new ArrayList<>();
        checkerThatCanCapture.clear();
        toJumpTo.clear();

        if (turn == CheckersGame.Player.HUMAN)
        {
            for (int row = 0; row < grid.length; row++)
            {
                for (int col = 0; col < grid.length; col++)
                {
                    if (grid[row][col].isOccupied() && grid[row][col].getChecker().getColour() == Checker.Colour.BLACK)
                    {
                        if (canMove(grid[row][col].getChecker(), turn))
                        {
                            movableCheckers.add(grid[row][col].getChecker());
                        }
                        if (canCapture(grid[row][col], turn))
                        {
                            checkerThatCanCapture.add(grid[row][col].getChecker());
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
                    if (grid[row][col].isOccupied() && grid[row][col].getChecker().getColour() == Checker.Colour.WHITE)
                    {
                        if (canMove(grid[row][col].getChecker(), turn))
                        {
                            movableCheckers.add(grid[row][col].getChecker());
                        }
                        if (canCapture(grid[row][col], turn))
                        {
                            checkerThatCanCapture.add(grid[row][col].getChecker());
                        }
                    }
                }
            }
        }

        if (!checkerThatCanCapture.isEmpty()) return checkerThatCanCapture;
        else return movableCheckers;
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
            if (checker.isKing() && ((topRight != null && !topRight.isOccupied()) || (topLeft != null && !topLeft.isOccupied())))
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
            if (checker.isKing() && ((botRight != null && !botRight.isOccupied()) || (botLeft != null && !botLeft.isOccupied())))
            {
                return true;
            }
        }
        return false;
    }

    public boolean canCapture(Cell c, CheckersGame.Player player)
    {
        int row = c.getRow();
        int col = c.getColumn();
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
        if (player == CheckersGame.Player.HUMAN)  // black
        {
            // if cell in between is occupied by a white cell and next cell is empty
            if (botRight != null && botRight.isOccupied() && botRight.getChecker().getColour() == Checker.Colour.WHITE && jumpBotRight != null && !jumpBotRight.isOccupied())
            {
                checkerInBetween = botRight.getChecker();
                toJumpTo.add(jumpBotRight);
                return true;
            }
            if (botLeft != null && botLeft.isOccupied() && botLeft.getChecker().getColour() == Checker.Colour.WHITE && jumpBotLeft != null && !jumpBotLeft.isOccupied())
            {
                checkerInBetween = botLeft.getChecker();
                toJumpTo.add(jumpBotLeft);
                return true;
            }
            if (c.getChecker().isKing() && topRight != null && topRight.isOccupied() && topRight.getChecker().getColour() == Checker.Colour.WHITE && jumpTopRight != null && !jumpTopRight.isOccupied())
            {
                checkerInBetween = topRight.getChecker();
                toJumpTo.add(jumpTopRight);
                return true;
            }
            if (c.getChecker().isKing() && topLeft != null && topLeft.isOccupied() && topLeft.getChecker().getColour() == Checker.Colour.WHITE && jumpTopLeft != null && !jumpTopLeft.isOccupied())
            {
                checkerInBetween = topLeft.getChecker();
                toJumpTo.add(jumpTopLeft);
                return true;
            }
        }
        else if (player == CheckersGame.Player.AI)    // white
        {
            // if cell in between is occupied by a black cell and next cell is empty
            if (topRight != null && topRight.isOccupied() && topRight.getChecker().getColour() == Checker.Colour.BLACK && jumpTopRight != null && !jumpTopRight.isOccupied())
            {
                checkerInBetween = topRight.getChecker();
                toJumpTo.add(jumpTopRight);
                return true;
            }
            if (topLeft != null && topLeft.isOccupied() && topLeft.getChecker().getColour() == Checker.Colour.BLACK && jumpTopLeft != null && !jumpTopLeft.isOccupied())
            {
                checkerInBetween = topLeft.getChecker();
                toJumpTo.add(jumpTopLeft);
                return true;
            }
            if (c.getChecker().isKing() && botRight != null && botRight.isOccupied() && botRight.getChecker().getColour() == Checker.Colour.BLACK && jumpBotRight != null && !jumpBotRight.isOccupied())
            {
                checkerInBetween = botRight.getChecker();
                toJumpTo.add(jumpBotRight);
                return true;
            }
            if (c.getChecker().isKing() && botLeft != null && botLeft.isOccupied() && botLeft.getChecker().getColour() == Checker.Colour.BLACK && jumpBotLeft != null && !jumpBotLeft.isOccupied())
            {
                checkerInBetween = botLeft.getChecker();
                toJumpTo.add(jumpBotLeft);
                return true;
            }
        }

        return false;
    }

    // successor function
    public List<Move> getAvailableStates(CheckersGame.Player player)
    {
        List<Move> availableMoves = new ArrayList<>();

        List<Checker> allMovableCheckers = getMovableCheckers(player);
        Move testMove;
        Cell source, target;

        for (int i = 0; i < allMovableCheckers.size(); i++)
        {
            source = getCellAt(allMovableCheckers.get(i).getRow(), allMovableCheckers.get(i).getColumn());

            for (int row = 0; row < getGrid().length; row++)
            {
                for (int col = 0; col < getGrid().length; col++)
                {
                    if (!getCellAt(row, col).isOccupied())
                    {
                        target = getCellAt(row, col);

                        testMove = new Move(source.getChecker(), target);

                        if (isMoveValid(testMove))
                        {
                            availableMoves.add(testMove);
                        }
                    }
                }
            }
        }

        return availableMoves;
    }

    public void crownKing(Checker checker)
    {
        checker.setKing();
    }

    public boolean isCaptureAvailable()
    {
        List<Checker> allMovableCheckers = getMovableCheckers(CheckersGame.Player.HUMAN);
        Cell source, target;
        Move testMove;

        for (int i = 0; i < allMovableCheckers.size(); i++)
        {
            source = getCellAt(allMovableCheckers.get(i).getRow(), allMovableCheckers.get(i).getColumn());

            for (int row = 0; row < getGrid().length; row++)
            {
                for (int col = 0; col < getGrid().length; col++)
                {
                    if (!getCellAt(row, col).isOccupied())
                    {
                        target = getCellAt(row, col);

                        testMove = new Move(source.getChecker(), target);

                        if (isMoveValid(testMove) && canCapture(source, CheckersGame.Player.HUMAN))
                        {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
