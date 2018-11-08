public class CheckersGame
{
    private Board board;
    private UserController userController;
    private Cell source;
    private Cell target;

    public CheckersGame()
    {
        board = new Board();
        userController = new UserController();
    }

    public Board getBoard()
    {
        return board;
    }

    public UserController getUserController() { return userController; }

    public boolean isMoveValid()
    {
        source = userController.getSource();
        target = userController.getTarget();

        if ((source == target) || !target.isBlack() ||
                !isMovingForward() || target.isOccupied() ||
                    !isValidJump())
        {
            return false;
        }

        return true;
    }

    private boolean isMovingForward()
    {
        if (source.getChecker().isBlack() &&
                (source.getRow() < target.getRow()))
        {
            return true;
        }
        else if (!source.getChecker().isBlack() &&
                    (source.getRow() > target.getRow()))
        {
            return true;
        }

        return false;
    }

    private boolean isValidJump()
    {
        if (((Math.abs(source.getRow() - target.getRow()) == 2) && isCheckerInBetween()) ||
            (Math.abs(source.getRow() - target.getRow()) == 1))
        {
            return true;
        }

        return false;
    }

    private boolean isCheckerInBetween()
    {
        if (source.getChecker().isBlack() && source.getColumn() < target.getColumn() &&
                board.getCellAt(source.getRow() + 1, source.getColumn() + 1).isOccupied() &&
                !board.getCellAt(source.getRow() + 1, source.getColumn() + 1).getChecker().isBlack())
        {
            board.getCellAt(source.getRow() + 1, source.getColumn() + 1).getChecker().setCaptured();
            board.getCellAt(source.getRow() + 1, source.getColumn() + 1).emptyCell();
            return true;
        }
        else if (source.getChecker().isBlack() && source.getColumn() > target.getColumn() &&
                    board.getCellAt(source.getRow() + 1, source.getColumn() - 1).isOccupied() &&
                    !board.getCellAt(source.getRow() + 1, source.getColumn() - 1).getChecker().isBlack())
        {
            board.getCellAt(source.getRow() + 1, source.getColumn() - 1).getChecker().setCaptured();
            board.getCellAt(source.getRow() + 1, source.getColumn() - 1).emptyCell();
            return true;
        }
        else if (!source.getChecker().isBlack() && source.getColumn() < target.getColumn() &&
                    board.getCellAt(source.getRow() - 1, source.getColumn() + 1).isOccupied() &&
                    board.getCellAt(source.getRow() - 1, source.getColumn() + 1).getChecker().isBlack())
        {
            board.getCellAt(source.getRow() - 1, source.getColumn() + 1).getChecker().setCaptured();
            board.getCellAt(source.getRow() - 1, source.getColumn() + 1).emptyCell();
            return true;
        }
        else if (!source.getChecker().isBlack() && source.getColumn() > target.getColumn() &&
                    board.getCellAt(source.getRow() - 1, source.getColumn() - 1).isOccupied() &&
                    board.getCellAt(source.getRow() - 1, source.getColumn() - 1).getChecker().isBlack())
        {
            board.getCellAt(source.getRow() - 1, source.getColumn() - 1).getChecker().setCaptured();
            board.getCellAt(source.getRow() - 1, source.getColumn() - 1).emptyCell();
            return true;
        }

        return false;
    }

    public boolean isCaptureAvailable()
    {
        return false;
    }
}
