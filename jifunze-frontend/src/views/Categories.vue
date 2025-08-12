<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 py-8 px-4">
    <div class="max-w-7xl mx-auto">
      <!-- Header -->
      <div class="text-center mb-12">
        <h1 class="text-4xl font-bold text-gray-900 mb-4">Educational Categories</h1>
        <p class="text-lg text-gray-600 max-w-2xl mx-auto">
          Choose your educational level to explore subjects and learning resources tailored to your needs.
        </p>
      </div>

      <!-- Categories Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
        <!-- Lower Secondary -->
        <div
          @click="selectCategory('lower-secondary')"
          class="category-card bg-white rounded-xl shadow-lg hover:shadow-xl transition-all duration-300 cursor-pointer transform hover:-translate-y-2"
        >
          <div class="p-6">
            <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mb-4 mx-auto">
              <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path>
              </svg>
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-2 text-center">Lower Secondary</h3>
            <p class="text-gray-600 text-center text-sm mb-4">Grades 7-8</p>
            <div class="text-center">
              <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                {{ getSubjectCount('lower-secondary') }} Subjects
              </span>
            </div>
          </div>
        </div>

        <!-- Upper Secondary -->
        <div
          @click="selectCategory('upper-secondary')"
          class="category-card bg-white rounded-xl shadow-lg hover:shadow-xl transition-all duration-300 cursor-pointer transform hover:-translate-y-2"
        >
          <div class="p-6">
            <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-4 mx-auto">
              <svg class="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"></path>
              </svg>
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-2 text-center">Upper Secondary</h3>
            <p class="text-gray-600 text-center text-sm mb-4">Grades 9-10</p>
            <div class="text-center">
              <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800">
                {{ getSubjectCount('upper-secondary') }} Subjects
              </span>
            </div>
          </div>
        </div>

        <!-- Junior Secondary -->
        <div
          @click="selectCategory('junior-secondary')"
          class="category-card bg-white rounded-xl shadow-lg hover:shadow-xl transition-all duration-300 cursor-pointer transform hover:-translate-y-2"
        >
          <div class="p-6">
            <div class="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mb-4 mx-auto">
              <svg class="w-8 h-8 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z"></path>
              </svg>
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-2 text-center">Junior Secondary</h3>
            <p class="text-gray-600 text-center text-sm mb-4">Grades 7-9</p>
            <div class="text-center">
              <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-purple-100 text-purple-800">
                {{ getSubjectCount('junior-secondary') }} Subjects
              </span>
            </div>
          </div>
        </div>

        <!-- High School -->
        <div
          @click="selectCategory('high-school')"
          class="category-card bg-white rounded-xl shadow-lg hover:shadow-xl transition-all duration-300 cursor-pointer transform hover:-translate-y-2"
        >
          <div class="p-6">
            <div class="w-16 h-16 bg-orange-100 rounded-full flex items-center justify-center mb-4 mx-auto">
              <svg class="w-8 h-8 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 14l9-5-9-5-9 5 9 5z"></path>
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 14l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14z"></path>
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 14l9-5-9-5-9 5 9 5zm0 0l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14z"></path>
              </svg>
            </div>
            <h3 class="text-xl font-semibold text-gray-900 mb-2 text-center">High School</h3>
            <p class="text-gray-600 text-center text-sm mb-4">Grades 11-12</p>
            <div class="text-center">
              <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-orange-100 text-orange-800">
                {{ getSubjectCount('high-school') }} Subjects
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Selected Category Subjects -->
      <div v-if="selectedCategory" class="bg-white rounded-xl shadow-lg p-8">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h2 class="text-2xl font-bold text-gray-900">{{ getCategoryTitle(selectedCategory) }}</h2>
            <p class="text-gray-600">{{ getCategoryDescription(selectedCategory) }}</p>
          </div>
          <button
            @click="selectedCategory = null"
            class="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <!-- Subjects Grid -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div
            v-for="subject in getSubjects(selectedCategory)"
            :key="subject.id"
            @click="selectSubject(subject)"
            class="subject-card bg-gray-50 rounded-lg p-6 hover:bg-gray-100 transition-colors cursor-pointer border border-gray-200 hover:border-gray-300"
          >
            <div class="flex items-center justify-between mb-4">
              <div class="w-12 h-12 rounded-lg flex items-center justify-center" :class="getSubjectColor(subject.name)">
                <span class="text-white font-semibold text-lg">{{ subject.name.charAt(0) }}</span>
              </div>
              <span class="text-xs text-gray-500">{{ subject.resourceCount }} resources</span>
            </div>
            <h3 class="text-lg font-semibold text-gray-900 mb-2">{{ subject.name }}</h3>
            <p class="text-gray-600 text-sm mb-4">{{ subject.description }}</p>
            <div class="flex items-center text-sm text-gray-500">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path>
              </svg>
              {{ subject.topics.length }} topics
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const selectedCategory = ref<string | null>(null)

