package com.hafn.chess.domain.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return row == cell.row && col == cell.col && isLight == cell.isLight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col, isLight);
    }

    @Override
    public String toString() {
        return "Cell{" +
                "col=" + col +
                ", row=" + row +
                ", isLight=" + isLight +
                '}';
    }
}
