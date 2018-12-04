/**
 * The MovesAndScores class represents a data structure
 * containing information about each move and its corresponding score.
 * Used in minimax.
 */
public class MovesAndScores
{
    private Move move;
    private int score;

    /**
     * Instantiates a new MovesAndScores object.
     *
     * @param move  the move.
     * @param score the score of the move.
     */
    public MovesAndScores(Move move, int score)
    {
        this.move = move;
        this.score = score;
    }

    /**
     * Getter for the move.
     *
     * @return the move.
     */
    public Move getMove() { return move; }

    /**
     * Getter for the score.
     *
     * @return the score.
     */
    public int getScore() { return score; }
}
