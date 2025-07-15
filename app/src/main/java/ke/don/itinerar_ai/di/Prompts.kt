package ke.don.itinerar_ai.di

object Prompts {
    fun buildItineraryPrompt(title: String, description: String): String {
        return """
        I am planning an itinerary titled "$title". Here's the description: "$description".

        Generate a list of up to 4 itinerary items for this trip. Respond ONLY with a valid JSON array matching the following structure:

        [
          {
            "id": "a1",
            "title": "Visit Nairobi National Park",
            "isLocked": false,
            "isGenerated": true
          },
          ...
        ]

        Use short titles. Use unique string IDs like "a1", "b2", etc. 
        Set isGenerated to true and isLocked to false.
        Do NOT include markdown (no ```json), no explanations, and no prose — ONLY the JSON array. 
    """.trimIndent()
    }


    fun buildDescriptionPrompt(title: String): String {
        return "Hi Gemini, I'm planning an itinerary titled '$title'." +
                "Could you help me write a description for it? Please keep it stictly within $MAX_TOKENS tokens." +
                "        Do not make it markdown or give me options, just a sentence, keep it first person and acknowledge if plural"
    }


    const val GEMINI_MODEL = "gemini-2.5-flash"
    const val MAX_TOKENS = 100

}