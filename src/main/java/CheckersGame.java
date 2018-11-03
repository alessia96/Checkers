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
}
