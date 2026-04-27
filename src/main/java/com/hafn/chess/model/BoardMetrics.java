package com.hafn.chess.model;

import java.util.Objects;

public class BoardMetrics {
    private final int padding = 30;
    private int side;
    private int cellSize;
    private int boardX;
    private int boardY;
    private final int rows;
    private final int cols;

    public BoardMetrics() {
        this.rows = 8;
        this.cols = 8;
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

    public int getPadding() {
        return padding;
    }

    public int getRows() {
        return rows;
    }

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BoardMetrics that = (BoardMetrics) o;
        return side == that.side && cellSize == that.cellSize && boardX == that.boardX && boardY == that.boardY &&
                rows == that.rows && cols == that.cols;
    }

    @Override
    public int hashCode() {
        return Objects.hash(padding, side, cellSize, boardX, boardY, rows, cols);
    }

    @Override
    public String toString() {
        return "BoardMetrics{" +
                "boardX=" + boardX +
                ", padding=" + padding +
                ", side=" + side +
                ", cellSize=" + cellSize +
                ", boardY=" + boardY +
                ", rows=" + rows +
                ", cols=" + cols +
                '}';
    }
}
