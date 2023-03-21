package data

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import model.Settings
import java.io.Serializable

@State(
    name = "ScreenGeneratorConfiguration",
    storages = [Storage(value = "screenGeneratorConfiguration.xml")]
)
class GenerateFileComponent : Serializable, PersistentStateComponent<GenerateFileComponent> {

    companion object {
        fun getInstance(project: Project) = ServiceManager.getService(project, GenerateFileComponent::class.java)
    }

    var settings: Settings = Settings()

    override fun getState(): GenerateFileComponent = this

    override fun loadState(state: GenerateFileComponent) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
