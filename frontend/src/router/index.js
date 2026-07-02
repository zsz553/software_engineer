import { createRouter, createWebHistory } from "vue-router";

import LoginView from "../views/LoginView.vue";
import MovieView from "../views/MovieView.vue";
import DigitRecognitionView from "../views/DigitRecognitionView.vue";

const routes = [
    {
        path: "/",
        component: LoginView
    },
    {
        path: "/movies",
        component: MovieView
    },
    {
        path: "/digit",
        component: DigitRecognitionView
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;