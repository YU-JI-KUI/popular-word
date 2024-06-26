package com.kris.compare;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Deal implements Changeable {
    private String dealId;
    @ComparableField(name = "dealName")
    private String dealName;
    @ComparableField(name = "codes")
    private List<String> codes;
    @ComparableField(name = "fees")
    private List<Fee> fees;

    private List<ChangeDetailVo> changeDetails = new ArrayList<>();

    @Override
    public String getId() {
        return dealId;
    }

    @Override
    public Changeable createPlaceholder() {
        return null;
    }
}
