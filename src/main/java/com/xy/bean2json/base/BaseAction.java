package com.xy.bean2json.base;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.xy.bean2json.error.ConvertException;

/**
 * BaseAction
 *
 * @author Created by gold on 2020/12/10 21:18
 */
public abstract class BaseAction extends AnAction {

    private final NotificationGroup notificationGroup;

    public BaseAction() {
        this.notificationGroup = new NotificationGroup("Bean2Json.NotificationGroup", NotificationDisplayType.BALLOON, true);
    }

    @Override
    public final void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            throw new IllegalStateException("Editor null");
        }

        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            throw new IllegalStateException("PsiFile null");
        }

        Project project = editor.getProject();

        try {
            String canonicalText = actionPerformed(e, editor, psiFile);

            String message = "Convert " + canonicalText + " to JSON success, copied to clipboard.";

            sendNotice(project, message);
        } catch (Throwable t) {
            if (t instanceof ConvertException) {
                return;
            }

            sendErrorNotice(project, "Convert to JSON failed：" + t.getMessage() + ".");

            t.printStackTrace();
        }
    }

    protected abstract String actionPerformed(AnActionEvent e, Editor editor, PsiFile psiFile);

    /**
     * 发送通知
     *
     * @param message 消息
     */
    protected void sendNotice(Project project, String message) {
        Notification error = notificationGroup.createNotification(message, NotificationType.INFORMATION);
        Notifications.Bus.notify(error, project);
    }

    /**
     * 发送错误通知
     *
     * @param message 消息
     */
    protected void sendErrorNotice(Project project, String message) {
        Notification error = notificationGroup.createNotification(message, NotificationType.ERROR);
        Notifications.Bus.notify(error, project);
    }

}
