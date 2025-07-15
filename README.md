# ✈️ Itinerar-AI

**Itinerar-AI** is a lightweight AI demo app that showcases how large language models (like Gemini) can be used to generate, edit, and extend travel itineraries.
It was built over two 4-hour days as an experiment in blending smart generation with a clean, Compose-driven UI.

### 🎥 Demo

[Click here to watch the screen recording](https://github.com/donald-okara/Itinerar-AI/Screen_recording_20250715_154208.mp4)

## 🎯 What This Is

This is **not** a production travel app.
It’s a **proof of concept**: a fast-built, opinionated prototype exploring how AI can be embedded into mobile user flows.
It’s also a **starter template** for anyone looking to build an AI-powered Compose app with Firebase Vertex AI (Gemini).




## 🚀 What It Does

* 🧠 **AI-generated descriptions**
  Give your trip a title, and the app writes a description for you.

* 📍 **Generated itinerary items**
  Get up to 4 suggested steps/locations for your trip in structured JSON form.

* 🛠️ **Manual + AI editing**
  Add, edit, and reorder items freely. You can also ask the AI to insert suggestions inline, avoiding duplicates and preserving list logic.

* 🖱️ **Drag and drop UI**
  Draggable cards for reordering with subtle scaling feedback.

* 💡 **AI badges + transparency**
  AI-generated items are marked so you know what was suggested vs. typed.


## ⚙️ Stack

* **Jetpack Compose**
* **Kotlin**
* **Firebase Vertex AI (Gemini)**
* **ViewModel + StateFlow**
* **Kotlinx Serialization**


## 🛠️ Setup

To run Itinerar-AI locally:

1. **Clone the repo**

   ```bash
   git clone https://github.com/yourusername/itinerar-ai.git
   ```

2. **Open in Android Studio**
   Use Arctic Fox or newer with Kotlin + Compose support.

3. **Connect Firebase**

   * Add your own `google-services.json`
   * Enable **Vertex AI** (Generative Model) from Firebase Extensions
   * Upgrade project to Blaze
   * (Optional) Disable or configure **App Check** to avoid token issues in dev mode

4. **Run the app**

   * The app should launch on an emulator or real device without additional config.
   * Errors like token or JSON failures may happen if the AI returns non-strict output.


## 🧪 Use as a Starter Template

Want to build your own AI-powered app?
You can clone this project as a **starter kit** with:

* Gemini API wired up
* Basic prompting patterns (description, structured JSON, insertion)
* Flow-based AI response handling
* Composable state management
* Clean UI with animation & drag-and-drop

Fork and build from here 🚀


## 📌 Notes

* AI output can be non-deterministic — handle parsing safely.
* This app doesn't persist data (no DB or cloud sync).
* No auth, no backend — everything runs locally except the Gemini calls.

## Credit
The draggable Lazy column was adapted from [Artemake](https://github.com/Artemake/Reordering-LazyColumn)

## 📄 License

MIT – Free to clone, adapt, and remix.

