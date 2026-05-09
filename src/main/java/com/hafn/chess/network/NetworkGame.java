package com.hafn.chess.network;

import com.hafn.chess.application.usecase.BoardNetworkServerController;
import com.hafn.chess.domain.port.BoardPort;
import lombok.Getter;

@Getter
public class NetworkGame {
    private final BoardNetworkServerController controller;
    private final BoardPort state;

    public NetworkGame(BoardPort state) {
        this.state = state;
        this.controller = new BoardNetworkServerController(state);
    }
}
