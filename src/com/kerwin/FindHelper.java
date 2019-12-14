package com.kerwin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import com.kerwin.ac.trie.Emit;
import com.kerwin.ac.trie.Trie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
     * 需要检索的文件集
     * Key  : 文件名
     * Value: VirtualFile
     */
    public static Map<String, VirtualFile> HANDLE_FILE_MAP = new HashMap<>();

    /***
     * 组装全量 可读文件 - 需要检索的文件
     * @param systemFile 系统级别文件目录
     */
    public static void getReadFiles (VirtualFile systemFile) throws Exception {
        if (!systemFile.isDirectory()) {
            throw new Exception("systemFile is not a directory.");
        }

        VirtualFile[] children = systemFile.getChildren();
        for (VirtualFile child : children) {

            // 跳过需要抛出的文件
            if (checkFileName(child)) {
                continue;
            }

            // 处理支持读取的文件
            if (!child.isDirectory() && checkFileType(child)) {
                READ_FILE_LIST.add(child);
            }

            // 处理图片资源, CSS资源文件, JS资源文件
            if (!child.isDirectory() && analyzeFileType(child)) {
                HANDLE_FILE_MAP.put(child.getParent().getName() + "/" + child.getName(), child);

            // 递归处理
            }  else if (child.isDirectory()){
                getReadFiles(child);
            }
        }
    }

    /***
     * 获取未引用的文件集合
     */
    public static List<VirtualFile> getUnUsedFiles (Project project) {

        // 待处理集合Names
        List<String> fileNames = new ArrayList<>(HANDLE_FILE_MAP.keySet());

        // 构建AC自动机
        Trie trie = Trie.compile(fileNames);

        // AC自动机检索
        for (VirtualFile virtualFile : READ_FILE_LIST) {
            PsiFile psiFile = PsiUtilBase.getPsiFile(project, virtualFile);
            String fileContent = psiFile.getText();

            Collection<Emit> emits = trie.parseText(fileContent);
            for (Emit emit : emits) {

                // 针对 CSS,JS文件处理 -> 如果得到的文件名包含在自身之中则跳过本次循环
                if (analyzeFileType(virtualFile) && virtualFile.getName().equals(emit.getKeyword())) {
                    continue;
                }

                HANDLE_FILE_MAP.remove(emit.getKeyword());
                logger.debug("{} have been used, remove.", emit.getKeyword());
            }
        }

        return new ArrayList<>(HANDLE_FILE_MAP.values());
    }

    /***
     * 获取文件集合大小
     * @param files VirtualFile 文件
     */
    public static double getFilesSize (List<VirtualFile> files) {
        long fileSzie = 0L;
        for (VirtualFile virtualFile : files) {
            File file = new File(virtualFile.getPath());
            fileSzie += file.length();
        }
        return (double) (fileSzie / 1024 / 1024);
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

    /***
     * 判断文件是否加入检索目录 -> 暂处理 图片,CSS,JS文件
     * @param file VirtualFile 文件
     */
    private static boolean analyzeFileType (VirtualFile file) {
        String fileType = file.getFileType().getName();
        if (IMAGE_TYPE.equals(fileType)) {
            return true;
        }

        if (CSS.equals(fileType)) {
            return true;
        }

        if (JAVA_SCRIPT.equals(fileType)) {
            return true;
        }
        return false;
    }

    /**
     * 初始化数据
     */
    public static void cleanData () {
        READ_FILE_LIST = new ArrayList<>();

        HANDLE_FILE_MAP = new HashMap<>();
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
    private static String SOURCE_MAP = "SourceMap";
    private static String PROPERTIES = "Properties";
    private static String MARKDOWN = "Markdown";

    private static List<String> SUPPORT_FILES = Arrays.asList(JAVA, JSP, HTML, JAVA_SCRIPT, JSON, XML, VUE, CSS, LESS, PLAIN_TEXT, SOURCE_MAP, PROPERTIES, MARKDOWN);

    private static List<String> IGNORE_FILES  = Arrays.asList(IDEA_TYPE, GIT_TYPE, SVN, NODE_MODULES, DS_STORE, IDEA_MODULE, OUT,TARGET);

    private static Logger logger = LoggerFactory.getLogger(FindHelper.class);
}
