package com.xy.bean2json.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.xy.bean2json.helper.ConvertToJsonHelper;
import com.xy.bean2json.base.BaseAction;
import com.xy.bean2json.utils.JavaUtils;
import com.xy.bean2json.utils.PluginUtils;

/**
 * ConvertToJsonAction
 *
 * @author Created by gold on 2020/3/4 16:24
 */
public class ConvertToJsonAction extends BaseAction {

    private final ConvertToJsonHelper helper = new ConvertToJsonHelper();

    @Override
    protected void actionPerformed(AnActionEvent e, Editor editor, PsiFile psiFile) {
        PsiClass selectedClass = PluginUtils.parseForFile(editor, psiFile);

        String json = helper.toJson(selectedClass);

        JavaUtils.copyToClipboard(json);
    }
}
