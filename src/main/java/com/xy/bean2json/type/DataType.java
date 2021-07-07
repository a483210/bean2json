package com.xy.bean2json.type;

/**
 * DataType
 *
 * @author Created by gold on 2020/12/11 11:18
 */
public enum DataType {

    /**
     * 默认值
     */
    DEFAULT_VALUE(0),

    /**
     * 类型
     */
    WRITE_TYPE(1),

    /**
     * mock数据
     */
    MOCK_DATA(0);

    private final int type;

    DataType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    /**
     * 等于
     *
     * @param type 类型
     * @return 是否等于
     */
    public boolean equals(int type) {
        return type == this.type;
    }
}
