<template>
  <el-container class="layout-container-demo" style="height: 100vh">
    <el-aside width="200px">
      <el-scrollbar>
        <el-menu :default-openeds="['1', '2', '3']">
          <el-sub-menu index="1">
            <template #title>
              <span class="main-menu-title">
                <el-icon><Document /></el-icon>计划管理
              </span>
            </template>
            <el-menu-item index="1-2" @click="handleMenuClick('majorInfo')">专业申报</el-menu-item>
            <el-menu-item index="1-4" @click="handleMenuClick('testInfo')">考试计划</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="3">
            <template #title>
              <span class="main-menu-title">
                <el-icon><Connection /></el-icon>校外拓展
              </span>
            </template>
            <el-menu-item index="3-1" @click="handleMenuClick('internship')">校外实习</el-menu-item>
            <el-menu-item index="3-2">校外竞赛</el-menu-item>
            <el-menu-item index="3-3">学术交流</el-menu-item>
          </el-sub-menu>
          <el-menu-item index="4" @click="handleMenuClick('serviceCenter')">
            <span class="main-menu-title">
              <el-icon><Service /></el-icon>服务中心
            </span>
          </el-menu-item>
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
          <div class="welcome-content" v-if="!showMajorInfo && !showTestInfo && !showServiceCenter && !showInternship">
            <h1>欢迎来到自学考试计划管理系统</h1>
            <p>请从左侧菜单选择您要使用的功能模块</p>
          </div>
          <div class="service-center-container" v-if="showServiceCenter">
            <div class="service-center-header">
              <h2>学习服务中心</h2>
            </div>
            <div class="service-grid">
              <div class="service-card" v-for="(service, index) in serviceList" :key="index" @click="handleServiceClick(service)">
                <el-icon class="service-icon">
                  <component :is="service.icon"></component>
                </el-icon>
                <div class="service-name">{{ service.name }}</div>
                <div class="service-desc">{{ service.description }}</div>
              </div>
            </div>
          </div>
          <div class="major-info-header" v-if="showMajorInfo">
            <h2>专业申报管理</h2>
            <div class="status-summary">
              <el-tag 
                type="primary" 
                size="large" 
                effect="plain"
                :class="{ 'active-filter': statusFilter === 'all' }"
                @click="handleFilterChange('all')"
                class="clickable-tag"
              >全部</el-tag>
              <el-tag 
                type="info" 
                size="large" 
                effect="plain"
                :class="{ 'active-filter': statusFilter === '未申报' }"
                @click="handleFilterChange('未申报')"
                class="clickable-tag"
              >未申报: {{ getStatusCount('未申报') }}</el-tag>
              <el-tag 
                type="warning" 
                size="large" 
                effect="plain"
                :class="{ 'active-filter': statusFilter === '已申报' }"
                @click="handleFilterChange('已申报')"
                class="clickable-tag"
              >已申报: {{ getStatusCount('已申报') }}</el-tag>
              <el-tag 
                type="success" 
                size="large" 
                effect="plain"
                :class="{ 'active-filter': statusFilter === '已完成' }"
                @click="handleFilterChange('已完成')"
                class="clickable-tag"
              >已完成: {{ getStatusCount('已完成') }}</el-tag>
            </div>
            <div class="search-box">
              <el-input
                v-model="searchQuery"
                placeholder="搜索学科ID或名称"
                prefix-icon="Search"
                clearable
                @input="handleSearch"
              ></el-input>
            </div>
          </div>
          <el-table v-if="showMajorInfo" :data="filteredMajorInfoList.length > 0 || searchQuery ? filteredMajorInfoList : majorInfoList" style="margin: 30px 0" :row-class-name="getRowClassName">
            <el-table-column prop="id" label="学科ID" align="center"/>
            <el-table-column prop="name" label="学科名称" align="center"/>
            <el-table-column prop="major" label="开设专业" align="center"/>
            <el-table-column prop="year" label="开设学年" align="center"/>
            <el-table-column prop="term" label="开设学期" align="center"/>
            <el-table-column prop="credit" label="学分" align="center"/>
            <el-table-column label="状态" align="center">
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
              <el-tag 
                type="primary" 
                size="large" 
                effect="plain"
                :class="{ 'active-filter': testStatusFilter === 'all' }"
                @click="handleTestFilterChange('all')"
                class="clickable-tag"
              >全部</el-tag>
              <el-tag 
                type="info" 
                size="large" 
                effect="plain"
                :class="{ 'active-filter': testStatusFilter === 'state:未考试' }"
                @click="handleTestFilterChange('state:未考试')"
                class="clickable-tag"
              >未考试: {{ getTestStatusCount('未考试') }}</el-tag>
              <el-tag 
                type="success" 
                size="large" 
                effect="plain"
                :class="{ 'active-filter': testStatusFilter === 'state:已考试' }"
                @click="handleTestFilterChange('state:已考试')"
                class="clickable-tag"
              >已考试: {{ getTestStatusCount('已考试') }}</el-tag>
              <el-tag 
                type="info" 
                size="large" 
                effect="plain"
                :class="{ 'active-filter': testStatusFilter === 'plan:未安排' }"
                @click="handleTestFilterChange('plan:未安排')"
                class="clickable-tag"
              >未安排: {{ getPlanStatusCount('未安排') }}</el-tag>
              <el-tag 
                type="warning" 
                size="large" 
                effect="plain"
                :class="{ 'active-filter': testStatusFilter === 'plan:已安排' }"
                @click="handleTestFilterChange('plan:已安排')"
                class="clickable-tag"
              >已安排: {{ getPlanStatusCount('已安排') }}</el-tag>
            </div>
            <div class="search-box">
              <el-input
                v-model="testSearchQuery"
                placeholder="搜索考试学科ID或名称"
                prefix-icon="Search"
                clearable
                @input="handleTestSearch"
              ></el-input>
            </div>
          </div>
          <el-table v-if="showTestInfo" :data="filteredTestInfoList.length > 0 || testSearchQuery ? filteredTestInfoList : testInfoList" style="margin: 30px 0" :row-class-name="getTestRowClassName">
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
          <template v-if="showTestInfo && testInfoList.length === 0">
            <div style="text-align:center;color:#888;margin:30px 0;font-size:1.2em;">
              暂无考试计划
            </div>
          </template>
          <!-- 实习信息部分 -->
          <div class="internship-info-header" v-if="showInternship">
            <h2>校外实习信息</h2>
            <div class="status-summary">
              <el-tag 
                type="primary" 
                size="large" 
                effect="plain"
                :class="{ 'active-filter': internshipStatusFilter === 'all' }"
                @click="handleInternshipFilterChange('all')"
                class="clickable-tag"
              >全部</el-tag>
              <el-tag 
                type="success" 
                size="large" 
                effect="plain"
                :class="{ 'active-filter': internshipStatusFilter === 'status:招募中' }"
                @click="handleInternshipFilterChange('status:招募中')"
                class="clickable-tag"
              >招募中: {{ getInternshipRecruitingCount() }}</el-tag>
              <el-tag 
                type="info" 
                size="large" 
                effect="plain"
                :class="{ 'active-filter': internshipStatusFilter === '未报名' }"
                @click="handleInternshipFilterChange('未报名')"
                class="clickable-tag"
              >未报名: {{ getInternshipStatusCount('未报名') }}</el-tag>
              <el-tag 
                type="warning" 
                size="large" 
                effect="plain"
                :class="{ 'active-filter': internshipStatusFilter === '已报名' }"
                @click="handleInternshipFilterChange('已报名')"
                class="clickable-tag"
              >已报名: {{ getInternshipStatusCount('已报名') }}</el-tag>
            </div>
            <div class="search-box">
              <el-input
                v-model="internshipSearchQuery"
                placeholder="搜索实习名称或方向"
                prefix-icon="Search"
                clearable
                @input="handleInternshipSearch"
              ></el-input>
            </div>
          </div>

          <el-table v-if="showInternship" :data="filteredInternshipList.length > 0 || internshipSearchQuery ? filteredInternshipList : internshipInfoList" style="margin: 30px 0" :row-class-name="getInternshipRowClassName">
            <el-table-column prop="id" label="ID" align="center" width="80"/>
            <el-table-column prop="name" label="实习名称" align="center" />
            <el-table-column prop="location" label="实习地点" align="center"/>
            <el-table-column prop="direction" label="实习方向" align="center"/>
            <el-table-column label="招募状态" align="center" width="200">
              <template #default="scope">
                <el-tag
                  :type="scope.row.status === '招募中' ? 'success' : 'info'"
                  size="large"
                  effect="plain"
                  class="status-tag"
                >{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="报名状态" align="center" width="200">
              <template #default="scope">
                <el-tag
                  v-if="scope.row.userState === '未报名'"
                  type="info"
                  size="large"
                  effect="plain"
                  class="status-tag"
                >未报名</el-tag>
                <el-tag
                  v-else-if="scope.row.userState === '已报名'"
                  type="warning"
                  size="large"
                  effect="plain"
                  class="status-tag"
                >已报名</el-tag>
                <el-tag
                  v-else-if="scope.row.userState === '已通过'"
                  type="success"
                  size="large"
                  effect="plain"
                  class="status-tag"
                >已通过</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="400">
              <template #default="scope">
                <el-button
                  v-if="scope.row.userState === '未报名' && scope.row.status === '招募中'"
                  type="primary"
                  size="small"
                  class="big-action-btn"
                  @click="applyInternship(scope.row)"
                >报名</el-button>
                <el-button
                  v-if="scope.row.userState === '未报名' && scope.row.status === '已结束'"
                  type="primary"
                  size="small"
                  class="big-action-btn"
                  disabled
                  style="background: #e4e7ed; color: #bbb; border-color: #e4e7ed; cursor: not-allowed;"
>报名</el-button>
<el-button
  v-if="scope.row.userState === '已报名' && (scope.row.status === '招募中' || scope.row.status === '已结束')"
  type="danger"
  size="small"
  class="big-action-btn"
  @click="cancelInternship(scope.row)"
>取消</el-button>
                <el-button
                  v-if="scope.row.userState === '已通过'"
                  type="success"
                  size="small"
                  class="big-action-btn"
                  @click="viewInternshipDetails(scope.row)"
                >查看</el-button>
                <el-button
                  type="info"
                  size="small"
                  class="big-action-btn"
                  @click="viewInternshipDetails(scope.row)"
                >详情</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-dialog v-model="internshipDetailDialog" :title="currentInternship.name + ' - 详细信息'" width="600px">
            <el-form label-position="left" label-width="100px">
              <el-form-item label="实习名称">
                <span>{{ currentInternship.name }}</span>
              </el-form-item>
              <el-form-item label="实习方向">
                <span>{{ currentInternship.direction }}</span>
              </el-form-item>
              <el-form-item label="实习地点">
                <span>{{ currentInternship.location }}</span>
              </el-form-item>
              <el-form-item label="开始时间">
                <span>{{ formatDate(currentInternship.startDate) }}</span>
              </el-form-item>
              <el-form-item label="持续时间">
                <span>{{ currentInternship.duration }}天</span>
              </el-form-item>
              <el-form-item label="实习单位">
                <span>{{ currentInternship.company }}</span>
              </el-form-item>
              <el-form-item label="联系人">
                <span>{{ currentInternship.contactPerson }}</span>
              </el-form-item>
              <el-form-item label="联系电话">
                <span>{{ currentInternship.contactPhone }}</span>
              </el-form-item>
              <el-form-item label="招募状态">
                <el-tag :type="currentInternship.status === '招募中' ? 'success' : 'info'">
                  {{ currentInternship.status }}
                </el-tag>
              </el-form-item>
              <el-form-item label="报名状态">
                <el-tag :type="getStatusTagType(currentInternship.userState)">
                  {{ currentInternship.userState }}
                </el-tag>
              </el-form-item>
              <el-form-item v-if="currentInternship.documentUrl" label="相关文档">
                <el-button type="primary" size="small" @click="downloadDocument(currentInternship.documentUrl)">
                  下载文档
                </el-button>
              </el-form-item>
            </el-form>
            <template #footer>
              <div class="dialog-footer">
                <el-button @click="internshipDetailDialog = false">关闭</el-button>
                <el-button 
                  v-if="currentInternship.userState === '未报名' && currentInternship.status === '招募中'"
                  type="primary"
                  @click="applyInternshipFromDialog"
                >
                  报名
                </el-button>
                <el-button 
                  v-if="currentInternship.userState === '已报名' && currentInternship.status === '招募中'"
                  type="danger"
                  @click="cancelInternshipFromDialog"
                >
                  取消报名
                </el-button>
              </div>
            </template>
          </el-dialog>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { Document, School, Setting, Search, Notebook, Reading, Money, Bicycle, Coffee, Collection, OfficeBuilding, Promotion, Wallet, Connection, Service, ChatDotRound } from '@element-plus/icons-vue'

export default {
  name: 'Student',
  components: {
    Document,
    School,
    Setting,
    Search,
    Notebook,
    Reading,
    Money,
    Bicycle,
    Coffee,
    Collection,
    OfficeBuilding,
    Promotion,
    Wallet,
    Connection,
    Service,
    ChatDotRound
  },
  data() {
    return {
      username: this.$route.query.username || '',
      userId: null,
      realName: '',
      searchQuery: '',
      statusFilter: 'all',
      filteredMajorInfoList: [],
      testSearchQuery: '',
      testStatusFilter: 'all',
      filteredTestInfoList: [],
      showServiceCenter: false,
      serviceList: [
        {
          name: '勤工俭学',
          icon: 'Money',
          description: '校内外勤工俭学岗位信息及申请',
          path: '/work-study'
        },
        {
          name: '助学金',
          icon: 'Wallet',
          description: '国家助学金、奖学金申请及发放查询',
          path: '/scholarship'
        },
        {
          name: '教学资料',
          icon: 'Reading',
          description: '课程资料、教材电子版下载',
          path: '/materials'
        },
        {
          name: '学习辅导',
          icon: 'Notebook',
          description: '一对一在线辅导和答疑',
          path: '/tutoring'
        },
        {
          name: '校园活动',
          icon: 'Promotion',
          description: '各类校园文体活动报名',
          path: '/activities'
        },
        {
          name: '考试培训',
          icon: 'Collection',
          description: '考前培训和模拟考试',
          path: '/training'
        },
        {
          name: '校园设施',
          icon: 'OfficeBuilding',
          description: '教室、实验室等设施预约',
          path: '/facilities'
        },
        {
          name: '交通出行',
          icon: 'Bicycle',
          description: '校车时刻表及校园出行导航',
          path: '/transportation'
        },
        {
          name: '生活服务',
          icon: 'Coffee',
          description: '食堂、超市等生活服务信息',
          path: '/life-services'
        },
        {
          name: '联系我们',
          icon: 'ChatDotRound',
          description: '联系管理员、教师或技术支持',
          path: '/contact'
        }
      ],
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
      showInternship: false,
      internshipInfoList: [],
      filteredInternshipList: [],
      internshipStatusFilter: 'all',
      internshipSearchQuery: '',
      internshipDetailDialog: false,
      currentInternship: {},
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
    async getCurrentUser() {
      const res = await axios.get('http://localhost:8080/api/user/getUser');
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
    },
    async     handleMenuClick(menu) {
      // 清除所有内容的显示状态
      this.showMajorInfo = false;
      this.showTestInfo = false;
      this.showServiceCenter = false;
      this.showInternship = false;
      
      if (menu === 'serviceCenter') {
        this.showServiceCenter = true;
      } else if (menu === 'majorInfo') {
        // 确保 userId 已赋值
        if (!this.userId) {
          await this.getCurrentUser();
        }
        const majorApi = 'http://localhost:8080/api/majorInfo/getMajorInfo';
        const majorRes = await axios.get(majorApi);
        let majors = Array.isArray(majorRes.data) ? majorRes.data : [];
        // 只保留前面信息和当前用户对应的stateX栏
        const stateKey = 'state' + this.userId;
        majors = majors.map(m => ({
          ...m,
          state: m[stateKey] || '未申报',
          stateKey // 记录当前行的state字段名，便于后续操作
        }));
        this.majorInfoList = majors;
        this.filteredMajorInfoList = [];
        this.searchQuery = '';
        this.statusFilter = 'all';
        // 设置专业申报为可见
        this.showMajorInfo = true;
      } else if (menu === 'testInfo') {
        try {
          // 1. 获取当前用户的专业申报信息
          const majorApi = 'http://localhost:8080/api/majorInfo/getMajorInfo';
          const majorRes = await axios.get(majorApi);
          let majors = Array.isArray(majorRes.data) ? majorRes.data : [];
          console.log('所有专业:', majors);
          
          const stateKey = 'state' + this.userId;
          // 只保留当前用户已申报/已完成的专业
          const filteredMajors = majors.filter(m => {
            const state = m[stateKey];
            return state === '已申报' || state === '已完成';
          });
          console.log('筛选后专业:', filteredMajors);
          
          const majorIds = filteredMajors.map(m => Number(m.id));
          console.log('专业id列表:', majorIds);
          
          if (majorIds.length === 0) {
            this.testInfoList = [];
            // 设置考试计划为可见，但内容为空
            this.showTestInfo = true;
            console.log('当前用户没有已申报或已完成的专业');
            return;
          }
          
          // 2. 获取所有考试计划，改用getTestInfo接口
          const testRes = await axios.get('http://localhost:8080/api/testInfo/getTestInfo');
          const allTests = Array.isArray(testRes.data) ? testRes.data : [];
          console.log('所有考试计划:', allTests);
          
          // 3. 根据专业ID筛选考试计划
          this.testInfoList = allTests.filter(t => majorIds.includes(Number(t.majorId)));
          console.log('筛选后考试计划:', this.testInfoList);
          
          // 重置筛选条件
          this.testSearchQuery = '';
          this.testStatusFilter = 'all';
          this.filteredTestInfoList = [];
          
          // 设置考试计划为可见
          this.showTestInfo = true;
        } catch (error) {
          console.error('获取考试计划出错:', error);
          this.testInfoList = [];
          ElMessage.error('获取考试计划失败');
        }
      } else if (menu === 'internship') {
        // 确保 userId 已赋值
        if (!this.userId) {
          await this.getCurrentUser();
        }
        
        try {
          // 获取实习信息
          const internshipRes = await axios.get('http://localhost:8080/api/internshipInfo/getInternshipInfo');
          let internships = Array.isArray(internshipRes.data) ? internshipRes.data : [];
          
          // 为每个实习信息添加当前用户的状态
          const stateKey = 'state' + this.userId;
          internships = internships.map(item => ({
            ...item,
            userState: item[stateKey] || '未报名',
          }));
          
          this.internshipInfoList = internships;
          this.filteredInternshipList = [];
          this.internshipSearchQuery = '';
          this.internshipStatusFilter = 'all';
          
          // 显示实习信息
          this.showInternship = true;
        } catch (error) {
          console.error('获取实习信息失败:', error);
          ElMessage.error('获取实习信息失败');
          this.internshipInfoList = [];
          this.showInternship = true;
        }
      }
    },
    async updateMajorStatusToCompleted(row, majorApi, stateKey) {
      const updated = { ...row, [stateKey]: '已完成' };
      await axios.post(majorApi.replace('get', 'update'), updated);
    },
    async updateMajorStatusToDeclared(row, majorApi, stateKey) {
      const updated = { ...row, [stateKey]: '已申报' };
      await axios.post(majorApi.replace('get', 'update'), updated);
    },
    async declareMajor(row) {
      const updated = { ...row, [row.stateKey]: '已申报' };
      const url = 'http://localhost:8080/api/majorInfo/updateMajorInfo';
      await axios.post(url, updated);
      row.state = '已申报';
      row.disabled = false;
      ElMessage.success('申报成功');
    },
    cancelMajor(row) {
      const updated = { ...row, [row.stateKey]: '未申报' };
      const url = 'http://localhost:8080/api/majorInfo/updateMajorInfo';
      axios.post(url, updated)
        .then(() => {
          row.state = '未申报';
          row.disabled = false;
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
    },
    handleSearch() {
      this.applyFilters();
    },
    
    handleFilterChange(command) {
      this.statusFilter = command;
      this.applyFilters();
    },
    
    applyFilters() {
      // Start with all major info
      let filtered = [...this.majorInfoList];
      
      // Apply status filter if not "all"
      if (this.statusFilter !== 'all') {
        filtered = filtered.filter(item => item.state === this.statusFilter);
      }
      
      // Apply search filter if there's a search query
      if (this.searchQuery.trim()) {
        const query = this.searchQuery.trim().toLowerCase();
        filtered = filtered.filter(item => {
          return (
            item.id.toString().includes(query) || 
            item.name.toLowerCase().includes(query)
          );
        });
      }
      
      // If no filters applied and no search, return empty array to display all items
      if (!this.searchQuery.trim() && this.statusFilter === 'all') {
        this.filteredMajorInfoList = [];
        return;
      }
      
      this.filteredMajorInfoList = filtered;
    },
    
    handleTestSearch() {
      this.applyTestFilters();
    },
    
    handleServiceClick(service) {
      // 这里稍后实现具体服务的跳转逻辑
      ElMessage({
        message: `您点击了${service.name}，该功能正在开发中...`,
        type: 'info'
      });
    },
    
    handleTestFilterChange(command) {
      this.testStatusFilter = command;
      this.applyTestFilters();
    },
    
    applyTestFilters() {
      // Start with all test info
      let filtered = [...this.testInfoList];
      
      // Apply status filter if not "all"
      if (this.testStatusFilter !== 'all') {
        // Check if the filter is for plan or state
        if (this.testStatusFilter.startsWith('plan:')) {
          const planStatus = this.testStatusFilter.replace('plan:', '');
          filtered = filtered.filter(item => item.plan === planStatus);
        } else if (this.testStatusFilter.startsWith('state:')) {
          const stateStatus = this.testStatusFilter.replace('state:', '');
          filtered = filtered.filter(item => item.state === stateStatus);
        }
      }
      
      // Apply search filter if there's a search query
      if (this.testSearchQuery.trim()) {
        const query = this.testSearchQuery.trim().toLowerCase();
        filtered = filtered.filter(item => {
          return (
            (item.majorId && item.majorId.toString().includes(query)) || 
            (item.name && item.name.toLowerCase().includes(query))
          );
        });
      }
      
      // If no filters applied and no search, return empty array to display all items
      if (!this.testSearchQuery.trim() && this.testStatusFilter === 'all') {
        this.filteredTestInfoList = [];
        return;
      }
      
      this.filteredTestInfoList = filtered;
    },
    
    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    },
    
    // 获取实习状态标签类型
    getStatusTagType(status) {
      switch(status) {
        case '未报名': return 'info';
        case '已报名': return 'warning';
        default: return 'info';
      }
    },
    
    // 获取实习状态数量
    getInternshipStatusCount(status) {
      return this.internshipInfoList.filter(item => item.userState === status).length;
    },
    
    // 获取实习招募中状态数量
    getInternshipRecruitingCount() {
      return this.internshipInfoList.filter(item => item.status === '招募中').length;
    },
    
    // 获取行样式
    getInternshipRowClassName({ row }) {
      if (row.userState === '已通过') return 'internship-completed-row';
      if (row.userState === '已报名') return 'internship-applied-row';
      return 'internship-unapplied-row';
    },
    
    // 处理实习筛选
    handleInternshipFilterChange(status) {
      this.internshipStatusFilter = status;
      this.applyInternshipFilters();
    },
    
    // 处理实习搜索
    handleInternshipSearch() {
      this.applyInternshipFilters();
    },
    
    // 应用实习筛选
    applyInternshipFilters() {
      // 开始筛选
      let filtered = [...this.internshipInfoList];
      
      // 应用状态筛选
      if (this.internshipStatusFilter !== 'all') {
        // 检查是否为报名状态筛选
        if (this.internshipStatusFilter.startsWith('status:')) {
          const statusValue = this.internshipStatusFilter.replace('status:', '');
          filtered = filtered.filter(item => item.status === statusValue);
        } else {
          // 按报名状态筛选
          filtered = filtered.filter(item => item.userState === this.internshipStatusFilter);
        }
      }
      
      // 应用搜索
      if (this.internshipSearchQuery.trim()) {
        const query = this.internshipSearchQuery.trim().toLowerCase();
        filtered = filtered.filter(item => 
          (item.name && item.name.toLowerCase().includes(query)) || 
          (item.direction && item.direction.toLowerCase().includes(query)) ||
          (item.company && item.company.toLowerCase().includes(query))
        );
      }
      
      // 如果没有筛选条件，返回空数组以显示所有项目
      if (!this.internshipSearchQuery.trim() && this.internshipStatusFilter === 'all') {
        this.filteredInternshipList = [];
        return;
      }
      
      this.filteredInternshipList = filtered;
    },
    
    // 报名实习
    applyInternship(internship) {
      ElMessageBox.confirm(`确定要报名参加"${internship.name}"吗？`, '报名确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(async () => {
        try {
          // 调用API更新状态
          await axios.get(`http://localhost:8080/api/internshipInfo/updateInternshipInfoState/${internship.id}/${this.userId}/已报名`);
          
          // 更新本地数据
          internship.userState = '已报名';
          ElMessage.success('报名成功');
        } catch (error) {
          console.error('报名失败:', error);
          ElMessage.error('报名失败，请重试');
        }
      }).catch(() => {
        // 用户取消，无需处理
      });
    },
    
    // 取消实习报名
    cancelInternship(internship) {
      ElMessageBox.confirm(`确定要取消"${internship.name}"的报名吗？`, '取消确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 调用API更新状态
          await axios.get(`http://localhost:8080/api/internshipInfo/updateInternshipInfoState/${internship.id}/${this.userId}/未报名`);
          
          // 更新本地数据
          internship.userState = '未报名';
          ElMessage.success('已取消报名');
        } catch (error) {
          console.error('取消报名失败:', error);
          ElMessage.error('取消报名失败，请重试');
        }
      }).catch(() => {
        // 用户取消，无需处理
      });
    },
    
    // 查看实习详情
    viewInternshipDetails(internship) {
      this.currentInternship = { ...internship };
      this.internshipDetailDialog = true;
    },
    
    // 下载文档
    downloadDocument(url) {
      if (!url) {
        ElMessage.warning('文档链接不可用');
        return;
      }
      
      // 在新窗口打开文档链接
      window.open(url, '_blank');
    },
    
    // 从详情对话框报名实习
    applyInternshipFromDialog() {
      this.applyInternship(this.currentInternship);
      // 关闭对话框
      this.internshipDetailDialog = false;
    },
    
    // 从详情对话框取消报名实习
    cancelInternshipFromDialog() {
      this.cancelInternship(this.currentInternship);
      // 关闭对话框
      this.internshipDetailDialog = false;
    },
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
  position: relative;
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

.search-box {
  position: absolute;
  top: 50%;
  right: 20px;
  transform: translateY(-50%);
  width: 500px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-box :deep(.el-input__wrapper) {
  padding: 0 15px;
}

.search-box :deep(.el-input__inner) {
  font-size: 1.2rem;
  height: 48px;
  border-radius: 8px;
  line-height: 48px;
}

.search-box :deep(.el-input) {
  font-size: 1.2rem;
  height: 48px;
  flex: 1;
}

.clickable-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.clickable-tag:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.active-filter {
  border-width: 2px;
  font-weight: 700;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-3px);
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
  position: relative;
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

/* 服务中心样式 */
.service-center-container {
  padding: 0;
}

.service-center-header {
  padding: 20px;
  background: linear-gradient(135deg, #36d1dc 0%, #5b86e5 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 20px;
  position: relative;
}

.service-center-header h2 {
  margin: 0 0 15px 0;
  font-size: 2rem;
  text-align: center;
  color: white;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 30px;
  padding: 20px;
}

.service-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 30px 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  text-align: center;
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 180px;
}

.service-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.service-icon {
  font-size: 3rem;
  color: #409EFF;
  margin-bottom: 15px;
  background: #f0f7ff;
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.service-name {
  font-size: 1.4rem;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.service-desc {
  font-size: 0.9rem;
  color: #666;
  line-height: 1.4;
}

/* 实习信息样式 */
.internship-info-header {
  padding: 20px;
  background: linear-gradient(135deg, #00b09b 0%, #96c93d 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 20px;
  position: relative;
}

.internship-info-header h2 {
  margin: 0 0 15px 0;
  font-size: 2rem;
  text-align: center;
}

::v-deep .internship-completed-row {
  background-color: #f0f9ff !important;
}

::v-deep .internship-applied-row {
  background-color: #fff7ed !important;
}

::v-deep .internship-unapplied-row {
  background-color: #fafafa !important;
}

.internship-detail {
  padding: 15px;
}

.internship-detail .detail-item {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.internship-detail .label {
  font-weight: bold;
  width: 100px;
  flex-shrink: 0;
}

.internship-detail .value {
  flex-grow: 1;
}

.internship-detail .el-button {
  margin-left: 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style> 