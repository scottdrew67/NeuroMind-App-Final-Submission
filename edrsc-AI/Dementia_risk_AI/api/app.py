from flask import Flask, request, jsonify
import joblib
import os
import pandas as pd
from flask_cors import CORS
from werkzeug.utils import secure_filename
import whisper

app = Flask(__name__)
CORS(app) # android calls cors


# Dynamically find base directory
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

# Correct absolute paths
model_path = os.path.join(BASE_DIR, "Model", "model", "risk_model.pkl")
scaler_path = os.path.join(BASE_DIR, "Model", "model", "scaler.pkl")

# Load model and scaler
model = joblib.load(model_path)
scaler = joblib.load(scaler_path)


print("Loading Whisper model...")
whisper_model = whisper.load_model("small")  # medium caused timeouts
print("Whisper loaded.")

@app.route("/upload-audio", methods=["POST"])
def upload_audio():
    # 1) ensure file exists
    if "audio" not in request.files:
        return jsonify({"error": "No audio file part named 'audio'"}), 400

    audio_file = request.files["audio"]
    if audio_file.filename == "":
        return jsonify({"error": "Empty filename"}), 400

    # 2) save file
    upload_dir = os.path.join(os.path.dirname(__file__), "uploads")
    os.makedirs(upload_dir, exist_ok=True)

    filename = secure_filename(audio_file.filename)
    save_path = os.path.join(upload_dir, filename)
    audio_file.save(save_path)

    print(f"✓ Received audio file: {filename} -> {save_path}", flush=True)

    # 3) transcribe with whisper
    try:
        result = whisper_model.transcribe(save_path, language="en")
        transcript = (result.get("text") or "").strip()
    except Exception as e:
        print("Whisper error:", str(e), flush=True)
        return jsonify({"error": f"Failed to transcribe audio: {e}"}), 500

    # 4) optional summary
    if transcript:
        words = transcript.split()
        summary = " ".join(words[:40]) + ("..." if len(words) > 40 else "")
    else:
        summary = ""

    return jsonify({
        "transcript": transcript,
        "summary": summary
    }), 200


def extract_speech_features(text: str):
    import re
    words = re.findall(r"\b\w+\b", (text or "").lower())
    word_count = len(words)
    avg_word_len = (sum(len(w) for w in words) / word_count) if word_count else 0.0
    fillers = {"um", "uh", "like", "you", "know", "so", "well", "actually", "basically", "literally"}
    filler_count = sum(1 for w in words if w in fillers)
    return word_count, avg_word_len, filler_count


# ===== Root endpoint to avoid 404 =====
@app.route("/", methods=["GET"])
def home():
    return jsonify({"message": "Flask API is running!"})


@app.route("/predict", methods=["POST"])
def predict():
    data = request.json

    print("Received JSON:", data, flush=True)  # moved before return

    try:
        age = data["age"]
        sleep_hours = data["sleep_hours"]
        memory_score = data["memory_score"]
        speech_text = data["speech_text"]

        # Extract speech features
        word_count, avg_word_len, filler_count = extract_speech_features(speech_text)

        feature_names = ["age", "sleep_hours", "memory_score", "word_count", "avg_word_len", "filler_count"]
        X_new = pd.DataFrame([[age, sleep_hours, memory_score, word_count, avg_word_len, filler_count]],
                             columns=feature_names)
        X_scaled = scaler.transform(X_new)

        # Predict
        prediction = model.predict(X_scaled)[0]
        probability = model.predict_proba(X_scaled)[0]

        result = {
            "prediction": "High Risk" if prediction == 1 else "Low Risk",
            "probability": {
                "low_risk": round(float(probability[0]) * 100, 2),
                "high_risk": round(float(probability[1]) * 100, 2)
            }
        }

        print("Returning:", result, flush=True)
        return jsonify(result)

    except Exception as e:
        print("Error:", str(e), flush=True)
        return jsonify({"error": str(e)}), 400


if __name__ == "__main__":
    print("Starting Flask API on 0.0.0.0:5000", flush=True)
    app.run(host="0.0.0.0", port=5000, debug=True)
