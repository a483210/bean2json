package com.xy.bean2json.action;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.KotlinFileType;

/**
 * ConvertGroupAction
 *
 * @author Created by gold on 2020/12/10 14:29
 */
public class ConvertGroupAction extends DefaultActionGroup {

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (psiFile == null) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }

        e.getPresentation().setEnabledAndVisible(true);
        FileType fileType = psiFile.getFileType();
        if (fileType instanceof JavaFileType
                || fileType instanceof KotlinFileType) {
            e.getPresentation().setVisible(true);
            super.update(e);
        } else {
            e.getPresentation().setVisible(false);
        }
    }
}