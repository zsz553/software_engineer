<template>
  <div class="login-page">
    <section class="intro">
      <div class="badge">Data Visualization Platform</div>

      <h1>Movie Data Analytics</h1>


      <div class="features">
        <span>TMDB Analytics</span>
        <span>ECharts Dashboard</span>
        <span>Neural Network</span>
        <span>Image Recognition</span>
      </div>
    </section>

    <section class="auth-card">
      <h2>{{ isLogin ? "Welcome back" : "Create account" }}</h2>
      <p class="subtitle">
        {{ isLogin ? "After logging in, you will enter the data analysis platform." : "After registering your account, start using the system." }}
      </p>

      <input v-model="form.username" placeholder="Username" />
      <input v-model="form.password" type="password" placeholder="Password" />

      <button @click="submit">
        {{ isLogin ? "Login" : "Register" }}
      </button>

      <p class="message">{{ message }}</p>

      <p class="switch">
        {{ isLogin ? "No account?" : "Already have account?" }}
        <span @click="toggleMode">
          {{ isLogin ? "Register" : "Login" }}
        </span>
      </p>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

const router = useRouter();

const isLogin = ref(true);
const message = ref("");

const form = reactive({
  username: "",
  password: ""
});

const toggleMode = () => {
  isLogin.value = !isLogin.value;
  message.value = "";
  form.username = "";
  form.password = "";
};

const submit = async () => {
  if (!form.username || !form.password) {
    message.value = "Please enter your username and password.";
    return;
  }

  const url = isLogin.value
      ? "http://localhost:8080/users/login"
      : "http://localhost:8080/users/register";

  const response = await axios.post(url, {
    username: form.username,
    password: form.password
  });

  message.value = response.data;

  if (response.data.message === "Login successfully") {
    localStorage.setItem("token", response.data.token);
    localStorage.setItem("username", response.data.username);
    router.push("/movies");
  }
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1.15fr 0.85fr;
  background:
      radial-gradient(circle at top left, rgba(56, 189, 248, 0.28), transparent 32%),
      radial-gradient(circle at bottom right, rgba(99, 102, 241, 0.32), transparent 34%),
      linear-gradient(135deg, #020617, #0f172a 55%, #111827);
  color: white;
  font-family: Inter, Arial, sans-serif;
}

.intro {
  padding: 120px 90px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.badge {
  width: fit-content;
  margin-bottom: 24px;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(56, 189, 248, 0.16);
  border: 1px solid rgba(125, 211, 252, 0.28);
  color: #bae6fd;
  font-size: 13px;
  font-weight: 700;
}

.intro h1 {
  margin: 0;
  max-width: 620px;
  font-size: 58px;
  line-height: 1.05;
  letter-spacing: -1.5px;
}

.intro p {
  max-width: 560px;
  margin-top: 22px;
  line-height: 1.8;
  color: #cbd5e1;
  font-size: 18px;
}

.features {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 36px;
}

.features span {
  padding: 10px 16px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.1);
  color: #e2e8f0;
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.auth-card {
  align-self: center;
  margin-right: 80px;
  padding: 42px;
  background: rgba(255, 255, 255, 0.96);
  color: #0f172a;
  border-radius: 26px;
  box-shadow: 0 28px 70px rgba(0, 0, 0, 0.38);
}

.auth-card h2 {
  margin: 0;
  font-size: 32px;
  letter-spacing: -0.5px;
}

.subtitle {
  margin: 10px 0 28px;
  color: #64748b;
}

input {
  width: 100%;
  padding: 15px;
  margin-bottom: 16px;
  border: 1px solid #dbe3ef;
  border-radius: 14px;
  box-sizing: border-box;
  font-size: 15px;
  background: #f8fafc;
}

input:focus {
  outline: none;
  border-color: #38bdf8;
  background: white;
  box-shadow: 0 0 0 4px rgba(56, 189, 248, 0.15);
}

button {
  width: 100%;
  padding: 15px;
  margin-top: 6px;
  border: none;
  border-radius: 14px;
  background: linear-gradient(135deg, #2563eb, #38bdf8);
  color: white;
  font-size: 16px;
  font-weight: 800;
  cursor: pointer;
}

button:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 24px rgba(37, 99, 235, 0.25);
}

.message {
  min-height: 22px;
  margin-top: 16px;
  color: #2563eb;
  font-weight: 700;
}

.switch {
  margin-top: 18px;
  color: #64748b;
  text-align: center;
}

.switch span {
  color: #2563eb;
  cursor: pointer;
  font-weight: 800;
}

@media (max-width: 900px) {
  .login-page {
    grid-template-columns: 1fr;
  }

  .intro {
    padding: 60px 32px 30px;
  }

  .intro h1 {
    font-size: 40px;
  }

  .auth-card {
    margin: 0 32px 60px;
  }
}
</style>