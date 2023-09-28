package com.github.leheyue.wrapper.resultmap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ResultList extends ArrayList<IResult> {

    private final Set<String> propertySet = new HashSet<>();


    @Override
    public boolean add(IResult result) {
        if (propertySet.contains(result.getProperty())) {
            super.removeIf(i -> i.getProperty().equals(result.getProperty()));
        } else {
            propertySet.add(result.getProperty());
        }
        return super.add(result);
    }
}
