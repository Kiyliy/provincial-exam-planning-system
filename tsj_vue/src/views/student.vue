<template>
  <el-container class="layout-container-demo" style="height: 100vh">
    <el-aside width="200px">
      <el-scrollbar>
        <el-menu :default-openeds="['1', '2']">
          <el-sub-menu index="1">
            <template #title>
              <span class="main-menu-title">
                <el-icon><Document /></el-icon>计划管理
              </span>
            </template>
            <el-menu-item index="1-1">专业计划</el-menu-item>
            <el-menu-item index="1-2" @click="handleMenuClick('majorInfo')">专业申报</el-menu-item>
            <el-menu-item index="1-4" @click="handleMenuClick('testInfo')">考试计划</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="2">
            <template #title>
              <span class="main-menu-title">
                <el-icon><School /></el-icon>助学管理
              </span>
            </template>
            <el-menu-item index="2-1">校外教学</el-menu-item>
            <el-menu-item index="2-2">服务中心</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container>
      <el-header style="text-align: right; font-size: 12px">
        <div class="toolbar">
          <span>欢迎您，{{ realName }}！</span>
          <el-dropdown>
            <el-icon style="margin-left: 10px; margin-top: 1px">
              <Setting />
            </el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="editProfile">修改个人信息</el-dropdown-item>
                <el-dropdown-item @click="goLogin">返回登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main>
        <el-scrollbar>
          <div class="welcome-content" v-if="!showMajorInfo && !showTestInfo">
            <h1>欢迎来到自学考试计划管理系统</h1>
            <p>请从左侧菜单选择您要使用的功能模块</p>
          </div>
          <div class="major-info-header" v-if="showMajorInfo">
            <h2>专业申报管理</h2>
            <div class="status-summary">
              <el-tag type="info" size="large" effect="plain">未申报: {{ getStatusCount('未申报') }}</el-tag>
              <el-tag type="warning" size="large" effect="plain">已申报: {{ getStatusCount('已申报') }}</el-tag>
              <el-tag type="success" size="large" effect="plain">已完成: {{ getStatusCount('已完成') }}</el-tag>
            </div>
          </div>
          <el-table v-if="showMajorInfo" :data="majorInfoList" style="margin: 30px 0" :row-class-name="getRowClassName">
            <el-table-column prop="id" label="学科ID" align="center"/>
            <el-table-column prop="name" label="学科名称" align="center"/>
            <el-table-column prop="major" label="开设专业" align="center"/>
            <el-table-column prop="year" label="开设学年" align="center"/>
            <el-table-column prop="term" label="开设学期" align="center"/>
            <el-table-column prop="credit" label="学分" align="center"/>
            <el-table-column prop="state" label="状态" align="center">
              <template #default="scope">
                <el-tag
                  v-if="scope.row.state === '未申报'"
                  type="info"
                  size="large"
                  effect="plain"
                  class="status-tag"
                >未申报</el-tag>
                <el-tag
                  v-else-if="scope.row.state === '已申报'"
                  type="warning"
                  size="large"
                  effect="plain"
                  class="status-tag"
                >已申报</el-tag>
                <el-tag
                  v-else-if="scope.row.state === '已完成'"
                  type="success"
                  size="large"
                  effect="plain"
                  class="status-tag"
                >已完成</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center">
              <template #default="scope">
                <el-button
                  v-if="scope.row.state === '未申报'"
                  type="primary"
                  size="small"
                  class="big-action-btn"
                  @click="declareMajor(scope.row)"
                  :disabled="scope.row.disabled"
                >申报</el-button>
                <el-button
                  v-else-if="scope.row.state === '已申报'"
                  type="danger"
                  size="small"
                  class="big-action-btn"
                  @click="cancelMajor(scope.row)"
                >取消</el-button>
                <el-button
                  v-else-if="scope.row.state === '已完成'"
                  type="info"
                  size="small"
                  class="big-action-btn"
                  @click="viewCompleted(scope.row)"
                >查看</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="test-info-header" v-if="showTestInfo">
            <h2>考试计划</h2>
            <div class="test-status-summary">
              <el-tag type="info" size="large" effect="plain">未考试: {{ getTestStatusCount('未考试') }}</el-tag>
              <el-tag type="success" size="large" effect="plain">已考试: {{ getTestStatusCount('已考试') }}</el-tag>
              <el-tag type="info" size="large" effect="plain">未安排: {{ getPlanStatusCount('未安排') }}</el-tag>
              <el-tag type="warning" size="large" effect="plain">已安排: {{ getPlanStatusCount('已安排') }}</el-tag>
            </div>
          </div>
          <el-table v-if="showTestInfo" :data="testInfoList" style="margin: 30px 0" :row-class-name="getTestRowClassName">
            <el-table-column prop="majorId" label="学科ID" align="center" />
            <el-table-column prop="name" label="考试学科" width="300" align="center" />
            <el-table-column prop="plan" label="考试计划" align="center">
              <template #default="scope">
                <el-tag
                  v-if="scope.row.plan === '未安排'"
                  type="info"
                  size="large"
                  effect="plain"
                  class="test-status-tag"
                >未安排</el-tag>
                <el-tag
                  v-else-if="scope.row.plan === '已安排'"
                  type="warning"
                  size="large"
                  effect="plain"
                  class="test-status-tag"
                >已安排</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="state" label="考试状态" align="center">
              <template #default="scope">
                <el-tag
                  v-if="scope.row.state === '未考试'"
                  type="info"
                  size="large"
                  effect="plain"
                  class="test-status-tag"
                >未考试</el-tag>
                <el-tag
                  v-else-if="scope.row.state === '已考试'"
                  type="success"
                  size="large"
                  effect="plain"
                  class="test-status-tag"
                >已考试</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="place" label="考试地点" align="center" />
            <el-table-column prop="start" label="考试日期" width = 300 align="center" />
            <el-table-column prop="when" label="考试时间" width = 200 align="center">
              <template #default="scope">
                <el-tag v-if="scope.row.when === '1'" type="danger" class="test-status-tag">第一堂</el-tag>
                <el-tag v-else-if="scope.row.when === '2'" type="danger" class="test-status-tag">第二堂</el-tag>
                <el-tag v-else-if="scope.row.when === '3'" type="danger" class="test-status-tag">第三堂</el-tag>
                <span v-else>{{ scope.row.when }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-scrollbar>
      </el-main>
    </el-container>

    <!-- 修改个人信息弹窗 -->
    <el-dialog v-model="editDialog" title="修改个人信息" width="400px">
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
  </el-container>
</template>

<script>
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { Document, School, Setting } from '@element-plus/icons-vue'

export default {
  name: 'Student',
  components: {
    Document,
    School,
    Setting
  },
  data() {
    return {
      username: this.$route.query.username || '',
      userId: null,
      realName: '',
      editDialog: false,
      editForm: { id: null, username: '', password: '', realName: '', phone: '', userType: null },
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
      },
      majorInfoList: [],
      showMajorInfo: false,
      testInfoList: [],
      showTestInfo: false,
    }
  },
  methods: {
    // 返回登录页面
    goLogin() {
      this.$router.push({ name: 'Login' })
    },
    // 修改个人信息
    editProfile() {
      // 获取当前用户信息
      axios.get('http://localhost:8080/api/user/getUser').then(res => {
        const users = Array.isArray(res.data) ? res.data.map(u => ({
          ...u,
          userType: u.userType ?? u.user_type,
          realName: u.realName ?? u.real_name
        })) : [];
        const currentUser = users.find(u => u.username === this.username);
        if (currentUser) {
          this.editForm = { ...currentUser };
          this.realName = currentUser.realName;
          this.editDialog = true;
        } else {
          ElMessage.error('获取用户信息失败');
        }
      });
    },
    // 保存修改
    handleEditSave() {
      this.$refs.editFormRef.validate(valid => {
        if (!valid) return;
        axios.post('http://localhost:8080/api/user/updateUser', this.editForm).then(() => {
          ElMessage.success('修改成功');
          this.editDialog = false;
          // 更新当前用户名显示
          this.username = this.editForm.username;
          this.realName = this.editForm.realName;
          setTimeout(() => {
            this.$router.push({ name: 'Login' });
          }, 800);
        }).catch(() => {
          ElMessage.error('修改失败');
        });
      });
    },
    async handleMenuClick(menu) {
      this.showMajorInfo = false;
      this.showTestInfo = false;
      if (menu === 'majorInfo') {
        let majorApi = '';
        if (this.userId === 2) {
          majorApi = 'http://localhost:8080/api/majorInfo2/getMajorInfo2';
        } else if (this.userId === 3) {
          majorApi = 'http://localhost:8080/api/majorInfo3/getMajorInfo3';
        } else {
          this.majorInfoList = [];
          this.showMajorInfo = true;
          ElMessage.info('暂无信息');
          return;
        }
        // 获取专业申报信息
        const majorRes = await axios.get(majorApi);
        let majors = Array.isArray(majorRes.data) ? majorRes.data : [];
        // 获取考试计划
        const testRes = await axios.get('http://localhost:8080/api/testInfo/getTestInfoWithMajors');
        const tests = Array.isArray(testRes.data) ? testRes.data : [];
        // 处理每个学科的申报按钮禁用和状态
        majors = majors.map(m => {
          const test = tests.find(t => t.majorId === m.id);
          if (test && test.state === '已考试') {
            if (m.state === '未申报') {
              return { ...m, disabled: true };
            } else if (m.state === '已申报') {
              // 自动更新为已完成
              this.updateMajorStatusToCompleted(m, majorApi);
              return { ...m, state: '已完成', disabled: true };
            }
          } else if (test && test.state === '未考试' && m.state === '已完成') {
            // 容错：如果状态为已完成但考试未考试，自动回退为已申报
            this.updateMajorStatusToDeclared(m, majorApi);
            return { ...m, state: '已申报', disabled: false };
          }
          return { ...m, disabled: false };
        });
        this.majorInfoList = majors;
        this.showMajorInfo = true;
      } else if (menu === 'testInfo') {
        let majorApi = '';
        if (this.userId === 2) {
          majorApi = 'http://localhost:8080/api/majorInfo2/getMajorInfo2';
        } else if (this.userId === 3) {
          majorApi = 'http://localhost:8080/api/majorInfo3/getMajorInfo3';
        } else {
          this.testInfoList = [];
          this.showTestInfo = true;
          ElMessage.info('暂无信息');
          return;
        }
        axios.get(majorApi)
          .then(res => {
            const majors = Array.isArray(res.data) ? res.data : [];
            const filteredMajors = majors.filter(m => m.state === '已申报' || m.state === '已完成');
            const majorIds = filteredMajors.map(m => m.id);
            if (majorIds.length === 0) {
              this.testInfoList = [];
              this.showTestInfo = true;
              return;
            }
            axios.get('http://localhost:8080/api/testInfo/getTestInfoWithMajors')
              .then(res2 => {
                const allTests = Array.isArray(res2.data) ? res2.data : [];
                const majorIdsNum = majorIds.map(id => Number(id));
                console.log('userId', this.userId);
                console.log('majorIds', majorIdsNum);
                console.log('allTests', allTests);
                this.testInfoList = allTests.filter(t => majorIdsNum.includes(Number(t.majorId)));
                this.showTestInfo = true;
              })
              .catch(() => {
                ElMessage.error('获取考试计划失败');
              });
          })
          .catch(() => {
            ElMessage.error('获取专业申报信息失败');
          });
      }
    },
    async updateMajorStatusToCompleted(row, majorApi) {
      const updated = { ...row, state: '已完成' };
      await axios.post(majorApi.replace('get', 'update'), updated);
    },
    async updateMajorStatusToDeclared(row, majorApi) {
      const updated = { ...row, state: '已申报' };
      await axios.post(majorApi.replace('get', 'update'), updated);
    },
    async declareMajor(row) {
      // 根据userId选择接口
      const updated = { ...row, state: '已申报' };
      let url = '';
      if (this.userId === 2) {
        url = 'http://localhost:8080/api/majorInfo2/updateMajorInfo2';
      } else if (this.userId === 3) {
        url = 'http://localhost:8080/api/majorInfo3/updateMajorInfo3';
      } else {
        ElMessage.error('无权限申报');
        return;
      }
      await axios.post(url, updated);
      row.state = '已申报';
      // 重新处理禁用和状态
      this.handleMenuClick('majorInfo');
      ElMessage.success('申报成功');
    },
    cancelMajor(row) {
      // 根据userId选择接口
      const updated = { ...row, state: '未申报' };
      let url = '';
      if (this.userId === 2) {
        url = 'http://localhost:8080/api/majorInfo2/updateMajorInfo2';
      } else if (this.userId === 3) {
        url = 'http://localhost:8080/api/majorInfo3/updateMajorInfo3';
      } else {
        ElMessage.error('无权限取消');
        return;
      }
      axios.post(url, updated)
        .then(() => {
          row.state = '未申报';
          ElMessage.success('已取消申报');
        })
        .catch(() => {
          ElMessage.error('取消失败');
        });
    },
    getStatusCount(status) {
      return this.majorInfoList.filter(item => item.state === status).length;
    },
    getRowClassName({ row }) {
      if (row.state === '已完成') return 'completed-row';
      if (row.state === '已申报') return 'declared-row';
      return 'undeclared-row';
    },
    getTestStatusCount(status) {
      return this.testInfoList.filter(item => item.state === status).length;
    },
    getPlanStatusCount(plan) {
      return this.testInfoList.filter(item => item.plan === plan).length;
    },
    getTestRowClassName({ row }) {
      if (row.state === '已考试') return 'test-completed-row';
      if (row.plan === '已安排') return 'test-arranged-row';
      return 'test-unarranged-row';
    }
  },
  mounted() {
    // 自动获取当前用户真实姓名和id
    axios.get('http://localhost:8080/api/user/getUser').then(res => {
      const users = Array.isArray(res.data) ? res.data.map(u => ({
        ...u,
        userType: u.userType ?? u.user_type,
        realName: u.realName ?? u.real_name
      })) : [];
      const currentUser = users.find(u => u.username === this.username);
      if (currentUser) {
        this.userId = currentUser.id;
        this.realName = currentUser.realName;
      }
    });
  }
}
</script>

