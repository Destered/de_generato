package ui.settings.widget

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.IdeBorderFactory
import model.AndroidComponent
import model.FileType
import ui.settings.SettingsState
import util.addTextChangeListener
import util.selectIndex
import util.updateText
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class ScreenElementDetailsPanel : JPanel() {

    var onNameTextChanged: ((String) -> Unit)? = null
    var onFileNameTextChanged: ((String) -> Unit)? = null
    var onSubdirectoryTextChanged: ((String) -> Unit)? = null
    var onSourceSetTextChanged: ((String) -> Unit)? = null
    var onFileTypeIndexChanged: ((Int) -> Unit)? = null
    var onAndroidComponentIndexChanged: ((Int) -> Unit)? = null

    private val nameTextField = JTextField()
    private val fileTypeComboBox = ComboBox(FileType.values())
    private val fileNameTextField = JTextField()
    private val fileNameSampleLabel = JLabel().apply { isEnabled = false }
    private val screenElementNameLabel = JLabel("Name:")
    private val fileNameLabel = JLabel("File Name:")
    private val fileTypeLabel = JLabel("File Type:")

    private val subdirectoryLabel = JLabel("Sub Package:")
    private val subdirectoryTextField = JTextField()

    private var listenersBlocked = false

    init {
        border = IdeBorderFactory.createTitledBorder("Settings", false)
        layout = GridBagLayout()
        add(screenElementNameLabel, constraintsLeft(0, 0))
        add(nameTextField, constraintsRight(1, 0))
        add(fileNameLabel, constraintsLeft(0, 1))
        add(fileNameTextField, constraintsRight(1, 1))
        add(fileTypeLabel, constraintsLeft(0, 2))
        add(
            fileTypeComboBox,
            constraintsRight(1, 2).apply {
                gridwidth = 1
                fill = GridBagConstraints.NONE
                anchor = GridBagConstraints.LINE_START
            }
        )
        add(
            fileNameSampleLabel,
            constraintsRight(2, 2).apply {
                gridwidth = 1
                weightx /= 2
                anchor = GridBagConstraints.LINE_END
                fill = GridBagConstraints.NONE
            }
        )
        add(subdirectoryLabel, constraintsLeft(0, 4))
        add(subdirectoryTextField, constraintsRight(1, 4))

        nameTextField.addTextChangeListener { if (!listenersBlocked) onNameTextChanged?.invoke(it) }
        fileNameTextField.addTextChangeListener { if (!listenersBlocked) onFileNameTextChanged?.invoke(it) }
        subdirectoryTextField.addTextChangeListener { if (!listenersBlocked) onSubdirectoryTextChanged?.invoke(it) }
        fileTypeComboBox.addActionListener { if (!listenersBlocked) onFileTypeIndexChanged?.invoke(fileTypeComboBox.selectedIndex) }
    }

    fun render(state: SettingsState) {
        listenersBlocked = true
        val selectedElement = state.selectedElement
        nameTextField.updateText(selectedElement?.name ?: "")
        fileTypeComboBox.selectIndex(selectedElement?.fileType?.ordinal ?: FileType.KOTLIN.ordinal)
        fileNameTextField.updateText(selectedElement?.fileNameTemplate ?: "")
        fileNameSampleLabel.updateText(state.fileNameRendered)
        subdirectoryTextField.updateText(selectedElement?.subdirectory ?: "")

        isEnabled = selectedElement != null
        components.filter { it != fileNameSampleLabel }.forEach { it.isEnabled = selectedElement != null }
        listenersBlocked = false
    }
}
