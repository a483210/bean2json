package com.xy.bean2json.menu;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.xy.bean2json.manager.ParamsManager;
import com.xy.bean2json.type.DataType;
import org.jetbrains.annotations.NotNull;

/**
 * MockDataAction
 *
 * @author Created by gold on 2020/12/10 11:02
 */
public class MockDataAction extends ToggleAction {

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return ParamsManager.get().isDataType(DataType.MOCK_DATA);
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        ParamsManager.get().setDataType(DataType.MOCK_DATA);
    }
}
