package com.chuntung.plugin;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetSettings;
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetsManager;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HideTrial implements ProjectActivity {
    private static Logger logger = LoggerFactory.getLogger(HideTrial.class);

    public static final String TRIAL_STATE_WIDGET = "TrialStateWidget";
     public static final String NON_COMMERCIAL = "NonCommercial";
    public static final String TRIAL_STATUS_BAR_WIDGET = "TrialStatusBarWidget";

    @Override
    public @Nullable Object execute(@NonNull Project project, @NonNull Continuation<? super Unit> continuation) {
        hideTrailWidget();
        hideLicenseWidget(project);
        return null;
    }

    private static void hideTrailWidget() {
        AnAction trialAction = ActionManager.getInstance().getAction(TRIAL_STATE_WIDGET);
        if (trialAction != null) {
            logger.info("Hiding trial widget in toolbar");
            ActionManager.getInstance().unregisterAction(TRIAL_STATE_WIDGET);
        }
    }

    /// https://beansoft.github.io/jetbrains/status-bar-widgets.html#controlling-widgets-programmatically
    public void hideLicenseWidget(Project project) {
        StatusBarWidgetsManager manager = project.getService(StatusBarWidgetsManager.class);
//        manager.getWidgetFactories().forEach(x->{
//            logger.info("status bar widget: {} - {}", x.getId(), x.getDisplayName());
//        });
        StatusBarWidgetFactory factory = manager.findWidgetFactory(TRIAL_STATUS_BAR_WIDGET);
        if (factory != null) {
            logger.info("Hiding trail widget in status bar");
            StatusBarWidgetSettings.getInstance().setEnabled(factory, false);
            manager.updateWidget(factory);
        }

        factory = manager.findWidgetFactory(NON_COMMERCIAL);
        if (factory != null) {
            logger.info("Hiding non commerical widget in status bar");
            StatusBarWidgetSettings.getInstance().setEnabled(factory, false);
            manager.updateWidget(factory);
        }

    }
}
