package com.kris.compare;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class Fee implements Changeable {
    private String feeId;
    private BigDecimal feeAmount;
    private String feeType;

    private List<ChangeDetailVo> changeDetails = new ArrayList<>();

    @Override
    public String getId() {
        return feeId;
    }

    @Override
    public void compareAndRecordChanges(Object last, Object current) {
        if (last instanceof Fee lastFee && current instanceof Fee currentFee) {
            currentFee.compareSimpleField(lastFee.feeType, currentFee.feeType, "feeType", lastFee.changeDetails, currentFee.changeDetails);
            currentFee.compareSimpleField(lastFee.feeAmount, currentFee.feeAmount, "feeAmount", lastFee.changeDetails, currentFee.changeDetails);
        }
    }

    @Override
    public Changeable createPlaceholder() {
        return new Fee();
    }
}
