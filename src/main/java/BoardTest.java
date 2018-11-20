import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BoardTest
{
    /*@Test
    public void cloneTest()
    {
        Board board = new Board(new CheckersGame());
        Board clonedBoard = board.clone();
        board.getGrid()[1][2] = null;
        assert(board.getGrid()[1][2] != clonedBoard.getGrid()[1][2]);
    }*/

    @Test
    public void getCheckersTest()
    {
        Board board = new Board(new CheckersGame());
        assert(board.getCheckers(true).size() == 12);
    }

    @Test
    public void makeMoveTest()
    {
        Board board = new Board(new CheckersGame());
        Checker source = board.getCellAt(5, 0).getChecker();
        Cell target = board.getCellAt(4, 1);
        Move move = new Move(source, target);
        board.makeMove(move, false);
        assertFalse(board.getCellAt(5, 0).isOccupied());
        assertTrue(board.getCellAt(4, 1).isOccupied());
    }

    @Test
    public void captureMoveTest()
    {
        Board board = new Board(new CheckersGame());
        Checker source = board.getCellAt(5, 0).getChecker();
        Cell target = board.getCellAt(4, 1);
        Move move = new Move(source, target);
        board.makeMove(move, false);

        source = board.getCellAt(2, 3).getChecker();
        target = board.getCellAt(3, 2);
        move = new Move(source, target);
        board.makeMove(move, false);

        source = board.getCellAt(4, 1).getChecker();
        target = board.getCellAt(2, 3);
        move = new Move(source, target);
        board.makeMove(move, false);

        assertFalse(board.getCellAt(4, 1).isOccupied());
        assertFalse(board.getCellAt(3, 2).isOccupied());
        assertTrue(board.getCellAt(2, 3).isOccupied());
        assertNotNull(board.getCheckerInBetween());
    }
}