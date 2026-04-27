package com.hafn.chess.logic;

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
    private final List<HistoryMove> historyMoves;
    private Cell clickedCell;

    private final int rows;
    private final int cols;

    public BoardState(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.pieceMap = new HashMap<>();
        this.historyMoves = new ArrayList<>();
        this.possibleMoves = new HashSet<>();
        this.cells = new Cell[rows][cols];
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

    public int getRows() {
        return this.rows;
    }

    public int getCols() {
        return this.cols;
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

    /*
        Single gate on the borders
     */
    public boolean inBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }
}