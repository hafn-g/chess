package com.hafn.chess.application.usecase;

import com.hafn.chess.domain.model.Cell;
import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.piece.Pawn;
import com.hafn.chess.domain.piece.Piece;
import com.hafn.chess.domain.piece.Queen;
import com.hafn.chess.domain.port.BoardPort;
import com.hafn.chess.domain.service.CheckRule;
import com.hafn.chess.network.MoveException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class BoardNetworkServerController {
    private final BoardPort state;

    public void move(String from, String to) throws MoveException {
        Cell cellFrom, cellTo;
        Piece pieceFrom;
        try {
            cellFrom = state.getCell(from);
            cellTo = state.getCell(to);

            pieceFrom = state.getPiece(cellFrom);
            if (pieceFrom == null) {
                throw new IllegalArgumentException("The piece in the cage was not found");
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }

        toMoveChess(pieceFrom, cellTo);
    }

    private void toMoveChess(Piece piece, Cell to) throws MoveException {
        Cell fromCell = piece.getCell();
        Piece clickedPiece = state.getPiece(to);

        state.setClickedCell(piece.getCell());

        if (state.isWhiteShah() || state.isBlackShah()) {
            // King is in check – try to move out of check
            piece.execute(state, to);
            CheckRule.detectChecks(state);

            if (state.isWhiteShah() || state.isBlackShah()) {
                // After the move, the king is still in check – illegal move
                piece.undo(state);
                CheckRule.detectChecks(state);
                log.warn("Move failed: King would still be in check.");
                throw new MoveException("King would still be in check");
            } else {
                // Move successfully resolved the check
                handlePromotionIfNeeded(piece, to);
                state.nextQueue();
                if (clickedPiece != null) {
                    log.info("Move successful (King is no longer in check): {} -> {} ({} was captured by {})", fromCell.getName(), to.getName(), clickedPiece, piece);
                } else {
                    log.info("Move successful (King is no longer in check): {} -> {}", fromCell.getName(), to.getName());
                }
                state.addTime(piece.getColor());
            }
        } else {
            // No check – normal move
            piece.execute(state, to);
            CheckRule.detectChecks(state);

            if (state.isShah(piece.getColor())) {
                // The move puts the moving player's own king in check – illegal
                piece.undo(state);
                CheckRule.detectChecks(state);
                log.warn("Move failed: Would put own king in check.");
                throw new MoveException("Would put own king in check");
            } else {
                // Legal move
                handlePromotionIfNeeded(piece, to);
                state.nextQueue();
                if (clickedPiece != null) {
                    log.info("Move successful: {} -> {} ({} was captured by {})", fromCell.getName(), to.getName(), clickedPiece, piece);
                } else {
                    log.info("Move successful: {} -> {}", fromCell.getName(), to.getName());
                }
                state.addTime(piece.getColor());
            }
        }
    }

    // Checking and handling pawn promotions
    private void handlePromotionIfNeeded(Piece piece, Cell cell) {
        if (piece instanceof Pawn) {
            int lastRow = (piece.getColor() == PieceColor.WHITE) ? 0 : state.getRows() - 1;
            if (cell.getRow() == lastRow && state != null) {
                PieceColor color = piece.getColor();
                Piece promoted = new Queen(color, cell);

                state.removePiece(cell);
                state.addPiece(promoted);
                CheckRule.detectChecks(state);
                log.info("Pawn of color {} ({}) promoted to piece {}", color, cell, promoted.getType());
            }
        }
    }
}
