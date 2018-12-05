import java.util.ArrayList;
import java.util.List;

/**
 * The Board class is the state representation.
 * Board handles filling up the board with tiles and checkers.
 * Board contains helper functions to aid minimax.
 * Getter functions in Board are used to transmit information about the
 * game state to external classes.
 * Board handles the logic of the game, and allows for moves to be made.
 * Board contains the rules of the games and checks if a move is valid.
 * Successor function runs within board, and uses the current game state
 * to return all available moves.
 * Kings are crowned within the Board class.
 */
public class Board
{
    private CheckersGame match;
    private Tile[][] grid;
    private Checker checkerInBetween;
    private List<Tile> toJumpTo;
    private List<Checker> checkersThatCanCapture;
    private boolean capturingMove;

    /**
     * Instantiates a new Board.
     *
     * @param match the current game.
     */
    public Board(CheckersGame match)
    {
        this.match = match;
        grid = new Tile[8][8];
        fillWithTiles();
        fillWithCheckers();
        // list of all checkers that can make a capturing move
        checkersThatCanCapture = new ArrayList<>();
        // list of all possible cells to jump to
        toJumpTo = new ArrayList<>();
    }

    /**
     * Fills the board with tiles.
     */
    private void fillWithTiles()
    {
        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                if ((col % 2 == 0 && row % 2 == 1) || (col % 2 == 1 && row % 2 == 0))
                {
                    grid[row][col] = new Tile(null, row, col, true);
                }
                else
                {
                    grid[row][col] = new Tile(null, row, col, false);
                }
            }
        }
    }

    /**
     * Clears the board from all checkers.
     */
    public void clearBoard()
    {
        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                grid[row][col].emptyTile();
            }
        }
    }

    /**
     * Fills the board with existing checkers.
     *
     * @param checkers the checkers with which to fill the board.
     */
    public void fillWithExistingCheckers(List<Checker> checkers)
    {
        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                for (Checker c : checkers)
                {
                    if (c.getTile() == grid[row][col])
                    {
                        grid[row][col].occupyTile(c);
                    }
                }
            }
        }
    }

    /**
     * Fills the board with checkers at the start of the game.
     */
    public void fillWithCheckers()
    {
        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                if (((row == 0 || row == 2) && col % 2 == 1) || row == 1 && col % 2 == 0)
                {
                    grid[row][col].occupyTile(new Checker(this, Checker.Colour.BLACK, row, col, false));     // fill up blacks
                }
                else if (((row == 5 || row == 7) && col % 2 == 0) || row == 6 && col % 2 == 1)
                {
                    grid[row][col].occupyTile(new Checker(this, Checker.Colour.RED, row, col, false));    // fill up reds
                }
            }
        }
    }

    /**
     * Gets tile at location row, col.
     *
     * @param row the row at which to get the tile.
     * @param col the column at which to get the tile.
     * @return the tile at location row, col.
     */
    public Tile getTileAt(int row, int col)
    {
        return grid[row][col];
    }

    /**
     * Clone list of checkers.
     *
     * @param source the source list.
     * @return the destination list.
     */
    public List<Checker> cloneList(List<Checker> source)
    {
        List<Checker> dest = new ArrayList<>();
        Checker temp;
        for(int i = 0; i < source.size(); i++)
        {
            temp = source.get(i);
            dest.add(temp);
        }
        return dest;
    }

    /**
     * Getter for checkers.
     *
     * @return list of all checkers.
     */
    public List<Checker> getCheckers()
    {
        List<Checker> checkers = new ArrayList<>();

        for (int row = 0; row < grid.length; row++)
        {
            for (int col = 0; col < grid.length; col++)
            {
                if (grid[row][col].isOccupied())
                {
                    checkers.add(grid[row][col].getChecker());
                }
            }
        }

        return checkers;
    }

    /**
     * Gets checkers of a given colour.
     *
     * @param colour the colour of checkers requested.
     * @return the checkers of given colour.
     */
    public List<Checker> getCheckers(Checker.Colour colour)
    {
        List<Checker> checkers = new ArrayList<>();

        for (int row = 0; row < grid.length; row++)
        {
            for (int col = 0; col < grid.length; col++)
            {
                if (grid[row][col].isOccupied())
                {
                    if (grid[row][col].getChecker().getColour() == colour)
                    {
                        checkers.add(grid[row][col].getChecker());
                    }
                }
            }
        }

        return checkers;
    }

    /**
     * Getter for the grid.
     *
     * @return the grid.
     */
    public Tile[][] getGrid()
    {
        return grid;
    }

    /**
     * Getter for the checker which represents a potential capture.
     *
     * @return the checker in between two opponents.
     */
    public Checker getCheckerInBetween()
    {
        return checkerInBetween;
    }

    /**
     * Makes a move by occupying the target tile with the source checker,
     * and by emptying the source tile.
     * Crowns a king if checker reaches opposite extremity of the board.
     * Records what checker is being captured, if capturing move.
     * Switches turn to opposite player.
     * Returns whether the move was a capturing move or not.
     *
     * @param move   the move to be made.
     * @param isTest whether the move is being made as part of a minimax implementation.
     * @return true if move was a capturing move, false otherwise.
     */
    public boolean makeMove(Move move, boolean isTest)
    {
        boolean hasJustCaptured = false;

        if (isCheckerInBetween(move))
        {
            if (!isTest)
            {
                move.setCapturedChecker(checkerInBetween);
            }

            hasJustCaptured = true;

            checkerInBetween.getTile().emptyTile();
            checkerInBetween = null;
            capturingMove = false;
        }

        grid[move.getTarget().getRow()][move.getTarget().getColumn()].occupyTile(new Checker(this, move.getSource().getColour(), move.getTarget().getRow(), move.getTarget().getColumn(), move.getSource().isKing()));

        grid[move.getSource().getRow()][move.getSource().getColumn()].emptyTile();

        if ((move.getSource().getColour() == Checker.Colour.BLACK && move.getTarget().getRow() == grid.length - 1)
                || (move.getSource().getColour() == Checker.Colour.RED && move.getTarget().getRow() == 0))
        {
            crownKing(move.getTarget().getChecker());
        }

        if (!isTest)
        {
            if (match.getTurn() == CheckersGame.Player.AI)
            {
                match.setTurn(CheckersGame.Player.HUMAN);
            }
            else
            {
                match.setTurn(CheckersGame.Player.AI);
            }
        }

        return hasJustCaptured;
    }

    /**
     * Checks if a move is valid.
     *
     * @param move the move to be checked.
     * @return true if the move is valid, false otherwise.
     */
    public boolean isMoveValid(Move move)
    {
        if (isTargetWhite(move) || isSourceSameAsTarget(move) || isMovingBackwards(move)
            || isTargetOccupied(move) || !isValidJump(move))
        {
            return false;
        }
        return true;
    }

    /**
     * Checks if the source tile is the same as the target tile.
     *
     * @param move the move to be checked.
     * @return true if the source is the same, false otherwise.
     */
    public boolean isSourceSameAsTarget(Move move)
    {
        if (move.getSource().getTile().getRow() == move.getTarget().getRow() &&
            move.getSource().getTile().getColumn() == move.getTarget().getColumn())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Checks if the target is a white tile.
     *
     * @param move the move to be checked.
     * @return true if the target is white, false otherwise.
     */
    public boolean isTargetWhite(Move move)
    {
        return !move.getTarget().isBlack();
    }

    /**
     * Checks if the source checker is moving backwards.
     *
     * @param move the move to be checked.
     * @return true if the source checker is moving backwards, false otherwise.
     */
    public boolean isMovingBackwards(Move move)
    {
        if (move.getSource().isKing()) return false;

        if (move.getSource().getColour() == Checker.Colour.BLACK &&
                (move.getSource().getRow() < move.getTarget().getRow()))
        {
            return false;
        }
        else if (move.getSource().getColour() == Checker.Colour.RED &&
                (move.getSource().getRow() > move.getTarget().getRow()))
        {
            return false;
        }
        return true;
    }

    /**
     * Checks if the target tile is already occupied.
     *
     * @param move the move to be checked.
     * @return true if the target tile is already occupied, false otherwise.
     */
    public boolean isTargetOccupied(Move move)
    {
        if (move.getTarget().isOccupied())
        {
            return true;
        }
        return false;
    }

    /**
     * Checks if the attempted jump is valid.
     *
     * @param move the move to be checked.
     * @return true if the jump is valid, false otherwise.
     */
    public boolean isValidJump(Move move)
    {
        if (isCheckerInBetween(move))
        {
            for (Tile c : toJumpTo)
            {
                if (c.getRow() == move.getTarget().getRow() && c.getColumn() == move.getTarget().getColumn())
                {
                    capturingMove = true;
                    return true;
                }
            }

            //return false;
        }
        else if (!toJumpTo.isEmpty()) return false;

        if (Math.abs(move.getSource().getRow() - move.getTarget().getRow()) == 1
                && Math.abs(move.getSource().getColumn() - move.getTarget().getColumn()) == 1)// && !move.getTarget().isOccupied())
        {
            return true;
        }
        return false;
    }

    /**
     * Resets capturing move to be false.
     */
    public void resetCheckerInBetween()
    {
        checkerInBetween = null;
    }

    /**
     * Checks if a checker is between the source and the target of a move.
     *
     * @param move the move.
     * @return true if there is a checker in between, false otherwise.
     */
    public boolean isCheckerInBetween(Move move)
    {
        boolean blackSource = move.getSource().getColour() == Checker.Colour.BLACK;
        boolean redSource = move.getSource().getColour() == Checker.Colour.RED;
        boolean moveRight = move.getSource().getColumn() < move.getTarget().getColumn();
        boolean moveLeft = move.getSource().getColumn() > move.getTarget().getColumn();
        Tile botRight = null;
        Tile botLeft = null;
        Tile topRight = null;
        Tile topLeft = null;
        Tile jumpTopRight = null;
        Tile jumpTopLeft = null;
        Tile jumpBotRight = null;
        Tile jumpBotLeft = null;

        if (move.getSource().getRow() + 1 < grid.length && move.getSource().getColumn() + 1 < grid.length)
            botRight = getTileAt(move.getSource().getRow() + 1, move.getSource().getColumn() + 1);

        if (move.getSource().getRow() + 1 < grid.length && move.getSource().getColumn() - 1 >= 0)
            botLeft = getTileAt(move.getSource().getRow() + 1, move.getSource().getColumn() - 1);

        if (move.getSource().getRow() - 1 >= 0 && move.getSource().getColumn() + 1 < grid.length)
            topRight = getTileAt(move.getSource().getRow() - 1, move.getSource().getColumn() + 1);

        if (move.getSource().getRow() - 1 >= 0 && move.getSource().getColumn() - 1 >= 0)
            topLeft = getTileAt(move.getSource().getRow() - 1, move.getSource().getColumn() - 1);

        if (topRight != null && topRight.getRow() - 1 >= 0 && topRight.getColumn() + 1 < grid.length)
        {
            jumpTopRight = getTileAt(topRight.getRow() - 1, topRight.getColumn() + 1);
        }
        if (topLeft != null && topLeft.getRow() - 1 >= 0 && topLeft.getColumn() - 1 >= 0)
        {
            jumpTopLeft = getTileAt(topLeft.getRow() - 1, topLeft.getColumn() - 1);
        }
        if (botRight != null && botRight.getRow() + 1 < grid.length && botRight.getColumn() + 1 < grid.length)
        {
            jumpBotRight = getTileAt(botRight.getRow() + 1, botRight.getColumn() + 1);
        }
        if (botLeft != null && botLeft.getRow() + 1 < grid.length && botLeft.getColumn() - 1 >= 0)
        {
            jumpBotLeft = getTileAt(botLeft.getRow() + 1, botLeft.getColumn() - 1);
        }

        if (blackSource && moveRight && botRight != null && botRight.isOccupied() && botRight.getChecker().getColour() == Checker.Colour.RED && jumpBotRight != null && !jumpBotRight.isOccupied())
        {
            checkerInBetween = botRight.getChecker();
            return true;
        }
        else if (blackSource && moveLeft && botLeft != null && botLeft.isOccupied() && botLeft.getChecker().getColour() == Checker.Colour.RED && jumpBotLeft != null && !jumpBotLeft.isOccupied())
        {
            checkerInBetween = botLeft.getChecker();
            return true;
        }
        else if (blackSource && move.getSource().isKing() && moveRight && topRight != null && topRight.isOccupied() && topRight.getChecker().getColour() == Checker.Colour.RED && jumpTopRight != null && !jumpTopRight.isOccupied())
        {
            checkerInBetween = topRight.getChecker();
            return true;
        }
        else if (blackSource && move.getSource().isKing() && moveLeft && topLeft != null && topLeft.isOccupied() && topLeft.getChecker().getColour() == Checker.Colour.RED && jumpTopLeft != null && !jumpTopLeft.isOccupied())
        {
            checkerInBetween = topLeft.getChecker();
            return true;
        }
        else if (redSource && moveRight && topRight != null && topRight.isOccupied() && topRight.getChecker().getColour() == Checker.Colour.BLACK && jumpTopRight != null && !jumpTopRight.isOccupied())
        {
            checkerInBetween = topRight.getChecker();
            return true;
        }
        else if (redSource && moveLeft && topLeft != null && topLeft.isOccupied() && topLeft.getChecker().getColour() == Checker.Colour.BLACK && jumpTopLeft != null && !jumpTopLeft.isOccupied())
        {
            checkerInBetween = topLeft.getChecker();
            return true;
        }
        else if (redSource && move.getSource().isKing() && moveRight && botRight != null && botRight.isOccupied() && botRight.getChecker().getColour() == Checker.Colour.BLACK && jumpBotRight != null && !jumpBotRight.isOccupied())
        {
            checkerInBetween = botRight.getChecker();
            return true;
        }
        else if (redSource && move.getSource().isKing() && moveLeft && botLeft != null && botLeft.isOccupied() && botLeft.getChecker().getColour() == Checker.Colour.BLACK && jumpBotLeft != null && !jumpBotLeft.isOccupied())
        {
            checkerInBetween = botLeft.getChecker();
            return true;
        }
        return false;
    }

    /**
     * Gets all movable checkers.
     *
     * @param turn the current turn.
     * @return all movable checkers.
     */
    public List<Checker> getMovableCheckers(CheckersGame.Player turn)
    {
        List<Checker> movableCheckers = new ArrayList<>();
        checkersThatCanCapture.clear();
        toJumpTo.clear();

        if (turn == CheckersGame.Player.HUMAN)
        {
            for (int row = 0; row < grid.length; row++)
            {
                for (int col = 0; col < grid.length; col++)
                {
                    if (grid[row][col].isOccupied() && grid[row][col].getChecker().getColour() == Checker.Colour.BLACK)
                    {
                        if (canMove(grid[row][col].getChecker(), turn))
                        {
                            // add all checkers that can move to a movableCheckers list
                            movableCheckers.add(grid[row][col].getChecker());
                        }
                        if (canCapture(grid[row][col], turn))
                        {
                            // add all checkers that can capture to a checkersThatCanCapture list
                            checkersThatCanCapture.add(grid[row][col].getChecker());
                        }
                    }
                }
            }
        }
        else if (turn == CheckersGame.Player.AI)
        {
            for (int row = 0; row < grid.length; row++)
            {
                for (int col = 0; col < grid.length; col++)
                {
                    if (grid[row][col].isOccupied() && grid[row][col].getChecker().getColour() == Checker.Colour.RED)
                    {
                        if (canMove(grid[row][col].getChecker(), turn))
                        {
                            movableCheckers.add(grid[row][col].getChecker());
                        }
                        if (canCapture(grid[row][col], turn))
                        {
                            checkersThatCanCapture.add(grid[row][col].getChecker());
                        }
                    }
                }
            }
        }

        // prioritise checkersThatCanCapture list, otherwise return all movableCheckers
        if (!checkersThatCanCapture.isEmpty()) return checkersThatCanCapture;
        else return movableCheckers;
    }

    /**
     * Checks if a specific checker can capture in a specific turn.
     *
     * @param checker the checker to be checked.
     * @param turn the current turn.
     * @return true if a checker can capture, false otherwise.
     */
    public boolean canCheckerCapture(Checker checker, CheckersGame.Player turn)
    {
        List<Checker> checkersThatCanCapture = getCheckersThatCanCapture(turn);

        for (Checker c : checkersThatCanCapture)
        {
            if (c.getRow() == checker.getRow() && c.getColumn() == checker.getColumn())
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets all checkers that can capture.
     *
     * @param turn the current turn.
     * @return all checkers that can capture another checker.
     */
    public List<Checker> getCheckersThatCanCapture(CheckersGame.Player turn)
    {
        checkersThatCanCapture.clear();

        if (turn == CheckersGame.Player.HUMAN)
        {
            for (int row = 0; row < grid.length; row++)
            {
                for (int col = 0; col < grid.length; col++)
                {
                    if (grid[row][col].isOccupied() && grid[row][col].getChecker().getColour() == Checker.Colour.BLACK)
                    {
                        if (canMove(grid[row][col].getChecker(), turn) && canCapture(grid[row][col], turn))
                        {
                            // add all checkers that can capture to a checkersThatCanCapture list
                            checkersThatCanCapture.add(grid[row][col].getChecker());
                        }
                    }
                }
            }
        }
        else if (turn == CheckersGame.Player.AI)
        {
            for (int row = 0; row < grid.length; row++)
            {
                for (int col = 0; col < grid.length; col++)
                {
                    if (grid[row][col].isOccupied() && grid[row][col].getChecker().getColour() == Checker.Colour.RED)
                    {
                        if (canMove(grid[row][col].getChecker(), turn) && canCapture(grid[row][col], turn))
                        {
                            checkersThatCanCapture.add(grid[row][col].getChecker());
                        }
                    }
                }
            }
        }

        return checkersThatCanCapture;
    }

    /**
     * Checks if a checker can move, given the current turn.
     *
     * @param checker the checker to check.
     * @param turn    the current turn.
     * @return true if the checker can move, false otherwise.
     */
    public boolean canMove(Checker checker, CheckersGame.Player turn)
    {
        int row = checker.getRow();
        int col = checker.getColumn();
        Tile topRight = null;
        Tile topLeft = null;
        Tile botRight = null;
        Tile botLeft = null;
        if (row - 1 >= 0 && col + 1 < grid.length)
        {
            topRight = getTileAt(row - 1, col + 1);
        }
        if (row - 1 >= 0 && col - 1 >= 0)
        {
            topLeft = getTileAt(row - 1, col - 1);
        }
        if (row + 1 < grid.length && col + 1 < grid.length)
        {
            botRight = getTileAt(row + 1, col + 1);
        }
        if (row + 1 < grid.length && col - 1 >= 0)
        {
            botLeft = getTileAt(row + 1, col - 1);
        }
        if (turn == CheckersGame.Player.HUMAN)
        {
            if (checker.isKing() && ((topRight != null && !topRight.isOccupied()) || (topLeft != null && !topLeft.isOccupied())))
            {
                return true;
            }
            if ((botRight != null && !botRight.isOccupied()) || (botLeft != null && !botLeft.isOccupied()))
            {
                return true;
            }
        }
        else if (turn == CheckersGame.Player.AI)
        {
            if (checker.isKing() && ((botRight != null && !botRight.isOccupied()) || (botLeft != null && !botLeft.isOccupied())))
            {
                return true;
            }
            if ((topRight != null && !topRight.isOccupied()) || (topLeft != null && !topLeft.isOccupied()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a checker can capture, given the current player.
     *
     * @param tile      the tile containing the checker to be checked.
     * @param player the current player.
     * @return true if the checker can capture, false otherwise.
     */
    public boolean canCapture(Tile tile, CheckersGame.Player player)
    {
        int row = tile.getRow();
        int col = tile.getColumn();
        Tile topRight = null;
        Tile topLeft = null;
        Tile botRight = null;
        Tile botLeft = null;
        Tile jumpTopRight = null;
        Tile jumpTopLeft = null;
        Tile jumpBotRight = null;
        Tile jumpBotLeft = null;
        if (row - 1 >= 0 && col + 1 < grid.length)
        {
            topRight = getTileAt(row - 1, col + 1);
        }
        if (row - 1 >= 0 && col - 1 >= 0)
        {
            topLeft = getTileAt(row - 1, col - 1);
        }
        if (row + 1 < grid.length && col + 1 < grid.length)
        {
            botRight = getTileAt(row + 1, col + 1);
        }
        if (row + 1 < grid.length && col - 1 >= 0)
        {
            botLeft = getTileAt(row + 1, col - 1);
        }
        if (topRight != null && topRight.getRow() - 1 >= 0 && topRight.getColumn() + 1 < grid.length)
        {
            jumpTopRight = getTileAt(topRight.getRow() - 1, topRight.getColumn() + 1);
        }
        if (topLeft != null && topLeft.getRow() - 1 >= 0 && topLeft.getColumn() - 1 >= 0)
        {
            jumpTopLeft = getTileAt(topLeft.getRow() - 1, topLeft.getColumn() - 1);
        }
        if (botRight != null && botRight.getRow() + 1 < grid.length && botRight.getColumn() + 1 < grid.length)
        {
            jumpBotRight = getTileAt(botRight.getRow() + 1, botRight.getColumn() + 1);
        }
        if (botLeft != null && botLeft.getRow() + 1 < grid.length && botLeft.getColumn() - 1 >= 0)
        {
            jumpBotLeft = getTileAt(botLeft.getRow() + 1, botLeft.getColumn() - 1);
        }
        if (player == CheckersGame.Player.HUMAN)
        {
            // if tile in between is occupied by a red checker and next tile is empty
            if (tile.getChecker().isKing() && topRight != null && topRight.isOccupied() && topRight.getChecker().getColour() == Checker.Colour.RED && jumpTopRight != null && !jumpTopRight.isOccupied())
            {
                checkerInBetween = topRight.getChecker();
                toJumpTo.add(jumpTopRight);
                return true;
            }
            if (tile.getChecker().isKing() && topLeft != null && topLeft.isOccupied() && topLeft.getChecker().getColour() == Checker.Colour.RED && jumpTopLeft != null && !jumpTopLeft.isOccupied())
            {
                checkerInBetween = topLeft.getChecker();
                toJumpTo.add(jumpTopLeft);
                return true;
            }
            if (botRight != null && botRight.isOccupied() && botRight.getChecker().getColour() == Checker.Colour.RED && jumpBotRight != null && !jumpBotRight.isOccupied())
            {
                checkerInBetween = botRight.getChecker();
                toJumpTo.add(jumpBotRight);
                return true;
            }
            if (botLeft != null && botLeft.isOccupied() && botLeft.getChecker().getColour() == Checker.Colour.RED && jumpBotLeft != null && !jumpBotLeft.isOccupied())
            {
                checkerInBetween = botLeft.getChecker();
                toJumpTo.add(jumpBotLeft);
                return true;
            }
        }
        else if (player == CheckersGame.Player.AI)
        {
            // if tile in between is occupied by a black checker and next tile is empty
            if (tile.getChecker().isKing() && botRight != null && botRight.isOccupied() && botRight.getChecker().getColour() == Checker.Colour.BLACK && jumpBotRight != null && !jumpBotRight.isOccupied())
            {
                checkerInBetween = botRight.getChecker();
                toJumpTo.add(jumpBotRight);
                return true;
            }
            if (tile.getChecker().isKing() && botLeft != null && botLeft.isOccupied() && botLeft.getChecker().getColour() == Checker.Colour.BLACK && jumpBotLeft != null && !jumpBotLeft.isOccupied())
            {
                checkerInBetween = botLeft.getChecker();
                toJumpTo.add(jumpBotLeft);
                return true;
            }
            if (topRight != null && topRight.isOccupied() && topRight.getChecker().getColour() == Checker.Colour.BLACK && jumpTopRight != null && !jumpTopRight.isOccupied())
            {
                checkerInBetween = topRight.getChecker();
                toJumpTo.add(jumpTopRight);
                return true;
            }
            if (topLeft != null && topLeft.isOccupied() && topLeft.getChecker().getColour() == Checker.Colour.BLACK && jumpTopLeft != null && !jumpTopLeft.isOccupied())
            {
                checkerInBetween = topLeft.getChecker();
                toJumpTo.add(jumpTopLeft);
                return true;
            }
        }

        return false;
    }

    /**
     * Gets all available states.
     * Represents the successor function.
     *
     * @param player the current player.
     * @return all the available states, given the current state of the board.
     */
    public List<Move> getAvailableStates(CheckersGame.Player player)
    {
        List<Move> availableMoves = new ArrayList<>();

        List<Checker> allMovableCheckers = getMovableCheckers(player);
        Move testMove;
        Tile source, target;

        for (int i = 0; i < allMovableCheckers.size(); i++)
        {
            // source tile to be tested
            source = getTileAt(allMovableCheckers.get(i).getRow(), allMovableCheckers.get(i).getColumn());

            for (int row = 0; row < getGrid().length; row++)
            {
                for (int col = 0; col < getGrid().length; col++)
                {
                    // all potential targets represented by empty tiles
                    if (!getTileAt(row, col).isOccupied())
                    {
                        target = getTileAt(row, col);

                        // test move to be checked for validity
                        testMove = new Move(source.getChecker(), target);

                        if (isMoveValid(testMove))
                        {
                            // if move is valid, it is added to list of all possible successors
                            availableMoves.add(testMove);
                        }
                    }
                }
            }
        }

        return availableMoves;
    }

    /**
     * Crowns kings.
     *
     * @param checker the checker which is to be crowned king.
     */
    public void crownKing(Checker checker)
    {
        checker.setKing();
    }

    /**
     * Heuristic function to evaluate the current board state.
     *
     * @return heuristic value of the current board state.
     */
    public int getHeuristics()
    {
        int score = 0;

        List<Checker> allCheckers = getCheckers(Checker.Colour.RED);

        for (Checker c : allCheckers)
        {
            if (c.isKing())
            {
                score += 5;
            }
            else if (c.getRow() <= 3)
            {
                score += 1;
            }
            else if (c.getRow() == 7)
            {
                score += 1;
            }
            else
            {
                score += 3;
            }
        }

        return score;
    }
}
