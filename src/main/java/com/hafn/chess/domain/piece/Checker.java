package com.hafn.chess.domain.piece;

import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.HistoryMove;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.model.PieceType;
import com.hafn.chess.domain.port.BoardPort;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Checker extends Piece {
    private HashMap<Cell, Piece> pieceDestructionCell;

    public Checker(PieceColor color, Cell cell) {
        super(PieceType.CHECKER, color, cell);
        pieceDestructionCell = new HashMap<>();
    }

    private enum Direction {
        LEFT_TOP((byte) -1, (byte) -1),
        RIGHT_TOP((byte) -1, (byte) 1),
        LEFT_BOTTOM((byte) 1, (byte) -1),
        RIGHT_BOTTOM((byte) 1, (byte) 1);

        final byte row;
        final byte col;

        Direction(byte row, byte col) {
            this.row = row;
            this.col = col;
        }
    }

    @Override
    public void getNewPossibleMoves(BoardPort state) {
        Set<Cell> moves = new HashSet<>();
        HashMap<Cell, Piece> hashMap = new HashMap<>();

        for (Direction direction : Direction.values()) {
            int r = this.getCell().getRow() + direction.row;
            int c = this.getCell().getCol() + direction.col;

            if (!state.inBounds(r, c)) continue;

            Cell cell = state.getCell(r, c);
            Piece piece = state.getPiece(cell);

            if (piece == null) {
                if (this.getColor().equals(PieceColor.WHITE)) {
                    if (!direction.equals(Direction.LEFT_BOTTOM) && !direction.equals(Direction.RIGHT_BOTTOM)) {
                        moves.add(cell);
                    }
                }
                if (this.getColor().equals(PieceColor.BLACK)) {
                    if (!direction.equals(Direction.LEFT_TOP) && !direction.equals(Direction.RIGHT_TOP)) {
                        moves.add(cell);
                    }
                }
            } else {
                if (!piece.getColor().equals(this.getColor())) {
                    Cell isCutCell = canInCutDown(state, piece, direction);
                    if (isCutCell != null) {
                        moves.add(cell);
                        moves.add(isCutCell);
                        hashMap.put(isCutCell, piece);
                    }
                }
            }
        }

        if (!hashMap.isEmpty()) {
            setPieceDestructionCell(hashMap);
        }

        setPossibleMoves(moves);
    }

    private Cell canInCutDown(BoardPort state, Piece targetPiece, Direction direction) {

        int r = targetPiece.getCell().getRow() + direction.row;
        int c = targetPiece.getCell().getCol() + direction.col;

        if (!state.inBounds(r, c)) {
            return null;
        }

        Cell cell = state.getCell(r, c);

        if (state.getPiece(cell) == null) {
            return cell;
        }

        return null;
    }

    private void setPieceDestructionCell(HashMap<Cell, Piece> pieceDestructionCell) {
        this.pieceDestructionCell = pieceDestructionCell;
    }

    public boolean isMoveIsAvailableAfterWalking(BoardPort state) {
        Set<Cell> moves = new HashSet<>();
        HashMap<Cell, Piece> hashMap = new HashMap<>();

        for (Direction direction : Direction.values()) {
            int r = this.getCell().getRow() + direction.row;
            int c = this.getCell().getCol() + direction.col;

            if (!state.inBounds(r, c)) continue;

            Cell targetCell = state.getCell(r, c);
            Piece targetPiece = state.getPiece(targetCell);

            if (targetPiece != null && !targetPiece.getColor().equals(this.getColor())) {
                Cell isCutCell = canInCutDown(state, targetPiece, direction);
                if (isCutCell != null) {
                    moves.add(targetCell);
                    moves.add(isCutCell);
                    hashMap.put(isCutCell, targetPiece);
                }
            }
        }

        if (!hashMap.isEmpty()) {
            setPieceDestructionCell(hashMap);
            setPossibleMoves(moves);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void execute(BoardPort state, Cell clicked) {
        Cell oldCel = state.getClickedCell();
        Piece clickedCellPiece = state.getPiece(clicked);

        if (oldCel == null) throw new NullPointerException("The variable oldCel is null");

        if (clickedCellPiece != null) {
            return;
        }

        Piece piece = this.pieceDestructionCell.get(clicked);
        if (piece != null) {
            state.removePiece(piece.getCell());
        }

        HistoryMove historyMove = new HistoryMove(this, oldCel, clicked, piece);
        state.addHistoryMoves(historyMove);
        state.setClickedCell(null);

        this.setCell(clicked);
        state.removePiece(oldCel);
        state.addPiece(this);
    }
}
