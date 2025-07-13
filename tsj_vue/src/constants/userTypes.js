// 用户类型常量
export const USER_TYPES = {
  ADMIN: 1,        // 管理员
  TEACHING_POINT: 2, // 教学点
  SERVICE_CENTER: 3   // 服务中心
}

// 用户类型显示文本
export const USER_TYPE_LABELS = {
  [USER_TYPES.ADMIN]: '管理员',
  [USER_TYPES.TEACHING_POINT]: '教学点',
  [USER_TYPES.SERVICE_CENTER]: '服务中心'
}

// 用户类型标签颜色
export const USER_TYPE_COLORS = {
  [USER_TYPES.ADMIN]: 'danger',
  [USER_TYPES.TEACHING_POINT]: 'warning',
  [USER_TYPES.SERVICE_CENTER]: 'success'
}

// 获取用户类型显示文本
export function getUserTypeLabel(userType) {
  return USER_TYPE_LABELS[userType] || '未知'
}

// 获取用户类型标签颜色
export function getUserTypeColor(userType) {
  return USER_TYPE_COLORS[userType] || 'info'
} 