<template>
  <div class="digit-page">
    <div class="top-bar">
      <div>
        <span class="tag">AI Module</span>
        <h1>Handwritten Digit Recognition</h1>
        <p>Upload a digit image and let the neural network predict the result.</p>
      </div>

      <button class="back-btn" @click="goBack">Back to Dashboard</button>
    </div>

    <div class="content">
      <div class="upload-card">
        <h2>Upload Image</h2>

        <label class="upload-box">
          <input type="file" accept="image/*" @change="handleFileChange" />
          <span v-if="!preview">Click to choose an image</span>
          <img v-else :src="preview" alt="preview" />
        </label>

        <button class="predict-btn" @click="predict" :disabled="loading">
          {{ loading ? "Predicting..." : "Predict Digit" }}
        </button>

        <p class="error" v-if="error">{{ error }}</p>
      </div>

      <div class="result-card">
        <h2>Prediction Result</h2>

        <div v-if="result" class="result">
          <div class="digit">{{ result.digit }}</div>
          <p>Confidence</p>
          <strong>{{ (result.confidence * 100).toFixed(2) }}%</strong>
        </div>

        <div v-else class="empty">
          No prediction yet.
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

const router = useRouter();

const file = ref(null);
const preview = ref(null);
const result = ref(null);
const loading = ref(false);
const error = ref("");

const goBack = () => {
  router.push("/movies");
};

const handleFileChange = (event) => {
  const selectedFile = event.target.files[0];

  if (!selectedFile) return;

  file.value = selectedFile;
  preview.value = URL.createObjectURL(selectedFile);
  result.value = null;
  error.value = "";
};

const predict = async () => {
  if (!file.value) {
    error.value = "Please upload an image first.";
    return;
  }

  try {
    loading.value = true;
    error.value = "";

    const formData = new FormData();
    formData.append("file", file.value);

    const response = await axios.post(
        "http://localhost:8080/digit/predict",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data"
          }
        }
    );

    result.value = response.data;
  } catch (e) {
    error.value = "Prediction failed. Please try again.";
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.digit-page {
  min-height: 100vh;
  padding: 36px;
  background: linear-gradient(135deg, #eef2ff 0%, #f8fafc 45%, #ecfeff 100%);
  font-family: Inter, Arial, sans-serif;
  color: #111827;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32px;
  border-radius: 24px;
  background: linear-gradient(135deg, #111827, #1e3a8a);
  color: white;
  margin-bottom: 28px;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.22);
}

.tag {
  display: inline-block;
  margin-bottom: 12px;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  font-size: 13px;
}

.top-bar h1 {
  margin: 0;
  font-size: 34px;
}

.top-bar p {
  margin: 10px 0 0;
  color: #cbd5e1;
}

.back-btn {
  border: none;
  border-radius: 12px;
  padding: 11px 18px;
  background: rgba(255, 255, 255, 0.12);
  color: white;
  font-weight: 700;
  cursor: pointer;
}

.content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.upload-card,
.result-card {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(226, 232, 240, 0.9);
  border-radius: 22px;
  padding: 28px;
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.08);
}

.upload-card h2,
.result-card h2 {
  margin-top: 0;
  font-size: 20px;
}

.upload-box {
  height: 280px;
  border: 2px dashed #93c5fd;
  border-radius: 20px;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  overflow: hidden;
  color: #64748b;
  font-weight: 700;
}

.upload-box input {
  display: none;
}

.upload-box img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.predict-btn {
  width: 100%;
  margin-top: 22px;
  padding: 14px;
  border: none;
  border-radius: 14px;
  background: linear-gradient(135deg, #2563eb, #38bdf8);
  color: white;
  font-size: 16px;
  font-weight: 800;
  cursor: pointer;
}

.predict-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.result-card {
  display: flex;
  flex-direction: column;
}

.result {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.digit {
  width: 150px;
  height: 150px;
  border-radius: 34px;
  background: #dbeafe;
  color: #1d4ed8;
  font-size: 86px;
  font-weight: 900;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 22px;
}

.result p {
  margin: 0;
  color: #64748b;
}

.result strong {
  margin-top: 8px;
  font-size: 30px;
}

.empty {
  flex: 1;
  min-height: 280px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
}

.error {
  color: #dc2626;
  margin-top: 14px;
  font-weight: 700;
}

@media (max-width: 900px) {
  .top-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 18px;
  }

  .content {
    grid-template-columns: 1fr;
  }
}
</style>