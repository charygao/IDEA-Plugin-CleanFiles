package com.kerwin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        if (null != systemFile) {
            // 文件分类
            try {
                FindHelper.getReadFiles(systemFile);
            } catch (Exception e) {
                logger.error(e.getMessage(), new Throwable(e));
            }

            // 获取未引用的图片
            List<VirtualFile> unUsedImages = FindHelper.getUnUsedImages(project);

            System.out.println(FindHelper.READ_FILE_LIST);

            System.out.println(FindHelper.IMAGE_FILE_MAP);

            System.out.println(unUsedImages);
        }


        /*

        VirtualFile file = virtualFile.getFileSystem().findFileByPath("C:\\Users\\Administrator\\Desktop\\Codes\\LeeCode");
        System.out.println(file);*/

       /* // 获取选中文件的内容
        Project project = getProject(event);
        PsiFile psiFile = PsiUtilBase.getPsiFile(project, virtualFile);
        String fileText = psiFile.getText();
        System.out.println(fileText);

        if (virtualFile != null && project != null) {
            List<VirtualFile> unusedImages = getUnusedImages(project, virtualFile);
            if (unusedImages.isEmpty()) {
                Messages.showInfoMessage(project, "Nothing was found", "Tips");
            }
        }

        */
    }

    private Project getProject(AnActionEvent event) {
        return event.getData(PlatformDataKeys.PROJECT);
    }

    private VirtualFile getVirtualFile(AnActionEvent event) {
        return event.getData(PlatformDataKeys.VIRTUAL_FILE);
    }

    private static Logger logger = LoggerFactory.getLogger(CleanFilesAction.class);
}
