<template>
  <div class="dashboard">
    <div class="hero">
      <div>
        <span class="tag">Data Visualization Platform</span>
        <h1>Movie Analytics Dashboard</h1>
        <p>TMDB movie data analysis with recognition module.</p>
      </div>

      <div class="actions">
        <button class="ai-btn" @click="goDigit">Handwritten Digit Recognition</button>
        <button class="logout-btn" @click="logout">Logout</button>
      </div>
    </div>

    <div class="stats">
      <div class="stat-card">
        <span>Total Movies</span>
        <strong>{{ movies.length }}</strong>
      </div>
      <div class="stat-card">
        <span>Highest Rating</span>
        <strong>{{ maxRating }}</strong>
      </div>
      <div class="stat-card">
        <span>Languages</span>
        <strong>{{ languageCount }}</strong>
      </div>
      <div class="stat-card">
        <span>Genres</span>
        <strong>{{ genreCount }}</strong>
      </div>
    </div>

    <div class="grid">
      <div class="chart-card wide">
        <h2>Top 10 Rated Movies</h2>
        <div ref="top10ChartRef" class="chart"></div>
      </div>

      <div class="chart-card">
        <h2>Language Distribution</h2>
        <div ref="languageChartRef" class="chart"></div>
      </div>

      <div class="chart-card">
        <h2>Year Distribution</h2>
        <div ref="yearChartRef" class="chart"></div>
      </div>

      <div class="chart-card">
        <h2>Genre Distribution</h2>
        <div ref="genreChartRef" class="chart"></div>
      </div>
    </div>

    <div class="table-card">
      <div class="table-title">
        <h2>Movie Records</h2>
        <span>Top 20</span>
      </div>

      <table>
        <thead>
        <tr>
          <th>Rank</th>
          <th>Title</th>
          <th>Original Title</th>
          <th>Rating</th>
          <th>Year</th>
          <th>Language</th>
          <th>Genre</th>
        </tr>
        </thead>

        <tbody>
        <tr v-for="movie in movies.slice(0, 20)" :key="movie.id">
          <td>{{ movie.rankNo }}</td>
          <td class="title-cell">{{ movie.title }}</td>
          <td>{{ movie.originalTitle }}</td>
          <td><span class="rating">{{ movie.rating }}</span></td>
          <td>{{ movie.releaseYear }}</td>
          <td>{{ movie.language }}</td>
          <td>{{ movie.genre }}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import * as echarts from "echarts";

const router = useRouter();

const movies = ref([]);

const top10ChartRef = ref(null);
const yearChartRef = ref(null);
const languageChartRef = ref(null);
const genreChartRef = ref(null);

const maxRating = computed(() => {
  if (movies.value.length === 0) return "-";
  return Math.max(...movies.value.map(m => m.rating)).toFixed(1);
});

const languageCount = computed(() => {
  return new Set(movies.value.map(m => m.language)).size;
});

const genreCount = computed(() => {
  const set = new Set();

  movies.value.forEach(movie => {
    if (movie.genre) {
      movie.genre.split(",").forEach(g => set.add(g.trim()));
    }
  });

  return set.size;
});

const loadMovies = async () => {
  const token = localStorage.getItem("token");
  const response = await axios.get("http://localhost:8080/movies",{
    headers: {
      Authorization: "Bearer " + token
    }
  });
  movies.value = response.data;
};

const loadTop10Chart = async () => {
  const token = localStorage.getItem("token");
  const response = await axios.get("http://localhost:8080/movies/top10",{
    headers: {
      Authorization: "Bearer " + token
    }
  });

  const data = response.data;
  const titles = data.map(m => m.title);
  const ratings = data.map(m => m.rating);

  const chart = echarts.init(top10ChartRef.value);

  chart.setOption({
    tooltip: {
      trigger: "axis"
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "18%",
      containLabel: true
    },
    xAxis: {
      type: "category",
      data: titles,
      axisLabel: {
        rotate: 35
      }
    },
    yAxis: {
      type: "value",
      min: 0,
      max: 10
    },
    series: [
      {
        name: "评分",
        type: "bar",
        data: ratings,
        barWidth: "45%",
        itemStyle: {
          borderRadius: [8, 8, 0, 0]
        }
      }
    ]
  });
};

const loadYearChart = async () => {
  const token = localStorage.getItem("token");
  const response = await axios.get("http://localhost:8080/movies/year-stats",{
    headers: {
      Authorization: "Bearer " + token
    }
  });

  const data = response.data;
  const years = Object.keys(data);
  const counts = Object.values(data);

  const chart = echarts.init(yearChartRef.value);

  chart.setOption({
    tooltip: {
      trigger: "axis"
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "10%",
      containLabel: true
    },
    xAxis: {
      type: "category",
      data: years
    },
    yAxis: {
      type: "value"
    },
    series: [
      {
        name: "电影数量",
        type: "bar",
        data: counts,
        itemStyle: {
          borderRadius: [6, 6, 0, 0]
        }
      }
    ]
  });
};

const languageNameMap = {
  en: "English",
  ja: "Japanese",
  ko: "Korean",
  zh: "Chinese",
  fr: "French",
  es: "Spanish",
  it: "Italian",
  hi: "Hindi",
  pt: "Portuguese",
  tr: "Turkish"
};

