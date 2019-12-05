package com.kerwin.ui;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.kerwin.interfac.OnCancelListener;
import com.kerwin.interfac.OnConfirmListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ******************************
 * author：      柯贤铭
 * createTime:   2019/12/5 10:50
 * description:  AnalysisResultEntry
 * version:      V1.0
 * ******************************
 */
public class AnalysisResultEntry extends JPanel {

    private  List<VirtualFile> unusedImages;
    private  OnCancelListener mOnCancelListener;
    private  OnConfirmListener<List<VirtualFile>> mOnConfirmListener;
    private  List<VirtualFile> selectedFiles = new ArrayList<>();
    private  List<JCheckBox> allCheckBoxs = new ArrayList<>();

    public AnalysisResultEntry (List<VirtualFile> unusedImages) {
        super();
        this.unusedImages = unusedImages;
        this.setPreferredSize(new Dimension(600, 600));
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(getHeader(), BorderLayout.PAGE_START);
        this.add(getJBScrollPane(unusedImages), BorderLayout.CENTER);
        this.add(getFooter(),BorderLayout.PAGE_END);
    }

    private JBScrollPane getJBScrollPane(List<VirtualFile> virtualFiles) {
        JPanel scrollContent = new JPanel();
        scrollContent.setBorder(JBUI.Borders.empty(10));
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.PAGE_AXIS));
        for (VirtualFile virtualFile : virtualFiles ) {
            JPanel jPanel = new JPanel();
            jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.LINE_AXIS));

            JCheckBox jCheckBox = new JCheckBox();
            allCheckBoxs.add(jCheckBox);

            jCheckBox.setPreferredSize(new Dimension(32, 32));
            jCheckBox.setSelected(false);
            jCheckBox.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {

                }
            });
            jCheckBox.addChangeListener(e -> {
                updateSelectedState(virtualFile, jCheckBox.isSelected());
            });

            jPanel.add(jCheckBox);
            JLabel jLabel = new JLabel();
            jLabel.setText(virtualFile.getName());
            jLabel.setPreferredSize(new Dimension(360, 32));
            jLabel.setBorder(JBUI.Borders.empty(10));
            jPanel.add(jLabel);
            jPanel.add(Box.createHorizontalGlue());
            scrollContent.add(jPanel);
        }
        return new JBScrollPane(scrollContent);
    }

    private JPanel getHeader() {
        JPanel mHeader = new JPanel();
        mHeader.setLayout(new BoxLayout(mHeader,BoxLayout.LINE_AXIS));
        mHeader.setPreferredSize(new Dimension(600, 60));
        mHeader.setMaximumSize(new Dimension(600,60));
        mHeader.setBorder(JBUI.Borders.empty(10));

        JCheckBox jCheckBox = new JCheckBox();
        jCheckBox.setPreferredSize(new Dimension(32, 32));
        jCheckBox.setSelected(false);
        jCheckBox.addChangeListener(e -> updateAllSelectedState(jCheckBox.isSelected()));
        mHeader.add(jCheckBox);
        mHeader.add(Box.createRigidArea(new Dimension(10, 30)));
        JLabel jLabel = new JLabel();
        jLabel.setPreferredSize(new Dimension(200, 32));
        jLabel.setText("Images Names");
        mHeader.add(jLabel);
        mHeader.add(Box.createHorizontalGlue());
        return mHeader;
    }

    private EntryFooter getFooter() {
        EntryFooter entryFooter = new EntryFooter("Cancel", "Delete");
        entryFooter.setOnCancelListener(() -> mOnCancelListener.onCancel());
        entryFooter.setOnConfirmListener(s -> mOnConfirmListener.onConfirm(selectedFiles));
        return entryFooter;
    }

    private void updateAllSelectedState(Boolean selected) {
        for (JCheckBox checkBox : this.allCheckBoxs) {
            checkBox.setSelected(selected);
        }
    }

    private void updateSelectedState(VirtualFile virtualFile, Boolean add) {
        if (add) {
            for (VirtualFile file : this.selectedFiles) {
                if (file.getName().equals(virtualFile.getName())) {
                    return;
                }
            }
            this.selectedFiles.add(virtualFile);
        } else {
            this.selectedFiles.remove(virtualFile);
        }
    }

    public void setOnConfirmListener(OnConfirmListener<List<VirtualFile>> listener) {
        mOnConfirmListener = listener;
    }

    public void setOnCancelListener(OnCancelListener listener) {
        mOnCancelListener = listener;
    }
}
