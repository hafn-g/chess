package com.hafn.data;

public class BoardMetrics {
    private int padding = 30;
    private int side;
    private int cellSize;
    private int boardX;
    private int boardY;
    private int rows;
    private int cols;

    public BoardMetrics(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    public void calcMetrics(int width, int height) {
        setSide(Math.min(width, height) - getPadding());
        setCellSize(getSide() / rows);
        setBoardX(getPadding());
        setBoardY(0);
    }

    public int getBoardX() {
        return boardX;
    }

    public void setBoardX(int boardX) {
        this.boardX = boardX;
    }

    public int getBoardY() {
        return boardY;
    }

    public void setBoardY(int boardY) {
        this.boardY = boardY;
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }
}
