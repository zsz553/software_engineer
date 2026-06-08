<template>
  <div class="dashboard">
    <div class="header">
      <div>
        <h1>TMDB 电影数据可视化系统</h1>
        <p>基于 Spring Boot + MySQL + Vue + ECharts</p>
      </div>

      <button class="logout-btn" @click="logout">退出登录</button>
    </div>

    <div class="stats">
      <div class="stat-card">
        <span>电影总数</span>
        <strong>{{ movies.length }}</strong>
      </div>

      <div class="stat-card">
        <span>最高评分</span>
        <strong>{{ maxRating }}</strong>
      </div>

      <div class="stat-card">
        <span>语言数量</span>
        <strong>{{ languageCount }}</strong>
      </div>

      <div class="stat-card">
        <span>类型数量</span>
        <strong>{{ genreCount }}</strong>
      </div>
    </div>

    <div class="grid">
      <div class="chart-card large">
        <h2>评分最高 Top10</h2>
        <div ref="top10ChartRef" class="chart"></div>
      </div>

      <div class="chart-card">
        <h2>语言分布</h2>
        <div ref="languageChartRef" class="chart"></div>
      </div>

      <div class="chart-card">
        <h2>年份分布</h2>
        <div ref="yearChartRef" class="chart"></div>
      </div>

      <div class="chart-card">
        <h2>类型分布</h2>
        <div ref="genreChartRef" class="chart"></div>
      </div>
    </div>

    <div class="table-card">
      <h2>电影列表</h2>

      <table>
        <thead>
        <tr>
          <th>排名</th>
          <th>电影名</th>
          <th>原名</th>
          <th>评分</th>
          <th>年份</th>
          <th>语言</th>
          <th>类型</th>
        </tr>
        </thead>

        <tbody>
        <tr v-for="movie in movies.slice(0, 20)" :key="movie.id">
          <td>{{ movie.rankNo }}</td>
          <td>{{ movie.title }}</td>
          <td>{{ movie.originalTitle }}</td>
          <td>{{ movie.rating }}</td>
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
  padding: 40px;
  background: #f5f7fb;
  font-family: Arial, sans-serif;
  color: #1f2937;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.header h1 {
  margin: 0;
  font-size: 32px;
}

.header p {
  margin-top: 8px;
  color: #6b7280;
}

.logout-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  background: #111827;
  color: white;
  cursor: pointer;
}

.stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 28px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 14px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
}

.stat-card span {
  color: #6b7280;
  font-size: 14px;
}

.stat-card strong {
  display: block;
  margin-top: 10px;
  font-size: 30px;
}

.grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.chart-card {
  background: white;
  padding: 24px;
  border-radius: 14px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
}

.chart-card.large {
  grid-column: span 1;
}

.chart-card h2,
.table-card h2 {
  margin-top: 0;
  font-size: 20px;
}

.chart {
  width: 100%;
  height: 380px;
}

.table-card {
  margin-top: 28px;
  background: white;
  padding: 24px;
  border-radius: 14px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
}

table {
  width: 100%;
  border-collapse: collapse;
}

th {
  text-align: left;
  color: #6b7280;
  font-weight: 600;
  padding: 12px;
  border-bottom: 1px solid #e5e7eb;
}

td {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
}

tr:hover {
  background: #f9fafb;
}

@media (max-width: 900px) {
  .stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .grid {
    grid-template-columns: 1fr;
  }
}
</style>