package com.hafn.data;

public class Cell {
    private final int row;
    private final int col;
    private final boolean isLight;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.isLight = (row + col) % 2 == 0;
    }

    public int getCol() {
        return col;
    }

    public boolean isLight() {
        return isLight;
    }

    public int getRow() {
        return row;
    }
}
