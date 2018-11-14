public class MovesAndScores
{
    private Move move;
    private int score;

    public MovesAndScores(Move move, int score)
    {
        this.move = move;
        this.score = score;
    }

    public Move getMove() { return move; }

    public int getScore() { return score; }
}
