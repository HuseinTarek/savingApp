package com.swedenrosca.ui;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public class TurnOrderInfo {
    private final int turnOrder;
    private final LocalDateTime paymentDate;
    private final BigDecimal amount;

    public TurnOrderInfo(int turnOrder, LocalDateTime paymentDate, BigDecimal amount) {
        this.turnOrder = turnOrder;
        this.paymentDate = paymentDate;
        this.amount = amount;
    }

    public int getTurnOrder() { return turnOrder; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public BigDecimal getAmount() { return amount; }
} 