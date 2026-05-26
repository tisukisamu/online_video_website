# 前端 UI 设计规范 (Frontend UI Design Specification)

## 1. 设计概述 (Overview)
本项目采用 **Douyin (抖音) 风格** 的深色主题设计，旨在提供沉浸式的短视频浏览体验。整体界面以黑色为基调，辅以高饱和度的品牌色（红/青）作为点缀，强调内容的视觉冲击力。

## 2. 色彩规范 (Color Palette)

### 2.1 品牌色 (Brand Colors)
- **Primary Red (主色红)**: `#fe2c55`
  - 用于：主要按钮、选中状态、点赞图标、强调文字。
- **Secondary Cyan (辅色青)**: `#25f4ee`
  - 用于：装饰性元素、加载动画、次要强调。
- **Gradient (渐变)**: `#25f4ee` to `#fe2c55`
  - 用于：特殊按钮背景、Logo 效果、加载条。

### 2.2 背景色 (Backgrounds)
- **Global Background**: `#000000` (纯黑)
  - 用于：全屏视频背景、主容器。
- **Card/Panel Background**: `#161823`
  - 用于：侧边栏、弹窗、卡片容器、表格背景。
- **Overlay/Modal Mask**: `rgba(0, 0, 0, 0.6)`
  - 用于：模态框遮罩。

### 2.3 文字颜色 (Typography Colors)
- **Primary Text**: `#ffffff` (100% White)
  - 用于：标题、主要内容。
- **Secondary Text**: `rgba(255, 255, 255, 0.7)` or `#b0b0b0`
  - 用于：次要信息、说明文字。
- **Disabled/Placeholder**: `rgba(255, 255, 255, 0.3)` or `#555555`
  - 用于：禁用状态、占位符。

## 3. 布局与导航 (Layout & Navigation)

### 3.1 移动端优先 (Mobile First)
- **顶部导航 (Top Nav)**:
  - **透明模式**: `bg-gradient-to-b from-black/60 to-transparent` (视频流页面)
  - **实色模式**: `bg-black/90 backdrop-blur-md` (非视频页面)
  - 高度: `50px`
- **底部导航 (Bottom Nav)**:
  - 背景: `bg-black`
  - 高度: `50px` + safe-area-inset
  - 包含核心功能入口：首页、发现、上传(+)、收藏、我。

### 3.2 响应式设计 (Responsive Design)
- **Grid Layout**: 使用 CSS Grid (`grid-cols-1 md:grid-cols-2 lg:grid-cols-4`) 适配不同屏幕尺寸。
- **Container**: `max-w-7xl mx-auto` 限制大屏内容宽度。

## 4. 组件规范 (Component Specs)

### 4.1 按钮 (Buttons)
- **Primary Button**:
  - Background: `#fe2c55`
  - Text: `#ffffff`
  - Hover: `#e62548`
  - Border-radius: `4px` (常规) or `9999px` (胶囊形)
- **Secondary/Ghost Button**:
  - Background: `rgba(255, 255, 255, 0.1)`
  - Text: `#ffffff`
  - Hover: `rgba(255, 255, 255, 0.2)`

### 4.2 卡片 (Cards)
- Background: `#161823`
- Border: `1px solid rgba(255, 255, 255, 0.05)`
- Border-radius: `12px` (rounded-xl)
- Hover Effect: `transform: scale(1.02)` transition

### 4.3 表格 (Tables - Dark Mode)
- **Header**: `bg-[#1f212e]`, text `#888`
- **Row**: `bg-transparent`, text `#fff`
- **Hover Row**: `bg-[#2a2c35]`
- **Border**: `border-bottom: 1px solid #222`

### 4.4 模态框 (Modals)
- 基于 Ant Design Vue 定制
- Background: `#1f212e`
- Title Color: `#ffffff`
- Close Icon: `#ffffff`
- Input Background: `#161823`

## 5. 动效 (Animations)

- **路由切换 (Route Transition)**:
  - Name: `fade`
  - Duration: `0.2s`
  - Mode: `out-in`
- **交互反馈**:
  - Hover: `transition-colors duration-200`
  - Click: `active:scale-95`

## 6. 图标 (Icons)
- 使用 **Ant Design Icons Vue** (`@ant-design/icons-vue`)
- 风格: 实心 (`Filled`) 用于选中状态，线框 (`Outlined`) 用于默认状态。
- 尺寸: `16px` - `24px`

## 7. 代码实现示例 (Code Examples)

### Tailwind CSS Utility Classes
```html
<!-- Primary Button -->
<button class="bg-[#fe2c55] text-white px-4 py-2 rounded hover:bg-[#e62548] transition-colors">
  关注
</button>

<!-- Dark Card -->
<div class="bg-[#161823] border border-white/5 rounded-xl p-4">
  <h3 class="text-white font-bold">标题</h3>
  <p class="text-gray-400">内容描述...</p>
</div>
```
