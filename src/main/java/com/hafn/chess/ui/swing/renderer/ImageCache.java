package com.hafn.chess.ui.swing.renderer;

import com.hafn.chess.domain.model.PieceColor;
import com.hafn.chess.domain.model.PieceType;
import com.hafn.chess.domain.piece.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class ImageCache {

    private static final Map<String, Image> images = new HashMap<>();

    static {
        load("white_pawn", "/assets/pieces/white/pawn.png");
        load("black_pawn", "/assets/pieces/black/pawn.png");
        load("white_rook", "/assets/pieces/white/rook.png");
        load("black_rook", "/assets/pieces/black/rook.png");
        load("white_knight", "/assets/pieces/white/knight.png");
        load("black_knight", "/assets/pieces/black/knight.png");
        load("white_bishop", "/assets/pieces/white/bishop.png");
        load("black_bishop", "/assets/pieces/black/bishop.png");
        load("white_queen", "/assets/pieces/white/queen.png");
        load("black_queen", "/assets/pieces/black/queen.png");
        load("white_king", "/assets/pieces/white/king.png");
        load("black_king", "/assets/pieces/black/king.png");

        load("white_checker", "/assets/pieces/white/checker.png");
        load("black_checker", "/assets/pieces/black/checker.png");
    }

    private static void load(String key, String path) {
        Image img = new ImageIcon(
                Objects.requireNonNull(ImageCache.class.getResource(path))
        ).getImage();
        images.put(key, img);
    }

    public static Image get(String key) {
        return images.get(key);
    }

    public static Image get(Piece piece) {
        String key = piece.getColor().name().toLowerCase() + "_" + piece.getType().name().toLowerCase();
        return get(key);
    }

    public static Image get(PieceType type, PieceColor color) {
        String key = color.name().toLowerCase() + "_" + type.name().toLowerCase();
        return get(key);
    }
}