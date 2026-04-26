package com.hafn.logic;

import com.hafn.data.BoardMetrics;
import com.hafn.data.Cell;
import com.hafn.data.Piece;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.hafn.logic.BoardInitializer.initCells;
import static com.hafn.logic.BoardInitializer.initPieces;

public class BoardState {

    private final List<Piece> pieces;
    private final Cell[][] cells;
    private Set<Point> possibleMoves;
    private final BoardMetrics metrics;

    public BoardState(BoardMetrics metrics) {
        this.pieces = new ArrayList<>();
        this.possibleMoves = new HashSet<>();
        this.metrics = metrics;
        this.cells = new Cell[metrics.getRows()][metrics.getCols()];

        initCells(this);
        initPieces(this);
    }

    public Piece getPiece(Cell cell) {
        if (cell == null) {
            return null;
        }

        for (Piece piece : pieces) {
            if (piece.getCell() == cell) {
                return piece;
            }
        }

        return null;
    }

    public Piece getPiece(int row, int col) {
        for (Piece piece : pieces) {
            if (piece.getCell().getCol() == col && piece.getCell().getRow() == row) {
                return piece;
            }
        }

        return null;
    }

    public void setPiece(Piece piece) {
        pieces.add(piece);
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

    public Cell getCell(Point point) {
        return cells[point.y][point.x];
    }

    public void addCell(int row, int col) {
        cells[row][col] = new Cell(row, col);
    }

    public Set<Point> getPossibleMoves() {
        return possibleMoves;
    }

    public void setPossibleMoves(Set<Point> possibleMoves) {
        this.possibleMoves = possibleMoves;
    }

    public void clearPossibleMoves() {
        this.possibleMoves = new HashSet<>();
    }

    public BoardMetrics getMetrics() {
        return metrics;
    }
}