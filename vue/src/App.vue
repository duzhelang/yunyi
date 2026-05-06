<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script setup>
import { useGlobalSound } from '@/composables/useGlobalSound'

useGlobalSound()
</script>
