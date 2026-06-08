<template>
  <div class="login-page">
    <div class="intro">
      <h1>TMDB 电影数据分析系统</h1>
      <p>基于 Spring Boot、MySQL、Vue 和 ECharts 构建的电影数据可视化平台。</p>

      <div class="features">
        <span>电影 Top100</span>
        <span>评分分析</span>
        <span>类型统计</span>
        <span>语言分布</span>
      </div>
    </div>

    <div class="auth-card">
      <h2>{{ isLogin ? "用户登录" : "用户注册" }}</h2>
      <p class="subtitle">
        {{ isLogin ? "登录后进入电影数据仪表盘" : "创建账号后即可登录系统" }}
      </p>

      <input
          v-model="form.username"
          placeholder="请输入用户名"
      />

      <input
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
      />

      <button @click="submit">
        {{ isLogin ? "登录" : "注册" }}
      </button>

      <p class="message">{{ message }}</p>

      <p class="switch">
        {{ isLogin ? "还没有账号？" : "已有账号？" }}
        <span @click="toggleMode">
          {{ isLogin ? "去注册" : "去登录" }}
        </span>
      </p>
    </div>
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
    message.value = "请输入用户名和密码";
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

  if (response.data.message === "登录成功") {
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
  grid-template-columns: 1.2fr 0.8fr;
  background: linear-gradient(135deg, #111827, #1f2937);
  color: white;
}

.intro {
  padding: 120px 90px;
}

.intro h1 {
  font-size: 48px;
  margin-bottom: 20px;
}

.intro p {
  max-width: 560px;
  line-height: 1.8;
  color: #d1d5db;
  font-size: 18px;
}

.features {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin-top: 36px;
}

.features span {
  padding: 10px 16px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
  color: #e5e7eb;
}

.auth-card {
  align-self: center;
  margin-right: 80px;
  padding: 42px;
  background: white;
  color: #111827;
  border-radius: 18px;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.35);
}

.auth-card h2 {
  margin: 0;
  font-size: 30px;
}

.subtitle {
  margin: 10px 0 28px;
  color: #6b7280;
}

input {
  width: 100%;
  padding: 14px;
  margin-bottom: 16px;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  box-sizing: border-box;
  font-size: 15px;
}

input:focus {
  outline: none;
  border-color: #2563eb;
}

button {
  width: 100%;
  padding: 14px;
  margin-top: 4px;
  border: none;
  border-radius: 10px;
  background: #2563eb;
  color: white;
  font-size: 16px;
  cursor: pointer;
}

button:hover {
  background: #1d4ed8;
}

.message {
  height: 22px;
  margin-top: 16px;
  color: #2563eb;
  font-weight: 600;
}

.switch {
  margin-top: 18px;
  color: #6b7280;
  text-align: center;
}

.switch span {
  color: #2563eb;
  cursor: pointer;
  font-weight: bold;
}

@media (max-width: 900px) {
  .login-page {
    grid-template-columns: 1fr;
  }

  .intro {
    padding: 60px 32px;
  }

  .intro h1 {
    font-size: 34px;
  }

  .auth-card {
    margin: 0 32px 60px;
  }
}
</style>