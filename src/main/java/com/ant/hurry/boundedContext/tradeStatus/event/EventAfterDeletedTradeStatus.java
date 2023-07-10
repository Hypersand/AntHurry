package com.ant.hurry.boundedContext.tradeStatus.event;

import com.ant.hurry.boundedContext.tradeStatus.entity.TradeStatus;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EventAfterDeletedTradeStatus {
    private List<TradeStatus> tradeStatusList = new ArrayList<>();

    public EventAfterDeletedTradeStatus(List<TradeStatus> tradeStatusList){
        this.tradeStatusList = tradeStatusList;
    }
}
