package com.chuntung.plugin;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class HideTrial implements ProjectActivity {
    @Override
    public @Nullable Object execute(@NonNull Project project, @NonNull Continuation<? super Unit> continuation) {
        AnAction trialAction = ActionManager.getInstance().getAction("TrialStateWidget");
        if (trialAction != null) {
            ActionManager.getInstance().replaceAction("TrialStateWidget", new AnAction("") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    // empty
                }
            });
        }
        return null;
    }
}
