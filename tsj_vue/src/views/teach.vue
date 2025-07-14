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
            <el-menu-item index="1-1" @click="handleMenuClick('majorPlan')">发布专业计划</el-menu-item>
            <el-menu-item index="1-3" @click="handleMenuClick('testInfo')">制订考试计划</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="2">
            <template #title>
              <span class="main-menu-title">
                <el-icon><Connection /></el-icon>校外拓展
              </span>
            </template>
            <el-menu-item index="2-1" @click="handleMenuClick('internshipRecruit')">发布实习招募</el-menu-item>
            <el-menu-item index="2-2" @click="handleMenuClick('competitionRecruit')">发布竞赛招募</el-menu-item>
            <el-menu-item index="2-3" @click="handleMenuClick('academicExchange')">开展学术交流</el-menu-item>
          </el-sub-menu>
          <el-menu-item index="3" @click="handleMenuClick('serviceCenter')">
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
          <div class="welcome-content" v-if="!showTestInfo && !showMajorPlan && !showServiceCenter && !showInternship">
            <h1>欢迎来到自学考试计划管理系统</h1>
            <p>请从左侧菜单选择您要使用的功能模块</p>
          </div>
          
          <!-- 服务中心 -->
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
          
          <!-- 实习招募管理 -->
          <div class="test-info-header" v-if="showInternship">
            <h2>实习招募管理</h2>
            <div class="test-status-summary">
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
                :class="{ 'active-filter': internshipStatusFilter === '招募中' }"
                @click="handleInternshipFilterChange('招募中')"
                class="clickable-tag"
              >招募中: {{ getInternshipStatusCount('招募中') }}</el-tag>
              <el-tag 
                type="info" 
                size="large" 
                effect="plain"
                :class="{ 'active-filter': internshipStatusFilter === '已结束' }"
                @click="handleInternshipFilterChange('已结束')"
                class="clickable-tag"
              >已结束: {{ getInternshipStatusCount('已结束') }}</el-tag>
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
            <el-table-column prop="name" label="实习名称" align="center"/>
            <el-table-column prop="direction" label="实习方向" align="center"/>
            <el-table-column prop="location" label="实习地点" align="center"/>
            <el-table-column prop="company" label="实习单位" align="center"/>
            <el-table-column label="招募状态" align="center" width="200">
              <template #default="scope">
                <el-tag
                  :type="scope.row.status === '招募中' ? 'success' : 'info'"
                  size="large"
                  effect="plain"
                  class="test-status-tag"
                >{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="400">
              <template #default="scope">
                <el-button
                  type="primary"
                  size="small"
                  class="big-action-btn"
                  @click="editInternship(scope.row)"
                >修改</el-button>
                <el-button
                  type="danger"
                  size="small"
                  class="big-action-btn"
                  @click="confirmDeleteInternship(scope.row)"
                >删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div v-if="showInternship" class="add-button-container">
            <el-button
              type="primary"
              icon="Plus"
              size="large"
              @click="showAddInternshipDialog"
            >新增实习招募</el-button>
          </div>

          <!-- 新增/编辑实习信息弹窗 -->
          <el-dialog
            v-model="internshipFormDialog"
            :title="internshipForm.id ? '修改实习信息' : '新增实习招募'"
            width="650px"
          >
            <el-form
              ref="internshipFormRef"
              :model="internshipForm"
              :rules="internshipFormRules"
              label-width="100px"
              label-position="left"
              hide-required-asterisk
            >
              <el-form-item label="实习名称" prop="name">
                <el-input v-model="internshipForm.name" placeholder="请输入实习名称"/>
              </el-form-item>
              <el-form-item label="实习方向" prop="direction">
                <el-input v-model="internshipForm.direction" placeholder="请输入实习方向"/>
              </el-form-item>
              <el-form-item label="实习地点" prop="location">
                <el-input v-model="internshipForm.location" placeholder="请输入实习地点"/>
              </el-form-item>
              <el-form-item label="开始时间" prop="startDate">
                <el-date-picker
                  v-model="internshipForm.startDate"
                  type="date"
                  placeholder="选择开始日期"
                  style="width: 100%"
                  value-format="YYYY-MM-DD"
                  format="YYYY-MM-DD"
                />
              </el-form-item>
              <el-form-item label="持续时间" prop="duration">
                <el-input-number
                  v-model="internshipForm.duration"
                  :min="1"
                  :max="365"
                  style="width: 200px"
                  placeholder="天数"
                />
              </el-form-item>
              <el-form-item label="文档链接" prop="documentUrl">
                <el-input v-model="internshipForm.documentUrl" placeholder="请输入文档下载链接（可选）"/>
              </el-form-item>
              <el-form-item label="实习单位" prop="company">
                <el-input v-model="internshipForm.company" placeholder="请输入实习单位"/>
              </el-form-item>
              <el-form-item label="联系人" prop="contactPerson">
                <el-input v-model="internshipForm.contactPerson" placeholder="请输入联系人姓名"/>
              </el-form-item>
              <el-form-item label="联系电话" prop="contactPhone">
                <el-input v-model="internshipForm.contactPhone" placeholder="请输入联系电话"/>
              </el-form-item>
              <el-form-item label="招募状态" prop="status">
                <el-select v-model="internshipForm.status" placeholder="请选择状态" style="width: 200px">
                  <el-option label="招募中" value="招募中"/>
                  <el-option label="已结束" value="已结束"/>
                </el-select>
              </el-form-item>
            </el-form>
            <template #footer>
              <div class="dialog-footer">
                <el-button @click="internshipFormDialog = false">取消</el-button>
                <el-button type="primary" @click="submitInternshipForm">确认</el-button>
              </div>
            </template>
          </el-dialog>
          
          <!-- 考试计划 -->
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
          
          <!-- 专业计划 -->
          <div class="major-plan-header" v-if="showMajorPlan">
            <h2>发布专业计划</h2>
            <el-form :model="majorPlanForm" :rules="majorPlanRules" ref="majorPlanFormRef" label-width="120px" class="major-plan-form" hide-required-asterisk>
              <el-form-item label="学科名称" prop="name">
                <el-input v-model="majorPlanForm.name" placeholder="请输入学科名称"></el-input>
              </el-form-item>
              <el-form-item label="开设专业" prop="major">
                <el-input v-model="majorPlanForm.major" placeholder="请输入开设专业"></el-input>
              </el-form-item>
              <el-form-item label="开设学年" prop="year">
                <el-select v-model="majorPlanForm.year" placeholder="请选择学年">
                  <el-option label="大一" value="一"></el-option>
                  <el-option label="大二" value="二"></el-option>
                  <el-option label="大三" value="三"></el-option>
                  <el-option label="大四" value="四"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="开设学期" prop="term">
                <el-radio-group v-model="majorPlanForm.term">
                  <el-radio label="上">上学期</el-radio>
                  <el-radio label="下">下学期</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="学分" prop="credit">
                <el-input-number v-model="majorPlanForm.credit" :min="0" :max="5" :step="0.5" :precision="1"></el-input-number>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" size="large" class="big-action-btn" @click="submitMajorPlan">确定</el-button>
                <el-button size="large" class="big-action-btn" @click="resetMajorPlanForm">重置</el-button>
              </el-form-item>
            </el-form>
            
            <div class="recent-plans" v-if="recentPlans.length > 0">
              <h3>最近添加的专业计划</h3>
              <el-table :data="recentPlans" style="margin-top: 20px">
                <el-table-column prop="name" label="学科名称" align="center"></el-table-column>
                <el-table-column prop="major" label="开设专业" align="center"></el-table-column>
                <el-table-column prop="year" label="学年" align="center"></el-table-column>
                <el-table-column prop="term" label="学期" align="center"></el-table-column>
                <el-table-column prop="credit" label="学分" align="center"></el-table-column>
              </el-table>
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
            <el-table-column prop="start" label="考试日期" align="center" />
            <el-table-column prop="when" label="考试时间" align="center">
              <template #default="scope">
                <el-tag v-if="scope.row.when === '1'" type="danger" class="test-status-tag">第一堂</el-tag>
                <el-tag v-else-if="scope.row.when === '2'" type="danger" class="test-status-tag">第二堂</el-tag>
                <el-tag v-else-if="scope.row.when === '3'" type="danger" class="test-status-tag">第三堂</el-tag>
                <span v-else>{{ scope.row.when }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="400" align="center">
              <template #default="scope">
                <el-button
                  v-if="scope.row.state === '未考试' && scope.row.plan === '未安排'"
                  type="primary"
                  size="small"
                  class="big-action-btn"
                  @click="showArrangeDialog(scope.row)"
                >安排</el-button>
                <template v-else-if="scope.row.state === '未考试' && scope.row.plan === '已安排'">
                  <el-button
                    type="success"
                    size="small"
                    class="big-action-btn"
                    @click="markAsCompleted(scope.row)"
                  >完成</el-button>
                  <el-button
                    type="danger"
                    size="small"
                    class="big-action-btn"
                    @click="cancelArrange(scope.row)"
                  >取消</el-button>
                </template>
                <!-- 添加删除按钮，仅对未安排的学科显示 -->
                <el-button
                  v-if="scope.row.plan === '未安排'"
                  type="danger"
                  size="small"
                  class="big-action-btn"
                  @click="confirmDelete(scope.row)"
                >删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-dialog v-model="arrangeDialog" title="安排考试" width="400px">
            <el-form :model="arrangeForm" :rules="arrangeFormRules" ref="arrangeFormRef" label-width="90px" hide-required-asterisk>
              <el-form-item label="考试地点" prop="place">
                <el-input v-model="arrangeForm.place" autocomplete="off" />
              </el-form-item>
              <el-form-item label="考试日期" prop="start">
                <el-date-picker
                  v-model="arrangeForm.start"
                  type="date"
                  placeholder="选择日期"
                  style="width: 100%;"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
              <el-form-item label="考试时间" prop="when">
                <el-select v-model="arrangeForm.when" placeholder="请选择第几堂">
                  <el-option label="第一堂" value="1" />
                  <el-option label="第二堂" value="2" />
                  <el-option label="第三堂" value="3" />
                </el-select>
              </el-form-item>
            </el-form>
            <template #footer>
              <el-button @click="arrangeDialog = false">取消</el-button>
              <el-button type="primary" @click="handleArrangeConfirm">确定</el-button>
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

    <el-dialog v-model="contactDialogVisible" title="开发者名单" width="1400px" :close-on-click-modal="true">
      <div class="dev-card-list">
        <div v-for="dev in devList" :key="dev.name" class="dev-card" :style="{ border: dev.border }">
          <div class="dev-avatar">
            <img :src="dev.avatar" alt="头像" />
          </div>
          <div class="dev-info">
            <div class="dev-name">{{ dev.name }}</div>
            <div class="dev-studentid">学号: {{ dev.studentId }}</div>
            <div class="dev-phone">📱 {{ dev.phone }}</div>
            <div class="dev-qq">QQ: {{ dev.qq }}</div>
            <div class="dev-wechat">微信: {{ dev.wechat }}</div>
          </div>
        </div>
      </div>
    </el-dialog>
  </el-container>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { Document, School, Setting, Service, Connection, Money, Bicycle, Coffee, Collection, OfficeBuilding, Promotion, Wallet, ChatDotRound, Search, Plus } from '@element-plus/icons-vue'

export default {
  name: 'Teach',
  components: {
    Document,
    School,
    Setting,
    Service,
    Connection,
    Money,
    Bicycle,
    Coffee,
    Collection,
    OfficeBuilding,
    Promotion,
    Wallet,
    ChatDotRound,
    Search,
    Plus
  },
  data() {
    return {
      username: this.$route.query.username || '',
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
      testInfoList: [],
      filteredTestInfoList: [],
      testSearchQuery: '',
      testStatusFilter: 'all',
      showTestInfo: false,
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
          icon: 'Collection',
          description: '课程资料、教材电子版下载',
          path: '/materials'
        },
        {
          name: '校园活动',
          icon: 'Promotion',
          description: '各类校园文体活动报名',
          path: '/activities'
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
      arrangeDialog: false,
      arrangeForm: {
        id: null,
        place: '',
        start: '',
        when: ''
      },
      arrangeFormRules: {
        place: [{ required: true, message: '请输入考试地点', trigger: 'blur' }],
        start: [{ required: true, message: '请选择考试日期', trigger: 'change' }],
        when: [{ required: true, message: '请选择第几堂', trigger: 'change' }]
      },
      currentArrangeRow: null,
      showMajorPlan: false,
      majorPlanForm: {
        name: '',
        major: '',
        year: '',
        term: '',
        credit: 0
      },
      majorPlanRules: {
        name: [
          { required: true, message: '请输入学科名称', trigger: 'blur' },
        ],
        major: [
          { required: true, message: '请输入开设专业', trigger: 'blur' },
        ],
        year: [
          { required: true, message: '请选择学年', trigger: 'change' },
        ],
        term: [
          { required: true, message: '请选择学期', trigger: 'change' },
        ],
        credit: [
          { required: true, message: '请输入学分', trigger: 'blur' },
          { type: 'number', min: 0, message: '学分不能为负数', trigger: 'blur' }
        ]
      },
      recentPlans: [],
      deleteConfirmVisible: false,
      currentDeleteRow: null,
      showInternship: false,
      internshipInfoList: [],
      filteredInternshipList: [],
      internshipStatusFilter: 'all',
      internshipSearchQuery: '',
      internshipFormDialog: false,
      internshipForm: {
        id: null,
        name: '',
        direction: '',
        location: '',
        startDate: '',
        duration: 30,
        documentUrl: '',
        company: '',
        contactPerson: '',
        contactPhone: '',
        status: '招募中',
        state2: '未报名',
        state3: '未报名',
        state4: '未报名'
      },
      internshipFormRules: {
        name: [
          { required: true, message: '请输入实习名称', trigger: 'blur' }
        ],
        direction: [
          { required: true, message: '请输入实习方向', trigger: 'blur' }
        ],
        location: [
          { required: true, message: '请输入实习地点', trigger: 'blur' }
        ],
        startDate: [
          { required: true, message: '请选择开始日期', trigger: 'change' }
        ],
        duration: [
          { required: true, message: '请输入持续时间', trigger: 'blur' },
          { type: 'number', min: 1, message: '持续时间必须大于0', trigger: 'blur' }
        ],
        company: [
          { required: true, message: '请输入实习单位', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择招募状态', trigger: 'change' }
        ]
      },
      contactDialogVisible: false,
      devList: [
        {
          name: '王一帆',
          phone: '1XXXXXXXXXXX',
          qq: 'XXXXXXXXXX',
          wechat: 'wangyifanwx',
          studentId: '2022112247',
          avatar: '/src/assets/wyf.jpg',
          border: '4px solid #409EFF',
        },
        {
          name: '宋佳昊',
          phone: '1XXXXXXXXXXX',
          qq: 'XXXXXXXXXX',
          wechat: 'songjiahaowx',
          studentId: '2022112558',
          avatar: '/src/assets/sjh.jpg',
          border: '4px solid #409EFF',
        },
        {
          name: '张健涛',
          phone: '1XXXXXXXXXXX',
          qq: 'XXXXXXXXXX',
          wechat: 'zhangjiantaowx',
          studentId: '2022112519',
          avatar: '/src/assets/zjt.jpg',
          border: '4px solid #409EFF',
        },
        {
          name: '赵智宇',
          phone: '1XXXXXXXXXXX',
          qq: 'XXXXXXXXXX',
          wechat: 'zhaozhiyuwx',
          studentId: '2022112516',
          avatar: '/src/assets/zzy.jpg',
          border: '4px solid #409EFF',
        },
        {
          name: '田思杰',
          phone: '1XXXXXXXXXXX',
          qq: 'XXXXXXXXXX',
          wechat: 'tiansijiewx',
          studentId: '2022112511',
          avatar: '/src/assets/tsj.jpg',
          border: '4px solid #409EFF',
        },
      ],
    }
  },
  methods: {
    goLogin() {
      this.$router.push({ name: 'Login' })
    },
    editProfile() {
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
    handleEditSave() {
      this.$refs.editFormRef.validate(valid => {
        if (!valid) return;
        axios.post('http://localhost:8080/api/user/updateUser', this.editForm).then(() => {
          ElMessage.success('修改成功');
          this.editDialog = false;
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
    handleMenuClick(menu) {
      this.showTestInfo = false;
      this.showMajorPlan = false;
      this.showServiceCenter = false;
      this.showInternship = false;
      
      if (menu === 'testInfo') {
        axios.get('http://localhost:8080/api/testInfo/getTestInfo')
          .then(res => {
            this.testInfoList = Array.isArray(res.data) ? res.data : [];
            this.showTestInfo = true;
            // Reset search and filter
            this.testSearchQuery = '';
            this.testStatusFilter = 'all';
            this.filteredTestInfoList = [];
          })
          .catch(() => {
            this.testInfoList = [];
            this.showTestInfo = true;
            ElMessage.error('获取考试计划失败');
          });
      } else if (menu === 'majorPlan') {
        this.showMajorPlan = true;
      } else if (menu === 'serviceCenter') {
        this.showServiceCenter = true;
      } else if (menu === 'internshipRecruit') {
        try {
          this.loadInternshipData();
          this.showInternship = true;
        } catch (error) {
          console.error('获取实习信息失败:', error);
          ElMessage.error('获取实习信息失败');
        }
      } else if (menu === 'competitionRecruit') {
        ElMessage.info('竞赛招募功能正在开发中...');
      } else if (menu === 'academicExchange') {
        ElMessage.info('学术交流功能正在开发中...');
      }
    },
    showArrangeDialog(row) {
      this.arrangeForm = {
        id: row.id,
        place: '',
        start: '',
        when: ''
      };
      this.currentArrangeRow = row;
      this.arrangeDialog = true;
    },
    handleArrangeConfirm() {
      this.$refs.arrangeFormRef.validate(valid => {
        if (!valid) return;
        const updated = {
          ...this.currentArrangeRow,
          place: this.arrangeForm.place,
          start: this.arrangeForm.start,
          when: this.arrangeForm.when,
          plan: '已安排'
        };
        axios.post('http://localhost:8080/api/testInfo/updateTestInfo', updated)
          .then(() => {
            Object.assign(this.currentArrangeRow, updated);
            this.arrangeDialog = false;
            ElMessage.success('安排成功');
          })
          .catch(() => {
            ElMessage.error('安排失败');
          });
      });
    },
    markAsCompleted(row) {
      const updated = { ...row, state: '已考试' };
      axios.post('http://localhost:8080/api/testInfo/updateTestInfo', updated)
        .then(() => {
          row.state = '已考试';
          ElMessage.success('已完成考试');
        })
        .catch(() => {
          ElMessage.error('操作失败');
        });
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
    cancelArrange(row) {
      const updated = {
        ...row,
        plan: '未安排',
        place: '',
        start: '',
        when: ''
      };
      axios.post('http://localhost:8080/api/testInfo/updateTestInfo', updated)
        .then(() => {
          Object.assign(row, updated);
          ElMessage.success('已取消安排');
        })
        .catch(() => {
          ElMessage.error('取消失败');
        });
    },
    submitMajorPlan() {
      this.$refs.majorPlanFormRef.validate(valid => {
        if (!valid) return;
        
        // 1. 先创建专业信息
        const majorInfo = {
          name: this.majorPlanForm.name,
          major: this.majorPlanForm.major,
          year: this.majorPlanForm.year,
          term: this.majorPlanForm.term,
          credit: this.majorPlanForm.credit,
          state2: '未申报',
          state3: '未申报',
          state4: '未申报'
        };
        
        // 保存到major_info表
        axios.post('http://localhost:8080/api/majorInfo/insertMajorInfo', majorInfo)
          .then(res => {
            // 获取新添加的专业ID
            return axios.get('http://localhost:8080/api/majorInfo/getMajorInfo');
          })
          .then(res => {
            const majors = Array.isArray(res.data) ? res.data : [];
            // 找到刚添加的专业信息
            const addedMajor = majors.find(m => 
              m.name === majorInfo.name && 
              m.major === majorInfo.major && 
              m.year === majorInfo.year && 
              m.term === majorInfo.term
            );
            
            if (addedMajor && addedMajor.id) {
              // 2. 创建考试计划
              const testInfo = {
                majorId: addedMajor.id,
                name: majorInfo.name,
                state: '未考试',
                plan: '未安排',
                place: '',
                start: '',
                when: ''
              };
              
              // 保存到test_info表
              return axios.post('http://localhost:8080/api/testInfo/insertTestInfo', testInfo);
            }
          })
          .then(() => {
            ElMessage.success('专业计划发布成功');
            // 添加到最近添加列表
            this.recentPlans.unshift({...this.majorPlanForm});
            // 重置表单
            this.resetMajorPlanForm();
          })
          .catch(error => {
            console.error('添加专业计划出错:', error);
            ElMessage.error('专业计划发布失败');
          });
      });
    },
    
    resetMajorPlanForm() {
      this.$refs.majorPlanFormRef.resetFields();
    },
    confirmDelete(row) {
      ElMessageBox.confirm(
        `确认删除学科"${row.name}"吗？此操作不可恢复，相关的专业计划也将被删除。`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
        .then(() => {
          this.deleteSubject(row);
        })
        .catch(() => {
          // 用户取消操作，无需处理
        });
    },
    
    deleteSubject(row) {
      // 先删除考试计划
      axios.get(`http://localhost:8080/api/testInfo/deleteTestInfo/${row.id}`)
        .then(() => {
          // 然后查找并删除关联的专业信息
          return axios.get('http://localhost:8080/api/majorInfo/getMajorInfo');
        })
        .then(res => {
          const majors = Array.isArray(res.data) ? res.data : [];
          const major = majors.find(m => m.id === Number(row.majorId));
          if (major) {
            return axios.get(`http://localhost:8080/api/majorInfo/deleteMajorInfo/${major.id}`);
          } else {
            throw new Error('未找到关联的专业信息');
          }
        })
        .then(() => {
          ElMessage.success('删除成功');
          // 从列表中移除该条目
          this.testInfoList = this.testInfoList.filter(item => item.id !== row.id);
        })
        .catch(error => {
          console.error('删除失败:', error);
          ElMessage.error('删除失败，请重试');
        });
    },
    handleTestSearch() {
      this.applyTestFilters();
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

    // 处理服务卡片点击
    handleServiceClick(service) {
      if (service.name === '联系我们') {
        this.contactDialogVisible = true;
        return;
      }
      ElMessage({
        message: `您点击了${service.name}，该功能正在开发中...`,
        type: 'info'
      });
    },
    
    // 加载实习数据
    async loadInternshipData() {
      const res = await axios.get('http://localhost:8080/api/internshipInfo/getInternshipInfo');
      this.internshipInfoList = Array.isArray(res.data) ? res.data : [];
      this.internshipSearchQuery = '';
      this.internshipStatusFilter = 'all';
      this.filteredInternshipList = [];
    },
    
    // 获取实习状态数量
    getInternshipStatusCount(status) {
      return this.internshipInfoList.filter(item => item.status === status).length;
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
        filtered = filtered.filter(item => item.status === this.internshipStatusFilter);
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
    
    // 获取行样式
    getInternshipRowClassName({ row }) {
      return row.status === '招募中' ? 'recruiting-row' : 'ended-row';
    },
    
    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return '未设置';
      const date = new Date(dateString);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    },
    
    // 显示新增实习对话框
    showAddInternshipDialog() {
      this.internshipForm = {
        id: null,
        name: '',
        direction: '',
        location: '',
        startDate: '',
        duration: 30,
        documentUrl: '',
        company: '',
        contactPerson: '',
        contactPhone: '',
        status: '招募中',
        state2: '未报名',
        state3: '未报名',
        state4: '未报名'
      };
      this.internshipFormDialog = true;
    },
    
    // 编辑实习信息
    editInternship(row) {
      this.internshipForm = { ...row };
      // 保证startDate为YYYY-MM-DD字符串
      if (this.internshipForm.startDate) {
        this.internshipForm.startDate = this.internshipForm.startDate.slice(0, 10);
      }
      this.internshipFormDialog = true;
    },
    
    // 确认删除实习信息
    confirmDeleteInternship(row) {
      ElMessageBox.confirm(`确定要删除"${row.name}"吗？此操作不可恢复。`, '删除确认', {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteInternship(row.id);
      }).catch(() => {
        // 用户取消删除，不执行操作
      });
    },
    
    // 删除实习信息
    async deleteInternship(id) {
      try {
        await axios.get(`http://localhost:8080/api/internshipInfo/deleteInternshipInfo/${id}`);
        ElMessage.success('删除成功');
        // 重新加载数据
        this.loadInternshipData();
      } catch (error) {
        console.error('删除失败:', error);
        ElMessage.error('删除失败，请重试');
      }
    },
    
    // 提交实习表单
    submitInternshipForm() {
      this.$refs.internshipFormRef.validate(async (valid) => {
        if (!valid) return;
        try {
          const isUpdate = !!this.internshipForm.id;
          const apiUrl = isUpdate 
            ? 'http://localhost:8080/api/internshipInfo/updateInternshipInfo' 
            : 'http://localhost:8080/api/internshipInfo/insertInternshipInfo';
          // 如果是新增，确保添加初始状态字段
          if (!isUpdate) {
            this.internshipForm.state2 = '未报名';
            this.internshipForm.state3 = '未报名';
            this.internshipForm.state4 = '未报名';
          }
          // 保证startDate为YYYY-MM-DD字符串
          if (this.internshipForm.startDate) {
            if (typeof this.internshipForm.startDate === 'object' && this.internshipForm.startDate instanceof Date) {
              // 格式化为YYYY-MM-DD
              const d = this.internshipForm.startDate;
              this.internshipForm.startDate = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
            } else if (typeof this.internshipForm.startDate === 'string') {
              this.internshipForm.startDate = this.internshipForm.startDate.slice(0, 10);
            }
          }
          await axios.post(apiUrl, this.internshipForm);
          ElMessage.success(isUpdate ? '修改成功' : '添加成功');
          this.internshipFormDialog = false;
          // 重新加载数据
          this.loadInternshipData();
        } catch (error) {
          console.error(isUpdate ? '修改失败:' : '添加失败:', error);
          ElMessage.error(isUpdate ? '修改失败，请重试' : '添加失败，请重试');
        }
      });
    },
  },
  mounted() {
    // 自动获取当前用户真实姓名
    axios.get('http://localhost:8080/api/user/getUser').then(res => {
      const users = Array.isArray(res.data) ? res.data.map(u => ({
        ...u,
        userType: u.userType ?? u.user_type,
        realName: u.realName ?? u.real_name
      })) : [];
      const currentUser = users.find(u => u.username === this.username);
      if (currentUser) {
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
  font-size: 1.33rem;
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
::v-deep .test-status-tag.el-tag--danger {
  border-color: #f56c6c !important;
  color: #f56c6c !important;
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
::v-deep .el-table .el-table__row:hover {
  background-color: #e6f7ff !important;
}
::v-deep .el-table th {
  background-color: #f5f7fa !important;
  color: #606266 !important;
  font-weight: bold !important;
}
::v-deep .el-table {
  font-size: 1.5rem !important;
}
::v-deep .el-table th, 
::v-deep .el-table td {
  font-size: 1.5rem !important;
}
::v-deep .el-table__row {
  height: 64px !important;
}
::v-deep .big-action-btn {
  font-size: 1.5rem !important;
  height: 2.25em !important;
  min-width: 3.75em !important;
  padding: 0 1.5em !important;
}
.test-info-header {
  padding: 20px;
  background: linear-gradient(135deg, #43cea2 0%, #185a9d 100%);
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
.major-plan-header {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 20px;
}

.major-plan-header h2 {
  margin: 0 0 15px 0;
  font-size: 2rem;
  text-align: center;
}

.major-plan-form {
  max-width: 600px;
  margin: 30px auto;
  padding: 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.major-plan-form .el-form-item__label {
  font-size: 1.2rem;
  font-weight: 500;
  color: #444;
}

.major-plan-form .el-input__inner,
.major-plan-form .el-radio__label {
  font-size: 1.2rem;
}

.major-plan-form .el-select {
  width: 100%;
}

.recent-plans {
  max-width: 800px;
  margin: 30px auto;
  padding: 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.recent-plans h3 {
  color: #444;
  font-size: 1.5rem;
  margin-bottom: 20px;
  text-align: center;
}

.recent-plans .el-table {
  margin-top: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.recent-plans .el-table th {
  background-color: #f5f7fa;
  font-size: 1.1rem;
  font-weight: bold;
  color: #444;
}

.recent-plans .el-table td {
  font-size: 1.1rem;
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

/* 实习招募样式 */
.internship-recruit-header {
  padding: 20px;
  background: linear-gradient(135deg, #43cea2 0%, #185a9d 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 20px;
  position: relative;
}

.internship-recruit-header h2 {
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

.add-button-container {
  display: flex;
  justify-content: flex-end;
  margin: 20px 40px 20px 0;
}
.add-button-container .el-button {
  font-size: 1.5rem !important;
  height: 2.25em !important;
  min-width: 3.75em !important;
  padding: 0 1.5em !important;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

::v-deep .recruiting-row {
  background-color: #f0f9eb !important;
}

::v-deep .ended-row {
  background-color: #f5f7fa !important;
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
.major-plan-header {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 20px;
}

.major-plan-header h2 {
  margin: 0 0 15px 0;
  font-size: 2rem;
  text-align: center;
}

.major-plan-form {
  max-width: 600px;
  margin: 30px auto;
  padding: 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.major-plan-form .el-form-item__label {
  font-size: 1.2rem;
  font-weight: 500;
  color: #444;
}

.major-plan-form .el-input__inner,
.major-plan-form .el-radio__label {
  font-size: 1.2rem;
}

.major-plan-form .el-select {
  width: 100%;
}

.recent-plans {
  max-width: 800px;
  margin: 30px auto;
  padding: 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.recent-plans h3 {
  color: #444;
  font-size: 1.5rem;
  margin-bottom: 20px;
  text-align: center;
}

.recent-plans .el-table {
  margin-top: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.recent-plans .el-table th {
  background-color: #f5f7fa;
  font-size: 1.1rem;
  font-weight: bold;
  color: #444;
}

.recent-plans .el-table td {
  font-size: 1.1rem;
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

.dev-card-list {
  display: flex;
  flex-wrap: nowrap;
  gap: 32px;
  justify-content: flex-start;
  margin-top: 24px;
  overflow-x: auto;
}
.dev-card {
  width: 400px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 4px 18px rgba(0,0,0,0.12);
  padding: 32px 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: box-shadow 0.2s;
}
.dev-card:hover {
  box-shadow: 0 8px 32px rgba(64,158,255,0.22);
}
.dev-avatar {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  overflow: hidden;
  margin-bottom: 18px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}
.dev-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.dev-info {
  text-align: center;
}
.dev-name {
  font-size: 2.2em;
  font-weight: bold;
  margin-bottom: 18px;
}
.dev-studentid {
  font-size: 1.5em;
  margin-bottom: 10px;
  color: #409EFF;
  font-weight: bold;
}
.dev-phone, .dev-qq, .dev-wechat {
  font-size: 1.5em;
  margin-bottom: 8px;
  color: #666;
}
</style> 