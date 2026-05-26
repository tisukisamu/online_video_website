import { defineConfig, presetUno, presetAttributify, presetIcons } from 'unocss'

export default defineConfig({
  presets: [
    presetUno(),
    presetAttributify(),
    presetIcons({
      scale: 1.2,
      cdn: 'https://esm.sh/'
    })
  ],
  rules: [
    ['glass', {
      'backdrop-filter': 'blur(20px)',
      'background': 'rgba(10, 10, 15, 0.95)'
    }],
    ['glow-cyan', {
      'box-shadow': '0 0 20px rgba(0, 212, 255, 0.3)'
    }],
    ['glow-cyan-strong', {
      'box-shadow': '0 0 40px rgba(0, 212, 255, 0.5)'
    }],
    ['text-glow-cyan', {
      'text-shadow': '0 0 10px rgba(0, 212, 255, 0.5)'
    }]
  ],
  shortcuts: {
    'flex-center': 'flex items-center justify-center',
    'flex-col-center': 'flex flex-col items-center justify-center',
    'card-dark': 'bg-[#1a1a2e] border border-[rgba(0,212,255,0.1)] rounded-3',
    'card-dark-hover': 'card-dark hover:border-[rgba(0,212,255,0.4)] hover:shadow-[0_20px_40px_rgba(0,212,255,0.15)]',
    'btn-primary': 'bg-gradient-to-r from-[#00d4ff] to-[#0099cc] text-white border-none rounded-full px-6 py-2 font-500',
    'btn-primary-hover': 'btn-primary hover:from-[#00e5ff] hover:to-[#00b8d4]',
    'btn-ghost': 'bg-transparent border border-[rgba(0,212,255,0.3)] text-gray-400 rounded-full px-4 py-2',
    'btn-ghost-hover': 'btn-ghost hover:border-[#00d4ff] hover:text-[#00d4ff]',
    'input-dark': 'bg-[rgba(255,255,255,0.05)] border border-[rgba(0,212,255,0.2)] rounded-2 text-white placeholder-gray-500',
    'input-dark-focus': 'input-dark focus:border-[#00d4ff] focus:shadow-[0_0_0_2px_rgba(0,212,255,0.1)]',
    'text-primary': 'text-[#00d4ff]',
    'text-secondary': 'text-gray-400',
    'text-muted': 'text-gray-500',
    'border-cyan': 'border-[rgba(0,212,255,0.2)]',
    'bg-dark': 'bg-[#0a0a0f]',
    'bg-dark-card': 'bg-[#1a1a2e]',
    'bg-dark-elevated': 'bg-[#16213e]'
  },
  theme: {
    colors: {
      primary: '#00d4ff',
      dark: {
        bg: '#0a0a0f',
        card: '#1a1a2e',
        elevated: '#16213e',
        border: 'rgba(0, 212, 255, 0.2)'
      }
    }
  }
})
