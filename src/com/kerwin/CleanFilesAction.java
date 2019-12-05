package com.kerwin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.kerwin.ui.AnalysisResultEntry;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;

/**
 * Created by Kerwin on 2019/11/28.
 */
public class CleanFilesAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        // 初始化数据
        FindHelper.cleanData();

        // 光标选中的虚拟路径
        VirtualFile virtualFile = getVirtualFile(event);

        // 项目
        Project project = getProject(event);

        String projectBasePath = project.getBasePath();
        VirtualFile systemFile = virtualFile.getFileSystem().findFileByPath(projectBasePath);

        if (null == systemFile) {
            Messages.showInfoMessage(project, "System Project was not found", "Tips");
            return;
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 文件分类
        try {
            FindHelper.getReadFiles(systemFile);
            logger.info("Get All ReadFiles consume time is: " + stopWatch.getTime());
        } catch (Exception e) {
            logger.error(e.getMessage(), new Throwable(e));
        } finally {
            stopWatch.reset();
            stopWatch.start();
        }

        // 获取未引用的图片
        List<VirtualFile> unUsedFiles = FindHelper.getUnUsedFiles(project);
        logger.info("Get unUsedFiles consume time is: " + stopWatch.getTime());

        if (CollectionUtils.isEmpty(unUsedFiles)) {
            Messages.showInfoMessage(project, "Nothing was not found", "Tips");
            return;
        }

        // 计算文件大小
        String fileSize = FindHelper.getFilesSize(unUsedFiles) + " M";
        Messages.showInfoMessage(project, "File Occupation is: " + fileSize, "Tips");

        AnalysisResultEntry resultEntry = new AnalysisResultEntry(unUsedFiles);
        JFrame dialog = getDefaultDialog(resultEntry);

        resultEntry.setOnCancelListener(() -> dialog.setVisible(false));
        resultEntry.setOnConfirmListener(virtualFiles -> {
            dialog.setVisible(false);
            //删除当前文件
            WriteCommandAction.runWriteCommandAction(project, () -> {
                for (VirtualFile file : virtualFiles) {
//                    PsiFile psiFile = PsiUtilBase.getPsiFile(project, file);
//                    psiFile.delete();
                    System.out.println(file.getFileType().getName() + " :: " + file.getName() + " -> " + file.getPath());
                }
            });
        });
    }

    private JFrame getDefaultDialog(JPanel jPanel) {
        JFrame dialog = new JFrame();
        dialog.add(jPanel);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        dialog.setTitle("IDEA-Plugin-CleanFiles");
        return dialog;
    }

    private Project getProject(AnActionEvent event) {
        return event.getData(PlatformDataKeys.PROJECT);
    }

    private VirtualFile getVirtualFile(AnActionEvent event) {
        return event.getData(PlatformDataKeys.VIRTUAL_FILE);
    }

    private static Logger logger = LoggerFactory.getLogger(CleanFilesAction.class);
}
