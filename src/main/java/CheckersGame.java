/**
 * The type Checkers game.
 */
public class CheckersGame
{
    private Board board;
    private UserController userController;
    private AIController aiController;

    /**
     * The enum Player.
     */
    public enum Player {
        /**
         * Ai player.
         */
        AI,
        /**
         * Human player.
         */
        HUMAN }
    private Player turn;

    /**
     * Constructs a new CheckersGame.
     */
    public CheckersGame()
    {
        board = new Board(this);
        userController = new UserController(board);
        aiController = new AIController(board);
        turn = Player.HUMAN;
    }

    /**
     * Setter for property 'turn'.
     *
     * @param turn Value to set for property 'turn'.
     */
    public void setTurn(Player turn)
    {
        this.turn = turn;
    }

    /**
     * Getter for property 'turn'.
     *
     * @return Value for property 'turn'.
     */
    public Player getTurn()
    {
        return turn;
    }

    /**
     * Getter for property 'board'.
     *
     * @return Value for property 'board'.
     */
    public Board getBoard()
    {
        return board;
    }

    /**
     * Getter for property 'userController'.
     *
     * @return Value for property 'userController'.
     */
    public UserController getUserController() { return userController; }

    /**
     * Getter for property 'AIController'.
     *
     * @return Value for property 'AIController'.
     */
    public AIController getAIController() { return aiController; }
}
