package com.xy.bean2json.menu;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.xy.bean2json.manager.ParamsManager;
import com.xy.bean2json.type.DataType;
import org.jetbrains.annotations.NotNull;

/**
 * WriteTypeAction
 *
 * @author Created by gold on 2020/12/11 10:58
 */
public class WriteTypeAction extends ToggleAction {

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return ParamsManager.get().isDataType(DataType.WRITE_TYPE);
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        ParamsManager.get().setDataType(DataType.WRITE_TYPE);
    }
}
