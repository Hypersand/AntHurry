package com.ant.hurry.boundedContext.tradeStatus.event;

import com.ant.hurry.boundedContext.tradeStatus.entity.Status;
import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import lombok.Getter;

@Getter
public class EventAfterUpdateStatus {
    private final TradeStatus tradeStatus;
    private final Status status;

    public EventAfterUpdateStatus(TradeStatus tradeStatus, Status status) {
        this.tradeStatus = tradeStatus;
        this.status = status;
    }
}
