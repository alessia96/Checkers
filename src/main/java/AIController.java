import java.util.ArrayList;
import java.util.List;

public class AIController
{
    private Board board, tempBoard;
    private List<Move> availableMoves;
    private List<MovesAndScores> successorEvaluations;
    private int seCount, deCount, pCount;
    private boolean blackCaptureHappened, whiteCaptureHappened;

    public AIController(Board board)
    {
        this.board = board;
        tempBoard = board;
    }

    public Move getAIMove()
    {
        successorEvaluations = new ArrayList<>();
        deCount = 0;
        seCount = 0;
        pCount = 0;
        blackCaptureHappened = false;
        whiteCaptureHappened = false;
        minimax(0, CheckersGame.Player.AI);

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

    public int minimax(int depth, CheckersGame.Player player)
    {
        int bestScore = 0;

        if (blackCaptureHappened)
        {
            seCount++;
            return 1;
        }
        else if (whiteCaptureHappened)
        {
            seCount++;
            return -1;
        }

        List<Move> movesAvailable = getAvailableStates(player);

        for (int i = 0; i < movesAvailable.size(); i++)
        {
            Move move = movesAvailable.get(i);
            deCount++;

            if (player == CheckersGame.Player.AI) // MAX - white (AI)
            {
                bestScore = Integer.MIN_VALUE;

                int sizeBeforeMove = tempBoard.getCheckers(true).size();
                if (tempBoard.isMoveValid(move))
                {
                    tempBoard.makeMove(move);
                    int sizeAfterMove = tempBoard.getCheckers(true).size();
                    if (sizeBeforeMove != sizeAfterMove) {
                        blackCaptureHappened = true;
                    } else {
                        blackCaptureHappened = false; }
                }
                else
                {
                    continue;
                }

                int currentScore = minimax(depth + 1, CheckersGame.Player.HUMAN);
                if (currentScore > bestScore)
                    bestScore = currentScore;

                if (depth == 0)
                    successorEvaluations.add(new MovesAndScores(move, currentScore));
            }
            else if (player == CheckersGame.Player.HUMAN)   // MIN - black (human)
            {
                bestScore = Integer.MAX_VALUE;

                int sizeBeforeMove = tempBoard.getCheckers(false).size();
                if (tempBoard.isMoveValid(move))
                {
                    tempBoard.makeMove(move);
                    int sizeAfterMove = tempBoard.getCheckers(false).size();
                    if (sizeBeforeMove != sizeAfterMove) {
                        whiteCaptureHappened = true;
                    } else {
                        whiteCaptureHappened = false; }
                }
                else
                {
                    continue;
                }

                int currentScore = minimax(depth + 1, CheckersGame.Player.AI);
                if (currentScore < bestScore)
                    bestScore = currentScore;
            }

            tempBoard = board;  // reset fake board
        }

        return bestScore;
    }

    private List<Move> getAvailableStates(CheckersGame.Player player)
    {
        availableMoves = new ArrayList<>();

        List<Checker> allMovableCheckers = new ArrayList<>();
        Move testMove = null;
        Cell source = null;
        Cell target = null;

        if (player == CheckersGame.Player.AI)    // get white checkers - AI
        {
            allMovableCheckers = tempBoard.getMovableCheckers(player);
        }
        else if (player == CheckersGame.Player.HUMAN)     // get black checkers - player
        {
            allMovableCheckers = tempBoard.getMovableCheckers(player);
        }

        for (int i = 0; i < allMovableCheckers.size(); i++)
        {
            source = tempBoard.getCellAt(allMovableCheckers.get(i).getRow(), allMovableCheckers.get(i).getCol());

            for (int row = 0; row < tempBoard.getGrid().length; row++)
            {
                for (int col = 0; col < tempBoard.getGrid().length; col++)
                {
                    if (!tempBoard.getCellAt(row, col).isOccupied())
                    {
                        target = tempBoard.getCellAt(row, col);

                        testMove = new Move(source, target);
                        if (tempBoard.isMoveValid(testMove))
                            availableMoves.add(new Move(source, target));
                    }
                }
            }

        }

        return availableMoves;
    }
}
