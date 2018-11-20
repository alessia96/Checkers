import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

class BoardTest extends GroovyTestCase
{
    void testClone()
    {
        Board board = new Board()
        Board clonedBoard = new Board(board, new CheckersGame())
        board.getGrid()[1][2] = null
        assertThat(board.getCellAt(1, 2) != clonedBoard.getCellAt(1, 2))
    }

    void testGetCellAt()
    {
    }

    void testGetCheckers()
    {
    }

    void testGetGrid()
    {
    }

    void testGetCheckerInBetween()
    {
    }

    void testSetCheckerInBetween()
    {
    }

    void testMakeMove()
    {
    }

    void testIsMoveValid()
    {
    }

    void testIsSourceSameAsTarget()
    {
    }

    void testIsTargetWhite()
    {
    }

    void testIsMovingBackwards()
    {
    }

    void testIsTargetOccupied()
    {
    }

    void testIsValidJump()
    {
    }

    void testIsCheckerInBetween()
    {
    }

    void testGetIdleCell()
    {
    }

    void testGetMovableCheckers()
    {
    }

    void testCanMove()
    {
    }

    void testGetAvailableStates()
    {
    }
}
