package com.kerwin;

import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.FindSettings;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.find.impl.FindInProjectUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Factory;
import com.intellij.usageView.UsageInfo;
import com.intellij.usages.*;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ******************************
 * author：      柯贤铭
 * createTime:   2019/11/29 11:18
 * description:  FindHelper
 * version:      V1.0
 * ******************************
 */
public class FindHelper extends FindInProjectManager {

    public FindHelper(Project project) {
        super(project);
    }

    public void findPath (@NotNull DataContext dataContext, @NotNull Project project) {
        FindManager findManager = FindManager.getInstance(project);
        FindModel findModel = findManager.getFindInProjectModel().clone();
        findModel.setReplaceState(false);
        findModel.setOpenInNewTabVisible(true);
        findModel.setOpenInNewTabEnabled(false);
        findModel.setOpenInNewTab(false);
        this.initModel(findModel, dataContext);

        this.findInProject(dataContext, findModel, project);
    }

    public void findInProject(@NotNull DataContext dataContext, @Nullable FindModel findModel, @Nullable Project project) {

        // presentation
        FindModel findModelCopy = findModel.clone();
        UsageViewPresentation presentation = FindInProjectUtil.setupViewPresentation(FindSettings.getInstance().isShowResultsInSeparateView(), findModelCopy);

        // processPresentation
        FindUsagesProcessPresentation processPresentation = FindInProjectUtil.setupProcessPresentation(project, false, presentation);

        // usageTarget
        ConfigurableUsageTarget usageTarget = new FindInProjectUtil.StringUsageTarget(project, findModel);

        com.intellij.usages.UsageViewManager manager = com.intellij.usages.UsageViewManager.getInstance(project);
        manager.searchAndShowUsages(new UsageTarget[]{usageTarget}, new Factory<UsageSearcher>() {
            @Override
            public UsageSearcher create() {
                return (Processor<Usage> processor) -> {
                    FindHelper.this.myIsFindInProgress = true;

                    try {
                        Processor<UsageInfo> consumer = (info) -> {
                            Usage usage = (Usage) UsageInfo2UsageAdapter.CONVERTER.fun(info);
                            usage.getPresentation().getIcon();
                            return processor.process(usage);
                        };

                        FindInProjectUtil.findUsages(findModelCopy, project, consumer, processPresentation);
                        System.out.println(consumer);
                    } finally {
                        FindHelper.this.myIsFindInProgress = false;
                    }

                };
            }
        }, processPresentation, presentation, null);
    }

    // myIsFindInProgress
    private volatile boolean myIsFindInProgress;
}
