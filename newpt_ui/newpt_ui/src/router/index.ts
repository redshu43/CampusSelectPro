// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'

// 1. 引入我们的页面组件
import Login from '../views/Login.vue'
import Dashboard from "../views/Dashboard.vue"

// 2. 定义路由规则 (Routes)
// 告诉领位员：客人访问哪个路径 (path)，就给他看哪个组件 (component)
const routes = [
    {
        path: '/',
        redirect: '/login' // 访问根路径时，自动跳转到登录页
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    // 后台首页路由
    {
        path: '/dashboard',
        component: Dashboard,
        redirect: '/dashboard/home', // 默认跳至欢迎页
        children: [
            {
                path: 'home', // 访问 /dashboard/home
                name: 'Home',
                component: () => import('../views/home/index.vue') // 懒加载
            },
            {
                path: 'student', // 访问 /dashboard/student
                name: 'StudentList',
                component: () => import('../views/student/index.vue')
            },
            {
                path: 'personal',
                name: 'Personal',
                component: () => import('../views/personal/index.vue')
            },
            {
                path: 'course',
                name: 'CourseList',
                component: () => import('../views/course/index.vue')
            },
            {
                path: 'selection',
                name: 'CourseSelection',
                component: () => import('../views/selection/index.vue')
            },
            {
                path: 'my-course', // URL: /dashboard/my-course
                name: 'MyCourse',
                component: () => import('../views/my-course/index.vue')

            }
        ]
    }
    // 未来我们就在这里添加 '/admin/students' 等路由
]

// 3. 创建路由实例
const router = createRouter({
    // history 模式：地址栏不带 # 号，更美观 (比如 http://localhost:5173/login)
    history: createWebHistory(),
    routes
})

// 4. 导出路由，给 main.ts 使用
export default router