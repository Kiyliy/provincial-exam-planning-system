-- 创建教师咨询表
CREATE TABLE IF NOT EXISTS teacher_consult (
    id INT PRIMARY KEY,                    -- 教师ID，关联到sys_user表
    teacher_name VARCHAR(50) NOT NULL,     -- 教师姓名
    specialty VARCHAR(100) NOT NULL,       -- 教师专业
    online_status VARCHAR(10) DEFAULT '离线',  -- 在线状态（在线/离线）
    phone VARCHAR(20) NOT NULL,            -- 手机号码
    email VARCHAR(100) NOT NULL,           -- 邮箱地址
    FOREIGN KEY (id) REFERENCES sys_user(id)  -- 外键约束，关联到sys_user表
);

-- 插入初始数据（ID为5,6,7,8,9的教师）
INSERT INTO teacher_consult (id, teacher_name, specialty, online_status, phone, email) VALUES
(5, '张教授', '高等数学', '在线', '13800000001', 'zhang@example.com'),
(6, '李教授', '计算机科学', '离线', '13800000002', 'li@example.com'),
(7, '王教授', '物理学', '在线', '13800000003', 'wang@example.com'),
(8, '赵教授', '英语文学', '离线', '13800000004', 'zhao@example.com'),
(9, '刘教授', '经济学', '在线', '13800000005', 'liu@example.com');

-- 创建索引以提高查询性能
CREATE INDEX idx_teacher_consult_online ON teacher_consult(online_status);
CREATE INDEX idx_teacher_consult_specialty ON teacher_consult(specialty); 