package com.xy.bean2json.menu;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.xy.bean2json.manager.ParamsManager;
import com.xy.bean2json.type.DataType;
import org.jetbrains.annotations.NotNull;

/**
 * DefaultAction
 *
 * @author Created by gold on 2020/12/11 10:56
 */
public class DefaultAction extends ToggleAction {

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return ParamsManager.get().isDataType(DataType.DEFAULT_VALUE);
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        ParamsManager.get().setDataType(DataType.DEFAULT_VALUE);
    }
}
