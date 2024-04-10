package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author TODO: YOUR NAME HERE
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */

    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        board.setViewingPerspective(side);

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.
        for(int i=3;i>=0;i--){
            for(int j=3;j>0;j--){
                int temp = j;//保存当前节点(后文称为入口节点),方便后续移动操作
                int tempValue = 0;
                if(board.tile(i,j)==null){//当前节点为空,就一直检测下一个节点,直到节点不为空或者遍历完当前列
                    while(j-1>0&&board.tile(i,j-1)==null){
                        j-=1;
                    }if(board.tile(i,j-1)!=null){//如果检测停下来的节点不为空,就将其移动到入口节点处
                    board.move(i,temp,board.tile(i,j-1));
                    tempValue=board.tile(i,temp).value();//暂存刚刚移动的节点的值,为后续可能合并作准备
                    changed = true;
                    }
                    while(j-1>0&&board.tile(i,j-1)==null){//如果上一步没有遍历完列,就继续遍历,直到节点不为空或者遍历完
                        j-=1;
                    }if(board.tile(i,j-1)!=null) {
                        if (board.tile(i, j-1 ).value() == tempValue) {//如果节点不为空,比较检测停下来的节点和刚刚暂存的节点的值
                            board.move(i, temp, board.tile(i, j-1));//如果相同就合并
                            changed = true;
                            score=score+2*tempValue;
                        }
                    }

                }
                else{//当前节点不为空
                    while(j-1>0&&board.tile(i,j-1)==null){//一直检测下一个节点,直到节点不为空或者遍历完当前列
                        j-=1;
                    }if(board.tile(i,j-1)!=null&&board.tile(i,temp)!=null) {
                        if (board.tile(i,j-1).value() == board.tile(i, temp).value()) {//如果停下来时的节点的值与入口节点相等,就合并
                            board.move(i, temp, board.tile(i,j-1));
                            changed = true;
                            score=score+board.tile(i, temp).value();
                        } else {
                            board.move(i, temp-1, board.tile(i,j-1));//不相等,就移动到入口节点的下一个节点
                            changed = true;
                        }
                    }
                }
                j = temp;

            }
        }

        board.setViewingPerspective(Side.NORTH);


        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        int size = b.size();
        int i,j;
        for(i=0;i<size;i++){
            for(j=0;j<size;j++){
                if(b.tile(i,j)==null){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.
        int size = b.size();
        int i,j;
        for(i=0;i<size;i++){
            for(j=0;j<size;j++){
                if(b.tile(i,j)!=null&&b.tile(i,j).value()==2048){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        int size = b.size();
        int col, raw;

        if (emptySpaceExists(b) || maxTileExists(b)) {
            return true;
        }

        int[][] checkBoard = new int[size + 2][size + 2];
        for(int i=0;i<size+2;i++){
            for(int j=0;j<size+2;j++){
                checkBoard [i][j]=1;
            }
        }

        for(int i=1;i<size+1;i++){
            for(int j=1;j<size+1;j++){
                checkBoard [i][j] = b.tile(i-1,j-1).value();
            }
        }

        for(int i=1;i<size+1;i++){
            for(int j=1;j<size+1;j++){
                if(     checkBoard[i][j]==checkBoard[i][j+1]||
                        checkBoard[i][j]==checkBoard[i][j-1]||
                        checkBoard[i][j]==checkBoard[i+1][j]||
                        checkBoard[i][j]==checkBoard[i-1][j]
                )
                    return true;
            }
        }

        return false;

    }
    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
