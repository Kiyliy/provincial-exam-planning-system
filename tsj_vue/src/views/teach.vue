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
            <el-menu-item index="1-1" @click="handleMenuClick('majorPlan')">专业计划管理</el-menu-item>
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
          <div class="user-info">
            <el-avatar :src="userAvatar" size="small" class="user-avatar"></el-avatar>
            <span>欢迎您，{{ realName }}！</span>
            <el-tag size="small" :type="userOnlineStatus === 1 ? 'success' : 'info'" class="status-tag">
              {{ userOnlineStatus === 1 ? '在线' : '离线' }}
            </el-tag>
          </div>
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
          <div class="welcome-content" v-if="!showTestInfo && !showMajorPlan && !showServiceCenter && !showInternship && !showMaterials">
            <h1>欢迎来到自学考试计划管理系统</h1>
            <p>请从左侧菜单选择您要使用的功能模块</p>
          </div>
          
          <!-- 教学资料 -->
          <div v-if="showMaterials" class="materials-container">
            <div class="materials-header">
              <div class="materials-header-controls">
                <div class="left-controls">
                  <el-button type="primary" class="back-button big-action-btn" @click="showMaterials = false; showServiceCenter = true">
                    <el-icon><Back /></el-icon> 返回
                  </el-button>
                </div>
                <h2>教学资料</h2>
                <div class="right-controls">
                  <el-input
                    v-model="materialsSearchQuery"
                    placeholder="搜索教材名称或作者"
                    prefix-icon="Search"
                    clearable
                    @input="handleMaterialsSearch"
                    class="material-search"
                  ></el-input>
                </div>
              </div>
              <p>查看和下载课程相关教材和资料</p>
            </div>
            
            <div class="materials-list">
              <div v-for="material in filteredMaterialsList.length > 0 || materialsSearchQuery ? filteredMaterialsList : materialsList" :key="material.id" class="material-card">
                <div class="material-cover">
                  <img :src="material.coverImage" alt="教材封面" />
                </div>
                <div class="material-info">
                  <h3>{{ material.title }}</h3>
                  <div class="material-meta">
                    <span>作者：{{ material.author }}</span>
                    <span>出版社：{{ material.publisher }}</span>
                    <span>出版年份：{{ material.year }}</span>
                  </div>
                  <p class="material-desc">{{ material.description }}</p>
                  <div class="material-action">
                    <el-button type="primary" class="big-action-btn" @click="showTextbookDetails(material)">
                      <el-icon><InfoFilled /></el-icon> 教材详情
                    </el-button>
                    <el-button type="primary" class="big-action-btn" @click="handleDownload(material)">
                      <el-icon><Download /></el-icon> 下载教材
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 服务中心 -->
          <div class="service-center-container" v-if="showServiceCenter">
            <div class="service-center-header">
              <h2>学习服务中心</h2>
            </div>
            <div class="service-grid">
              <div class="service-card" v-for="(service, index) in serviceList" :key="index" @click="handleServiceClick()">
                <el-icon class="service-icon">
                  <component :is="service.icon"></component>
                </el-icon>
                <div class="service-name">{{ service.name }}</div>
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
            width="600px"
            class="arrange-dialog-beauty"
          >
            <div class="arrange-form-beauty-wrapper">
              <el-form
                ref="internshipFormRef"
                :model="internshipForm"
                :rules="internshipFormRules"
                label-width="auto"
                label-position="left"
                hide-required-asterisk
                class="arrange-form-beauty"
              >
                <el-form-item label="实习名称" prop="name">
                  <el-input v-model="internshipForm.name" placeholder="请输入实习名称" />
                </el-form-item>
                <el-form-item label="实习方向" prop="direction">
                  <el-input v-model="internshipForm.direction" placeholder="请输入实习方向" />
                </el-form-item>
                <el-form-item label="实习地点" prop="location">
                  <el-input v-model="internshipForm.location" placeholder="请输入实习地点" />
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
                    style="width: 100%"
                    placeholder="天数"
                  />
                </el-form-item>
                <el-form-item label="文档链接" prop="documentUrl">
                  <el-input v-model="internshipForm.documentUrl" placeholder="请输入文档下载链接（可选）" />
                </el-form-item>
                <el-form-item label="实习单位" prop="company">
                  <el-input v-model="internshipForm.company" placeholder="请输入实习单位" />
                </el-form-item>
                <el-form-item label="联系人" prop="contactPerson">
                  <el-input v-model="internshipForm.contactPerson" placeholder="请输入联系人姓名" />
                </el-form-item>
                <el-form-item label="联系电话" prop="contactPhone">
                  <el-input v-model="internshipForm.contactPhone" placeholder="请输入联系电话" />
                </el-form-item>
                <el-form-item label="招募状态" prop="status">
                  <el-select v-model="internshipForm.status" placeholder="请选择状态" style="width: 100%">
                    <el-option label="招募中" value="招募中"/>
                    <el-option label="已结束" value="已结束"/>
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <div class="arrange-btn-group">
                    <el-button @click="internshipFormDialog = false" class="big-action-btn">取消</el-button>
                    <el-button type="primary" @click="submitInternshipForm" class="big-action-btn">确认</el-button>
                  </div>
                </el-form-item>
              </el-form>
            </div>
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
          
          <!-- 专业计划管理 -->
          <div class="test-info-header" v-if="showMajorPlan">
            <h2>专业计划管理</h2>
            <div class="search-box">
              <el-input
                v-model="majorPlanSearchQuery"
                placeholder="搜索学科名称或开设专业"
                prefix-icon="Search"
                clearable
                @input="handleMajorPlanSearch"
                style="width: 400px;"
              ></el-input>
            </div>
          </div>
          <el-table v-if="showMajorPlan" :data="filteredMajorPlanList.length > 0 || majorPlanSearchQuery ? filteredMajorPlanList : majorPlanList" style="margin: 30px 0" border :row-class-name="getMajorPlanRowClassName">
            <el-table-column prop="id" label="ID" align="center" width="80"/>
            <el-table-column prop="name" label="学科名称" align="center"/>
            <el-table-column prop="major" label="开设专业" align="center"/>
            <el-table-column prop="year" label="开设学年" align="center"/>
            <el-table-column prop="term" label="开设学期" align="center"/>
            <el-table-column prop="credit" label="学分" align="center"/>
            <el-table-column label="操作" align="center" width="300">
              <template #default="scope">
                <el-button type="primary" size="small" class="big-action-btn" @click="editMajorPlan(scope.row)">编辑</el-button>
                <el-button type="danger" size="small" class="big-action-btn" @click="confirmDeleteMajorPlan(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="showMajorPlan" class="add-button-container" style="text-align:right;margin:20px 0 0 0;">
            <el-button type="primary" icon="Plus" size="large" @click="showAddMajorPlanDialog">新增专业计划</el-button>
          </div>
          <el-dialog v-model="majorPlanFormDialog" :title="majorPlanForm.id ? '编辑专业计划' : '新增专业计划'" width="600px" class="arrange-dialog-beauty">
            <div class="arrange-form-beauty-wrapper">
              <el-form ref="majorPlanFormRef" :model="majorPlanForm" :rules="majorPlanRules" label-width="auto" label-position="left" hide-required-asterisk class="arrange-form-beauty">
                <el-form-item label="学科名称" prop="name">
                  <el-input v-model="majorPlanForm.name" placeholder="请输入学科名称" />
                </el-form-item>
                <el-form-item label="开设专业" prop="major">
                  <el-input v-model="majorPlanForm.major" placeholder="请输入开设专业" />
                </el-form-item>
                <el-form-item label="开设学年" prop="year">
                  <el-select v-model="majorPlanForm.year" placeholder="请选择学年" style="width: 100%">
                    <el-option label="大一" value="一"/>
                    <el-option label="大二" value="二"/>
                    <el-option label="大三" value="三"/>
                    <el-option label="大四" value="四"/>
                  </el-select>
                </el-form-item>
                <el-form-item label="开设学期" prop="term">
                  <el-radio-group v-model="majorPlanForm.term">
                    <el-radio label="上">上学期</el-radio>
                    <el-radio label="下">下学期</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="学分" prop="credit">
                  <el-input-number v-model="majorPlanForm.credit" :min="0" :max="5" :step="0.5" :precision="1" style="width: 100%" />
                </el-form-item>
                <el-form-item>
                  <div class="arrange-btn-group">
                    <el-button @click="majorPlanFormDialog = false" class="big-action-btn">取消</el-button>
                    <el-button type="primary" @click="submitMajorPlanForm" class="big-action-btn">确认</el-button>
                  </div>
                </el-form-item>
              </el-form>
            </div>
          </el-dialog>
          
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
          <el-dialog v-model="arrangeDialog" title="安排考试" width="600px" class="arrange-dialog-beauty">
            <div class="arrange-form-beauty-wrapper">
              <el-form :model="arrangeForm" :rules="arrangeFormRules" ref="arrangeFormRef" label-width="auto" hide-required-asterisk class="arrange-form-beauty">
                <el-form-item label="考试地点" prop="place">
                  <el-input v-model="arrangeForm.place" autocomplete="off" />
                </el-form-item>
                <el-form-item label="考试日期" prop="start">
                  <el-date-picker
                    v-model="arrangeForm.start"
                    type="date"
                    placeholder="选择日期"
                    style="width: 100%"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>
                <el-form-item label="考试时间" prop="when">
                  <el-select v-model="arrangeForm.when" placeholder="请选择第几堂" style="width: 100%">
                    <el-option label="第一堂" value="1" />
                    <el-option label="第二堂" value="2" />
                    <el-option label="第三堂" value="3" />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <div class="arrange-btn-group">
                    <el-button @click="arrangeDialog = false" class="big-action-btn">取消</el-button>
                    <el-button type="primary" @click="handleArrangeConfirm" class="big-action-btn">确定</el-button>
                  </div>
                </el-form-item>
              </el-form>
            </div>
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

    <el-dialog v-model="textbookDetailsDialogVisible" title="教材详情" width="800px" top="5vh">
      <div class="textbook-details-container">
        <div class="textbook-details-header">
          <div class="textbook-cover-large">
            <img :src="currentTextbook?.coverImage" alt="教材封面" />
          </div>
          <div class="textbook-title-section">
            <h2>{{ currentTextbook?.title }}</h2>
            <p v-if="currentTextbook?.edition">{{ currentTextbook?.edition }}</p>
          </div>
        </div>
        
        <el-table :data="textbookTableData" stripe border class="textbook-details-table">
          <el-table-column label="属性" prop="label" width="180" />
          <el-table-column label="内容" prop="value" />
        </el-table>
        
        <div class="textbook-description-section">
          <h3>教材简介</h3>
          <p>{{ currentTextbook?.description }}</p>
        </div>
        
        <div class="textbook-actions">
          <el-button type="primary" class="big-action-btn" @click="handleDownload(currentTextbook)">
            <el-icon><Download /></el-icon> 下载教材
          </el-button>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="showAdmissionCard" title="准考证" width="600px" top="5vh" :close-on-click-modal="true">
      <div class="admission-card-container">
        <div class="admission-card-header">
          <el-avatar :src="userAvatar" size="large" class="admission-avatar" style="width:100px;height:100px;margin-bottom:20px;"/>
          <h2 style="font-size:2.2rem;margin-bottom:10px;">准考证</h2>
        </div>
        <el-table :data="[admissionCardInfo]" border style="font-size:1.5rem;">
          <el-table-column prop="realName" label="姓名" align="center"/>
          <el-table-column prop="studentId" label="学号" align="center"/>
          <el-table-column prop="schoolId" label="学籍号" align="center"/>
          <el-table-column prop="idCard" label="身份证号" align="center"/>
          <el-table-column prop="phone" label="手机号" align="center"/>
          <el-table-column prop="major" label="专业" align="center"/>
        </el-table>
      </div>
    </el-dialog>
  </el-container>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { Document, School, Setting, Service, Connection, Money, Bicycle, Coffee, Collection, OfficeBuilding, Promotion, Wallet, ChatDotRound, Search, Plus, Notebook, Download, Back, InfoFilled, Printer } from '@element-plus/icons-vue'

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
    Plus,
    Notebook,
    Download,
    Back,
    InfoFilled,
    Printer
  },
  data() {
    return {
      username: this.$route.query.username || '',
      realName: '',
      userAvatar: '',
      userOnlineStatus: 0,
      editDialog: false,
      editForm: { id: null, username: '', password: '', realName: '', phone: '', userType: null, email: '', avatar: '', onlineStatus: 0 },
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
        { name: '打印准考证', icon: 'Printer' },
        { name: '教学资料', icon: 'Reading' },
        { name: '学习辅导', icon: 'Notebook' },
        { name: '校园活动', icon: 'Promotion' },
        { name: '考试培训', icon: 'Collection' },
        { name: '校园设施', icon: 'OfficeBuilding' },
        { name: '交通出行', icon: 'Bicycle' },
        { name: '生活服务', icon: 'Coffee' },
        { name: '勤工俭学', icon: 'Money' },
        { name: '助学金', icon: 'Wallet' },
        { name: '联系我们', icon: 'ChatDotRound' }
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
      showMaterials: false,
      materialsList: [],
      materialsSearchQuery: '',
      filteredMaterialsList: [],
      textbookDetailsDialogVisible: false,
      currentTextbook: null,
      majorPlanSearchQuery: '',
      filteredMajorPlanList: [],
      majorPlanList: [],
      majorPlanFormDialog: false,
      showAdmissionCard: false,
    }
  },
  computed: {
    textbookTableData() {
      if (!this.currentTextbook) return [];
      
      return [
        { label: '作者', value: this.currentTextbook.author || '未知' },
        { label: '出版社', value: this.currentTextbook.publisher || '未知' },
        { label: '出版年份', value: this.currentTextbook.year || '未知' },
        { label: '版本', value: this.currentTextbook.edition || '未知' },
      ];
    },
    admissionCardInfo() {
      return {
        realName: this.realName,
        studentId: this.studentId || '202211XXXX',
        schoolId: this.schoolId || 'XXXXXXXXXX',
        idCard: this.idCard || 'XXXXXXXXXXXXX',
        phone: this.phone || 'XXXXXXXXXXX',
        major: '软件工程',
      };
    },
  },
  methods: {
    goLogin() {
      // 获取URL参数中的userId
      const userId = this.$route.query.userId;
      if (userId) {
        // 调用登出API更新在线状态
        axios.post(`http://localhost:8080/api/user/logout?userId=${userId}`)
          .then(() => {
            // 无论成功与否都跳转到登录页
            this.$router.push({ name: 'Login' });
          })
          .catch(error => {
            console.error('登出失败:', error);
            // 即使失败也跳转到登录页
            this.$router.push({ name: 'Login' });
          });
      } else {
        // 没有userId直接跳转
        this.$router.push({ name: 'Login' });
      }
    },
    editProfile() {
      axios.get('http://localhost:8080/api/user/getUser').then(res => {
        const users = Array.isArray(res.data) ? res.data.map(u => ({
          ...u,
          userType: u.userType ?? u.user_type,
          realName: u.realName ?? u.real_name,
          onlineStatus: u.onlineStatus ?? u.online_status,
          avatar: u.avatar ?? u.avatar_url,
          email: u.email ?? ''
        })) : [];
        const currentUser = users.find(u => u.username === this.username);
        if (currentUser) {
          this.editForm = { ...currentUser };
          this.realName = currentUser.realName;
          this.userAvatar = currentUser.avatar || '/src/assets/default-avatar.jpg';
          this.userOnlineStatus = currentUser.onlineStatus;
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
          this.userAvatar = this.editForm.avatar;
          this.userOnlineStatus = this.editForm.onlineStatus;
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
      this.showMaterials = false;
      
      if (menu === 'testInfo') {
        axios.get('http://localhost:8080/api/testInfo/getTestInfo')
          .then(res => {
            this.testInfoList = Array.isArray(res.data) ? res.data : [];
            this.showTestInfo = true;
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
        axios.get('http://localhost:8080/api/majorInfo/getMajorInfo')
          .then(res => {
            this.majorPlanList = Array.isArray(res.data) ? res.data : [];
            this.filteredMajorPlanList = [];
            this.majorPlanSearchQuery = '';
            this.showMajorPlan = true;
          })
          .catch(() => {
            this.majorPlanList = [];
            this.showMajorPlan = true;
            ElMessage.error('获取专业计划失败');
          });
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
          
          // 获取所有专业信息，更新相关学科的申报状态
          axios.get('http://localhost:8080/api/majorInfo/getMajorInfo')
            .then(res => {
              const majors = Array.isArray(res.data) ? res.data : [];
              // 找到对应的专业信息
              const majorInfo = majors.find(m => m.id === Number(row.majorId));
              
              if (majorInfo) {
                // 更新所有状态
                const updatedFields = {};
                
                // 对于已申报的学生，将状态改为已完成
                if (majorInfo.state2 === '已申报') {
                  updatedFields.state2 = '已完成';
                }
                if (majorInfo.state3 === '已申报') {
                  updatedFields.state3 = '已完成';
                }
                if (majorInfo.state4 === '已申报') {
                  updatedFields.state4 = '已完成';
                }
                
                // 只有在有字段需要更新时才发送请求
                if (Object.keys(updatedFields).length > 0) {
                  const updatedMajor = { ...majorInfo, ...updatedFields };
                  axios.post('http://localhost:8080/api/majorInfo/updateMajorInfo', updatedMajor)
                    .then(() => {
                      console.log('已更新学生申报状态为已完成');
                    })
                    .catch(error => {
                      console.error('更新学科申报状态失败:', error);
                    });
                }
              }
            })
            .catch(error => {
              console.error('获取专业信息失败:', error);
            });
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
    submitMajorPlanForm() {
      this.$refs.majorPlanFormRef.validate(valid => {
        if (!valid) return;
        const isUpdate = !!this.majorPlanForm.id;
        const majorInfo = {
          id: this.majorPlanForm.id,
          name: this.majorPlanForm.name,
          major: this.majorPlanForm.major,
          year: this.majorPlanForm.year,
          term: this.majorPlanForm.term,
          credit: this.majorPlanForm.credit,
          state2: '未申报',
          state3: '未申报',
          state4: '未申报'
        };
        const apiUrl = isUpdate
          ? 'http://localhost:8080/api/majorInfo/updateMajorInfo'
          : 'http://localhost:8080/api/majorInfo/insertMajorInfo';
        axios.post(apiUrl, majorInfo)
          .then(() => {
            if (!isUpdate) {
              // 新增时，查找刚插入的major，插入考试计划
              return axios.get('http://localhost:8080/api/majorInfo/getMajorInfo')
                .then(res => {
                  const majors = Array.isArray(res.data) ? res.data : [];
                  // 找到最新一条（假设id最大）
                  const addedMajor = majors.reduce((max, cur) => cur.id > max.id ? cur : max, majors[0]);
                  if (addedMajor) {
                    const testInfo = {
                      majorId: addedMajor.id,
                      name: addedMajor.name,
                      state: '未考试',
                      plan: '未安排',
                      place: '',
                      start: '',
                      when: ''
                    };
                    return axios.post('http://localhost:8080/api/testInfo/insertTestInfo', testInfo);
                  }
                });
            }
          })
          .then(() => {
            ElMessage.success(isUpdate ? '修改成功' : '添加成功');
            this.majorPlanFormDialog = false;
            // 重新加载数据
            return axios.get('http://localhost:8080/api/majorInfo/getMajorInfo');
          })
          .then(res => {
            this.majorPlanList = Array.isArray(res.data) ? res.data : [];
            this.filteredMajorPlanList = [];
            this.majorPlanSearchQuery = '';
          })
          .catch(error => {
            console.error(isUpdate ? '修改失败:' : '添加失败:', error);
            ElMessage.error(isUpdate ? '修改失败，请重试' : '添加失败，请重试');
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
      let filtered = [...this.testInfoList];
      
      if (this.testStatusFilter !== 'all') {
        if (this.testStatusFilter.startsWith('plan:')) {
          const planStatus = this.testStatusFilter.replace('plan:', '');
          filtered = filtered.filter(item => item.plan === planStatus);
        } else if (this.testStatusFilter.startsWith('state:')) {
          const stateStatus = this.testStatusFilter.replace('state:', '');
          filtered = filtered.filter(item => item.state === stateStatus);
        }
      }
      
      if (this.testSearchQuery.trim()) {
        const query = this.testSearchQuery.trim().toLowerCase();
        filtered = filtered.filter(item => {
          return (
            (item.majorId && item.majorId.toString().includes(query)) || 
            (item.name && item.name.toLowerCase().includes(query))
          );
        });
      }
      
      if (!this.testSearchQuery.trim() && this.testStatusFilter === 'all') {
        this.filteredTestInfoList = [];
        return;
      }
      
      this.filteredTestInfoList = filtered;
    },

    handleServiceClick() {
      this.$message.info('仅学生可查看');
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
          if (this.internshipForm.startDate) {
            if (typeof this.internshipForm.startDate === 'object' && this.internshipForm.startDate instanceof Date) {
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
    handleMaterialsSearch() {
      this.applyMaterialsFilters();
    },
    
    applyMaterialsFilters() {
      let filtered = [...this.materialsList];
      
      if (this.materialsSearchQuery.trim()) {
        const query = this.materialsSearchQuery.trim().toLowerCase();
        filtered = filtered.filter(item => {
          return (
            (item.title && item.title.toLowerCase().includes(query)) || 
            (item.author && item.author.toLowerCase().includes(query))
          );
        });
      }
      
      if (!this.materialsSearchQuery.trim()) {
        this.filteredMaterialsList = [];
        return;
      }
      
      this.filteredMaterialsList = filtered;
    },
    
    loadTextbooksData() {
      axios.get('http://localhost:8080/api/textbook/getTextbooks')
        .then(res => {
          this.materialsList = res.data.map(textbook => ({
            id: textbook.id,
            title: textbook.title,
            author: textbook.author,
            publisher: textbook.publisher,
            year: textbook.publishYear,
            description: textbook.description,
            downloadUrl: textbook.downloadUrl,
            coverImage: textbook.coverImagePath || "/src/assets/logo.svg",
            edition: textbook.edition
          }));
        })
        .catch(error => {
          console.error('获取教材信息失败:', error);
          ElMessage.error('获取教材信息失败');
          // 如果API调用失败，使用默认数据
          this.materialsList = [
            {
              id: 1,
              title: "高等数学（第七版）",
              author: "同济大学数学系",
              publisher: "高等教育出版社",
              year: "2020",
              description: "本教材全面系统地阐述了高等数学的基本内容，包括函数、极限、微积分、微分方程等。",
              downloadUrl: "http://example.com/download/math.pdf",
              coverImage: "/src/assets/logo.svg",
              edition: "第七版"
            },
          ];
        });
    },
    
    showTextbookDetails(textbook) {
      this.currentTextbook = textbook;
      this.textbookDetailsDialogVisible = true;
    },
    
    handleDownload(material) {
      if (!material.downloadUrl) {
        ElMessage.warning('该教材暂无下载链接');
        return;
      }
      
      let url = material.downloadUrl;
      // 确保URL有协议前缀
      if (!/^https?:\/\//i.test(url)) {
        url = 'http://' + url;
      }
      
      try {
        window.open(url, '_blank');
      } catch (error) {
        console.error('打开下载链接失败:', error);
        ElMessage.error('打开下载链接失败，请检查链接是否有效');
      }
    },
    handleAvatarChange(file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        this.editForm.avatar = e.target.result;
      };
      reader.readAsDataURL(file.raw);
    },
    handleMajorPlanSearch() {
      this.applyMajorPlanFilters();
    },
    
    applyMajorPlanFilters() {
      let filtered = [...this.majorPlanList];
      
      if (this.majorPlanSearchQuery.trim()) {
        const query = this.majorPlanSearchQuery.trim().toLowerCase();
        filtered = filtered.filter(item => {
          return (
            (item.name && item.name.toLowerCase().includes(query)) || 
            (item.major && item.major.toLowerCase().includes(query))
          );
        });
      }
      
      if (!this.majorPlanSearchQuery.trim()) {
        this.filteredMajorPlanList = [];
        return;
      }
      
      this.filteredMajorPlanList = filtered;
    },
    
    editMajorPlan(row) {
      this.majorPlanForm = { ...row };
      this.majorPlanFormDialog = true;
    },
    
    confirmDeleteMajorPlan(row) {
      ElMessageBox.confirm(`确定要删除"${row.name}"吗？此操作不可恢复。`, '删除确认', {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteMajorPlan(row.id);
      }).catch(() => {
        // 用户取消删除，不执行操作
      });
    },
    
    deleteMajorPlan(id) {
      // 先删除所有相关考试计划
      axios.get(`http://localhost:8080/api/testInfo/deleteTestInfoByMajorId/${id}`)
        .then(() => {
          // 然后删除专业信息
          return axios.get(`http://localhost:8080/api/majorInfo/deleteMajorInfo/${id}`);
        })
        .then(() => {
          ElMessage.success('删除成功');
          this.majorPlanList = this.majorPlanList.filter(item => item.id !== id);
        })
        .catch(error => {
          console.error('删除失败:', error);
          ElMessage.error('删除失败，请重试');
        });
    },
    showAddMajorPlanDialog() {
      this.majorPlanForm = {
        id: null,
        name: '',
        major: '',
        year: '',
        term: '',
        credit: 0
      };
      this.majorPlanFormDialog = true;
    },
    getMajorPlanRowClassName({ row }) {
      // 可根据需要高亮某些行，这里保持普通
      return '';
    },
  },
  mounted() {
    // 自动获取当前用户真实姓名
    axios.get('http://localhost:8080/api/user/getUser').then(res => {
      const users = Array.isArray(res.data) ? res.data.map(u => ({
        ...u,
        userType: u.userType ?? u.user_type,
        realName: u.realName ?? u.real_name,
        onlineStatus: u.onlineStatus ?? u.online_status,
        avatar: u.avatar ?? u.avatar_url,
        email: u.email ?? ''
      })) : [];
      const currentUser = users.find(u => u.username === this.username);
      if (currentUser) {
        this.realName = currentUser.realName;
        this.userAvatar = currentUser.avatar || '/src/assets/default-avatar.jpg';
        this.userOnlineStatus = currentUser.onlineStatus;
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
  height: 60px !important;
  text-align: center !important;
  border-right: none !important;
}
::v-deep .el-table th:last-child, ::v-deep .el-table td:last-child {
  border-right: none !important;
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

.major-plan-content {
  padding: 30px;
}

.major-plan-form {
  max-width: 800px;
  margin: 0 auto;
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
.major-plan-content {
  padding: 30px;
}

.major-plan-form {
  max-width: 800px;
  margin: 0 auto;
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

.materials-container {
  padding: 20px;
}

.materials-header {
  text-align: center;
  margin-bottom: 40px; /* 增加底部间距 */
  background: linear-gradient(135deg, #8e2de2 0%, #4a00e0 100%);
  padding: 20px 20px 40px 20px; /* 增加底部内边距 */
  border-radius: 8px;
  color: white;
  position: relative;
}

.materials-header h2 {
  font-size: 2rem;
  margin: 0 0 15px 0;
  text-align: center;
}

.materials-header p {
  font-size: 1.2rem;
  opacity: 0.8;
  margin: 0;
}

.materials-header-controls {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
  position: relative;
  height: 60px; /* 增加高度 */
}

.materials-header-controls h2 {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  margin: 0;
  z-index: 1;
  top: 10%; /* 位置稍微上移 */
}

.left-controls {
  display: flex;
  align-items: center;
  position: absolute;
  left: 0;
  top: 110%; /* 下移返回按钮 */
  transform: translateY(-50%);
}

.right-controls {
  position: absolute;
  right: 0;
  top: 110%; /* 下移搜索框 */
  transform: translateY(-50%);
  width: 500px;
}

.right-controls .el-input__wrapper {
  padding: 0 15px;
}

.right-controls .el-input__inner {
  font-size: 1.2rem;
  height: 48px;
  border-radius: 8px;
  line-height: 48px;
}

.right-controls .el-input {
  font-size: 1.2rem;
  height: 48px;
}

.back-button {
  margin-right: 20px;
}

.materials-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(700px, 1fr));
  gap: 20px;
}

.material-card {
  display: flex;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  padding: 20px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.material-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transform: translateY(-5px);
}

.material-cover {
  flex: 0 0 150px;
  margin-right: 20px;
  border-radius: 6px;
  overflow: hidden;
}

.material-cover img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.material-info {
  flex: 1;
  position: relative;
  padding-bottom: 60px; /* 为按钮预留空间 */
}

.material-info h3 {
  font-size: 1.5rem;
  margin: 0 0 10px;
  color: #333;
}

.material-meta {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 10px;
  gap: 15px;
  font-size: 0.95rem;
  color: #666;
}

.material-desc {
  line-height: 1.6;
  color: #666;
  margin-bottom: 15px;
  max-height: 80px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.material-action {
  position: absolute;
  bottom: 0;
  right: 0;
  display: flex;
  gap: 10px;
}

.textbook-details {
  display: flex;
  align-items: center;
  gap: 20px;
}

.textbook-cover {
  width: 150px;
  height: 200px;
  flex-shrink: 0;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  overflow: hidden;
}

.textbook-cover img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.textbook-info {
  flex-grow: 1;
}

.textbook-info h3 {
  font-size: 1.8rem;
  margin-bottom: 10px;
  color: #303133;
}

.textbook-meta {
  margin-bottom: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  font-size: 1.1rem;
  color: #606266;
}

.textbook-desc {
  font-size: 1rem;
  line-height: 1.6;
  color: #606266;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.user-info {
  display: flex;
  align-items: center;
  margin-right: 10px;
}

.user-avatar {
  margin-right: 8px;
}

.status-tag {
  margin-left: 8px;
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
  margin: 0 auto;
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

.admission-card-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 0 10px 0;
}
.admission-card-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}
::v-deep .el-table {
  font-size: 1.5rem !important;
  border-radius: 12px !important;
  margin-top: 10px;
  width: 100%;
}
::v-deep .el-table th, ::v-deep .el-table td {
  font-size: 1.5rem !important;
  height: 60px !important;
  text-align: center !important;
}
::v-deep .el-dialog__body {
  padding: 0 30px 30px 30px !important;
}
.arrange-dialog-beauty >>> .el-dialog {
  border-radius: 18px !important;
  box-shadow: 0 8px 32px rgba(0,0,0,0.18) !important;
  background: #fff !important;
}
.arrange-form-beauty-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 24px 0 0 0;
}
.arrange-form-beauty {
  width: 100%;
  max-width: 480px;
  /* background: #fff; */
  /* border-radius: 14px; */
  /* box-shadow: 0 2px 12px rgba(0,0,0,0.08); */
  padding: 0 0 0 0;
}
.arrange-form-beauty >>> .el-form-item {
  margin-bottom: 28px !important;
}
.arrange-form-beauty >>> .el-form-item__label {
  font-size: 1.1rem !important;
  color: #333 !important;
  font-weight: 500;
}
.arrange-form-beauty >>> .el-input__inner,
.arrange-form-beauty >>> .el-input,
.arrange-form-beauty >>> .el-date-editor,
.arrange-form-beauty >>> .el-date-picker,
.arrange-form-beauty >>> .el-select,
.arrange-form-beauty >>> .el-select__wrapper {
  font-size: 1.1rem !important;
  min-height: 40px !important;
  border-radius: 8px !important;
}
.arrange-form-beauty >>> .el-input__wrapper {
  min-height: 40px !important;
  border-radius: 8px !important;
}
.arrange-form-beauty >>> .el-select-dropdown__item {
  font-size: 1.1rem !important;
  min-height: 40px !important;
}
.arrange-form-beauty >>> .el-picker-panel {
  font-size: 1.1rem !important;
  min-width: 320px !important;
  min-height: 180px !important;
}
.arrange-btn-group {
  display: flex;
  justify-content: flex-end;
  gap: 18px;
  width: 100%;
}
</style> 