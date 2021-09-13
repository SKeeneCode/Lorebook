package org.ksoftware.lorebook.actions

import javafx.stage.Stage
import org.ksoftware.lorebook.main.ProjectModel
import java.io.File

/**
 * Represents a request by the user to save a project. Requires a stage parameter to pass the current stage of a
 * projects workspace (which is not the primary stage) so that the dialog to select a save location appears in the
 * correct place.
 */
data class SaveProjectAction(
        val project: ProjectModel,
        val stage: Stage
)