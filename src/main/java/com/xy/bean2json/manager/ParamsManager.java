package com.xy.bean2json.manager;

import com.xy.bean2json.type.DataType;

/**
 * ParamsManager
 *
 * @author Created by gold on 2020/3/11 11:14
 */
public class ParamsManager {

    private static volatile ParamsManager instance;

    public static ParamsManager get() {
        if (instance == null) {
            synchronized (ParamsManager.class) {
                if (instance == null) {
                    instance = new ParamsManager();
                }
            }
        }
        return instance;
    }

    private ParamsManager() {
    }

    private DataType dataType = DataType.MOCK_DATA;

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public boolean isDataType(DataType dataType) {
        return this.dataType == dataType;
    }
}
