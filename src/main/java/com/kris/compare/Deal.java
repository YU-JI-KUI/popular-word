package com.kris.compare;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Deal implements Changeable {
    private String dealId;
    private String dealName;
    private List<String> codes;
    private List<Fee> fees;

    private List<ChangeDetailVo> changeDetails = new ArrayList<>();

    @Override
    public String getId() {
        return dealId;
    }

    @Override
    public void compareAndRecordChanges(Object last, Object current) {
        if (last instanceof Deal lastDeal && current instanceof Deal currentDeal) {
            currentDeal.compareSimpleField(lastDeal.dealName, currentDeal.dealName, "dealName", lastDeal.changeDetails, currentDeal.changeDetails);
            currentDeal.compareSimpleField(lastDeal.codes, currentDeal.codes, "codes", lastDeal.changeDetails, currentDeal.changeDetails);

            var pair = compareList(lastDeal.fees, currentDeal.fees);
            lastDeal.setFees(pair.getLeft());
            currentDeal.setFees(pair.getRight());
        }
    }

    @Override
    public Changeable createPlaceholder() {
        return null;
    }
}
