package com.xy.bean2json.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiType;
import com.xy.bean2json.helper.ClassResolver;
import com.xy.bean2json.base.BaseAction;
import com.xy.bean2json.utils.JsonUtils;
import com.xy.bean2json.utils.PluginUtils;

/**
 * ConvertToJsonAction
 *
 * @author Created by gold on 2020/3/4 16:24
 */
public class ConvertToJsonAction extends BaseAction {

    @Override
    protected String actionPerformed(AnActionEvent e, Editor editor, PsiFile psiFile) {
        Project project = editor.getProject();

        PsiType selectedType = PluginUtils.parsePsiFile(project, psiFile);

        String json = ClassResolver.toJson(project, psiFile, selectedType);

        JsonUtils.copyToClipboard(json);

        return psiFile.getName();
    }
}