// Category data
const categories = {
  'lower-secondary': {
    title: 'Lower Secondary',
    description: 'Grades 7-8: Building strong foundations in core subjects',
    subjects: [
      {
        id: 1,
        name: 'Mathematics',
        description: 'Algebra, geometry, and basic mathematical concepts',
        resourceCount: 45,
        topics: ['Algebra', 'Geometry', 'Statistics', 'Number Theory']
      },
      {
        id: 2,
        name: 'English',
        description: 'Language skills, literature, and communication',
        resourceCount: 38,
        topics: ['Grammar', 'Literature', 'Writing', 'Comprehension']
      },
      {
        id: 3,
        name: 'Science',
        description: 'Biology, chemistry, and physics fundamentals',
        resourceCount: 52,
        topics: ['Biology', 'Chemistry', 'Physics', 'Environmental Science']
      },
      {
        id: 4,
        name: 'Social Studies',
        description: 'History, geography, and civic education',
        resourceCount: 29,
        topics: ['History', 'Geography', 'Civics', 'Economics']
      },
      {
        id: 5,
        name: 'Kiswahili',
        description: 'Swahili language and literature',
        resourceCount: 31,
        topics: ['Grammar', 'Literature', 'Composition', 'Oral Skills']
      },
      {
        id: 6,
        name: 'Religious Education',
        description: 'Religious studies and moral values',
        resourceCount: 24,
        topics: ['Christian Religious Education', 'Islamic Religious Education', 'Hindu Religious Education']
      }
    ]
  },
  'upper-secondary': {
    title: 'Upper Secondary',
    description: 'Grades 9-10: Advanced concepts and specialized learning',
    subjects: [
      {
        id: 7,
        name: 'Advanced Mathematics',
        description: 'Complex algebra, trigonometry, and calculus',
        resourceCount: 67,
        topics: ['Algebra', 'Trigonometry', 'Calculus', 'Statistics']
      },
      {
        id: 8,
        name: 'English Literature',
        description: 'Advanced reading, writing, and literary analysis',
        resourceCount: 43,
        topics: ['Poetry', 'Prose', 'Drama', 'Creative Writing']
      },
      {
        id: 9,
        name: 'Biology',
        description: 'Cell biology, genetics, and human anatomy',
        resourceCount: 58,
        topics: ['Cell Biology', 'Genetics', 'Human Anatomy', 'Ecology']
      },
      {
        id: 10,
        name: 'Chemistry',
        description: 'Organic chemistry, reactions, and laboratory work',
        resourceCount: 49,
        topics: ['Organic Chemistry', 'Inorganic Chemistry', 'Physical Chemistry', 'Analytical Chemistry']
      },
      {
        id: 11,
        name: 'Physics',
        description: 'Mechanics, electricity, and modern physics',
        resourceCount: 55,
        topics: ['Mechanics', 'Electricity', 'Waves', 'Modern Physics']
      },
      {
        id: 12,
        name: 'Geography',
        description: 'Physical and human geography concepts',
        resourceCount: 41,
        topics: ['Physical Geography', 'Human Geography', 'Environmental Geography', 'Geographic Information Systems']
      }
    ]
  },
  'junior-secondary': {
    title: 'Junior Secondary',
    description: 'Grades 7-9: Comprehensive secondary education foundation',
    subjects: [
      {
        id: 13,
        name: 'Integrated Science',
        description: 'Combined approach to scientific concepts',
        resourceCount: 63,
        topics: ['Biology', 'Chemistry', 'Physics', 'Environmental Science']
      },
      {
        id: 14,
        name: 'Business Studies',
        description: 'Introduction to business and entrepreneurship',
        resourceCount: 35,
        topics: ['Business Management', 'Accounting', 'Marketing', 'Entrepreneurship']
      },
      {
        id: 15,
        name: 'Computer Studies',
        description: 'Information technology and programming basics',
        resourceCount: 47,
        topics: ['Programming', 'Web Development', 'Database Management', 'Computer Hardware']
      },
      {
        id: 16,
        name: 'Agriculture',
        description: 'Agricultural science and farming practices',
        resourceCount: 39,
        topics: ['Crop Production', 'Animal Husbandry', 'Soil Science', 'Agricultural Economics']
      },
      {
        id: 17,
        name: 'Home Science',
        description: 'Home economics and life skills',
        resourceCount: 28,
        topics: ['Food and Nutrition', 'Clothing and Textiles', 'Home Management', 'Child Development']
      },
      {
        id: 18,
        name: 'Music',
        description: 'Musical theory, instruments, and performance',
        resourceCount: 32,
        topics: ['Music Theory', 'Instrumental Music', 'Vocal Music', 'Music History']
      }
    ]
  },
  'high-school': {
    title: 'High School',
    description: 'Grades 11-12: Specialized subjects and exam preparation',
    subjects: [
      {
        id: 19,
        name: 'Advanced Biology',
        description: 'Specialized biological concepts and research',
        resourceCount: 72,
        topics: ['Molecular Biology', 'Ecology', 'Evolution', 'Biotechnology']
      },
      {
        id: 20,
        name: 'Advanced Chemistry',
        description: 'Complex chemical reactions and analysis',
        resourceCount: 65,
        topics: ['Organic Chemistry', 'Physical Chemistry', 'Analytical Chemistry', 'Biochemistry']
      },
      {
        id: 21,
        name: 'Advanced Physics',
        description: 'Advanced physics concepts and applications',
        resourceCount: 69,
        topics: ['Mechanics', 'Electromagnetism', 'Thermodynamics', 'Quantum Physics']
      },
      {
        id: 22,
        name: 'Economics',
        description: 'Economic theory and practical applications',
        resourceCount: 51,
        topics: ['Microeconomics', 'Macroeconomics', 'International Economics', 'Development Economics']
      },
      {
        id: 23,
        name: 'History',
        description: 'World history and historical analysis',
        resourceCount: 44,
        topics: ['World History', 'African History', 'Modern History', 'Historical Research']
      },
      {
        id: 24,
        name: 'Literature',
        description: 'Advanced literary analysis and creative writing',
        resourceCount: 38,
        topics: ['Poetry Analysis', 'Novel Studies', 'Drama', 'Creative Writing']
      }
    ]
  }
}