<style scoped>
.layout-container-demo .el-header {
  position: relative;
  background-color: var(--el-color-primary-light-7);
  color: var(--el-text-color-primary);
}
.layout-container-demo .el-aside {
  color: var(--el-text-color-primary);
  background: var(--el-color-primary-light-8);
}
.layout-container-demo .el-menu {
  border-right: none;
}
.layout-container-demo .el-main {
  padding: 0;
}
.layout-container-demo .toolbar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  right: 20px;
  font-size: 1.33rem;
}
.layout-container-demo .toolbar .el-icon {
  font-size: 1.66rem;
  margin-right: 10px;
  font-weight: bold;
  filter: drop-shadow(0 0 1px #333);
}
.layout-container-demo .toolbar span {
  font-size: 1.33rem;
  font-weight: bold;
}
/* 设置下拉菜单项 */
::v-deep .el-dropdown-menu__item {
  font-size: 1.5rem !important;
  padding: 12px 20px;
}
/* 主菜单 */
.layout-container-demo .el-menu {
  font-size: 2rem;
}
::v-deep .main-menu-title {
  font-size: 1.5rem !important;
  font-weight: bold !important;
  line-height: 2rem !important;
  display: flex;
  align-items: center;
}
::v-deep .main-menu-title .el-icon {
  font-size: 1.5rem !important;
  margin-right: 8px;
}
.layout-container-demo .el-menu-item {
  font-size: 1.33rem;
}
.welcome-content {
  padding: 40px;
  text-align: center;
}
.welcome-content h1 {
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 20px;
}
.welcome-content p {
  font-size: 1.2rem;
  color: #666;
}
::v-deep .el-table {
  font-size: 1.5rem !important;
}
::v-deep .el-table th, 
::v-deep .el-table td {
  font-size: 1.5rem !important;
}
::v-deep .big-action-btn {
  font-size: 1.5rem !important;
  height: 2.25em !important;
  min-width: 3.75em !important;
  padding: 0 1.5em !important;
}

.major-info-header {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 20px;
}

.major-info-header h2 {
  margin: 0 0 15px 0;
  font-size: 2rem;
  text-align: center;
}

.status-summary {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.status-summary .el-tag {
  font-size: 1.2rem;
  padding: 8px 16px;
}

::v-deep .status-tag {
  font-size: 1.8rem !important;
  font-weight: bold !important;
  padding: 15px 20px !important;
  border-width: 2px !important;
  border-radius: 8px !important;
  min-height: 48px !important;
  line-height: 1.8 !important;
  display: inline-flex !important;
  align-items: center !important;
}

::v-deep .status-tag.el-tag--info {
  border-color: #909399 !important;
  color: #606266 !important;
}

::v-deep .status-tag.el-tag--warning {
  border-color: #e6a23c !important;
  color: #e6a23c !important;
}

::v-deep .status-tag.el-tag--success {
  border-color: #67c23a !important;
  color: #67c23a !important;
}

::v-deep .completed-row {
  background-color: #f0f9ff !important;
}

::v-deep .declared-row {
  background-color: #fff7ed !important;
}

::v-deep .undeclared-row {
  background-color: #fafafa !important;
}

::v-deep .el-table .el-table__row:hover {
  background-color: #e6f7ff !important;
}

::v-deep .el-table th {
  background-color: #f5f7fa !important;
  color: #606266 !important;
  font-weight: bold !important;
}

.test-info-header {
  padding: 20px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 20px;
}

.test-info-header h2 {
  margin: 0 0 15px 0;
  font-size: 2rem;
  text-align: center;
}

.test-status-summary {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.test-status-summary .el-tag {
  font-size: 1.2rem;
  padding: 8px 16px;
}

::v-deep .test-status-tag {
  font-size: 1.8rem !important;
  font-weight: bold !important;
  padding: 15px 20px !important;
  border-width: 2px !important;
  border-radius: 8px !important;
  min-height: 48px !important;
  line-height: 1.8 !important;
  display: inline-flex !important;
  align-items: center !important;
}
::v-deep .test-status-tag.el-tag--info {
  border-color: #909399 !important;
  color: #606266 !important;
}
::v-deep .test-status-tag.el-tag--success {
  border-color: #67c23a !important;
  color: #67c23a !important;
}
::v-deep .test-status-tag.el-tag--warning {
  border-color: #e6a23c !important;
  color: #e6a23c !important;
}
::v-deep .test-completed-row {
  background-color: #f0f9ff !important;
}
::v-deep .test-arranged-row {
  background-color: #fff7ed !important;
}
::v-deep .test-unarranged-row {
  background-color: #fafafa !important;
}
::v-deep .test-status-tag.el-tag--danger {
  border-color: #f56c6c !important;
  color: #f56c6c !important;
}
</style> 