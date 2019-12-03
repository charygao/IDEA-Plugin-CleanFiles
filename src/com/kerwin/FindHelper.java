package com.kerwin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;

import java.util.*;

/**
 * ******************************
 * author：      柯贤铭
 * createTime:   2019/11/29 11:18
 * description:  FindHelper
 * version:      V1.0
 * ******************************
 */
public class FindHelper {

    /** 全量可读文件 **/
    public static List<VirtualFile> READ_FILE_LIST = new ArrayList<>();

    /***
     * 图片文件集
     * Key  : 文件名
     * Value: VirtualFile
     */
    public static Map<String, VirtualFile> IMAGE_FILE_MAP = new HashMap<>();

    /***
     * 组装全量 可读文件-图片文件
     * @param systemFile 系统级别文件目录
     */
    public static void getReadFiles (VirtualFile systemFile) throws Exception {
        if (!systemFile.isDirectory()) {
            throw new Exception("systemFile is not a directory.");
        }

        VirtualFile[] children = systemFile.getChildren();
        for (VirtualFile child : children) {

            System.out.println(child.getName() + " ---> " + child.getFileType().getName());

            // 跳过需要抛出的文件
            if (checkFileName(child)) {
                continue;
            }

            // 处理支持读取的文件
            if (!child.isDirectory() && checkFileType(child)) {
                READ_FILE_LIST.add(child);

            // 处理图片资源文件
            } else if (!child.isDirectory() && IMAGE_TYPE.equals(child.getFileType().getName())) {
                IMAGE_FILE_MAP.put(child.getName(), child);

            // 递归处理
            }  else {
                getReadFiles(child);
            }
        }
    }

    /***
     * 获取未引用的文件集合
     */
    public static List<VirtualFile> getUnUsedImages (Project project) {
        // 返回结果
        List<VirtualFile> result = new ArrayList<>();

        Set<Map.Entry<String, VirtualFile>> entries = IMAGE_FILE_MAP.entrySet();
        for (Map.Entry<String, VirtualFile> entry : entries) {
            String fileName = entry.getKey();

            for (VirtualFile virtualFile : READ_FILE_LIST) {
                PsiFile psiFile = PsiUtilBase.getPsiFile(project, virtualFile);
                String fileContent = psiFile.getText();
                if (fileContent.indexOf(fileName) > 0) {
                    result.add(entry.getValue());
                }
            }
        }
        return result;
    }

    /***
     * 判断文件是否需要加入可读集合
     * @param file VirtualFile 文件
     */
    private static boolean checkFileType (VirtualFile file) throws Exception {
        if (file.isDirectory()) {
            throw new Exception("file should not be a directory.");
        }

        String fileType = file.getFileType().getName();
        for (String supportName : SUPPORT_FILES) {
            if (supportName.equals(fileType)) {
                return true;
            }
        }
        return false;
    }

    /***
     * 判断文件是否需要抛出
     * @param file VirtualFile 文件
     */
    private static boolean checkFileName (VirtualFile file) {
        String name = file.getName();
        for (String ignoreName : IGNORE_FILES) {
            if (ignoreName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 初始化数据
     */
    public static void cleanData () {
        READ_FILE_LIST = new ArrayList<>();

        IMAGE_FILE_MAP = new HashMap<>();
    }

    private static String IMAGE_TYPE = "Image";

    // Ignore Files Names
    private static String IDEA_TYPE = ".idea";
    private static String IDEA_MODULE = "IDEA_MODULE";
    private static String GIT_TYPE = ".git";
    private static String OUT = "out";
    private static String TARGET = "target";
    private static String SVN = ".svn";
    private static String NODE_MODULES = "node_modules";
    private static String DS_STORE = ".DS_Store";

    // Support Files Types
    private static String XML = "XML";
    private static String HTML = "HTML";
    private static String JSON = "JSON";
    private static String VUE = "Vue.js";
    private static String CSS = "CSS";
    private static String LESS = "Less";
    private static String PLAIN_TEXT = "PLAIN_TEXT";
    private static String JAVA_SCRIPT = "JavaScript";
    private static String JAVA = "JAVA";
    private static String JSP = "JSP";

    private static List<String> SUPPORT_FILES = Arrays.asList(JAVA, JSP, HTML, JAVA_SCRIPT, JSON, XML, VUE, CSS, LESS, PLAIN_TEXT);

    private static List<String> IGNORE_FILES  = Arrays.asList(IDEA_TYPE, GIT_TYPE, SVN, NODE_MODULES, DS_STORE, IDEA_MODULE, OUT,TARGET);
}
