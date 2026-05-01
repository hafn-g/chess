package com.hafn.chess.ui.swing.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Slf4j
public class BoardMetrics {
    private final int padding = 30;
    private final int rows;
    private final int cols;
    private int side;
    private int cellSize;
    private int boardX;
    private int boardY;

    public BoardMetrics() {
        this.rows = 8;
        this.cols = 8;
    }

    public void calcMetrics(int width, int height) {
        setSide(Math.min(width, height) - getPadding());
        setCellSize(getSide() / rows);
        setBoardX(getPadding());
        setBoardY(0);
        log.trace("Board metrics were recalculated: {}", this);
    }
}