const loadLanguageChart = async () => {
  const token = localStorage.getItem("token");
  const response = await axios.get("http://localhost:8080/movies/language-stats",{
    headers: {
      Authorization: "Bearer " + token
    }
  });

  const data = response.data;

  let chartData = Object.keys(data).map(key => {
    return {
      name: languageNameMap[key] || key,
      value: data[key]
    };
  });

  chartData.sort((a, b) => b.value - a.value);

  const topLanguages = chartData.slice(0, 5);

  const otherValue = chartData
      .slice(5)
      .reduce((sum, item) => sum + item.value, 0);

  if (otherValue > 0) {
    topLanguages.push({
      name: "Others",
      value: otherValue
    });
  }

  const chart = echarts.init(languageChartRef.value);

  chart.setOption({
    tooltip: {
      trigger: "item",
      formatter: "{b}: {c} 部 ({d}%)"
    },

    legend: {
      orient: "horizontal",
      bottom: 10,
      left: "center"
    },

    series: [
      {
        name: "语言分布",
        type: "pie",
        radius: ["42%", "62%"],
        center: ["50%", "42%"],
        data: topLanguages,

        label: {
          show: false
        },

        labelLine: {
          show: false
        },

        emphasis: {
          scale: true,
          scaleSize: 8
        }
      }
    ]
  });
};
const goDigit = () => {
  router.push("/digit");
};
const loadGenreChart = async () => {
  const token = localStorage.getItem("token");
  const response = await axios.get("http://localhost:8080/movies/genre-stats",{
    headers: {
      Authorization: "Bearer " + token
    }
  });

  const data = response.data;
  const genres = Object.keys(data);
  const counts = Object.values(data);

  const chart = echarts.init(genreChartRef.value);

  chart.setOption({
    tooltip: {
      trigger: "axis"
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "18%",
      containLabel: true
    },
    xAxis: {
      type: "category",
      data: genres,
      axisLabel: {
        rotate: 35
      }
    },
    yAxis: {
      type: "value"
    },
    series: [
      {
        name: "电影数量",
        type: "bar",
        data: counts,
        itemStyle: {
          borderRadius: [6, 6, 0, 0]
        }
      }
    ]
  });
};

const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("username");
  router.push("/");
};

onMounted(async () => {
  await loadMovies();
  await loadTop10Chart();
  await loadYearChart();
  await loadLanguageChart();
  await loadGenreChart();
});
</script>

<style scoped>
.dashboard {

  min-height: 100vh;

  padding: 36px;

  background: linear-gradient(135deg, #eef2ff 0%, #f8fafc 45%, #ecfeff 100%);

  font-family: Inter, Arial, sans-serif;

  color: #111827;

}

.hero {

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

.hero h1 {

  margin: 0;

  font-size: 34px;

}

.hero p {

  margin: 10px 0 0;

  color: #cbd5e1;

}

.actions {

  display: flex;

  gap: 12px;

}

.ai-btn,

.logout-btn {

  border: none;

  border-radius: 12px;

  padding: 11px 18px;

  font-weight: 700;

  cursor: pointer;

}

.ai-btn {

  background: #38bdf8;

  color: #0f172a;

}

.logout-btn {

  background: rgba(255, 255, 255, 0.12);

  color: white;

}

.stats {

  display: grid;

  grid-template-columns: repeat(4, 1fr);

  gap: 18px;

  margin-bottom: 24px;

}

.stat-card,

.chart-card,

.table-card {

  background: rgba(255, 255, 255, 0.88);

  border: 1px solid rgba(226, 232, 240, 0.9);

  border-radius: 20px;

  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.08);

  backdrop-filter: blur(10px);

}

.stat-card {

  padding: 22px;

}

.stat-card span {

  color: #64748b;

  font-size: 14px;

}

.stat-card strong {

  display: block;

  margin-top: 10px;

  font-size: 32px;

}

.grid {

  display: grid;

  grid-template-columns: 2fr 1fr;

  gap: 22px;

}

.chart-card {

  padding: 22px;

}

.chart-card h2,

.table-card h2 {

  margin: 0 0 16px;

  font-size: 18px;

}

.chart {

  width: 100%;

  height: 360px;

}

.table-card {

  margin-top: 24px;

  padding: 22px;

}

.table-title {

  display: flex;

  justify-content: space-between;

  align-items: center;

}

.table-title span {

  color: #64748b;

  font-size: 13px;

}

table {

  width: 100%;

  border-collapse: collapse;

  overflow: hidden;

}

th {

  text-align: left;

  color: #64748b;

  font-weight: 700;

  font-size: 13px;

  padding: 13px 10px;

  border-bottom: 1px solid #e5e7eb;

}

td {

  padding: 13px 10px;

  border-bottom: 1px solid #f1f5f9;

  color: #334155;

  font-size: 14px;

}

tr:hover {

  background: #f8fafc;

}

.title-cell {

  color: #0f172a;

  font-weight: 700;

}

.rating {

  display: inline-block;

  padding: 4px 8px;

  border-radius: 999px;

  background: #dbeafe;

  color: #1d4ed8;

  font-weight: 700;

}

@media (max-width: 900px) {

  .hero {

    flex-direction: column;

    align-items: flex-start;

    gap: 18px;

  }

  .stats {

    grid-template-columns: repeat(2, 1fr);

  }

  .grid {

    grid-template-columns: 1fr;

  }

}
</style>