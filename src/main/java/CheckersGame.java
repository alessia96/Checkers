/**
 * The CheckersGame class represents a single game of checkers.
 * Handles the relationships between a board and its User and AI controllers.
 * Contains information on the current player.
 */
public class CheckersGame
{
    private Board board;
    private UserController userController;
    private AIController aiController;

    public enum Player { AI, HUMAN }
    private Player turn;

    /**
     * Constructs a new CheckersGame object, giving the initial turn to the player.
     */
    public CheckersGame()
    {
        board = new Board(this);
        userController = new UserController(board);
        aiController = new AIController(board);
        turn = Player.HUMAN;
    }

    /**
     * Setter for the turn.
     *
     * @param turn the turn to be set.
     */
    public void setTurn(Player turn)
    {
        this.turn = turn;
    }

    /**
     * Getter for the turn.
     *
     * @return the current turn.
     */
    public Player getTurn()
    {
        return turn;
    }

    /**
     * Getter for the board.
     *
     * @return the board associated with the game.
     */
    public Board getBoard()
    {
        return board;
    }

    /**
     * Getter for the user controller.
     *
     * @return the user controller.
     */
    public UserController getUserController() { return userController; }

    /**
     * Getter for the AI controller.
     *
     * @return the AI controller.
     */
    public AIController getAIController() { return aiController; }
}
