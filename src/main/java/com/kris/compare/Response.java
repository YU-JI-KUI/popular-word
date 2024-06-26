package com.kris.compare;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Response implements Changeable {
    @ComparableField(name = "deal")
    private Deal deal;
    private List<Facility> facilities;

    private List<ChangeDetailVo> changeDetails = new ArrayList<>();

    @Override
    public String getId() {
        return deal.getDealId();
    }

    @Override
    public Changeable createPlaceholder() {
        return null;
    }
}
