package com.xy.bean2json.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiType;
import com.xy.bean2json.base.BaseAction;
import com.xy.bean2json.helper.ClassResolver;
import com.xy.bean2json.utils.JsonUtils;
import com.xy.bean2json.utils.PluginUtils;

/**
 * ConvertToJsonRearAction
 *
 * @author Created by gold on 2020/3/4 16:25
 */
public class ConvertToJsonRearAction extends BaseAction {

    @Override
    protected String actionPerformed(AnActionEvent e, Editor editor, PsiFile psiFile) {
        PsiType selectedType = PluginUtils.parsePsiFile(psiFile);

        String json = ClassResolver.toJsonRear(psiFile, selectedType);

        JsonUtils.copyToClipboard(json);

        return psiFile.getName();
    }
}
