public class CheckersGame
{
    private Board board;
    private UserController userController;
    private AIController aiController;
    public enum Player { AI, HUMAN }
    private Player turn;

    public CheckersGame()
    {
        board = new Board();
        userController = new UserController(board);
        aiController = new AIController(board);
        turn = Player.HUMAN;
    }

    public void setTurn(Player turn)
    {
        this.turn = turn;
    }

    public Player getTurn()
    {
        return turn;
    }

    public Board getBoard()
    {
        return board;
    }

    public UserController getUserController() { return userController; }

    public AIController getAIController() { return aiController; }
}
