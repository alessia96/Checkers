import java.util.ArrayList;
import java.util.List;

public class AIController
{
    private Board board;
    private List<Move> availableMoves;
    public List<MovesAndScores> successorEvaluations;
    public int seCount, deCount, pCount;
    private int maxDepth = 5;

    public AIController(Board board)
    {
        this.board = board;
    }

    public Move getAIMove(int difficulty)
    {
        successorEvaluations = new ArrayList<>();
        maxDepth = difficulty;
        deCount = 0;
        seCount = 0;
        pCount = 0;
        minimax(0, CheckersGame.Player.AI, Integer.MIN_VALUE, Integer.MAX_VALUE);

        int best = -1;
        int MAX = -100;
        for (int i = 0; i < successorEvaluations.size(); i++)
        {
            if (MAX < successorEvaluations.get(i).getScore())
            {
                MAX = successorEvaluations.get(i).getScore();
                best = i;
            }
        }
        return successorEvaluations.get(best).getMove();
    }

    public int minimax(int depth, CheckersGame.Player player, int alpha, int beta)
    {
        int bestScore = 0;

        if (depth > maxDepth)
        {
            seCount++;
            return board.getCheckers(true).size() - board.getCheckers(false).size();
        }

        //System.out.println(depth);
        List<Move> movesAvailable = board.getAvailableStates(player);
        if (movesAvailable.isEmpty())
        {
            seCount++;
            //System.out.println("empty moves");
            return 0;
        }

        for (int i = 0; i < movesAvailable.size(); i++)
        {
            Board temp = null;

            try
            {
                temp = (Board) board.clone();
            }
            catch (Exception e)
            {
                //
            }

            Move move = movesAvailable.get(i);
//            System.out.println(move.getSource().getRow() + " " + move.getSource().getColumn() +
//                    " to " + move.getTarget().getRow() + " " + move.getTarget().getColumn());
            deCount++;

            if (player == CheckersGame.Player.AI) // MAX - white (AI)
            {
                bestScore = Integer.MIN_VALUE;

                board.makeMove(move, true);

                int currentScore = minimax(depth + 1, CheckersGame.Player.HUMAN, alpha, beta);
                bestScore = Math.max(bestScore, currentScore);
                alpha = Math.max(currentScore, alpha);

                if (depth == 0)
                    successorEvaluations.add(new MovesAndScores(move, currentScore));
            }
            else if (player == CheckersGame.Player.HUMAN)   // MIN - black (human)
            {
                bestScore = Integer.MAX_VALUE;

                board.makeMove(move, true);

                int currentScore = minimax(depth + 1, CheckersGame.Player.AI, alpha, beta);
                bestScore = Math.min(bestScore, currentScore);
                beta = Math.min(currentScore, beta);
            }

            board = temp;

            if(alpha >= beta)
            {
                pCount++;
                //System.out.println("Pruning at level "+depth+" because "+alpha+">="+beta+").");
                break;
            }
        }

        return bestScore;
    }

    public void updateBoard(Board board)
    {
        this.board = board;
    }
}
