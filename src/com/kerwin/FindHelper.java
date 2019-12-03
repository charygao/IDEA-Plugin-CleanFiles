package com.kerwin;

import com.intellij.openapi.vfs.VirtualFile;

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
     * 组装全量可读文件-图片文件
     * @param systemFile 系统级别文件目录
     */
    public static void getReadFiles (VirtualFile systemFile) throws Exception {
        List<VirtualFile> files = new ArrayList<>();

        if (!systemFile.isDirectory()) {
            throw new Exception("systemFile is not a directory.");
        }

        VirtualFile[] children = systemFile.getChildren();
        for (VirtualFile child : children) {

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
     * 组装全量可读文件-图片文件
     */
    public static List<VirtualFile> getUnUsedImages () {
        return new ArrayList<>();
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

    private static String IMAGE_TYPE = "Image";
    private static String IDEA_TYPE = ".idea";
    private static String GIT_TYPE = ".git";
    private static String SVN = ".svn";
    private static String NODE_MODULES = "node_modules";
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

    private static List<String> SUPPORT_FILES = Arrays.asList(XML, HTML, JAVA_SCRIPT, JSON, VUE, CSS, LESS, PLAIN_TEXT);

    private static List<String> IGNORE_FOLDS  = Arrays.asList(IDEA_TYPE,GIT_TYPE,SVN,NODE_MODULES,DS_STORE,FOLD_1,PYCACHE,".hprof",".pyc",".pyo",".rbc",".yarb");
}
