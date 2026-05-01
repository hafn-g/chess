package com.hafn.chess.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class Cell {
    private final int row;
    private final int col;
    private final String name;
    private final boolean isLight;

    public Cell(int row, int col, String name) {
        this.row = row;
        this.col = col;
        this.name = name;
        this.isLight = (row + col) % 2 == 0;
    }
}
