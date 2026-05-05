package com.hafn.chess.ui.swing.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardMetricsTest {
    private BoardMetrics metrics;

    @BeforeEach
    void setUp() {
        metrics = new BoardMetrics(8, 8);
    }

    @Test
    void testCalcMetricsShouldCalculateAllFieldsCorrectly() {
        metrics.calcMetrics(640, 770);
        Assertions.assertAll(
                () -> Assertions.assertEquals(610, metrics.getSide()),
                () -> Assertions.assertEquals(76, metrics.getCellSize()),
                () -> Assertions.assertEquals(30, metrics.getBoardX()),
                () -> Assertions.assertEquals(0, metrics.getBoardY())
        );
    }
}
