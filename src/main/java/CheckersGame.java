public class CheckersGame
{
    private Board board;
    private UserController userController;

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
        Cell source = userController.getSource();
        Cell target = userController.getTarget();

        if (source != target)
        {
            return true;
        }

        return false;
    }
}
