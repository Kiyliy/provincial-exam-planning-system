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
            <el-menu-item index="3-2" @click="handleMenuClick('competition')">校外竞赛</el-menu-item>
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
          <div class="welcome-content" v-if="!showMajorInfo && !showTestInfo && !showServiceCenter && !showInternship && !showMaterials && !showTutoring && !showCompetition">
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
          
          <!-- 学习辅导 -->
          <div v-if="showTutoring" class="tutoring-container">
            <div class="tutoring-header">
              <div class="tutoring-header-controls">
                <div class="left-controls">
                  <el-button type="primary" class="back-button big-action-btn" @click="showTutoring = false; showServiceCenter = true">
                    <el-icon><Back /></el-icon> 返回
                  </el-button>
                </div>
                <h2>学习辅导</h2>
              </div>
              <p>在线教育局人员一对一辅导和答疑</p>
            </div>
            
            <div class="tutoring-list">
              <div v-for="staff in educationStaff" :key="staff.id" class="tutor-card">
                <div class="tutor-avatar">
                  <img :src="staff.avatar" alt="头像" />
                  <div class="status-indicator" :class="{ 'online': staff.onlineStatus === 1 }"></div>
                </div>
                <div class="tutor-info">
                  <h3 class="tutor-name">{{ staff.realName }}</h3>
                  <div class="tutor-meta">
                    <p>
                      <el-icon><Phone /></el-icon>
                      <span class="meta-text">{{ staff.phone }}</span>
                    </p>
                    <p>
                      <el-icon><Message /></el-icon>
                      <span class="meta-text">{{ staff.email }}</span>
                    </p>
                    <p>
                      <el-tag size="small" :type="staff.onlineStatus === 1 ? 'success' : 'info'">
                        {{ staff.onlineStatus === 1 ? '在线' : '离线' }}
                      </el-tag>
                    </p>
                  </div>
                  <div class="tutor-action">
                    <el-button 
                      type="primary" 
                      class="big-action-btn" 
                      :disabled="staff.onlineStatus !== 1"
                      :class="{ 'disabled-btn': staff.onlineStatus !== 1 }"
                      @click="startConsultation(staff)"
                    >
                      <el-icon><ChatDotRound /></el-icon> 咨询
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 校外竞赛 -->
          <div v-if="showCompetition" class="competition-container">
            <div class="competition-header">
              <div class="competition-header-controls">
                <h2>校外竞赛信息</h2>
                <div class="competition-status-summary">
                  <el-tag 
                    type="primary" 
                    size="large" 
                    effect="plain"
                    :class="{ 'active-filter': competitionTypeFilter === 'all' }"
                    @click="handleCompetitionFilterChange('all')"
                    class="clickable-tag"
                  >全部</el-tag>
                  <el-tag 
                    type="success" 
                    size="large" 
                    effect="plain"
                    :class="{ 'active-filter': competitionTypeFilter === '科技创新' }"
                    @click="handleCompetitionFilterChange('科技创新')"
                    class="clickable-tag"
                  >科技创新</el-tag>
                  <el-tag 
                    type="warning" 
                    size="large" 
                    effect="plain"
                    :class="{ 'active-filter': competitionTypeFilter === '学科竞赛' }"
                    @click="handleCompetitionFilterChange('学科竞赛')"
                    class="clickable-tag"
                  >学科竞赛</el-tag>
                  <el-tag 
                    type="danger" 
                    size="large" 
                    effect="plain"
                    :class="{ 'active-filter': competitionTypeFilter === '创业竞赛' }"
                    @click="handleCompetitionFilterChange('创业竞赛')"
                    class="clickable-tag"
                  >创业竞赛</el-tag>
                  <el-tag 
                    type="info" 
                    size="large" 
                    effect="plain"
                    :class="{ 'active-filter': competitionTypeFilter === '文化艺术' }"
                    @click="handleCompetitionFilterChange('文化艺术')"
                    class="clickable-tag"
                  >文化艺术</el-tag>
                </div>
                <div class="search-box">
                  <el-input
                    v-model="competitionSearchQuery"
                    placeholder="搜索竞赛名称或组织方"
                    prefix-icon="Search"
                    clearable
                    @input="handleCompetitionSearch"
                  ></el-input>
                </div>
              </div>
            </div>
            
            <div class="competition-list">
              <div v-for="competition in filteredCompetitionList" :key="competition.id" class="competition-card">
                <div class="competition-card-header">
                  <div class="competition-title">{{ competition.name }}</div>
                  <el-tag :type="getCompetitionTagType(competition.type)" size="large">{{ competition.type }}</el-tag>
                </div>
                
                <div class="competition-info">
                  <div class="competition-organizer">
                    <el-icon><OfficeBuilding /></el-icon>
                    <span>主办方：{{ competition.organizer }}</span>
                  </div>
                  <div class="competition-date">
                    <el-icon><Calendar /></el-icon>
                    <span>报名截止：{{ competition.deadline }}</span>
                  </div>
                  <div class="competition-level">
                    <el-icon><Trophy /></el-icon>
                    <span>竞赛级别：{{ competition.level }}</span>
                  </div>
                </div>
                
                <div class="competition-description">{{ competition.description }}</div>
                
                <div class="competition-actions">
                  <div class="spacer"></div>
                  <div class="action-buttons">
                    <el-button type="primary" class="big-action-btn" @click="viewCompetitionDetails(competition)">
                      <el-icon><InfoFilled /></el-icon> 查看详情
                    </el-button>
                    <el-button type="success" class="big-action-btn" @click="openCompetitionLink(competition)">
                      <el-icon><Link /></el-icon> 报名链接
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
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

    <!-- 添加教材详情弹窗 -->
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

    <!-- 咨询对话框 -->
    <el-dialog v-model="consultationDialogVisible" :title="`与 ${currentStaff.realName || ''} 的咨询`" width="700px">
      <div class="chat-container">
        <div class="chat-messages">
          <div v-for="(message, index) in chatMessages" :key="index" class="message" :class="message.sender === 'staff' ? 'staff-message' : 'student-message'">
            <div class="message-avatar">
              <img :src="message.sender === 'staff' ? currentStaff.avatar : userAvatar" alt="头像" />
            </div>
            <div class="message-content">
              <div class="message-name">{{ message.sender === 'staff' ? currentStaff.realName : realName }}</div>
              <div class="message-text">{{ message.text }}</div>
              <div class="message-time">{{ message.time }}</div>
            </div>
          </div>
          <div ref="messagesEnd" style="height: 1px;"></div>
        </div>
        <div class="chat-input">
          <el-input
            v-model="newMessage"
            type="textarea"
            :rows="3"
            placeholder="请输入您的问题..."
            @keydown.enter.prevent="sendMessage"
          ></el-input>
          <el-button type="primary" class="send-button" @click="sendMessage">
            <el-icon><Position /></el-icon> 发送
          </el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 竞赛详情对话框 -->
    <el-dialog v-model="competitionDetailsVisible" :title="currentCompetition?.name || '竞赛详情'" width="800px">
      <div v-if="currentCompetition" class="competition-details-container">
        <div class="competition-details-header">
          <div class="competition-details-title">
            <h2>{{ currentCompetition.name }}</h2>
            <el-tag :type="getCompetitionTagType(currentCompetition.type)" size="large">{{ currentCompetition.type }}</el-tag>
          </div>
        </div>
        
        <el-descriptions :column="1" border>
          <el-descriptions-item label="主办单位">{{ currentCompetition.organizer }}</el-descriptions-item>
          <el-descriptions-item label="竞赛级别">{{ currentCompetition.level }}</el-descriptions-item>
          <el-descriptions-item label="报名截止日期">{{ currentCompetition.deadline }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="competition-details-section">
          <h3>竞赛简介</h3>
          <p>{{ currentCompetition.description }}</p>
        </div>
        
        <div class="competition-details-actions">
          <el-button type="primary" size="large" @click="openCompetitionLink(currentCompetition)">
            <el-icon><Link /></el-icon> 前往报名
          </el-button>
        </div>
      </div>
    </el-dialog>
  </el-container>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { Document, School, Setting, Search, Notebook, Reading, Money, Bicycle, Coffee, Collection, OfficeBuilding, Promotion, Wallet, Connection, Service, ChatDotRound, Download, Back, InfoFilled, Plus, Phone, Message, Position, Calendar, Trophy, Link } from '@element-plus/icons-vue'

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
    ChatDotRound,
    Download,
    Back,
    InfoFilled,
    Plus,
    Phone,
    Message,
    Position,
    Calendar,
    Trophy,
    Link
  },
  data() {
    return {
      username: this.$route.query.username || '',
      userId: null,
      realName: '',
      userAvatar: '',
      userOnlineStatus: 0,
      searchQuery: '',
      statusFilter: 'all',
      filteredMajorInfoList: [],
      testSearchQuery: '',
      testStatusFilter: 'all',
      filteredTestInfoList: [],
      showServiceCenter: false,
      showTutoring: false,
      educationStaff: [],
      serviceList: [
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
          name: '联系我们',
          icon: 'ChatDotRound',
          description: '联系管理员、教师或技术支持',
          path: '/contact'
        }
      ],
      editDialog: false,
      editForm: { id: null, username: '', password: '', realName: '', phone: '', email: '', avatar: '', userType: null },
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
        email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
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
      filteredMaterialsList: [],
      textbookDetailsDialogVisible: false,
      currentTextbook: null,
      consultationDialogVisible: false,
      currentStaff: {},
      chatMessages: [],
      newMessage: '',
      competitionSearchQuery: '',
      competitionTypeFilter: 'all',
      filteredCompetitionList: [],
      competitionList: [
        {
          id: 1,
          name: '全国大学生数学建模竞赛',
          type: '学科竞赛',
          organizer: '中国工业与应用数学学会',
          deadline: '2025-09-10',
          level: '国家级',
          description: '全国大学生数学建模竞赛是全国性的群众性科技活动，旨在培养学生的创新能力、团队协作精神，提高学生建立数学模型和运用计算机解决实际问题的能力。',
          detailsUrl: 'http://www.mcm.edu.cn',
          registrationUrl: 'https://www.mcm.edu.cn/'
        },
        {
          id: 2,
          name: '互联网+大学生创新创业大赛',
          type: '创业竞赛',
          organizer: '教育部',
          deadline: '2025-07-15',
          level: '国家级',
          description: '中国"互联网+"大学生创新创业大赛是由教育部与政府、企业共同举办的创新创业赛事，旨在深化高等教育综合改革，激发大学生创造力。',
          detailsUrl: 'http://cy.ncss.cn',
          registrationUrl: 'http://cy.ncss.cn/registration'
        },
        {
          id: 3,
          name: '全国大学生电子设计竞赛',
          type: '科技创新',
          organizer: '教育部高等教育司',
          deadline: '2025-08-20',
          level: '国家级',
          description: '全国大学生电子设计竞赛是电子信息类专业最具影响力的学科竞赛之一，旨在提高大学生的创新能力、协作精神和工程实践能力。',
          detailsUrl: 'http://nuedc.xjtu.edu.cn',
          registrationUrl: 'https://www.nuedc-training.com.cn/'
        },
        {
          id: 4,
          name: '全国大学生广告艺术大赛',
          type: '文化艺术',
          organizer: '教育部高等教育司',
          deadline: '2025-10-05',
          level: '国家级',
          description: '全国大学生广告艺术大赛是中国广告教育领域最具权威性和影响力的赛事，旨在培养大学生的创意思维和实践能力。',
          detailsUrl: 'http://www.ad-contest.org',
          registrationUrl: 'https://www.sun-ada.net/'
        },
        {
          id: 5,
          name: '中国大学生计算机设计大赛',
          type: '科技创新',
          organizer: '教育部高等教育司',
          deadline: '2025-09-30',
          level: '国家级',
          description: '中国大学生计算机设计大赛是全国性的计算机应用设计类学科竞赛，旨在提升大学生计算机应用设计与开发水平。',
          detailsUrl: 'http://jsjds.ruc.edu.cn',
          registrationUrl: 'https://jsjds.blcu.edu.cn/index.htm'
        },
        {
          id: 6,
          name: '全国大学生英语竞赛',
          type: '学科竞赛',
          organizer: '高等学校大学外语教学指导委员会',
          deadline: '2025-11-15',
          level: '国家级',
          description: '全国大学生英语竞赛是教育部批准的全国性英语学科竞赛，旨在提高大学生英语综合应用能力。',
          detailsUrl: 'http://www.chinaneccs.cn',
          registrationUrl: 'https://www.chinaneccs.cn/'
        },
        {
          id: 7,
          name: '创青春全国大学生创业大赛',
          type: '创业竞赛',
          organizer: '共青团中央',
          deadline: '2025-08-30',
          level: '国家级',
          description: '创青春全国大学生创业大赛是国内大学生创业实践最高级别赛事，旨在培养大学生创新精神和创业能力。',
          detailsUrl: 'http://chuangqingchun.net',
          registrationUrl: 'https://cqc.yeeol.com/'
        }
      ],
      competitionDetailsVisible: false,
      currentCompetition: null,
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
    }
  },
  methods: {
    // 返回登录页面
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
    // 修改个人信息
    editProfile() {
      // 获取当前用户信息
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
    async getCurrentUser() {
      const res = await axios.get('http://localhost:8080/api/user/getUser');
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
        this.userId = currentUser.id;
        this.realName = currentUser.realName;
        this.userAvatar = currentUser.avatar || '/src/assets/default-avatar.jpg';
        this.userOnlineStatus = currentUser.onlineStatus;
      }
    },
    async handleMenuClick(menu) {
      // 清除所有内容的显示状态
      this.showMajorInfo = false;
      this.showTestInfo = false;
      this.showServiceCenter = false;
      this.showInternship = false;
      this.showMaterials = false;
      this.showTutoring = false;
      this.showCompetition = false;
      
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
      } else if (menu === 'materials') {
        // 清除其他内容显示
        this.showMajorInfo = false;
        this.showTestInfo = false;
        this.showInternship = false;
        this.showMaterials = true;
        this.showServiceCenter = false;
        this.showTutoring = false;
      } else if (menu === 'tutoring') {
        this.showTutoring = true;
      } else if (menu === 'competition') {
        // Reset filters
        this.competitionSearchQuery = '';
        this.competitionTypeFilter = 'all';
        // Initialize filtered list with all competitions
        this.filteredCompetitionList = [...this.competitionList];
        // Show competition section
        this.showCompetition = true;
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
      if (service.name === '教学资料') {
        this.loadTextbooksData();
        this.showServiceCenter = false;
        this.showMaterials = true;
        this.showTutoring = false;
      } else if (service.name === '学习辅导') {
        this.loadEducationStaff();
        this.showServiceCenter = false;
        this.showMaterials = false;
        this.showTutoring = true;
      } else if (service.name === '联系我们') {
        this.contactDialogVisible = true;
      } else {
        ElMessage.info(`${service.name}功能正在开发中...`);
      }
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
            // ... other default items ...
          ];
        });
    },
    
    showTextbookDetails(textbook) {
      this.currentTextbook = textbook;
      this.textbookDetailsDialogVisible = true;
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

    // 处理教学资料搜索
    handleMaterialsSearch() {
      this.applyMaterialsFilters();
    },

    // 应用教学资料筛选
    applyMaterialsFilters() {
      // Start with all materials
      let filtered = [...this.materialsList];
      
      // Apply search filter if there's a search query
      if (this.materialsSearchQuery.trim()) {
        const query = this.materialsSearchQuery.trim().toLowerCase();
        filtered = filtered.filter(item => 
          (item.title && item.title.toLowerCase().includes(query)) || 
          (item.author && item.author.toLowerCase().includes(query))
        );
      }
      
      // If no filters applied and no search, return empty array to display all items
      if (!this.materialsSearchQuery.trim()) {
        this.filteredMaterialsList = [];
        return;
      }
      
      this.filteredMaterialsList = filtered;
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
    handleAvatarChange(file, fileList) {
      this.editForm.avatar = URL.createObjectURL(file.raw);
    },
    loadEducationStaff() {
      axios.get('http://localhost:8080/api/user/getUser')
        .then(res => {
          // Filter users with userType = 2 (education bureau staff)
          this.educationStaff = Array.isArray(res.data) ? res.data
            .filter(user => user.userType === 2 || user.user_type === 2)
            .map(user => ({
              id: user.id,
              username: user.username,
              realName: user.realName || user.real_name,
              phone: user.phone,
              email: user.email || '',
              avatar: user.avatar || user.avatar_url || '/src/assets/default-avatar.jpg',
              onlineStatus: user.onlineStatus || user.online_status || 0
            })) : [];
        })
        .catch(error => {
          console.error('获取教育局人员信息失败:', error);
          ElMessage.error('获取教育局人员信息失败');
          this.educationStaff = [];
        });
    },
    startConsultation(staff) {
      if (staff.onlineStatus !== 1) {
        ElMessage.warning(`${staff.realName}当前不在线，无法咨询`);
        return;
      }
      
      ElMessage.success(`开始与${staff.realName}的咨询，正在建立连接...`);
      
      // Set current staff and clear previous messages
      this.currentStaff = staff;
      this.chatMessages = [];
      
      // Add initial staff message after a short delay (simulating connection)
      setTimeout(() => {
        // Show the chat dialog
        this.consultationDialogVisible = true;
        
        // No initial greeting message
        this.scrollToBottom();
      }, 1000);
    },
    
    // Send a new message
    sendMessage() {
      if (!this.newMessage.trim()) return;
      
      // Add student message
      this.chatMessages.push({
        sender: 'student',
        text: this.newMessage.trim(),
        time: this.getCurrentTime()
      });
      
      // Clear input field
      const sentMessage = this.newMessage.trim();
      this.newMessage = '';
      
      // Simulate staff typing response (in a real app, this would be a websocket/API call)
      setTimeout(() => {
        // Show typing indicator
        ElMessage({
          type: 'info',
          message: `${this.currentStaff.realName}正在输入...`,
          duration: 1500,
        });
        
        // After a delay, add staff response
        setTimeout(() => {
          // Generate a contextual response based on the student's message
          let responseText = '';
          
          if (sentMessage.includes('你好') || sentMessage.includes('您好') || sentMessage.includes('hi') || sentMessage.includes('hello')) {
            responseText = '你好！我是' + this.currentStaff.realName + '，很高兴为你提供帮助。请详细描述你的问题。';
          } else if (sentMessage.includes('考试') || sentMessage.includes('考点')) {
            responseText = '关于考试的问题，建议你查看最新的考试大纲和历年真题，这对于备考非常有帮助。如果你有具体疑问，请告诉我。';
          } else if (sentMessage.includes('专业') || sentMessage.includes('申报')) {
            responseText = '关于专业申报，请确保你已经了解该专业的全部要求和申报流程。如果你需要具体指导，可以预约我的线下咨询时间。';
          } else if (sentMessage.includes('感谢') || sentMessage.includes('谢谢')) {
            responseText = '不客气！如果还有其他问题，随时可以咨询我。祝你学习顺利！';
          } else if (sentMessage.includes('再见') || sentMessage.includes('拜拜')) {
            responseText = '好的，如有其他问题欢迎随时咨询。再见！';
            // Optionally close the dialog after some time
            setTimeout(() => {
              this.consultationDialogVisible = false;
            }, 2000);
          } else {
            responseText = '我了解了你的问题。建议你可以查阅相关的学习资料或者参加我们的线下辅导课程。你还有其他具体的问题吗？';
          }
          
          // Add staff response to chat
          this.chatMessages.push({
            sender: 'staff',
            text: responseText,
            time: this.getCurrentTime()
          });
          
          // Scroll to bottom to show latest message
          this.scrollToBottom();
        }, 1500);
      }, 500);
    },
    
    // Get current time for messages
    getCurrentTime() {
      const now = new Date();
      const hours = now.getHours().toString().padStart(2, '0');
      const minutes = now.getMinutes().toString().padStart(2, '0');
      return `${hours}:${minutes}`;
    },
    
    // Scroll to bottom of chat
    scrollToBottom() {
      this.$nextTick(() => {
        if (this.$refs.messagesEnd) {
          this.$refs.messagesEnd.scrollIntoView({ behavior: "smooth" });
        }
      });
    },
    handleCompetitionSearch() {
      this.applyCompetitionFilters();
    },
    handleCompetitionFilterChange(command) {
      this.competitionTypeFilter = command;
      this.applyCompetitionFilters();
    },
    applyCompetitionFilters() {
      // Start with all competition info
      let filtered = [...this.competitionList];
      
      // Apply type filter if not "all"
      if (this.competitionTypeFilter !== 'all') {
        filtered = filtered.filter(item => item.type === this.competitionTypeFilter);
      }
      
      // Apply search filter if there's a search query
      if (this.competitionSearchQuery.trim()) {
        const query = this.competitionSearchQuery.trim().toLowerCase();
        filtered = filtered.filter(item => {
          return (
            item.name.toLowerCase().includes(query) || 
            item.organizer.toLowerCase().includes(query)
          );
        });
      }
      
      this.filteredCompetitionList = filtered;
    },
    getCompetitionTagType(type) {
      switch(type) {
        case '科技创新': return 'success';
        case '学科竞赛': return 'warning';
        case '创业竞赛': return 'danger';
        case '文化艺术': return 'info';
        default: return 'primary';
      }
    },
    viewCompetitionDetails(competition) {
      this.currentCompetition = competition;
      this.competitionDetailsVisible = true;
    },
    openCompetitionLink(competition) {
      // Open the registration URL in a new tab
      window.open(competition.registrationUrl, '_blank');
    },
  },
  mounted() {
    // 自动获取当前用户真实姓名和id
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
        this.userId = currentUser.id;
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
}
.layout-container-demo .el-main {
  padding: 0;
}
.layout-container-demo .toolbar {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  height: 100%;
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

/* 教材详情弹窗样式 */
.textbook-details-container {
  padding: 0 20px;
}

.textbook-details-header {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  gap: 30px;
}

.textbook-cover-large {
  width: 180px;
  height: 250px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.textbook-cover-large img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.textbook-title-section {
  flex: 1;
}

.textbook-title-section h2 {
  font-size: 2.2rem;
  margin: 0 0 10px 0;
  color: #303133;
}

.textbook-title-section p {
  font-size: 1.3rem;
  color: #606266;
  margin: 0;
}

.textbook-details-table {
  margin-bottom: 30px;
}

.textbook-details-table .el-table__row td:first-child {
  font-weight: bold;
  background-color: #f5f7fa;
}

.textbook-description-section {
  margin-bottom: 30px;
}

.textbook-description-section h3 {
  font-size: 1.5rem;
  margin-bottom: 15px;
  color: #303133;
}

.textbook-description-section p {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #606266;
  white-space: pre-line;
}

.textbook-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar-preview {
  width: 178px;
  height: 178px;
  display: block;
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

.tutoring-container {
  padding: 20px;
}

.tutoring-header {
  text-align: center;
  margin-bottom: 40px;
  background: linear-gradient(135deg, #2980b9 0%, #6dd5fa 100%);
  padding: 20px 20px 40px 20px;
  border-radius: 8px;
  color: white;
  position: relative;
}

.tutoring-header h2 {
  font-size: 2rem;
  margin: 0 0 15px 0;
  text-align: center;
}

.tutoring-header p {
  font-size: 1.2rem;
  opacity: 0.8;
  margin: 0;
}

.tutoring-header-controls {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
  position: relative;
  height: 60px;
}

.tutoring-header-controls h2 {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  margin: 0;
  z-index: 1;
  top: 10%;
}

.tutoring-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(500px, 1fr));
  gap: 25px;
}

.tutor-card {
  display: flex;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  padding: 20px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.tutor-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transform: translateY(-5px);
}

.tutor-avatar {
  position: relative;
  flex: 0 0 120px;
  margin-right: 20px;
  border-radius: 10px;
  overflow: hidden;
  height: 120px;
}

.tutor-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.status-indicator {
  position: absolute;
  bottom: 5px;
  right: 5px;
  width: 15px;
  height: 15px;
  border-radius: 50%;
  background-color: #909399;
  border: 2px solid white;
}

.status-indicator.online {
  background-color: #67c23a;
}

.tutor-info {
  flex: 1;
  position: relative;
  padding-bottom: 60px;
}

.tutor-name {
  font-size: 1.5rem;
  margin: 0 0 10px;
  color: #333;
}

.tutor-meta {
  margin-bottom: 10px;
}

.tutor-meta p {
  margin: 5px 0;
  color: #666;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
}

.meta-text {
  margin-left: 8px;
}

.tutor-action {
  position: absolute;
  bottom: 0;
  right: 0;
}

.disabled-btn {
  opacity: 0.6;
}

/* 聊天对话框样式 */
.chat-container {
  display: flex;
  flex-direction: column;
  height: 450px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background-color: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 16px;
  max-height: 350px;
}

.message {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-start;
}

.staff-message {
  flex-direction: row;
}

.student-message {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 10px;
}

.message-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-content {
  max-width: 70%;
  padding: 10px;
  border-radius: 10px;
  position: relative;
}

.staff-message .message-content {
  background-color: #f0f2f5;
  border-top-left-radius: 2px;
}

.student-message .message-content {
  background-color: #e6f7ff;
  border-top-right-radius: 2px;
  text-align: right;
}

.message-name {
  font-size: 0.85rem;
  color: #666;
  margin-bottom: 4px;
}

.message-text {
  font-size: 1rem;
  line-height: 1.5;
  word-break: break-word;
}

.message-time {
  font-size: 0.75rem;
  color: #999;
  margin-top: 4px;
}

.chat-input {
  display: flex;
  gap: 10px;
}

.chat-input .el-input {
  flex: 1;
}

.send-button {
  align-self: flex-end;
}

.competition-container {
  padding: 20px;
}

.competition-header {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 20px;
  position: relative;
}

.competition-header h2 {
  margin: 0 0 15px 0;
  font-size: 2rem;
  text-align: center;
}

.competition-header-controls {
  position: relative;
  padding-bottom: 60px;
}

.competition-status-summary {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

.search-box {
  position: absolute;
  bottom: 0;
  right: 20px;
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
}

.competition-filter {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.competition-search {
  flex: 1;
}

.competition-tags {
  display: flex;
  gap: 10px;
}

.competition-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
  margin-top: 30px;
  padding: 0 20px;
}

.competition-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
  min-height: 400px;
  position: relative;
}

.competition-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.competition-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  width: 100%;
}

.competition-title {
  font-size: 1.8rem;
  font-weight: bold;
  color: #333;
}

.competition-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
  width: 100%;
  text-align: left;
}

.competition-organizer, .competition-date, .competition-level {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #666;
  font-size: 1.1rem;
}

.competition-organizer .el-icon, .competition-date .el-icon, .competition-level .el-icon {
  color: #409EFF;
  font-size: 1.2rem;
}

.competition-description {
  font-size: 1.1rem;
  color: #666;
  line-height: 1.6;
  margin-bottom: 20px;
  text-align: left;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  width: 100%;
}

.competition-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: auto;
  width: 100%;
  position: absolute;
  bottom: 25px;
  right: 25px;
}

.spacer {
  flex-grow: 1;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

/* 竞赛详情样式 */
.competition-details-container {
  padding: 0 20px;
}

.competition-details-header {
  margin-bottom: 30px;
}

.competition-details-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.competition-details-title h2 {
  font-size: 2rem;
  margin: 0;
  color: #333;
}

.competition-details-section {
  margin: 30px 0;
}

.competition-details-section h3 {
  font-size: 1.5rem;
  margin-bottom: 15px;
  color: #333;
}

.competition-details-section p {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #666;
}

.competition-details-actions {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.competition-card-header .el-tag {
  font-size: 1.1rem;
  padding: 8px 12px;
  height: auto;
  border-radius: 6px;
  font-weight: bold;
}
</style> 