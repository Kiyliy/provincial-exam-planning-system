-- 创建教师咨询表
CREATE TABLE IF NOT EXISTS teacher_consultation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    teacher_name VARCHAR(50) NOT NULL COMMENT '教师姓名',
    specialization VARCHAR(100) NOT NULL COMMENT '教师专业',
    online_status ENUM('在线', '离线') DEFAULT '离线' COMMENT '在线状态',
    phone VARCHAR(20) NOT NULL COMMENT '手机号码',
    email VARCHAR(100) NOT NULL COMMENT '邮箱地址',
    available_time VARCHAR(200) COMMENT '可咨询时间',
    description TEXT COMMENT '教师简介',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 添加索引
CREATE INDEX idx_teacher_name ON teacher_consultation(teacher_name);
CREATE INDEX idx_specialization ON teacher_consultation(specialization);
CREATE INDEX idx_online_status ON teacher_consultation(online_status);

-- 插入示例数据
INSERT INTO teacher_consultation 
    (teacher_name, specialization, online_status, phone, email, available_time, description) 
VALUES 
    ('张明', '高等数学', '在线', '13800138001', 'zhangming@example.com', '周一至周五 9:00-11:00, 14:00-16:00', '数学系教授，主要研究方向为微分方程，拥有20年教学经验'),
    ('李华', '大学物理', '离线', '13800138002', 'lihua@example.com', '周二、周四 10:00-12:00, 15:00-17:00', '物理系副教授，专注于理论物理和量子力学领域的教学与研究'),
    ('王芳', '计算机科学', '在线', '13800138003', 'wangfang@example.com', '周一、周三、周五 13:00-17:00', '计算机系教授，研究方向包括人工智能和机器学习，有多项国家级科研项目'),
    ('刘强', '英语文学', '离线', '13800138004', 'liuqiang@example.com', '周一至周五 9:00-15:00', '英语系教授，研究方向为英美文学，曾在国外高校任教多年'),
    ('陈静', '经济学', '在线', '13800138005', 'chenjing@example.com', '周二、周五 14:00-18:00', '经济学院教授，专注于宏观经济理论研究，曾担任多个国家级经济规划项目负责人'); 