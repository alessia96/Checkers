import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AIController
{
    private Board board;
    public List<MovesAndScores> successorEvaluations;
    public int seCount, deCount, pCount;
    private int maxDepth;

    public AIController(Board board)
    {
        this.board = board;
    }

    public Move getAIMove(int difficulty)
    {
        if (difficulty == 0)
        {
            return getRandomMove();
        }
        else
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
    }

    private Move getRandomMove()
    {
        List<Move> movesAvailable = board.getAvailableStates(CheckersGame.Player.AI);
        int random = (int) Math.random() * movesAvailable.size();
        return movesAvailable.get(random);
    }

    public int minimax(int depth, CheckersGame.Player player, int alpha, int beta)
    {
        int bestScore = 0;

        if (depth > maxDepth)
        {
            seCount++;
            return board.getCheckers(true).size() - board.getCheckers(false).size();
        }

        List<Move> movesAvailable = board.getAvailableStates(player);
        if (movesAvailable.isEmpty())
        {
            seCount++;
            return 0;
        }

        for (int i = 0; i < movesAvailable.size(); i++)
        {
            List<Checker> allCheckers = board.cloneHistory(board.getCheckers());

            Move move = movesAvailable.get(i);
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

            board.clearBoard();
            board.fillWithExistingCheckers(allCheckers);
            if (move.capturedChecker != null) move.capturedChecker.getCell().emptyCell();

            if(alpha >= beta)
            {
                pCount++;
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
