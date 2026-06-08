import { createRouter, createWebHistory } from "vue-router";

import LoginView from "../views/LoginView.vue";
import MovieView from "../views/MovieView.vue";

const routes = [
    {
        path: "/",
        component: LoginView
    },
    {
        path: "/movies",
        component: MovieView
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;