package com.hafn.ui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ImageCache {

    private static final Map<String, Image> images = new HashMap<>();

    static {
        load("white_pawn", "/white-pawn.png");
        load("black_pawn", "/black-pawn.png");
        load("white_rook", "/white-rook.png");
        load("black_rook", "/black-rook.png");
        load("white_knight", "/white-knight.png");
        load("black_knight", "/black-knight.png");
        load("white_bishop", "/white-bishop.png");
        load("black_bishop", "/black-bishop.png");
        load("white_queen", "/white-queen.png");
        load("black_queen", "/black-queen.png");
        load("white_king", "/white-king.png");
        load("black_king", "/black-king.png");
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
}