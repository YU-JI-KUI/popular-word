package com.kris.compare;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Response implements Changeable {
    private Deal deal;
    private List<Facility> facilities;

    private List<ChangeDetailVo> changeDetails = new ArrayList<>();

    @Override
    public String getId() {
        return deal.getDealId();
    }

    @Override
    public void compareAndRecordChanges(Object last, Object current) {
        if (last instanceof Response lastResponse && current instanceof Response currentResponse) {
            currentResponse.compareSimpleField(lastResponse.deal, currentResponse.deal, "deal", null, null);
        }
    }

    @Override
    public Changeable createPlaceholder() {
        return null;
    }
}
