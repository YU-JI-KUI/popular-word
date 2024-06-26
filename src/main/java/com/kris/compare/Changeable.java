package com.kris.compare;

import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public interface Changeable {

    String getId();

//    void compareAndRecordChanges(Object last, Object current);

    Changeable createPlaceholder();

    List<ChangeDetailVo> getChangeDetails();
    default void compareAndRecordChanges(Object last, Object current) {
        if (last == null || current == null) {
            return;
        }

        Class<?> clazz = last.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(ComparableField.class)) {
                String fieldName = field.getAnnotation(ComparableField.class).name();
                if (fieldName.isEmpty()) {
                    fieldName = field.getName();
                }

                try {
                    // 使用 getter 方法访问字段
                    Method getter = clazz.getMethod("get" + capitalize(field.getName()));
                    Object lastValue = getter.invoke(last);
                    Object currentValue = getter.invoke(current);

                    if (lastValue instanceof List<?> lastList && currentValue instanceof List<?> currentList) {
                        // 处理 Changeable 类型的列表
                        if (isChangeableList(lastList, currentList)) {
                            var pair = compareList((List<Changeable>) lastList, (List<Changeable>) currentList);
                            Method setter = clazz.getMethod("set" + capitalize(field.getName()), List.class);
                            setter.invoke(last, pair.getLeft());
                            setter.invoke(current, pair.getRight());
                        } else {
                            // 处理简单类型的列表
                            compareSimpleField(lastList, currentList, fieldName, ((Changeable) last).getChangeDetails(), ((Changeable) current).getChangeDetails());
                        }
                    } else {
                        // 处理其他类型
                        compareSimpleField(lastValue, currentValue, fieldName, ((Changeable) last).getChangeDetails(), ((Changeable) current).getChangeDetails());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    default boolean isChangeableList(List<?> lastList, List<?> currentList) {
        return !lastList.isEmpty() && !currentList.isEmpty() && lastList.get(0) instanceof Changeable && currentList.get(0) instanceof Changeable;
    }

    default void compareSimpleField(Object last, Object current, String fieldName, List<ChangeDetailVo> lastChangeDetails, List<ChangeDetailVo> currentChangeDetails) {
        boolean isChanged = false;

        if (last instanceof List<?> lastList && current instanceof List<?> currentList) {
            // Compare simple lists
            if (!compareSimpleLists(lastList, currentList)) {
                isChanged = true;
            }
        } else if (last instanceof Changeable lastChangeable && current instanceof Changeable currentChangeable) {
            // Compare nested Changeable objects
            List<ChangeDetailVo> tempLastChangeDetails = new ArrayList<>();
            List<ChangeDetailVo> tempCurrentChangeDetails = new ArrayList<>();
            currentChangeable.compareAndRecordChanges(lastChangeable, currentChangeable);
            if (!tempLastChangeDetails.isEmpty() || !tempCurrentChangeDetails.isEmpty()) {
                isChanged = true;
                lastChangeDetails.addAll(tempLastChangeDetails);
                currentChangeDetails.addAll(tempCurrentChangeDetails);
            }
        } else {
            // Compare other simple types
            if (!Objects.equals(last, current)) {
                isChanged = true;
            }
        }

        if (isChanged) {
            ChangeDetailVo detail = new ChangeDetailVo();
            detail.setType("FIELD");
            detail.setCode(fieldName);
            detail.setValue(String.format("Changed from %s to %s", last, current));
            lastChangeDetails.add(detail);
            currentChangeDetails.add(detail);
        }
    }

    default boolean compareSimpleLists(List<?> lastList, List<?> currentList) {
        if (lastList == null || currentList == null) {
            return lastList == currentList;
        }
        if (lastList.size() != currentList.size()) {
            return false;
        }
        for (int i = 0; i < lastList.size(); i++) {
            if (!Objects.equals(lastList.get(i), currentList.get(i))) {
                return false;
            }
        }
        return true;
    }

    default <T extends Changeable> Pair<List<T>, List<T>> compareList(List<T> lastList, List<T> currentList) {
        List<T> mutableLastList = new ArrayList<>(lastList != null ? lastList : Collections.emptyList());
        List<T> mutableCurrentList = new ArrayList<>(currentList != null ? currentList : Collections.emptyList());

        Map<String, T> lastMap = new HashMap<>();
        Map<String, T> currentMap = new HashMap<>();

        for (T item : mutableLastList) {
            lastMap.put(item.getId(), item);
        }

        for (T item : mutableCurrentList) {
            currentMap.put(item.getId(), item);
        }

        for (Map.Entry<String, T> entry : currentMap.entrySet()) {
            String id = entry.getKey();
            T currentItem = entry.getValue();
            T lastItem = lastMap.get(id);

            if (lastItem == null) {
                ChangeDetailVo addDetail = new ChangeDetailVo();
                addDetail.setType("ADD");
                addDetail.setCode("ADD");
                addDetail.setValue(String.format("Item added with ID %s", id));
                currentItem.getChangeDetails().add(addDetail);

                T placeholder = (T) currentItem.createPlaceholder();
                placeholder.getChangeDetails().add(addDetail);
                mutableLastList.add(placeholder);
            } else {
                currentItem.compareAndRecordChanges(lastItem, currentItem);
            }
        }

        for (Map.Entry<String, T> entry : lastMap.entrySet()) {
            String id = entry.getKey();
            if (!currentMap.containsKey(id)) {
                ChangeDetailVo removeDetail = new ChangeDetailVo();
                removeDetail.setType("REMOVE");
                removeDetail.setCode("REMOVE");
                removeDetail.setValue(String.format("Item removed with ID %s", id));
                entry.getValue().getChangeDetails().add(removeDetail);

                T placeholder = (T) entry.getValue().createPlaceholder();
                placeholder.getChangeDetails().add(removeDetail);
                mutableCurrentList.add(placeholder);
            }
        }

        return Pair.of(mutableLastList, mutableCurrentList);
    }
}
