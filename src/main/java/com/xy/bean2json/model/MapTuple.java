package com.xy.bean2json.model;

/**
 * MapTuple
 *
 * @author Created by gold on 2022/9/12 10:58
 * @since 1.0.0
 */
public class MapTuple {

    public static MapTuple create(FieldAttribute key, FieldAttribute value) {
        return new MapTuple(key, value);
    }

    /**
     * 键
     */
    private final FieldAttribute key;
    /**
     * 值
     */
    private final FieldAttribute value;

    private MapTuple(FieldAttribute key, FieldAttribute value) {
        this.key = key;
        this.value = value;
    }

    public FieldAttribute getKey() {
        return key;
    }

    public FieldAttribute getValue() {
        return value;
    }
}
