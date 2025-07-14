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

    override fun generateDescription(title: String): Flow<DescriptionResult> = flow {
        emit(DescriptionResult.Loading)

        val responseStream = model.generateContentStream("Hi Gemini, here is a title for a new itinerary: $title, please generate a description with max tokens $MAX_TOKENS")

        responseStream.collect { chunk ->
            emit(DescriptionResult.Success(chunk.text.orEmpty()))
        }
    }.catch { e ->
        Log.e("VertexAI", "🔥 Failed to generate content", e)
        emit(DescriptionResult.Error(e.message ?: "Unknown error"))
    }

    companion object{
        const val GEMINI_MODEL = "gemini-2.5-flash"
        const val MAX_TOKENS = 100
    }
}

interface VertexProvider{
    fun generateDescription(title: String): Flow<DescriptionResult>
}

sealed class DescriptionResult {
    data object Loading : DescriptionResult()
    data class Success(val text: String) : DescriptionResult()
    data class Error(val message: String) : DescriptionResult()
}
