package com.hafn.chess.logic;

import com.hafn.chess.model.BoardMetrics;
import com.hafn.chess.model.Cell;
import com.hafn.chess.model.HistoryMove;
import com.hafn.chess.model.piece.Piece;

import java.util.*;

import static com.hafn.chess.logic.BoardInitializer.initCells;
import static com.hafn.chess.logic.BoardInitializer.initPieces;

public class BoardState {

    private final Map<Cell, Piece> pieceMap;
    private final Cell[][] cells;
    private Set<Cell> possibleMoves;
    private final BoardMetrics metrics;
    private final List<HistoryMove> historyMoves;
    private Cell clickedCell;

    public BoardState(BoardMetrics metrics) {
        this.pieceMap = new HashMap<>();
        this.historyMoves = new ArrayList<>();
        this.possibleMoves = new HashSet<>();
        this.metrics = metrics;
        this.cells = new Cell[metrics.getRows()][metrics.getCols()];
        this.clickedCell = null;

        initCells(this);
        initPieces(this);
    }

    public Piece getPiece(Cell cell) {
        if (cell == null) {
            return null;
        }

        return pieceMap.get(cell);
    }

    public void addPiece(Piece piece) {
        pieceMap.put(piece.getCell(), piece);
    }

    public void removePiece(Cell cell) {
        pieceMap.remove(cell);
    }

    public int rows() {
        return metrics.getRows();
    }

    public int cols() {
        return metrics.getCols();
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    public void addCell(int row, int col) {
        cells[row][col] = new Cell(row, col);
    }

    public Set<Cell> getPossibleMoves() {
        return possibleMoves;
    }

    public void setPossibleMoves(Set<Cell> possibleMoves) {
        this.possibleMoves = possibleMoves;
    }

    public void clearPossibleMoves() {
        this.possibleMoves = new HashSet<>();
        this.setClickedCell(null);
    }

    public BoardMetrics getMetrics() {
        return metrics;
    }

    public void addHistoryMoves(HistoryMove historyMove) {
        this.historyMoves.add(historyMove);
    }

    public Cell getClickedCell() {
        return clickedCell;
    }

    public void setClickedCell(Cell clickedCell) {
        this.clickedCell = clickedCell;
    }
}