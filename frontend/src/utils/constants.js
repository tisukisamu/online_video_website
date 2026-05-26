export const DEFAULT_AVATAR = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

export const resolveAvatarUrl = (avatar) => {
  if (!avatar) return ''
  if (avatar.startsWith('http://') || avatar.startsWith('https://') || avatar.startsWith('data:') || avatar.startsWith('blob:')) {
    return avatar
  }
  if (avatar.startsWith('/api/')) return avatar
  if (avatar.startsWith('/uploads/')) return `/api${avatar}`
  if (avatar.startsWith('uploads/')) return `/api/${avatar}`
  if (avatar.startsWith('/')) return `/api${avatar}`
  return `/api/${avatar}`
}
