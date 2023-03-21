package ui.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ui.core.UI
import ui.help.HelpDialog
import ui.settings.dagger.DaggerSettingsComponent
import ui.settings.widget.SettingsPanel
import javax.inject.Inject
import javax.swing.JComponent
import kotlin.coroutines.CoroutineContext

class SettingsConfigurable(private val project: Project) : Configurable, CoroutineScope {

    private lateinit var panel: SettingsPanel
    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext = Dispatchers.UI + job

    @Inject
    lateinit var viewModel: SettingsViewModel

    override fun isModified() = if (::viewModel.isInitialized) viewModel.state.value.isModified else false

    override fun getDisplayName() = "Generate File Plugin"

    override fun apply() {
        launch { viewModel.actionFlow.emit(SettingsAction.ApplySettings) }
    }

    override fun reset() {
        launch { viewModel.actionFlow.emit(SettingsAction.ResetSettings) }
    }

    override fun createComponent(): JComponent {
        DaggerSettingsComponent.factory().create(project).inject(this)
        panel = SettingsPanel(project)
        with(panel) {

            categoriesPanel.onAddClicked = { launch { viewModel.actionFlow.emit(SettingsAction.AddCategory) } }
            categoriesPanel.onRemoveClicked =
                { launch { viewModel.actionFlow.emit(SettingsAction.RemoveCategory(it)) } }
            categoriesPanel.onMoveUpClicked =
                { launch { viewModel.actionFlow.emit(SettingsAction.MoveUpCategory(it)) } }
            categoriesPanel.onMoveDownClicked =
                { launch { viewModel.actionFlow.emit(SettingsAction.MoveDownCategory(it)) } }
            categoriesPanel.onItemSelected =
                { launch { viewModel.actionFlow.emit(SettingsAction.SelectCategory(it)) } }

            categoryDetailsPanel.onNameTextChanged =
                { launch { viewModel.actionFlow.emit(SettingsAction.ChangeCategoryName(it)) } }

            screenElementsPanel.onAddClicked =
                { launch { viewModel.actionFlow.emit(SettingsAction.AddScreenElement) } }
            screenElementsPanel.onRemoveClicked =
                { launch { viewModel.actionFlow.emit(SettingsAction.RemoveScreenElement(it)) } }
            screenElementsPanel.onMoveDownClicked =
                { launch { viewModel.actionFlow.emit(SettingsAction.MoveDownScreenElement(it)) } }
            screenElementsPanel.onMoveUpClicked =
                { launch { viewModel.actionFlow.emit(SettingsAction.MoveUpScreenElement(it)) } }
            screenElementsPanel.onItemSelected =
                { launch { viewModel.actionFlow.emit(SettingsAction.SelectScreenElement(it)) } }

            screenElementDetailsPanel.onNameTextChanged =
                { launch { viewModel.actionFlow.emit(SettingsAction.ChangeName(it)) } }
            screenElementDetailsPanel.onFileNameTextChanged =
                { launch { viewModel.actionFlow.emit(SettingsAction.ChangeFileName(it)) } }
            screenElementDetailsPanel.onFileTypeIndexChanged =
                { launch { viewModel.actionFlow.emit(SettingsAction.ChangeFileType(it)) } }
            screenElementDetailsPanel.onSubdirectoryTextChanged =
                { launch { viewModel.actionFlow.emit(SettingsAction.ChangeSubdirectory(it)) } }
            screenElementDetailsPanel.onSourceSetTextChanged =
                { launch { viewModel.actionFlow.emit(SettingsAction.ChangeSourceSet(it)) } }
            screenElementDetailsPanel.onAndroidComponentIndexChanged =
                { launch { viewModel.actionFlow.emit(SettingsAction.ChangeAndroidComponent(it)) } }

            onTemplateTextChanged = { launch { viewModel.actionFlow.emit(SettingsAction.ChangeTemplate(it)) } }
            onHelpClicked = { launch { viewModel.actionFlow.emit(SettingsAction.ClickHelp) } }

            launch { viewModel.state.collect { render(it) } }
            launch { viewModel.effect.collect { it.handle() } }

        }

        return panel
    }

    override fun disposeUIResources() {
        job.cancel()
        viewModel.clear()
        super.disposeUIResources()
    }

    private fun SettingsEffect.handle() = when (this) {
        SettingsEffect.ShowHelp -> HelpDialog().show()
    }
}
