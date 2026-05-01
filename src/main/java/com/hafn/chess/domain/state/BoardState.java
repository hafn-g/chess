package com.hafn.chess.domain.state;

import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.HistoryMove;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.piece.Piece;
import com.hafn.chess.domain.port.BoardPort;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@EqualsAndHashCode
public class BoardState implements BoardPort {

    private final Map<Cell, Piece> pieceMap;
    private final Cell[][] cells;
    private final LocalDateTime startGame;
    private final AtomicInteger whiteTime;
    private final AtomicInteger blackTime;

    @Setter
    @Getter
    private Cell clickedCell;
    @Setter
    @Getter
    private boolean isWhiteShah = false;
    @Setter
    @Getter
    private boolean isBlackShah = false;

    @Getter
    private final List<HistoryMove> historyMoves;
    @Getter
    private PieceColor queue;
    @Getter
    private final int rows;
    @Getter
    private final int cols;
    @Getter
    private boolean pauseGame = false;

    private ScheduledExecutorService scheduler;

    public BoardState(int rows, int cols, PieceColor queue) {
        this.rows = rows;
        this.cols = cols;
        this.pieceMap = new HashMap<>();
        this.historyMoves = new ArrayList<>();
        this.cells = new Cell[rows][cols];
        this.clickedCell = null;
        this.queue = queue;
        this.startGame = LocalDateTime.now();
        this.whiteTime = new AtomicInteger(600);
        this.blackTime = new AtomicInteger(600);

        startTimer();
    }

    private void startTimer() {
        scheduler = Executors.newSingleThreadScheduledExecutor();

        log.info(
                "Timer was started (Start time {}, Black time {}s, White time {}s)",
                startGame,
                blackTime,
                whiteTime
        );
        Runnable task = () -> {
            if (queue.equals(PieceColor.BLACK)) {
                blackTime.decrementAndGet();
            } else {
                whiteTime.decrementAndGet();
            }

            if (whiteTime.get() <= 0 || blackTime.get() <= 0) {
                log.info(
                        "Timer stopped due to one of the players running out of time (Start time {}, Black time {}s, White time {}s)",
                        startGame,
                        blackTime,
                        whiteTime
                );
                stopGame();
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

    public void stopGame() {
        scheduler.shutdown();
        pauseGame = true;

        log.info("Game finished");
    }

    public Piece getPiece(Cell cell) {
        if (cell == null) {
            return null;
        }

        return pieceMap.get(cell);
    }

    public Map<Cell, Piece> getPieces() {
        return pieceMap;
    }

    public void addPiece(Piece piece) {
        pieceMap.put(piece.getCell(), piece);
    }

    public void removePiece(Cell cell) {
        pieceMap.remove(cell);
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    public void addCell(int row, int col, String name) {
        cells[row][col] = new Cell(row, col, name);
    }

    public void addHistoryMoves(HistoryMove historyMove) {
        this.historyMoves.add(historyMove);
    }

    public boolean isShah(PieceColor color) {
        if (color.equals(PieceColor.BLACK)) {
            return isBlackShah;
        } else {
            return isWhiteShah;
        }
    }

    public void nextQueue() {
        this.queue = queue.next();
    }

    public LocalDateTime getStartGameDateTime() {
        return startGame;
    }

    public int getBlackTime() {
        return blackTime.get();
    }

    public int getWhiteTime() {
        return whiteTime.get();
    }

    public void addTime(PieceColor color) {
        if (color.equals(PieceColor.BLACK)) {
            blackTime.addAndGet(10);
        } else {
            whiteTime.addAndGet(10);
        }
    }

    /**
     *  Single gate on the borders
     */
    public boolean inBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    @Override
    public String toString() {
        return "BoardState{" +
                "blackTime=" + blackTime +
                ", clickedCell=" + clickedCell +
                ", cols=" + cols +
                ", isBlackShah=" + isBlackShah +
                ", isWhiteShah=" + isWhiteShah +
                ", pauseGame=" + pauseGame +
                ", pieceMap=" + pieceMap +
                ", queue=" + queue +
                ", rows=" + rows +
                ", startGame=" + startGame +
                ", whiteTime=" + whiteTime +
                '}';
    }
}
