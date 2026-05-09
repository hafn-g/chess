package com.hafn.chess.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
public class GameConfig {
    private final GameType gameType;
    private final int rows;
    private final int cols;
    private final int playerTime;
    private final PieceColor queue;

    @Getter
    @Setter
    private String nickname;

    @Getter
    @Setter
    private PieceColor myColor;

    public GameConfig(GameType gameType, int rows, int cols, int playerTime, PieceColor queue) {
        if (rows < 8 || cols < 8) {
            throw new IllegalArgumentException("The minimum is 8 columns and rows");
        }

        this.cols = cols;
        this.gameType = gameType;
        this.playerTime = playerTime;
        this.queue = queue;
        this.rows = rows;
    }
}
