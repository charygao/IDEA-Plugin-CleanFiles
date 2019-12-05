package com.kerwin.ui;

import com.kerwin.interfac.OnCancelListener;
import com.kerwin.interfac.OnConfirmListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * ******************************
 * author：      柯贤铭
 * createTime:   2019/12/5 11:15
 * description:  EntryFooter
 * version:      V1.0
 * ******************************
 */
public class EntryFooter extends JPanel{

    private OnConfirmListener<String> mOnConfirmListener;
    private OnCancelListener mOnCancelListener;

    public EntryFooter (String leftButtonName, String rightButtonName) {

        JButton mCancel = getConfirmOrCancelButton(leftButtonName,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        mOnCancelListener.onCancel();
                    }
                });

        JButton mConfirm = getConfirmOrCancelButton(rightButtonName,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        mOnConfirmListener.onConfirm("");
                    }
                });


        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mCancel);
        add(Box.createRigidArea(new Dimension(30, 0)));
        add(mConfirm);
    }

    void setOnConfirmListener(OnConfirmListener<String> listener) {
        mOnConfirmListener = listener;
    }

    void setOnCancelListener(OnCancelListener listener) {
        mOnCancelListener = listener;
    }

    private JButton getConfirmOrCancelButton(String buttonName, Action action) {
        JButton mConfirm = new JButton();
        mConfirm.setAction(action);
        mConfirm.setPreferredSize(new Dimension(120, 32));
        mConfirm.setText(buttonName);
        mConfirm.setVisible(true);
        return mConfirm;
    }
}