// Methods
const selectCategory = (category: string) => {
  selectedCategory.value = category
  // Navigate to resources page with category filter
  router.push({
    name: 'resources',
    query: {
      category: category
    }
  })
}

const selectSubject = (subject: { name: string }) => {
  // Navigate to subject resources page
  router.push({
    name: 'resources',
    query: {
      subject: subject.name,
      category: selectedCategory.value
    }
  })
}

const getCategoryTitle = (category: string) => {
  return categories[category as keyof typeof categories]?.title || ''
}

const getCategoryDescription = (category: string) => {
  return categories[category as keyof typeof categories]?.description || ''
}

const getSubjects = (category: string) => {
  return categories[category as keyof typeof categories]?.subjects || []
}

const getSubjectCount = (category: string) => {
  return categories[category as keyof typeof categories]?.subjects.length || 0
}

const getSubjectColor = (subjectName: string) => {
  const colors = [
    'bg-blue-500',
    'bg-green-500',
    'bg-purple-500',
    'bg-orange-500',
    'bg-red-500',
    'bg-indigo-500',
    'bg-pink-500',
    'bg-teal-500'
  ]
  const index = subjectName.length % colors.length
  return colors[index]
}
</script>

<style scoped>
.category-card {
  border: 1px solid #e5e7eb;
}

.category-card:hover {
  border-color: #3b82f6;
}

.subject-card {
  transition: all 0.3s ease;
}

.subject-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}
</style>
