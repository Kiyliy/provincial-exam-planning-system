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
          <div class="welcome-content" v-if="!showMajorInfo && !showTestInfo && !showServiceCenter && !showInternship && !showMaterials && !showTutoring && !showCompetition && !showCampusActivity && !showExamTraining && !showCampusFacility">
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
            <el-table-column prop="name" label="学科名称" width = '400' align="center"/>
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

          <el-dialog v-model="internshipDetailDialog" :title="currentInternship.name + ' - 详细信息'" width="900px" class="arrange-dialog-beauty">
            <div class="internship-detail-table-wrapper">
              <table class="internship-detail-table">
                <tr>
                  <td class="label">实习名称</td>
                  <td class="value">{{ currentInternship.name }}</td>
                  <td class="label">实习方向</td>
                  <td class="value">{{ currentInternship.direction }}</td>
                </tr>
                <tr>
                  <td class="label">实习地点</td>
                  <td class="value">{{ currentInternship.location }}</td>
                  <td class="label">开始时间</td>
                  <td class="value">{{ formatDate(currentInternship.startDate) }}</td>
                </tr>
                <tr>
                  <td class="label">持续时间</td>
                  <td class="value">{{ currentInternship.duration }}天</td>
                  <td class="label">实习单位</td>
                  <td class="value">{{ currentInternship.company }}</td>
                </tr>
                <tr>
                  <td class="label">联系人</td>
                  <td class="value">{{ currentInternship.contactPerson }}</td>
                  <td class="label">联系电话</td>
                  <td class="value">{{ currentInternship.contactPhone }}</td>
                </tr>
                <tr>
                  <td class="label">招募状态</td>
                  <td class="value">
                    <el-tag :type="currentInternship.status === '招募中' ? 'success' : 'info'" class="detail-tag">
                      {{ currentInternship.status }}
                    </el-tag>
                  </td>
                  <td class="label">报名状态</td>
                  <td class="value">
                    <el-tag :type="getStatusTagType(currentInternship.userState)" class="detail-tag">
                      {{ currentInternship.userState }}
                    </el-tag>
                  </td>
                </tr>
                <tr v-if="currentInternship.documentUrl">
                  <td class="label">相关文档</td>
                  <td class="value" colspan="3">
                    <el-button type="primary" size="large" @click="downloadDocument(currentInternship.documentUrl)">
                      下载文档
                    </el-button>
                  </td>
                </tr>
              </table>
            </div>
            <template #footer>
              <div class="arrange-btn-group dialog-footer-fixed">
                <el-button @click="internshipDetailDialog = false" class="big-action-btn">关闭</el-button>
                <el-button 
                  type="primary"
                  class="big-action-btn"
                  :disabled="currentInternship.userState === '已报名' || currentInternship.status !== '招募中'"
                  @click="applyInternshipFromDialog"
                >报名</el-button>
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
                  <div class="tutor-row tutor-row-1">
                    <span class="tutor-name">{{ staff.realName }}</span>
                  </div>
                  <div class="tutor-row tutor-row-2">
                    <el-icon><Phone /></el-icon>
                    <span class="tutor-phone">{{ staff.phone }}</span>
                  </div>
                  <div class="tutor-row tutor-row-3">
                    <el-icon><Message /></el-icon>
                    <span class="tutor-email">{{ staff.email }}</span>
                  </div>
                  <div class="tutor-action-row-fixed">
                    <el-tag class="tutor-status-tag-fixed" :type="staff.onlineStatus === 1 ? 'success' : 'info'">
                      {{ staff.onlineStatus === 1 ? '在线' : '离线' }}
                    </el-tag>
                    <el-button 
                      type="primary" 
                      class="big-action-btn tutor-consult-btn" 
                      :disabled="staff.onlineStatus !== 1"
                      :class="{ 'disabled-btn': staff.onlineStatus !== 1 }"
                      @click="startConsultation(staff)"
                    >
                      <el-icon><ChatDotRound /></el-icon> 咨询
                    </el-button>
                  </div>
                  <span class="tutor-profession-fixed">{{ staff.profession }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 校外竞赛 -->
          <div v-if="showCompetition" class="competition-container">
            <div class="competition-header">
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
                  type="danger" 
                  size="large" 
                  effect="plain"
                  :class="{ 'active-filter': competitionTypeFilter === '科技创新' }"
                  @click="handleCompetitionFilterChange('科技创新')"
                  class="clickable-tag"
                >科技创新</el-tag>
                <el-tag 
                  type="success" 
                  size="large" 
                  effect="plain"
                  :class="{ 'active-filter': competitionTypeFilter === '文化艺术' }"
                  @click="handleCompetitionFilterChange('文化艺术')"
                  class="clickable-tag"
                >文化艺术</el-tag>
                <el-tag 
                  type="warning" 
                  size="large" 
                  effect="plain"
                  :class="{ 'active-filter': competitionTypeFilter === '学科竞赛' }"
                  @click="handleCompetitionFilterChange('学科竞赛')"
                  class="clickable-tag"
                >学科竞赛</el-tag>
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
          <!-- 校园活动 -->
          <div v-if="showCampusActivity" class="campus-activity-container">
            <div class="campus-activity-header">
              <div class="campus-activity-header-controls">
                <div class="left-controls">
                  <el-button type="primary" class="back-button big-action-btn" @click="backToServiceCenterFromActivity">
                    <el-icon><Back /></el-icon> 返回
                  </el-button>
                </div>
                <h2>校园活动</h2>
                <div class="right-controls">
                  <el-input
                    v-model="campusActivitySearchQuery"
                    placeholder="搜索活动名称或主办方"
                    prefix-icon="Search"
                    clearable
                    @input="handleCampusActivitySearch"
                    class="material-search"
                  ></el-input>
                </div>
              </div>
              <p>查看和参与丰富多彩的校园活动</p>
            </div>
            <div class="campus-activity-list">
              <div v-for="activity in filteredCampusActivityList.length > 0 || campusActivitySearchQuery ? filteredCampusActivityList : sortedCampusActivityList" :key="activity.id" class="campus-activity-card" @click="viewCampusActivityDetails(activity)">
                <div class="campus-activity-card-header">
                  <div class="campus-activity-title">{{ activity.name }}</div>
                  <el-tag type="primary" size="large">{{ activity.organizer }}</el-tag>
                </div>
                <div class="campus-activity-info">
                  <div class="campus-activity-date">
                    <el-icon><Calendar /></el-icon>
                    <span>活动时间：{{ activity.date }}</span>
                  </div>
                  <div class="campus-activity-location">
                    <el-icon><OfficeBuilding /></el-icon>
                    <span>地点：{{ activity.location }}</span>
                  </div>
                </div>
                <div class="campus-activity-description">{{ activity.description }}</div>
              </div>
            </div>
            <el-dialog v-model="campusActivityDetailsVisible" title="活动详情" width="700px">
              <div class="campus-activity-details-container">
                <h2>{{ currentCampusActivity?.name }}</h2>
                <div style="margin-bottom: 12px;">
                  <el-tag type="primary" size="large">{{ currentCampusActivity?.organizer }}</el-tag>
                  <span style="margin-left: 18px; color: #666; font-size: 1.1rem;">{{ currentCampusActivity?.date }} | {{ currentCampusActivity?.location }}</span>
                </div>
                <div style="font-size: 1.2rem; color: #333; margin-bottom: 18px;">{{ currentCampusActivity?.description }}</div>
                <div style="font-size: 1.1rem; color: #666; white-space: pre-line;">{{ currentCampusActivity?.detail }}</div>
                <div style="text-align: right; margin-top: 24px;">
                  <el-button @click="backToCampusActivityList">返回</el-button>
                </div>
              </div>
            </el-dialog>
          </div>
          <div v-if="showExamTraining" class="exam-training-container campus-activity-container">
            <div class="campus-activity-header">
              <div class="campus-activity-header-controls">
                <div class="left-controls">
                  <el-button type="primary" class="back-button big-action-btn" @click="backToServiceCenterFromExamTraining">
                    <el-icon><Back /></el-icon> 返回
                  </el-button>
                </div>
                <h2>考试培训</h2>
                <div class="right-controls">
                  <el-input
                    v-model="examTrainingSearchQuery"
                    placeholder="搜索培训名称或主办方"
                    prefix-icon="Search"
                    clearable
                    @input="handleExamTrainingSearch"
                    class="material-search"
                  ></el-input>
                </div>
              </div>
              <p>查看和参与各类考试培训活动</p>
            </div>
            <div class="campus-activity-list">
              <div v-for="training in (filteredExamTrainingList.length > 0 || examTrainingSearchQuery ? filteredExamTrainingList : examTrainingList).slice().sort((a, b) => new Date(b.date) - new Date(a.date))" :key="training.id" class="campus-activity-card" @click="viewExamTrainingDetails(training)">
                <div class="campus-activity-card-header">
                  <div class="campus-activity-title">{{ training.name }}</div>
                  <el-tag type="primary" size="large">{{ training.organizer }}</el-tag>
                </div>
                <div class="campus-activity-info">
                  <div class="campus-activity-date">
                    <el-icon><Calendar /></el-icon>
                    <span>培训时间：{{ training.date }}</span>
                  </div>
                  <div class="campus-activity-location">
                    <el-icon><OfficeBuilding /></el-icon>
                    <span>地点：{{ training.location }}</span>
                  </div>
                </div>
                <div class="campus-activity-description">{{ training.description }}</div>
              </div>
            </div>
            <el-dialog v-model="examTrainingDetailsVisible" title="培训详情" width="700px">
              <div class="campus-activity-details-container">
                <h2>{{ currentExamTraining?.name }}</h2>
                <div style="margin-bottom: 12px;">
                  <el-tag type="primary" size="large">{{ currentExamTraining?.organizer }}</el-tag>
                  <span style="margin-left: 18px; color: #666; font-size: 1.1rem;">{{ currentExamTraining?.date }} | {{ currentExamTraining?.location }}</span>
                </div>
                <div style="font-size: 1.2rem; color: #333; margin-bottom: 18px;">{{ currentExamTraining?.description }}</div>
                <div style="font-size: 1.1rem; color: #666; white-space: pre-line;">{{ currentExamTraining?.detail }}</div>
                <div style="text-align: right; margin-top: 24px;">
                  <el-button @click="backToExamTrainingList">返回</el-button>
                </div>
              </div>
            </el-dialog>
          </div>
          <div v-if="showCampusFacility" class="campus-facility-container exam-training-container">
            <div class="campus-facility-header campus-activity-header">
              <div class="campus-activity-header-controls exam-training-header-controls">
                <div class="left-controls">
                  <el-button type="primary" class="back-button big-action-btn" @click="showCampusFacility = false; showServiceCenter = true">
                    <el-icon><Back /></el-icon> 返回
                  </el-button>
                </div>
                <h2>校园设施租用</h2>
              </div>
              <div class="facility-filter-bar">
                <el-tag
                  v-for="type in facilityTypeOptions"
                  :key="type"
                  :type="facilityType === type ? 'primary' : 'info'"
                  size="large"
                  effect="plain"
                  :class="['facility-filter-tag', { 'active-filter': facilityType === type }]"
                  @click="handleFacilityTypeChange(type)"
                  class="clickable-tag"
                >{{ type }}</el-tag>
              </div>
            </div>
            <el-table :data="filteredFacilityList" style="width: 100%; margin-top: 20px;">
              <el-table-column prop="name" label="设施名称" align="center" />
              <el-table-column prop="type" label="类型" align="center" />
              <el-table-column prop="location" label="位置" align="center" />
              <el-table-column prop="status" label="状态" align="center" >
                <template #default="scope">
                  <el-tag
                    v-if="scope.row.status === '空闲' && scope.row.renting"
                    type="warning"
                    class="detail-tag"
                  >正在租用</el-tag>
                  <el-tag
                    v-else-if="scope.row.status === '空闲'"
                    type="success"
                    class="detail-tag"
                  >空闲</el-tag>
                  <el-tag
                    v-else
                    type="info"
                    class="detail-tag"
                  >{{ scope.row.status }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center">
                <template #default="scope">
                  <div
                    v-if="scope.row.status === '空闲'"
                    class="rent-btn-flip-wrapper"
                  >
                    <div
                      class="rent-btn-flip"
                      :class="{ flipped: scope.row.renting }"
                      @click="toggleRenting(scope.row)"
                    >
                      <div class="rent-btn-face rent-btn-front">
                        <el-button type="primary" class="big-action-btn rent-btn-inner" >租用</el-button>
                      </div>
                      <div class="rent-btn-face rent-btn-back">
                        <el-button type="danger" class="big-action-btn rent-btn-inner" >取消</el-button>
                      </div>
                    </div>
                  </div>
                  <el-button
                    v-else
                    type="primary"
                    class="big-action-btn rent-btn"
                    disabled
                  >租用</el-button>
                </template>
              </el-table-column>
            </el-table>
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
          <el-input
            v-model="editForm.password"
            :type="editPasswordVisible ? 'text' : 'password'"
            autocomplete="off"
            placeholder="请输入密码"
          >
            <template #suffix>
              <el-icon
                class="password-toggle-icon"
                @click="editPasswordVisible = !editPasswordVisible"
                style="cursor: pointer;"
              >
                <View v-if="editPasswordVisible" />
                <Hide v-else />
              </el-icon>
            </template>
          </el-input>
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
    
    <el-dialog v-model="contactDialogVisible" title="开发者名单" width="1400px" top="20vh" :close-on-click-modal="true">
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
    <el-dialog v-model="consultationDialogVisible" :title="`与 ${currentStaff.realName || ''} 的咨询`" width="900px" top="15vh">
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
        <div class="chat-input-row">
          <el-input
            v-model="newMessage"
            placeholder="请输入您的问题..."
            @keydown.enter.prevent="sendMessage"
            class="chat-input-fixed"
            :maxlength="200"
            show-word-limit
            clearable
          ></el-input>
          <el-button type="primary" class="send-button-fixed" @click="sendMessage">
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

    <el-dialog v-model="showAdmissionCard" width="900px" top="20vh" :close-on-click-modal="true">
      <div class="admission-card-container">
        <div class="admission-card-header">
          <el-avatar :src="userAvatar" size="large" class="admission-avatar" style="width:96px;height:96px;margin-bottom:12px;"/>
          <h2 style="font-size:2rem;margin-bottom:10px;letter-spacing:4px;">准考证</h2>
        </div>
        <table class="admission-table">
          <tr>
            <td class="admission-label">姓名</td>
            <td class="admission-value">{{ admissionCardInfo.realName }}</td>
            <td class="admission-label">学号</td>
            <td class="admission-value">{{ admissionCardInfo.studentId }}</td>
          </tr>
          <tr>
            <td class="admission-label">学籍号</td>
            <td class="admission-value" colspan="3">{{ admissionCardInfo.schoolId }}</td>
          </tr>
          <tr>
            <td class="admission-label">身份证号</td>
            <td class="admission-value" colspan="3">{{ admissionCardInfo.idCard }}</td>
          </tr>
          <tr>
            <td class="admission-label">手机号</td>
            <td class="admission-value">{{ admissionCardInfo.phone }}</td>
            <td class="admission-label">专业</td>
            <td class="admission-value">{{ admissionCardInfo.major }}</td>
          </tr>
        </table>
      </div>
    </el-dialog>
  </el-container>
</template>

<script>
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { Document, School, Setting, Search, Notebook, Reading, Money, Bicycle, Coffee, Collection, OfficeBuilding, Promotion, Wallet, Connection, Service, ChatDotRound, Download, Back, InfoFilled, Plus, Phone, Message, Position, Calendar, Trophy, Link, Printer, View, Hide } from '@element-plus/icons-vue'

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
    Link,
    Printer,
    View,
    Hide
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
        { name: '打印准考证', icon: 'Printer', description: '在线打印个人准考证' },
        { name: '教学资料', icon: 'Reading', description: '课程资料、教材电子版下载', path: '/materials' },
        { name: '学习辅导', icon: 'Notebook', description: '一对一在线辅导和答疑', path: '/tutoring' },
        { name: '校园活动', icon: 'Promotion', description: '各类校园文体活动报名', path: '/activities' },
        { name: '考试培训', icon: 'Collection', description: '考前培训和模拟考试', path: '/training' },
        { name: '校园设施', icon: 'OfficeBuilding', description: '教室、实验室等设施预约', path: '/facilities' },
        { name: '交通出行', icon: 'Bicycle', description: '校车时刻表及校园出行导航', path: '/transportation' },
        { name: '生活服务', icon: 'Coffee', description: '食堂、超市等生活服务信息', path: '/life-services' },
        { name: '勤工俭学', icon: 'Money', description: '校内外勤工俭学岗位信息及申请', path: '/work-study' },
        { name: '助学金', icon: 'Wallet', description: '国家助学金、奖学金申请及发放查询', path: '/scholarship' },
        { name: '联系我们', icon: 'ChatDotRound', description: '联系管理员、教师或技术支持', path: '/contact' }
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
      competitionList: [], // 改为接口获取
      competitionDetailsVisible: false,
      currentCompetition: null,
      showAdmissionCard: false,
      campusActivityList: [
        {
          id: 1,
          name: '迎新晚会',
          organizer: '校团委',
          date: '2025-09-10',
          location: '大礼堂',
          description: '新生入学欢迎晚会，丰富多彩的文艺表演。',
          detail: '迎新晚会是为新生准备的盛大文艺活动，包含歌舞、小品、互动游戏等环节，欢迎全体师生参加。'
        },
        {
          id: 2,
          name: '校园马拉松',
          organizer: '体育部',
          date: '2025-10-15',
          location: '田径场',
          description: '全校师生参与的健康长跑活动。',
          detail: '校园马拉松旨在增强师生体质，促进交流，设有奖品，欢迎报名参与。'
        },
        {
          id: 3,
          name: '科技节',
          organizer: '科技协会',
          date: '2025-11-05',
          location: '创新楼',
          description: '展示学生科技创新成果，体验科技乐趣。',
          detail: '科技节包含科技作品展、机器人比赛、科普讲座等，激发同学们的创新热情。'
        },
        {
          id: 4,
          name: '书画大赛',
          organizer: '艺术团',
          date: '2025-09-25',
          location: '美术馆',
          description: '展示师生书画才艺，评选优秀作品。',
          detail: '书画大赛面向全校师生征集书法、国画、油画等作品，设有丰厚奖品。'
        },
        {
          id: 5,
          name: '英语演讲比赛',
          organizer: '外语系',
          date: '2025-10-08',
          location: '报告厅',
          description: '锻炼英语口语能力，展现自信风采。',
          detail: '英语演讲比赛分为初赛和决赛，邀请外教担任评委，欢迎报名。'
        },
        {
          id: 6,
          name: '志愿服务月',
          organizer: '青年志愿者协会',
          date: '2025-10-20',
          location: '全校各地',
          description: '参与志愿服务，传递爱心正能量。',
          detail: '志愿服务月期间将组织支教、环保、助老等多项公益活动，欢迎同学们积极参与。'
        },
        {
          id: 7,
          name: '校园歌手大赛',
          organizer: '音乐社',
          date: '2025-11-12',
          location: '音乐厅',
          description: '展现歌喉，争夺校园歌王称号。',
          detail: '校园歌手大赛分为海选、复赛和决赛，优胜者有机会录制单曲。'
        },
        {
          id: 8,
          name: '创业讲座',
          organizer: '创新创业中心',
          date: '2025-09-18',
          location: '学术交流中心',
          description: '知名企业家分享创业经验。',
          detail: '创业讲座邀请多位知名校友和企业家，分享创业故事与实用经验。'
        },
        {
          id: 9,
          name: '棋类比赛',
          organizer: '棋社',
          date: '2025-10-28',
          location: '活动室',
          description: '象棋、围棋、国际象棋等多项赛事。',
          detail: '棋类比赛分组进行，设有象棋、围棋、国际象棋等项目，欢迎棋艺爱好者参加。'
        },
        {
          id: 10,
          name: '心理健康周',
          organizer: '心理健康中心',
          date: '2025-11-20',
          location: '心理活动室',
          description: '关注心理健康，开展讲座与咨询。',
          detail: '心理健康周将举办心理讲座、团体辅导、心理测试等活动，帮助同学们缓解压力。'
        },
        {
          id: 11,
          name: '摄影展',
          organizer: '摄影协会',
          date: '2025-09-30',
          location: '图书馆大厅',
          description: '展示校园美景与生活瞬间。',
          detail: '摄影展征集全校师生摄影作品，优秀作品将在校内展出并颁发证书。'
        },
        {
          id: 12,
          name: '环保知识竞赛',
          organizer: '绿色协会',
          date: '2025-10-22',
          location: '多功能厅',
          description: '普及环保知识，倡导绿色生活。',
          detail: '环保知识竞赛以团队形式进行，内容涵盖垃圾分类、节能减排等。'
        },
        {
          id: 13,
          name: '篮球联赛',
          organizer: '体育部',
          date: '2025-11-02',
          location: '体育馆',
          description: '各院系篮球队同场竞技。',
          detail: '篮球联赛分为小组赛和淘汰赛，冠亚季军将获得奖杯和奖品。'
        }
      ],
      filteredCampusActivityList: [],
      campusActivitySearchQuery: '',
      showCampusActivity: false,
      campusActivityDetailsVisible: false,
      currentCampusActivity: null,
      showExamTraining: false,
      examTrainingList: [
        {
          id: 1,
          name: '四六级英语考前培训',
          organizer: '外语系',
          date: '2025-12-01',
          location: '教学楼A101',
          description: '针对大学英语四六级考试的考前冲刺培训，涵盖听力、阅读、写作等模块。',
          detail: '本培训由资深英语教师主讲，内容包括历年真题讲解、答题技巧、模拟测试等，帮助同学们高效备考。'
        },
        {
          id: 2,
          name: '计算机等级考试辅导',
          organizer: '计算机学院',
          date: '2025-11-15',
          location: '机房B202',
          description: '全国计算机等级考试（NCRE）考前辅导，重点讲解考试难点。',
          detail: '辅导内容涵盖一级、二级考试知识点，提供上机实操演练和答疑环节。'
        },
        {
          id: 3,
          name: '教师资格证面试培训',
          organizer: '师范学院',
          date: '2025-10-20',
          location: '师范楼C303',
          description: '教师资格证面试技巧与实战演练，提升面试通过率。',
          detail: '邀请面试专家现场指导，模拟真实面试场景，帮助考生自信应对。'
        },
        // 新增10条
        {
          id: 4,
          name: '公务员考试申论写作班',
          organizer: '人文学院',
          date: '2025-12-10',
          location: '文科楼D201',
          description: '公务员考试申论高分技巧，真题精讲。',
          detail: '由申论阅卷专家主讲，系统梳理申论写作思路，提升实战能力。'
        },
        {
          id: 5,
          name: '考研数学冲刺营',
          organizer: '数学学院',
          date: '2025-11-28',
          location: '理科楼E101',
          description: '考研数学重点难点突破，模拟测试。',
          detail: '针对考研数学一、二、三，分模块讲解，配套全真模拟题。'
        },
        {
          id: 6,
          name: '雅思口语实战训练',
          organizer: '国际交流中心',
          date: '2025-12-05',
          location: '国际楼F305',
          description: '雅思口语高分技巧，外教一对一点评。',
          detail: '外教全程参与，模拟真实考试场景，提升口语表达能力。'
        },
        {
          id: 7,
          name: '注册会计师（CPA）考前串讲',
          organizer: '商学院',
          date: '2025-11-22',
          location: '商学楼G202',
          description: 'CPA各科目考点梳理，答题技巧。',
          detail: '邀请CPA高分学长学姐分享备考经验，梳理易错点。'
        },
        {
          id: 8,
          name: '普通话水平测试培训',
          organizer: '语言文字工作委员会',
          date: '2025-12-08',
          location: '语音室H101',
          description: '普通话测试流程与评分标准讲解。',
          detail: '模拟测试，逐句纠音，提升普通话水平。'
        },
        {
          id: 9,
          name: '法律职业资格考试讲座',
          organizer: '法学院',
          date: '2025-11-30',
          location: '法学楼I201',
          description: '法考重点难点解析，真题演练。',
          detail: '主讲老师为法考命题组成员，带你吃透考试大纲。'
        },
        {
          id: 10,
          name: '医师资格考试冲刺班',
          organizer: '医学院',
          date: '2025-12-12',
          location: '医学楼J301',
          description: '医师资格考试高频考点梳理。',
          detail: '结合最新考试政策，讲解高频考点与答题技巧。'
        },
        {
          id: 11,
          name: '日语能力测试（JLPT）N2强化',
          organizer: '外国语学院',
          date: '2025-11-18',
          location: '外语楼K102',
          description: 'JLPT N2词汇、语法、听力全方位提升。',
          detail: '日籍教师授课，配套真题训练，提升应试能力。'
        },
        {
          id: 12,
          name: '大学生创新创业大赛辅导',
          organizer: '创新创业中心',
          date: '2025-12-15',
          location: '创新楼L201',
          description: '创新创业大赛赛前辅导，项目路演技巧。',
          detail: '邀请创业导师指导项目BP撰写与路演答辩。'
        },
        {
          id: 13,
          name: '心理咨询师考试培训',
          organizer: '心理健康中心',
          date: '2025-11-25',
          location: '心理楼M101',
          description: '心理咨询师考试理论与实操辅导。',
          detail: '理论知识串讲+案例实操演练，助力顺利取证。'
        }
      ],
      filteredExamTrainingList: [],
      examTrainingSearchQuery: '',
      examTrainingDetailsVisible: false,
      currentExamTraining: null,
      showCampusFacility: false,
      facilityType: '全部',
      facilityTypeOptions: ['全部', '教室', '实验室', '其他'],
      facilityList: [
        { id: 1, name: 'A101', type: '教室', location: '教学楼A', status: '空闲', renting: false },
        { id: 2, name: 'A102', type: '教室', location: '教学楼A', status: '已租用', renting: false },
        { id: 3, name: 'B201', type: '教室', location: '教学楼B', status: '空闲', renting: false },
        { id: 4, name: 'B202', type: '教室', location: '教学楼B', status: '已租用', renting: false },
        { id: 5, name: 'C301', type: '实验室', location: '实验楼C', status: '空闲', renting: false },
        { id: 6, name: 'C302', type: '实验室', location: '实验楼C', status: '已租用', renting: false },
        { id: 7, name: 'D101', type: '实验室', location: '实验楼D', status: '空闲', renting: false },
        { id: 8, name: 'E101', type: '其他', location: '行政楼E', status: '空闲', renting: false },
        { id: 9, name: 'E102', type: '其他', location: '行政楼E', status: '已租用', renting: false },
        { id: 10, name: 'F201', type: '其他', location: '体育馆F', status: '空闲', renting: false },
      ],
      editPasswordVisible: false,
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
    sortedCampusActivityList() {
      // 按时间倒序排序，较新在上
      return [...this.campusActivityList].sort((a, b) => new Date(b.date) - new Date(a.date));
    },
    filteredFacilityList() {
      if (this.facilityType === '全部') return this.facilityList;
      return this.facilityList.filter(item => item.type === this.facilityType);
    },
  },
  methods: {
    // 返回登录页面
    goLogin() {
      const userId = this.$route.query.userId;
      if (userId) {
        axios.post(`http://localhost:8080/api/user/logout?userId=${userId}`)
          .then(() => {
            this.$router.push({ name: 'Login' });
          })
          .catch(error => {
            console.error('登出失败:', error);
            this.$router.push({ name: 'Login' });
          });
      } else {
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
      this.showCampusActivity = false;
      this.showExamTraining = false;
      this.showCampusFacility = false;
      
      if (menu === 'serviceCenter') {
        this.showServiceCenter = true;
      } else if (menu === 'majorInfo') {
        if (!this.userId) {
          await this.getCurrentUser();
        }
        const majorApi = 'http://localhost:8080/api/majorInfo/getMajorInfo';
        const majorRes = await axios.get(majorApi);
        let majors = Array.isArray(majorRes.data) ? majorRes.data : [];
        
        // 获取所有考试计划
        const testRes = await axios.get('http://localhost:8080/api/testInfo/getTestInfo');
        const allTests = Array.isArray(testRes.data) ? testRes.data : [];
        
        const stateKey = 'state' + this.userId;
        majors = majors.map(m => {
          // 查找对应的考试计划
          const relatedTest = allTests.find(t => Number(t.majorId) === Number(m.id));
          
          const isTestCompleted = relatedTest && relatedTest.state === '已考试';
          const isUndeclared = m[stateKey] === '未申报' || !m[stateKey];
          
          // 如果考试已完成但未申报
          const isDisabled = isTestCompleted && isUndeclared;
          
          return {
            ...m,
            state: m[stateKey] || '未申报',
            stateKey,
            disabled: isDisabled, 
            testStatus: relatedTest ? relatedTest.state : null
          };
        });
        
        this.majorInfoList = majors;
        this.filteredMajorInfoList = [];
        this.searchQuery = '';
        this.statusFilter = 'all';
        // 设置专业申报为可见
        this.showMajorInfo = true;
      } else if (menu === 'testInfo') {
        try {
          const majorApi = 'http://localhost:8080/api/majorInfo/getMajorInfo';
          const majorRes = await axios.get(majorApi);
          let majors = Array.isArray(majorRes.data) ? majorRes.data : [];
          console.log('所有专业:', majors);
          
          const stateKey = 'state' + this.userId;
          const filteredMajors = majors.filter(m => {
            const state = m[stateKey];
            return state === '已申报' || state === '已完成';
          });
          console.log('筛选后专业:', filteredMajors);
          
          const majorIds = filteredMajors.map(m => Number(m.id));
          console.log('专业id列表:', majorIds);
          
          if (majorIds.length === 0) {
            this.testInfoList = [];
            this.showTestInfo = true;
            console.log('当前用户没有已申报或已完成的专业');
            return;
          }
          
          const testRes = await axios.get('http://localhost:8080/api/testInfo/getTestInfo');
          const allTests = Array.isArray(testRes.data) ? testRes.data : [];
          console.log('所有考试计划:', allTests);
          
          this.testInfoList = allTests.filter(t => majorIds.includes(Number(t.majorId)));
          console.log('筛选后考试计划:', this.testInfoList);
          
          this.testSearchQuery = '';
          this.testStatusFilter = 'all';
          this.filteredTestInfoList = [];
          
          this.showTestInfo = true;
        } catch (error) {
          console.error('获取考试计划出错:', error);
          this.testInfoList = [];
          ElMessage.error('获取考试计划失败');
        }
      } else if (menu === 'internship') {
        if (!this.userId) {
          await this.getCurrentUser();
        }
        
        try {
          const internshipRes = await axios.get('http://localhost:8080/api/internshipInfo/getInternshipInfo');
          let internships = Array.isArray(internshipRes.data) ? internshipRes.data : [];
          
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
        this.competitionSearchQuery = '';
        this.competitionTypeFilter = 'all';
        this.filteredCompetitionList = [...this.competitionList];
        this.showCompetition = true;
      } else if (menu === 'campusActivity') {
        this.showCampusActivity = true;
        this.showServiceCenter = false;
        this.filteredCampusActivityList = [];
        this.campusActivitySearchQuery = '';
        this.campusActivityDetailsVisible = false;
      } else if (menu === 'contact') {
        this.contactDialogVisible = true;
      } else if (menu === 'examTraining') {
        this.showExamTraining = true;
        this.showServiceCenter = false;
        this.examTrainingSearchQuery = '';
        this.filteredExamTrainingList = [];
        this.examTrainingDetailsVisible = false;
      } else if (menu === 'campusFacility') {
        this.showCampusFacility = true;
        this.showServiceCenter = false;
        this.facilityType = '全部';
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
      if (row.disabled) return 'disabled-row'; // 添加已考试但未申报的行样式
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
      let filtered = [...this.majorInfoList];
      
      if (this.statusFilter !== 'all') {
        filtered = filtered.filter(item => item.state === this.statusFilter);
      }
      
      if (this.searchQuery.trim()) {
        const query = this.searchQuery.trim().toLowerCase();
        filtered = filtered.filter(item => {
          return (
            item.id.toString().includes(query) || 
            item.name.toLowerCase().includes(query)
          );
        });
      }
      
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
      if (service.name === '打印准考证') {
        this.showAdmissionCard = true;
        return;
      }
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
      } else if (service.name === '校园活动') {
        this.showCampusActivity = true;
        this.showServiceCenter = false;
        this.filteredCampusActivityList = [];
        this.campusActivitySearchQuery = '';
        this.campusActivityDetailsVisible = false;
      } else if (service.name === '联系我们') {
        this.contactDialogVisible = true;
      } else if (service.name === '考试培训') {
        this.showExamTraining = true;
        this.showServiceCenter = false;
        this.examTrainingSearchQuery = '';
        this.filteredExamTrainingList = [];
        this.examTrainingDetailsVisible = false;
      } else if (service.name === '校园设施') {
        this.showCampusFacility = true;
        this.showServiceCenter = false;
        this.facilityType = '全部';
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
      
      window.open(url, '_blank');
    },
    
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
      let filtered = [...this.materialsList];
      
      if (this.materialsSearchQuery.trim()) {
        const query = this.materialsSearchQuery.trim().toLowerCase();
        filtered = filtered.filter(item => 
          (item.title && item.title.toLowerCase().includes(query)) || 
          (item.author && item.author.toLowerCase().includes(query))
        );
      }
      
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
          this.educationStaff = Array.isArray(res.data) ? res.data
            .filter(user => user.userType === 2 || user.user_type === 2)
            .map(user => ({
              id: user.id,
              username: user.username,
              realName: user.realName || user.real_name,
              phone: user.phone,
              email: user.email || '',
              avatar: user.avatar || user.avatar_url || '/src/assets/default-avatar.jpg',
              onlineStatus: user.onlineStatus || user.online_status || 0,
              profession: user.id === 5 ? '软件工程教师' :
                          user.id === 6 ? '打野' :
                          user.id === 7 ? '上单' :
                          user.id === 8 ? '辅助' :
                          user.id === 9 ? '上单' :
                          user.id === 10 ? '上单' :
                          user.id === 11 ? '中单' :
                          (user.profession || '教师')
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
      
      this.currentStaff = staff;
      this.chatMessages = [];
      
      setTimeout(() => {
        this.consultationDialogVisible = true;
        
        this.scrollToBottom();
      }, 1000);
    },
    
    sendMessage() {
      if (!this.newMessage.trim()) return;
      
      this.chatMessages.push({
        sender: 'student',
        text: this.newMessage.trim(),
        time: this.getCurrentTime()
      });
      
      const sentMessage = this.newMessage.trim();
      this.newMessage = '';
      
      setTimeout(() => {
        ElMessage({
          type: 'info',
          message: `${this.currentStaff.realName}正在输入...`,
          duration: 1500,
        });
        
        setTimeout(() => {
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
            setTimeout(() => {
              this.consultationDialogVisible = false;
            }, 2000);
          } else {
            responseText = '我了解了你的问题。建议你可以查阅相关的学习资料或者参加我们的线下辅导课程。你还有其他具体的问题吗？';
          }
          
          this.chatMessages.push({
            sender: 'staff',
            text: responseText,
            time: this.getCurrentTime()
          });
          
          this.scrollToBottom();
        }, 1500);
      }, 500);
    },
    
    getCurrentTime() {
      const now = new Date();
      const hours = now.getHours().toString().padStart(2, '0');
      const minutes = now.getMinutes().toString().padStart(2, '0');
      return `${hours}:${minutes}`;
    },

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
      let filtered = [...this.competitionList];
      
      if (this.competitionTypeFilter !== 'all') {
        filtered = filtered.filter(item => item.type === this.competitionTypeFilter);
      }
      
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
        case '科技创新': return 'danger';
        case '学科竞赛': return 'warning';
        case '文化艺术': return 'success';
        default: return 'primary';
      }
    },
    viewCompetitionDetails(competition) {
      this.currentCompetition = competition;
      this.competitionDetailsVisible = true;
    },
    openCompetitionLink(competition) {

      window.open(competition.registrationUrl, '_blank');
    },
    handleCampusActivitySearch() {
      this.applyCampusActivityFilters();
    },
    applyCampusActivityFilters() {
      let filtered = [...this.campusActivityList];
      if (this.campusActivitySearchQuery.trim()) {
        const query = this.campusActivitySearchQuery.trim().toLowerCase();
        filtered = filtered.filter(item =>
          (item.name && item.name.toLowerCase().includes(query)) ||
          (item.organizer && item.organizer.toLowerCase().includes(query))
        );
      }
      if (!this.campusActivitySearchQuery.trim()) {
        this.filteredCampusActivityList = [];
        return;
      }
      this.filteredCampusActivityList = filtered;
    },
    viewCampusActivityDetails(activity) {
      this.currentCampusActivity = activity;
      this.campusActivityDetailsVisible = true;
    },
    backToCampusActivityList() {
      this.campusActivityDetailsVisible = false;
    },
    backToServiceCenterFromActivity() {
      this.showCampusActivity = false;
      this.showServiceCenter = true;
    },
    handleExamTrainingSearch() {
      this.applyExamTrainingFilters();
    },
    applyExamTrainingFilters() {
      let filtered = [...this.examTrainingList];
      if (this.examTrainingSearchQuery.trim()) {
        const query = this.examTrainingSearchQuery.trim().toLowerCase();
        filtered = filtered.filter(item =>
          (item.name && item.name.toLowerCase().includes(query)) ||
          (item.organizer && item.organizer.toLowerCase().includes(query))
        );
      }
      if (!this.examTrainingSearchQuery.trim()) {
        this.filteredExamTrainingList = [];
        return;
      }
      this.filteredExamTrainingList = filtered;
    },
    viewExamTrainingDetails(training) {
      this.currentExamTraining = training;
      this.examTrainingDetailsVisible = true;
    },
    backToExamTrainingList() {
      this.examTrainingDetailsVisible = false;
    },
    backToServiceCenterFromExamTraining() {
      this.showExamTraining = false;
      this.showServiceCenter = true;
    },
    handleFacilityTypeChange(type) {
      this.facilityType = type;
    },
    toggleRenting(row) {
      row.renting = !row.renting;
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
    // 新增：获取竞赛数据
    axios.get('http://localhost:8080/api/competition/getCompetitions').then(res => {
      if (Array.isArray(res.data)) {
        // 字段兼容处理
        this.competitionList = res.data.map(item => ({
          ...item,
          type: item.category, // 保持原有过滤逻辑
          registrationUrl: item.signupLink // 保持原有报名链接字段
        }));
        this.filteredCompetitionList = [...this.competitionList];
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
  font-size: 1.2rem;
  padding: 8px 16px;
  min-height: 36px;
  border-radius: 8px;
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
  color: #666;
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
  min-height: 220px;
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

.tutor-profession {
  font-size: 1.3rem;
  color: #409EFF;
  margin-bottom: 10px;
  font-weight: bold;
}

.tutor-meta {
  margin-bottom: 10px;
}

.tutor-meta p {
  margin: 5px 0;
  color: #666;
  font-size: 1.15rem;
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

.chat-input-row {
  display: flex;
  align-items: center;
  gap: 18px;
  margin-top: 18px;
}

.chat-input-fixed {
  flex: 1;
  font-size: 1.25rem !important;
  height: 54px !important;
  line-height: 54px !important;
  min-height: 54px !important;
  max-height: 54px !important;
  resize: none !important;
}

.chat-input-fixed .el-input__inner {
  font-size: 1.25rem !important;
  height: 54px !important;
  line-height: 54px !important;
  min-height: 54px !important;
  max-height: 54px !important;
  resize: none !important;
}

.send-button-fixed {
  font-size: 1.25rem !important;
  height: 54px !important;
  min-width: 120px;
  padding: 0 32px !important;
  display: flex;
  align-items: center;
  justify-content: center;
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

.disabled-row {
  opacity: 0.6;
  background-color: #f5f5f5 !important;
}
.disabled-row:hover td {
  background-color: #f5f5f5 !important;
}

.admission-card-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 0 10px 0;
}
.admission-card-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 18px;
}
.admission-table {
  width: 100%;
  max-width: 800px;
  border-collapse: separate;
  border-spacing: 0;
  font-size: 1.3rem;
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(64,158,255,0.18);
  overflow: hidden;
}
.admission-table tr {
  height: 56px;
}
.admission-label {
  background: #f5f7fa;
  color: #409EFF;
  font-weight: bold;
  text-align: right;
  padding: 0 24px;
  font-size: 1.15rem;
  border-bottom: 2px solid #e4e7ed;
  min-width: 100px;
}
.admission-value {
  background: #fff;
  color: #333;
  text-align: left;
  padding: 0 24px;
  font-size: 1.15rem;
  border-bottom: 2px solid #e4e7ed;
  min-width: 140px;
}
.admission-table tr:last-child .admission-label,
.admission-table tr:last-child .admission-value {
  border-bottom: none;
}

.tutor-info {
  flex: 1;
  position: relative;
  padding-bottom: 60px;
}
.tutor-row {
  display: flex;
  align-items: center;
  gap: 18px;
  margin-bottom: 8px;
}
.tutor-row-1 {
  font-size: 2.1rem;
  font-weight: bold;
  color: #333;
  gap: 24px;
}
.tutor-row-1 .tutor-profession {
  font-size: 1.3rem;
  color: #409EFF;
  font-weight: bold;
}
.tutor-row-2 {
  font-size: 1.45rem;
  color: #222;
  font-weight: bold;
}
.tutor-phone {
  font-size: 1.45rem;
  color: #222;
  font-weight: bold;
}
.tutor-row-3 {
  font-size: 1.25rem;
  color: #555;
}
.tutor-email {
  font-size: 1.25rem;
  color: #555;
}
.tutor-action-row {
  display: flex;
  align-items: center;
  gap: 18px;
  margin-top: 10px;
}
.tutor-status-tag {
  font-size: 1.25rem !important;
  padding: 8px 22px !important;
  font-weight: bold;
  border-radius: 8px !important;
  height: 2.2em;
  display: flex;
  align-items: center;
}
.tutor-action-row .el-button {
  font-size: 1.15rem !important;
  padding: 0.6em 1.5em !important;
}

/* 职业右上角大号 */
.tutor-profession-fixed {
  position: absolute;
  top: 18px;
  right: 32px;
  font-size: 2.1rem;
  color: #409EFF;
  font-weight: bold;
  z-index: 2;
}

/* 在线和咨询右下角大号 */
.tutor-action-row-fixed {
  position: absolute;
  right: 16px;
  bottom: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  z-index: 2;
}

.tutor-status-tag-fixed {
  font-size: 1.2rem !important;
  padding: 6px 14px !important;
  font-weight: bold;
  border-radius: 8px !important;
  height: 2em;
  display: flex;
  align-items: center;
}

.tutor-consult-btn {
  font-size: 1.1rem !important;
  padding: 0.4em 1.2em !important;
}

.campus-activity-container {
  padding: 20px;
}
.campus-activity-header {
  padding: 20px;
  background: linear-gradient(135deg, #43cea2 0%, #185a9d 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 20px;
  position: relative;
}
.campus-activity-header h2 {
  font-size: 2rem;
  margin: 0 0 15px 0;
  text-align: center;
}
.campus-activity-header p {
  font-size: 1.2rem;
  opacity: 0.8;
  margin: 0;
  text-align: center;
}
.campus-activity-header-controls {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  position: relative;
  height: 60px;
}
.campus-activity-header-controls h2 {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  margin: 0;
  z-index: 1;
  font-size: 2rem;
  text-align: center;
}
.campus-activity-header-controls .left-controls {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(0%);
}
.campus-activity-header-controls .right-controls {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(0%);
}
.campus-activity-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.campus-activity-card {
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  padding: 20px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  cursor: pointer;
}
.campus-activity-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transform: translateY(-5px);
}
.campus-activity-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
  width: 100%;
}
.campus-activity-title {
  font-size: 1.6rem;
  font-weight: bold;
  color: #333;
}
.campus-activity-info {
  display: flex;
  gap: 30px;
  margin-bottom: 12px;
  color: #666;
  font-size: 1.1rem;
}
.campus-activity-date, .campus-activity-location {
  display: flex;
  align-items: center;
  gap: 8px;
}
.campus-activity-description {
  font-size: 1.1rem;
  color: #666;
  line-height: 1.6;
  margin-bottom: 10px;
  text-align: left;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  width: 100%;
}
.campus-activity-details-container {
  padding: 0 10px;
}
.dialog-footer-fixed {
  position: absolute;
  right: 32px;
  bottom: 24px;
  z-index: 10;
  background: transparent;
}
.arrange-dialog-beauty >>> .el-dialog {
  position: relative;
  padding-bottom: 80px !important;
}
.internship-detail-table-wrapper {
  padding: 24px 0 0 0;
  display: flex;
  justify-content: center;
}
.internship-detail-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 18px;
  font-size: 1.1rem;
}
.internship-detail-table .label {
  font-weight: bold;
  color: #333;
  width: 120px;
  padding: 8px 12px;
  font-size: 1.1rem;
}
.internship-detail-table .value {
  color: #222;
  padding: 8px 12px;
  font-size: 1.1rem;
}
.detail-tag {
  font-size: 1.1rem !important;
  padding: 8px 18px !important;
  border-radius: 8px !important;
}
.exam-training-container {
  padding: 20px;
}
.exam-training-header {
  text-align: center;
  margin-bottom: 40px;
  background: linear-gradient(135deg, #2980b9 0%, #6dd5fa 100%);
  padding: 20px 20px 40px 20px;
  border-radius: 8px;
  color: white;
  position: relative;
}
.exam-training-header h2 {
  font-size: 2rem;
  margin: 0 0 15px 0;
  text-align: center;
}
.exam-training-header p {
  font-size: 1.2rem;
  opacity: 0.8;
  margin: 0;
}
.exam-training-header-controls {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
  position: relative;
  height: 60px;
}
.exam-training-header-controls h2 {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  margin: 0;
  z-index: 1;
  top: 10%;
}
.exam-training-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(500px, 1fr));
  gap: 25px;
}
.exam-training-card {
  display: flex;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  padding: 20px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  min-height: 220px;
}
.exam-training-card:hover {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transform: translateY(-5px);
}
.exam-training-avatar {
  position: relative;
  flex: 0 0 120px;
  margin-right: 20px;
  border-radius: 10px;
  overflow: hidden;
  height: 120px;
}
.exam-training-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.exam-training-info {
  flex: 1;
  position: relative;
  padding-bottom: 60px;
}
.exam-training-name {
  font-size: 1.5rem;
  margin: 0 0 10px;
  color: #333;
}
.exam-training-profession {
  font-size: 1.3rem;
  color: #409EFF;
  margin-bottom: 10px;
  font-weight: bold;
}
.exam-training-meta {
  margin-bottom: 10px;
}
.exam-training-meta p {
  margin: 5px 0;
  color: #666;
  font-size: 1.15rem;
  display: flex;
  align-items: center;
}
.exam-training-text {
  margin-left: 8px;
}
.exam-training-action {
  position: absolute;
  bottom: 0;
  right: 0;
}
.exam-training-disabled-btn {
  opacity: 0.6;
}
.exam-training-details-container {
  padding: 0 10px;
}
.exam-training-details-header {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  gap: 30px;
}
.exam-training-cover-large {
  width: 180px;
  height: 250px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
.exam-training-cover-large img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.exam-training-title-section {
  flex: 1;
}
.exam-training-title-section h2 {
  font-size: 2.2rem;
  margin: 0 0 10px 0;
  color: #303133;
}
.exam-training-title-section p {
  font-size: 1.3rem;
  color: #606266;
  margin: 0;
}
.exam-training-details-table {
  margin-bottom: 30px;
}
.exam-training-details-table .el-table__row td:first-child {
  font-weight: bold;
  background-color: #f5f7fa;
}
.exam-training-description-section {
  margin-bottom: 30px;
}
.exam-training-description-section h3 {
  font-size: 1.5rem;
  margin-bottom: 15px;
  color: #303133;
}
.exam-training-description-section p {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #666;
  white-space: pre-line;
}
.exam-training-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
.exam-training-details-btn {
  font-size: 1.5rem !important;
  padding: 0.8em 2.2em !important;
}
.exam-training-back-button {
  font-size: 1.5rem !important;
  padding: 0.6em 1.5em !important;
}
.campus-facility-container {
  padding: 20px;
}
.campus-facility-header {
  background: linear-gradient(135deg, #43cea2 0%, #185a9d 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 20px;
  position: relative;
  padding: 20px 20px 40px 20px;
}
.campus-facility-header h2 {
  font-size: 2rem;
  margin: 0;
  text-align: center;
}
.campus-facility-header p {
  font-size: 1.2rem;
  opacity: 0.8;
  margin: 0;
  text-align: center;
}
.campus-facility-header .campus-activity-header-controls {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
  position: relative;
  height: 60px;
}
.campus-facility-header .left-controls {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(0%);
}
.campus-facility-header .right-controls {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(0%);
}
.campus-facility-header h2 {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  margin: 0;
  z-index: 1;
  font-size: 2rem;
  text-align: center;
}
.facility-type-select {
  min-width: 180px;
}
.facility-filter-tag {
  font-size: 1.2rem !important;
  padding: 8px 16px !important;
  margin-left: 8px;
  cursor: pointer;
  border-radius: 8px !important;
  transition: all 0.2s;
}
.facility-filter-tag.active-filter {
  border-width: 2px;
  font-weight: 700;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-3px);
}
.facility-filter-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin: 18px 0 0 0;
}
.rent-btn-flip-wrapper {
  perspective: 600px;
  display: inline-block;
}
.rent-btn-flip {
  width: 120px;
  height: 48px;
  position: relative;
  transform-style: preserve-3d;
  transition: transform 0.5s cubic-bezier(0.4, 0.2, 0.2, 1);
  cursor: pointer;
  display: inline-block;
}
.rent-btn-flip.flipped {
  transform: rotateY(180deg);
}
.rent-btn-face {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  backface-visibility: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
.rent-btn-front {
  z-index: 2;
}
.rent-btn-back {
  transform: rotateY(180deg);
  z-index: 1;
}
.rent-btn-inner {
  width: 100%;
  height: 100%;
  font-size: 1.5rem !important;
}
</style> 