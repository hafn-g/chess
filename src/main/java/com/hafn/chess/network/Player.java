package com.hafn.chess.network;

import com.hafn.chess.domain.model.PieceColor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Player {
    private String nickname;
    private PieceColor color;

    public Player(String nickname, PieceColor color) {
        this.nickname = nickname;
        this.color = color;
    }
}
