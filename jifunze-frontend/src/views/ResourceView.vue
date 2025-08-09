<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation -->
    <nav class="bg-white shadow-lg border-b border-pink-200">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <h1 class="text-2xl font-bold text-pink-600">Jifunze Online</h1>
            </div>
          </div>
          <div class="flex items-center space-x-4">
            <router-link to="/" class="text-pink-600 hover:text-pink-800 px-3 py-2 rounded-md text-sm font-medium">
              Home
            </router-link>
            <router-link to="/login" class="text-pink-600 hover:text-pink-800 px-3 py-2 rounded-md text-sm font-medium">
              Login
            </router-link>
          </div>
        </div>
      </div>
    </nav>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-4">Learning Resources</h1>
        <p class="text-lg text-gray-600">Discover quality educational materials to enhance your learning journey.</p>
      </div>

      <!-- Search and Filters -->
      <div class="bg-white rounded-lg shadow p-6 mb-8">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="md:col-span-2">
            <label for="search" class="block text-sm font-medium text-gray-700 mb-2">Search Resources</label>
            <div class="relative">
              <input
                id="search"
                v-model="searchQuery"
                type="text"
                placeholder="Search by title, subject, or description..."
                class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-pink-500 focus:border-pink-500"
              />
              <div class="absolute inset-y-0 right-0 pr-3 flex items-center">
                <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
                </svg>
              </div>
            </div>
          </div>
          <div>
            <label for="category" class="block text-sm font-medium text-gray-700 mb-2">Category</label>
            <select
              id="category"
              v-model="selectedCategory"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-pink-500 focus:border-pink-500"
            >
              <option value="">All Categories</option>
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
            <label for="type" class="block text-sm font-medium text-gray-700 mb-2">Type</label>
            <select
              id="type"
              v-model="selectedType"
              class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-pink-500 focus:border-pink-500"
            >
              <option value="">All Types</option>
              <option value="free">Free</option>
              <option value="premium">Premium</option>
            </select>
          </div>
        </div>
      </div>

      <!-- Resources Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div v-for="resource in filteredResources" :key="resource.id" class="bg-white rounded-lg shadow-lg overflow-hidden hover:shadow-xl transition-shadow">
          <div class="p-6">
            <div class="flex items-center justify-between mb-4">
              <div class="flex items-center">
                <div class="w-10 h-10 bg-pink-100 rounded-lg flex items-center justify-center">
                  <svg class="w-6 h-6 text-pink-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                  </svg>
                </div>
                <div class="ml-3">
                  <span :class="resource.type === 'premium' ? 'bg-yellow-100 text-yellow-800' : 'bg-green-100 text-green-800'" class="px-2 py-1 text-xs font-medium rounded-full">
                    {{ resource.type }}
                  </span>
                </div>
              </div>
              <div class="text-right">
                <div class="text-sm text-gray-500">{{ resource.duration }}</div>
                <div class="text-sm text-gray-500">{{ resource.fileSize }}</div>
              </div>
            </div>

            <h3 class="text-lg font-semibold text-gray-900 mb-2">{{ resource.title }}</h3>
            <p class="text-gray-600 text-sm mb-4">{{ resource.description }}</p>

            <div class="flex items-center justify-between mb-4">
              <span class="text-sm text-gray-500">{{ resource.subject }}</span>
              <div class="flex items-center text-sm text-gray-500">
                <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"></path>
                </svg>
                {{ resource.views }}
              </div>
            </div>

            <div class="flex items-center justify-between">
              <div class="flex items-center">
                <div class="w-8 h-8 bg-gray-100 rounded-full flex items-center justify-center">
                  <svg class="w-4 h-4 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
                  </svg>
                </div>
                <span class="ml-2 text-sm text-gray-600">{{ resource.author }}</span>
              </div>
              <button class="px-4 py-2 bg-pink-600 text-white rounded-md hover:bg-pink-700 transition-colors">
                {{ resource.type === 'premium' ? 'Purchase' : 'Download' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div class="mt-8 flex justify-center">
        <nav class="flex items-center space-x-2">
          <button class="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50">
            Previous
          </button>
          <button class="px-3 py-2 text-sm font-medium text-white bg-pink-600 border border-pink-600 rounded-md">
            1
          </button>
          <button class="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50">
            2
          </button>
          <button class="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50">
            3
          </button>
          <button class="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50">
            Next
          </button>
        </nav>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const searchQuery = ref('')
const selectedCategory = ref('')
const selectedType = ref('')

const resources = ref([
  {
    id: 1,
    title: 'Advanced Calculus: Derivatives and Integrals',
    description: 'Comprehensive guide to calculus fundamentals with practical examples and exercises.',
    subject: 'Mathematics',
    type: 'premium',
    duration: '3 hours',
    fileSize: '2.5 MB',
    views: 1247,
    author: 'Dr. Sarah Johnson'
  },
  {
    id: 2,
    title: 'Physics Fundamentals: Mechanics',
    description: 'Introduction to classical mechanics covering Newton\'s laws and motion principles.',
    subject: 'Science',
    type: 'free',
    duration: '2 hours',
    fileSize: '1.8 MB',
    views: 892,
    author: 'Prof. Michael Chen'
  },
  {
    id: 3,
    title: 'English Literature: Shakespeare Analysis',
    description: 'Deep dive into Shakespeare\'s works with modern interpretations and analysis.',
    subject: 'Literature',
    type: 'premium',
    duration: '4 hours',
    fileSize: '3.2 MB',
    views: 567,
    author: 'Dr. Emily Davis'
  },
  {
    id: 4,
    title: 'Computer Science: Data Structures',
    description: 'Learn fundamental data structures and algorithms with practical implementations.',
    subject: 'Computer Science',
    type: 'free',
    duration: '2.5 hours',
    fileSize: '2.1 MB',
    views: 1345,
    author: 'Prof. Robert Wilson'
  },
  {
    id: 5,
    title: 'World History: Ancient Civilizations',
    description: 'Explore the rise and fall of ancient civilizations across different continents.',
    subject: 'History',
    type: 'premium',
    duration: '3.5 hours',
    fileSize: '2.8 MB',
    views: 723,
    author: 'Dr. Lisa Thompson'
  },
  {
    id: 6,
    title: 'Business Management: Strategic Planning',
    description: 'Learn strategic planning and business management principles for modern organizations.',
    subject: 'Business',
    type: 'premium',
    duration: '3 hours',
    fileSize: '2.3 MB',
    views: 456,
    author: 'Prof. James Anderson'
  }
])

const filteredResources = computed(() => {
  return resources.value.filter(resource => {
    const matchesSearch = searchQuery.value === '' ||
      resource.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      resource.description.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      resource.subject.toLowerCase().includes(searchQuery.value.toLowerCase())

    const matchesCategory = selectedCategory.value === '' ||
      resource.subject.toLowerCase() === selectedCategory.value.toLowerCase()

    const matchesType = selectedType.value === '' ||
      resource.type === selectedType.value

    return matchesSearch && matchesCategory && matchesType
  })
})
</script>
