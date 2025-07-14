package ke.don.itinerar_ai.di

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerateContentResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class VertexProviderImpl: VertexProvider {
    private val vertexAI = Firebase.ai
    private val model = vertexAI.generativeModel(GEMINI_MODEL)

    override fun generateDescription(): Flow<GenerateContentResponse> {
        val responseStream = model.generateContentStream("Hi gemini")


        return responseStream.catch { e ->
            Log.e("VertexAI", "Failed to generate content", e)
        }

    }

    companion object{
        const val GEMINI_MODEL = "gemini-2.5-flash"
    }
}

interface VertexProvider{
    fun generateDescription(): Flow<GenerateContentResponse>
}

sealed class DescriptionResult {
    data object Loading : DescriptionResult()
    data class Success(val text: String) : DescriptionResult()
    data class Error(val message: String) : DescriptionResult()
}
