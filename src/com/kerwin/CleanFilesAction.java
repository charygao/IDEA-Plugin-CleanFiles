package com.kerwin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kerwin on 2019/11/28.
 */
public class CleanFilesAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        // TODO: insert action logic here

        VirtualFile virtualFile = getVirtualFile(event);

        /*for (VirtualFile imageFile : resourceImages) {
            String fileName = imageFile.getName();
            for (VirtualFile sourceFile : files) {
                if(sourceFile.getName().indexOf(".") > 0){
                    PsiFile psiFile = PsiUtilBase.getPsiFile(project, sourceFile);
                    String fileText = psiFile.getText();
                    if (fileText.contains(fileName)) {
                        referenceList.add(imageFile);
                        break;
                    }
                }
            }
        }
        VirtualFile file = virtualFile.getFileSystem().findFileByPath("C:\\Users\\Administrator\\Desktop\\Codes\\LeeCode");
        System.out.println(file);*/

        // 获取选中文件的内容
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
    }

    private Project getProject(AnActionEvent event) {
        return event.getData(PlatformDataKeys.PROJECT);
    }

    private VirtualFile getVirtualFile(AnActionEvent event) {
        return event.getData(PlatformDataKeys.VIRTUAL_FILE);
    }

    /**
     * 获取项目中未使用的图片文件
     */
    private List<VirtualFile> getUnusedImages(Project project, VirtualFile virtualFile) {
        // 原生图片
        List<VirtualFile> imagesFiles = getImagesFiles(virtualFile);
        if(imagesFiles.isEmpty()){
            return imagesFiles;
        }else {
            List<VirtualFile> targetFiles = getModuleTargetFiles(project, virtualFile);

            //被引用的图片
            List<VirtualFile> referenceImages= getReferenceImages(project, imagesFiles, targetFiles);
        }

        return null;
    }

    // 判断文件列表中是否包含当前文件
    private Boolean containFile(List<VirtualFile> fileList, VirtualFile file) {
        for (VirtualFile itemFile : fileList) {
            if (itemFile.getName().equals(file.getName())) {
                return true;
            }
        }
        return false;
    }
    /**
     * 获取当前目录下所有的图片资源
     */
    private List<VirtualFile> getImagesFiles(VirtualFile virtualFile) {
        List<VirtualFile> fileList = new ArrayList<>();
        if (virtualFile.isDirectory()) {
            // 选中目录下的文件（图片文件）
            VirtualFile[] virtualFiles = virtualFile.getChildren();
            if (virtualFiles != null && virtualFiles.length > 0) {
                for (VirtualFile file : virtualFiles) {
                    if (IMAGE_TYPE == file.getFileType().getName()) {
                        fileList.add(file);
                    } else if (file.isDirectory()) {
                        fileList.addAll(getImagesFiles(file));
                    }
                }
            }
        } else if (IMAGE_TYPE.equals(virtualFile.getFileType().getName())) {
            fileList.add(virtualFile);
        }
        return fileList;
    }

    /**
     * 获取除图片外的文件
     */
    private List<VirtualFile> getScanningFile(VirtualFile virtualFile)  {
        List<VirtualFile> files = new ArrayList<>();
        // 虚拟文件名
        String virtualName = virtualFile.getName();
        if (!IDEA_TYPE.equals(virtualName) && !GIT_TYPE.equals(virtualName)) {
            if (virtualFile.isDirectory() && IGNORE_FOLDS.indexOf(virtualName) < 0) {
                VirtualFile[] virtualFiles = virtualFile.getChildren();
                for (VirtualFile file : virtualFiles) {
                    files.addAll(getScanningFile(file));
                }
            } else {
                String fileTypeName = virtualFile.getFileType().getName();
                if (SUPPORT_FILES.indexOf(fileTypeName)>= 0) {
                    files.add(virtualFile);
                }
            }
        }
        return files;
    }


    /**
     * 获取当前项目下的所有文件
     */
    private List<VirtualFile> getModuleTargetFiles(Project project, VirtualFile virtualFile) {
        String projectBasePath = project.getBasePath();
        if (StringUtils.isNotBlank(projectBasePath)) {
            VirtualFile projectBasePathVirtualFile = virtualFile.getFileSystem().findFileByPath(projectBasePath);
            return projectBasePathVirtualFile == null ? new ArrayList<>() : getScanningFile(projectBasePathVirtualFile);
        }
        return new ArrayList<>();
    }

    /**
     * 获取所有的引用的图片文件
     */
    private List<VirtualFile> getReferenceImages (Project project, List<VirtualFile> resourceImages, List<VirtualFile> files) {
        int size = resourceImages.size();
        if (size == 0) {
            return new ArrayList<>();
        } else {
            List<VirtualFile> referenceList = new ArrayList<>();
            for (VirtualFile imageFile : resourceImages) {
                String fileName = imageFile.getName();
                for (VirtualFile sourceFile : files) {
                    if(sourceFile.getName().indexOf(".") > 0){
                        PsiFile psiFile = PsiUtilBase.getPsiFile(project, sourceFile);
                        String fileText = psiFile.getText();
                        if (fileText.contains(fileName)) {
                            referenceList.add(imageFile);
                            break;
                        }
                    }
                }
            }
            return referenceList;
        }
    }

    private static String IMAGE_TYPE = "Image";
    private static String IDEA_TYPE = ".idea";
    private static String GIT_TYPE = ".git";
    private static String SVN = ".svn";
    private static String NODE_MODULES = "node_modules";//库文件
    private static String DS_STORE = ".DS_Store";
    private static String FOLD_1 = "*~";
    private static String PYCACHE = ";__pycache__";
    private static String XML = "XML";
    private static String HTML = "HTML";
    private static String JSON = "JSON";
    private static String VUE = "Vue.js";
    private static String CSS = "CSS";
    private static String LESS = "Less";
    private static String PLAIN_TEXT = "PLAIN_TEXT";
    private static String JAVA_SCRIPT = "JavaScript";
    List<String> SUPPORT_FILES = Arrays.asList(XML, HTML, JAVA_SCRIPT, JSON, VUE, CSS, LESS, PLAIN_TEXT);
    List<String> IGNORE_FOLDS  = Arrays.asList(IDEA_TYPE,GIT_TYPE,SVN,NODE_MODULES,DS_STORE,FOLD_1,PYCACHE);
    List<String> IGNORE_FILES  = Arrays.asList(".hprof",".pyc",".pyo",".rbc",".yarb");
}
