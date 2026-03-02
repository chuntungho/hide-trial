package com.chuntung.plugin;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetsManager;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class HideTrial implements ProjectActivity {
    private static final String SETTINGS_CLASS = "com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetSettings";
    private static Logger logger = LoggerFactory.getLogger(HideTrial.class);

    public static final String TRIAL_STATE_WIDGET = "TrialStateWidget";
    public static final String NON_COMMERCIAL = "NonCommercial";
    public static final String TRIAL_STATUS_BAR_WIDGET = "TrialStatusBarWidget";

    @Override
    public @Nullable Object execute(@NonNull Project project, @NonNull Continuation<? super Unit> continuation) {
        hideTrailWidget();
        hideStatusBarWidget(project);
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
    public void hideStatusBarWidget(Project project) {
        StatusBarWidgetsManager manager = project.getService(StatusBarWidgetsManager.class);
        manager.getWidgetFactories().forEach(x->{
            logger.debug("status bar widget: {} - {}", x.getId(), x.getDisplayName());
        });
        StatusBarWidgetFactory factory = manager.findWidgetFactory(TRIAL_STATUS_BAR_WIDGET);
        if (factory != null) {
            logger.info("Hiding trail widget in status bar");
            disableStatusBarWidget(factory);
            manager.updateWidget(factory);
        }

        factory = manager.findWidgetFactory(NON_COMMERCIAL);
        if (factory != null) {
            logger.info("Hiding non-commercial widget in status bar");
            disableStatusBarWidget(factory);
            manager.updateWidget(factory);
        }
    }

    private void disableStatusBarWidget(StatusBarWidgetFactory factory) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(SETTINGS_CLASS);
            Optional<Method> setEnabled = Arrays.stream(clazz.getMethods())
                    .filter(x -> Objects.equals("setEnabled", x.getName()))
                    .findFirst();
            Object setting = ApplicationManager.getApplication().getService(clazz);
            if (setEnabled.isPresent()) {
                setEnabled.get().invoke(setting, factory, Boolean.FALSE);
            }
            logger.info("Disable {}", factory.getId());
        } catch (ReflectiveOperationException e) {
            logger.warn("Failed to disable {}", factory.getId(), e);
            if (clazz != null) {
                Arrays.stream(clazz.getMethods()).forEach(x-> {
                    logger.info("Declared method: {}", x.getName());
                });
            }
        }
    }

}
