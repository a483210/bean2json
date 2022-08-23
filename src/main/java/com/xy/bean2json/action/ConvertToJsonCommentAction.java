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

import java.util.Map;

/**
 * ConvertToJsonCommentAction
 *
 * @author Created by gold on 2020/3/4 16:25
 */
public class ConvertToJsonCommentAction extends BaseAction {

    @Override
    protected String actionPerformed(AnActionEvent e, Editor editor, PsiFile psiFile) {
        Project project = editor.getProject();

        PsiType selectedType = PluginUtils.parsePsiFile(project, psiFile);

        Map<String, Object> json = ClassResolver.resolveComment(project, psiFile, selectedType);

        JsonUtils.copyToClipboard(JsonUtils.toJson(json));

        return psiFile.getName();
    }
}
