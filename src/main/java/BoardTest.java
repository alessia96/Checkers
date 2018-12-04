import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class BoardTest
{
    @Test
    public void getCheckersTest()
    {
        Board board = new Board(new CheckersGame());
        assert(board.getCheckers(Checker.Colour.BLACK).size() == 12);
    }

    @Test
    public void makeMoveTest()
    {
        Board board = new Board(new CheckersGame());
        Checker source = board.getTileAt(5, 0).getChecker();
        Tile target = board.getTileAt(4, 1);
        Move move = new Move(source, target);
        board.makeMove(move, false);
        assertFalse(board.getTileAt(5, 0).isOccupied());
        assertTrue(board.getTileAt(4, 1).isOccupied());
    }

    @Test
    public void captureMoveTest()
    {
        Board board = new Board(new CheckersGame());
        Checker source = board.getTileAt(5, 0).getChecker();
        Tile target = board.getTileAt(4, 1);
        Move move = new Move(source, target);
        board.makeMove(move, false);

        source = board.getTileAt(2, 3).getChecker();
        target = board.getTileAt(3, 2);
        move = new Move(source, target);
        board.makeMove(move, false);

        source = board.getTileAt(4, 1).getChecker();
        target = board.getTileAt(2, 3);
        move = new Move(source, target);
        board.makeMove(move, false);

        assertFalse(board.getTileAt(4, 1).isOccupied());
        assertFalse(board.getTileAt(3, 2).isOccupied());
        assertTrue(board.getTileAt(2, 3).isOccupied());
        assertNotNull(board.getCheckerInBetween());
    }
}