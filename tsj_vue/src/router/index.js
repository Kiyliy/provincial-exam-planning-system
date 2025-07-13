import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/elementplus',
      name: 'elementplus',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/elementplus.vue'),
    },
    {
      path: '/',
      name: 'Login',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/Login.vue'),
    },
    {
      path: '/student',
      name: 'Student',
      component: () => import('../views/student.vue'),
    },
    {
      path: '/teach',
      name: 'Teach',
      component: () => import('../views/teach.vue'),
    },
  ],
})
export default router