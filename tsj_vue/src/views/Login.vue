<template>
  <div class="login-container">
    <el-card class="login-card">
      <div class="login-title">用户登录</div>
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" label-width="80px" hide-required-asterisk>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" autocomplete="off" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" style="width: 100%">登录</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="registerDialog = true" style="width: 100%">注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 注册弹窗 -->
    <el-dialog v-model="registerDialog" title="用户注册" width="400px" @close="resetRegisterForm">
      <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" label-width="90px" hide-required-asterisk>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" autocomplete="off" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="registerForm.realName" autocomplete="off" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" autocomplete="off" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" autocomplete="off" />
        </el-form-item>
        <el-form-item label="头像" prop="avatar">
          <el-upload
            class="avatar-uploader"
            action="#"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleRegisterAvatarChange">
            <img v-if="registerForm.avatar" :src="registerForm.avatar" class="avatar-preview" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="registerForm.userType" placeholder="请选择用户类型">
            <el-option label="教育局" :value="2" />
            <el-option label="普通用户" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeRegisterDialog">取消</el-button>
        <el-button type="primary" :loading="registerLoading" @click="handleRegister">确定</el-button>
      </template>
    </el-dialog>

    <!-- 管理员用户表格 -->
    <el-card v-if="isAdmin" class="user-table-card">
      <div class="user-table-header">
        <div class="user-table-title">用户信息管理 <span class="user-count">(共 {{ userList.length }} 名用户)</span></div>
        <el-button type="primary" @click="fetchUsers" size="small">刷新</el-button>
      </div>
      <el-scrollbar>
        <el-table :data="userList" style="width: 100%; margin-top: 20px;">
          <el-table-column prop="id" label="编号" width="60" align="center" />
          <el-table-column prop="username" label="用户名" width="100" align="center" />
          <el-table-column prop="password" label="密码" width="100" align="center" />
          <el-table-column prop="realName" label="真实姓名" width="100" align="center" />
          <el-table-column prop="phone" label="手机号" width="120" align="center" />
          <el-table-column prop="email" label="邮箱" width="160" align="center" />
          <el-table-column prop="avatar" label="头像" width="80" align="center">
            <template #default="scope">
              <el-avatar :src="scope.row.avatar" :size="40"></el-avatar>
            </template>
          </el-table-column>
          <el-table-column prop="onlineStatus" label="状态" width="80" align="center">
            <template #default="scope">
              <el-tag
                :type="scope.row.onlineStatus === 1 ? 'success' : 'info'"
                size="small"
                @click="toggleUserStatus(scope.row)"
                style="cursor: pointer;"
              >
                {{ scope.row.onlineStatus === 1 ? '在线' : '离线' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="userType" label="用户类型" width="100" align="center">
            <template #default="scope">
              <el-tag v-if="scope.row.userType === 1" type="danger">管理员</el-tag>
              <el-tag v-else-if="scope.row.userType === 2" type="warning">教育局</el-tag>
              <el-tag v-else-if="scope.row.userType === 3" type="success">普通用户</el-tag>
              <el-tag v-else type="info">未知</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" align="center">
            <template #default="scope">
              <el-button v-if="scope.row.username !== 'admin'" type="primary" size="small" @click="editUser(scope.row)">编辑</el-button>
              <el-button v-if="scope.row.username !== 'admin'" type="danger" size="small" @click="deleteUser(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-scrollbar>
    </el-card>

    <!-- 编辑用户弹窗 -->
    <el-dialog v-model="editDialog" title="编辑用户" width="400px">
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="90px" hide-required-asterisk>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editForm.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="editForm.password" type="password" autocomplete="off" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="editForm.realName" autocomplete="off" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" autocomplete="off" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" autocomplete="off" />
        </el-form-item>
        <el-form-item label="头像" prop="avatar">
          <el-upload
            class="avatar-uploader"
            action="#"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleAvatarChange">
            <img v-if="editForm.avatar" :src="editForm.avatar" class="avatar-preview" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="editForm.userType" placeholder="请选择用户类型">
            <el-option label="教育局" :value="2" />
            <el-option label="普通用户" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialog = false">取消</el-button>
        <el-button type="primary" @click="handleEditSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { Plus } from '@element-plus/icons-vue'

export default {
  data() {
    return {
      loginForm: { username: '', password: '' },
      loginRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      registerDialog: false,
      registerForm: { 
        username: '', 
        password: '', 
        realName: '', 
        phone: '', 
        userType: null,
        email: '',
        avatar: '/src/assets/default-avatar.jpg',
        onlineStatus: 0
      },
      registerLoading: false,
      registerRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }]
      },
      userList: [],
      isAdmin: false,
      editDialog: false,
      editForm: { 
        id: null, 
        username: '', 
        password: '', 
        realName: '', 
        phone: '', 
        userType: null,
        email: '',
        avatar: '/src/assets/default-avatar.jpg',
        onlineStatus: 0
      },
      editRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { validator: async (rule, value, callback) => {
              if (!value) return callback();
              try {
                const res = await axios.get('http://localhost:8080/api/user/getUser');
                const users = Array.isArray(res.data) ? res.data : [];
                // 排除当前编辑的用户id
                const exist = users.some(u => u.username === value && u.id !== this.editForm.id);
                if (exist) {
                  callback(new Error('用户名已存在'));
                } else {
                  callback();
                }
              } catch {
                callback();
              }
            }, trigger: 'blur' }
        ],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }]
      }
    }
  },
  methods: {
    handleAvatarChange(file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        this.editForm.avatar = e.target.result;
      };
      reader.readAsDataURL(file.raw);
    },
    handleRegisterAvatarChange(file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        this.registerForm.avatar = e.target.result;
      };
      reader.readAsDataURL(file.raw);
    },
    handleLogin() {
      this.$refs.loginFormRef.validate(valid => {
        if (!valid) return
        
        axios.post('http://localhost:8080/api/user/login', {
          username: this.loginForm.username,
          password: this.loginForm.password
        }).then(res => {
          if (res.data.success) {
            const user = res.data.user;
            ElMessage.success('登录成功');
            
            if (user.username === 'admin') {
              this.isAdmin = true;
              this.fetchUsers();
            } else {
              if (user.userType === 3) {
                this.$router.push({ name: 'Student', query: { username: user.username, userId: user.id } });
              } else if (user.userType === 2) {
                this.$router.push({ name: 'Teach', query: { username: user.username, userId: user.id } });
              } else {
                this.$router.push({ name: 'Student', query: { username: user.username, userId: user.id } });
              }
            }
          } else {
            ElMessage.error(res.data.message || '用户名或密码错误');
          }
        }).catch(error => {
          console.error('登录请求失败:', error);
          ElMessage.error('登录失败，请稍后重试');
        });
      })
    },
    handleRegister() {
      this.registerLoading = true;
      if (!this.$refs.registerFormRef) {
        this.registerLoading = false;
        return;
      }
      this.$refs.registerFormRef.validate(valid => {
        if (!valid) {
          this.registerLoading = false;
          return;
        }
        axios.get('http://localhost:8080/api/user/getUser').then(res => {
          const users = res.data
          if (users.some(u => u.username === this.registerForm.username)) {
            ElMessage.error('用户名已存在')
            this.registerLoading = false;
            return
          }
          axios.post('http://localhost:8080/api/user/insertUser', {
            ...this.registerForm
          }).then(() => {
            ElMessage.success('注册成功，请登录')
            this.registerDialog = false
            this.resetRegisterForm()
          }).finally(() => {
            this.registerLoading = false;
          })
        }).catch(() => {
          this.registerLoading = false;
        })
      })
    },
    closeRegisterDialog() {
      this.registerDialog = false;
      this.resetRegisterForm();
    },
    resetRegisterForm() {
      if (this.$refs.registerFormRef) {
        this.$refs.registerFormRef.resetFields();
      }
      this.registerForm = { username: '', password: '', realName: '', phone: '', userType: null, email: '', avatar: '/src/assets/default-avatar.jpg', onlineStatus: 0 };
      this.registerLoading = false;
    },
    editUser(row) {
      this.editForm = { ...row }
      this.editDialog = true
    },
    handleEditSave() {
      this.$refs.editFormRef.validate(valid => {
        if (!valid) return
        axios.post('http://localhost:8080/api/user/updateUser', this.editForm).then(() => {
          ElMessage.success('修改成功')
          this.editDialog = false
          this.fetchUsers()
        })
      })
    },
    deleteUser(row) {
      this.$confirm('确定要删除该用户吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        axios.get(`http://localhost:8080/api/user/deleteUser/${row.id}`).then(() => {
          ElMessage.success('删除成功')
          this.fetchUsers()
        })
      })
    },
    toggleUserStatus(user) {
      const newStatus = user.onlineStatus === 1 ? 0 : 1;
      this.$confirm(`确定要将用户 "${user.username}" 的状态改为 ${newStatus === 1 ? '在线' : '离线'} 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        axios.get(`http://localhost:8080/api/user/updateOnlineStatus/${user.id}/${newStatus}`).then(() => {
          ElMessage.success(`用户 "${user.username}" 已改为 ${newStatus === 1 ? '在线' : '离线'}`);
          this.fetchUsers();
        }).catch(error => {
          console.error('更新用户状态失败:', error);
          ElMessage.error('更新用户状态失败，请稍后重试');
        });
      }).catch(() => {
        // 用户取消操作
      });
    },
    fetchUsers() {
      axios.get('http://localhost:8080/api/user/getUser').then(res => {
        this.userList = Array.isArray(res.data) ? res.data.map(u => ({
          ...u,
          userType: u.userType ?? u.user_type,
          realName: u.realName ?? u.real_name,
          onlineStatus: u.onlineStatus ?? u.online_status,
          avatar: u.avatar ?? u.avatar_url,
          email: u.email ?? ''
        })) : [];
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}
.login-card {
  max-width: 400px;
  width: 100%;
  margin: 0 auto;
  padding: 40px 30px 20px 30px;
  font-size: 20px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.12);
}
.login-title {
  font-size: 28px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 30px;
}
.user-table-card {
  max-width: 1200px;
  width: 100%;
  margin: 40px auto 0 auto;
  font-size: 18px;
}
.user-table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.user-table-title {
  font-size: 22px;
  font-weight: bold;
}
.user-count {
  font-size: 16px;
  color: #606266;
  margin-left: 10px;
}
.avatar-uploader {
  width: 100%;
  text-align: center;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
}
.avatar-preview {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #d9d9d9;
}
</style>
