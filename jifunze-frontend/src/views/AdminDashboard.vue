<template>
  <div class="min-h-screen bg-gradient-to-br from-red-50 to-pink-100">
    <!-- Navigation -->
    <nav class="bg-white shadow-lg">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center">
            <h1 class="text-xl font-semibold text-gray-800">Admin Dashboard</h1>
          </div>
          <div class="flex items-center space-x-4">
            <span class="text-gray-700">{{ user?.fullName }}</span>
            <button
              @click="logout"
              class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg transition-colors"
            >
              Logout
            </button>
          </div>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Welcome Section -->
      <div class="bg-white rounded-lg shadow-md p-6 mb-8">
        <h2 class="text-2xl font-bold text-gray-800 mb-2">
          Welcome back, {{ user?.fullName }}!
        </h2>
        <p class="text-gray-600">Manage your platform and monitor system performance.</p>
      </div>

      <!-- Stats Grid -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <div class="bg-white rounded-lg shadow-md p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-blue-100 text-blue-600">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Total Users</p>
              <p class="text-2xl font-semibold text-gray-900">{{ stats.totalUsers }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow-md p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-green-100 text-green-600">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Total Instructors</p>
              <p class="text-2xl font-semibold text-gray-900">{{ stats.totalInstructors }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow-md p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-purple-100 text-purple-600">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Total Students</p>
              <p class="text-2xl font-semibold text-gray-900">{{ stats.totalStudents }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow-md p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-full bg-yellow-100 text-yellow-600">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1"></path>
              </svg>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Total Revenue</p>
              <p class="text-2xl font-semibold text-gray-900">${{ stats.totalRevenue }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        <div class="bg-white rounded-lg shadow-md p-6">
          <h3 class="text-lg font-semibold text-gray-800 mb-4">Resource Management</h3>
          <div class="space-y-3">
            <button
              @click="showResourceUpload = true"
              class="w-full bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg transition-colors"
            >
              Upload Resource
            </button>
            <button
              @click="loadResources"
              class="w-full bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded-lg transition-colors"
            >
              View All Resources
            </button>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow-md p-6">
          <h3 class="text-lg font-semibold text-gray-800 mb-4">User Management</h3>
          <div class="space-y-3">
            <button
              @click="showAddInstructor = true"
              class="w-full bg-purple-500 hover:bg-purple-600 text-white px-4 py-2 rounded-lg transition-colors"
            >
              Add Instructor
            </button>
            <button
              @click="loadUsers"
              class="w-full bg-indigo-500 hover:bg-indigo-600 text-white px-4 py-2 rounded-lg transition-colors"
            >
              View All Users
            </button>
          </div>
        </div>
      </div>

      <!-- Resources Table -->
      <div v-if="resources.length > 0" class="bg-white rounded-lg shadow-md p-6 mb-8">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">Resources</h3>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Title</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Category</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Downloads</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Revenue</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="resource in resources" :key="resource.id">
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ resource.title }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ resource.category }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ resource.downloads }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${{ resource.revenue }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <button class="text-indigo-600 hover:text-indigo-900 mr-3">Edit</button>
                  <button class="text-red-600 hover:text-red-900">Delete</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Users Table -->
      <div v-if="users.length > 0" class="bg-white rounded-lg shadow-md p-6 mb-8">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">Users</h3>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Role</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Total Spent</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="user in users" :key="user.id">
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ user.fullName }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ user.email }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  <span :class="getRoleBadgeClass(user.role)" class="px-2 py-1 text-xs font-medium rounded-full">
                    {{ user.role }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${{ user.totalSpent }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Resource Upload Modal -->
      <div v-if="showResourceUpload" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
        <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
          <div class="mt-3">
            <h3 class="text-lg font-medium text-gray-900 mb-4">Upload Resource</h3>
            <form @submit.prevent="uploadResource" class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700">Title</label>
                <input v-model="resourceForm.title" type="text" required class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">Description</label>
                <textarea v-model="resourceForm.description" required class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"></textarea>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">Price</label>
                <input v-model="resourceForm.price" type="number" step="0.01" required class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">Category</label>
                <select v-model="resourceForm.category" required class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2">
                  <option value="">Select Category</option>
                  <option value="mathematics">Mathematics</option>
                  <option value="science">Science</option>
                  <option value="languages">Languages</option>
                  <option value="history">History</option>
                  <option value="geography">Geography</option>
                  <option value="literature">Literature</option>
                  <option value="computer-science">Computer Science</option>
                  <option value="business">Business</option>
                  <option value="arts">Arts</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">File</label>
                <input @change="handleFileSelect" type="file" accept=".pdf,.doc,.docx,.ppt,.pptx" required class="mt-1 block w-full">
              </div>
              <div class="flex justify-end space-x-3">
                <button type="button" @click="showResourceUpload = false" class="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400">
                  Cancel
                </button>
                <button type="submit" :disabled="uploading" class="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 disabled:opacity-50">
                  {{ uploading ? 'Uploading...' : 'Upload' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- Add Instructor Modal -->
      <div v-if="showAddInstructor" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
        <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
          <div class="mt-3">
            <h3 class="text-lg font-medium text-gray-900 mb-4">Add Instructor</h3>
            <form @submit.prevent="addInstructor" class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700">Full Name</label>
                <input v-model="instructorForm.fullName" type="text" required class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">Phone Number</label>
                <input v-model="instructorForm.phoneNumber" type="tel" required class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">Email</label>
                <input v-model="instructorForm.email" type="email" required class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">Specialization</label>
                <input v-model="instructorForm.specialization" type="text" required class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2">
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">Bio</label>
                <textarea v-model="instructorForm.bio" required class="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"></textarea>
              </div>
              <div class="flex justify-end space-x-3">
                <button type="button" @click="showAddInstructor = false" class="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400">
                  Cancel
                </button>
                <button type="submit" :disabled="addingInstructor" class="px-4 py-2 bg-purple-500 text-white rounded-md hover:bg-purple-600 disabled:opacity-50">
                  {{ addingInstructor ? 'Adding...' : 'Add Instructor' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import axios from 'axios'

interface Resource {
  id: number
  title: string
  category: string
  downloads: number
  revenue: number
}

interface User {
  id: number
  fullName: string
  email: string
  role: string
  totalSpent: number
}

const authStore = useAuthStore()
const user = authStore.user

// State
const stats = ref({
  totalUsers: 0,
  totalInstructors: 0,
  totalStudents: 0,
  totalRevenue: 0
})

const resources = ref<Resource[]>([])
const users = ref<User[]>([])
const showResourceUpload = ref(false)
const showAddInstructor = ref(false)
const uploading = ref(false)
const addingInstructor = ref(false)

const resourceForm = ref({
  title: '',
  description: '',
  price: '',
  category: '',
  file: null as File | null
})

const instructorForm = ref({
  fullName: '',
  phoneNumber: '',
  email: '',
  specialization: '',
  bio: ''
})

// Configure axios
axios.defaults.baseURL = 'http://localhost:8080'
axios.defaults.withCredentials = true

// Methods
const loadDashboardData = async () => {
  try {
    const response = await axios.get('/api/dashboard/admin')
    if (response.data.status === 'success') {
      stats.value = response.data.data
    }
  } catch (error) {
    console.error('Failed to load dashboard data:', error)
  }
}

const loadResources = async () => {
  try {
    const response = await axios.get('/admin/resources')
    if (response.data.status === 'success') {
      resources.value = response.data.resources
    }
  } catch (error) {
    console.error('Failed to load resources:', error)
  }
}

const loadUsers = async () => {
  try {
    const response = await axios.get('/admin/users')
    if (response.data.status === 'success') {
      users.value = response.data.users
    }
  } catch (error) {
    console.error('Failed to load users:', error)
  }
}

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files[0]) {
    resourceForm.value.file = target.files[0]
  }
}

const uploadResource = async () => {
  if (!resourceForm.value.file) return

  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', resourceForm.value.file)
    formData.append('title', resourceForm.value.title)
    formData.append('description', resourceForm.value.description)
    formData.append('price', resourceForm.value.price)
    formData.append('category', resourceForm.value.category)

    const response = await axios.post('/admin/resources', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (response.data.status === 'success') {
      showResourceUpload.value = false
      resourceForm.value = {
        title: '',
        description: '',
        price: '',
        category: '',
        file: null
      }
      await loadResources()
    }
  } catch (error) {
    console.error('Failed to upload resource:', error)
  } finally {
    uploading.value = false
  }
}

const addInstructor = async () => {
  addingInstructor.value = true
  try {
    const response = await axios.post('/api/instructors/register', {
      fullName: instructorForm.value.fullName,
      phoneNumber: instructorForm.value.phoneNumber,
      email: instructorForm.value.email,
      specialization: instructorForm.value.specialization,
      bio: instructorForm.value.bio,
      role: 'INSTRUCTOR'
    })

    if (response.data.status === 'success') {
      showAddInstructor.value = false
      instructorForm.value = {
        fullName: '',
        phoneNumber: '',
        email: '',
        specialization: '',
        bio: ''
      }
      await loadDashboardData()
    }
  } catch (error) {
    console.error('Failed to add instructor:', error)
  } finally {
    addingInstructor.value = false
  }
}

const getRoleBadgeClass = (role: string) => {
  switch (role) {
    case 'ADMIN':
      return 'bg-red-100 text-red-800'
    case 'INSTRUCTOR':
      return 'bg-green-100 text-green-800'
    case 'STUDENT':
      return 'bg-blue-100 text-blue-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

const logout = () => {
  authStore.logout()
}

onMounted(async () => {
  await loadDashboardData()
})
</script>
