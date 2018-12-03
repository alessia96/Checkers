/**
 * The type Moves and scores.
 */
public class MovesAndScores
{
    private Move move;
    private int score;

    /**
     * Instantiates a new Moves and scores.
     *
     * @param move  the move
     * @param score the score
     */
    public MovesAndScores(Move move, int score)
    {
        this.move = move;
        this.score = score;
    }

    /**
     * Getter for property 'move'.
     *
     * @return Value for property 'move'.
     */
    public Move getMove() { return move; }

    /**
     * Getter for property 'score'.
     *
     * @return Value for property 'score'.
     */
    public int getScore() { return score; }
}
