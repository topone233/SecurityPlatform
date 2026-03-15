-- 测试用例库表
CREATE TABLE IF NOT EXISTS test_case (
    id BIGSERIAL PRIMARY KEY,
    case_name VARCHAR(255) NOT NULL,
    vulnerability_type VARCHAR(100) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    applicable_components JSONB,
    applicable_danger_functions JSONB,
    detection_method VARCHAR(50),
    test_steps TEXT,
    payload_template TEXT,
    expected_result TEXT,
    references TEXT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 配置检查清单表
CREATE TABLE IF NOT EXISTS config_check_item (
    id BIGSERIAL PRIMARY KEY,
    item_name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    config_key_pattern VARCHAR(255) NOT NULL,
    check_rule TEXT,
    expected_value VARCHAR(500),
    unsafe_values JSONB,
    risk_level VARCHAR(20) NOT NULL,
    description TEXT,
    remediation TEXT,
    standard_reference VARCHAR(255),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 测试清单表 (为每个制品生成的测试任务清单)
CREATE TABLE IF NOT EXISTS test_checklist (
    id BIGSERIAL PRIMARY KEY,
    artifact_id BIGINT NOT NULL,
    test_case_id BIGINT,
    case_name VARCHAR(255) NOT NULL,
    vulnerability_type VARCHAR(100) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    matched_component VARCHAR(255),
    matched_danger_function VARCHAR(255),
    matched_config_key VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'TODO',
    exclusion_reason TEXT,
    exclusion_rule_id BIGINT,
    test_result TEXT,
    test_notes TEXT,
    tested_by VARCHAR(100),
    tested_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_checklist_artifact FOREIGN KEY (artifact_id) REFERENCES artifact(id) ON DELETE CASCADE,
    CONSTRAINT fk_checklist_test_case FOREIGN KEY (test_case_id) REFERENCES test_case(id) ON DELETE SET NULL
);

-- 风险排除规则表
CREATE TABLE IF NOT EXISTS risk_exclusion_rule (
    id BIGSERIAL PRIMARY KEY,
    rule_name VARCHAR(255) NOT NULL,
    rule_type VARCHAR(50) NOT NULL,
    applicable_components JSONB,
    applicable_function_types JSONB,
    exclusion_condition TEXT,
    exclusion_reason TEXT,
    confidence_level VARCHAR(20),
    requires_manual_review BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    priority INTEGER DEFAULT 100,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 风险排除记录表
CREATE TABLE IF NOT EXISTS risk_exclusion (
    id BIGSERIAL PRIMARY KEY,
    artifact_id BIGINT NOT NULL,
    danger_function_id BIGINT,
    rule_id BIGINT,
    function_name VARCHAR(255),
    function_type VARCHAR(100),
    class_name VARCHAR(255),
    file_path VARCHAR(500),
    line_number INTEGER,
    original_risk_level VARCHAR(20),
    exclusion_reason TEXT,
    confidence_level VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reviewer VARCHAR(100),
    review_notes TEXT,
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_exclusion_artifact FOREIGN KEY (artifact_id) REFERENCES artifact(id) ON DELETE CASCADE,
    CONSTRAINT fk_exclusion_danger_function FOREIGN KEY (danger_function_id) REFERENCES danger_function(id) ON DELETE SET NULL,
    CONSTRAINT fk_exclusion_rule FOREIGN KEY (rule_id) REFERENCES risk_exclusion_rule(id) ON DELETE SET NULL
);

-- 创建索引
CREATE INDEX idx_test_case_vulnerability_type ON test_case(vulnerability_type);
CREATE INDEX idx_test_case_enabled ON test_case(enabled);
CREATE INDEX idx_config_check_category ON config_check_item(category);
CREATE INDEX idx_config_check_enabled ON config_check_item(enabled);
CREATE INDEX idx_checklist_artifact_id ON test_checklist(artifact_id);
CREATE INDEX idx_checklist_status ON test_checklist(status);
CREATE INDEX idx_checklist_vulnerability_type ON test_checklist(vulnerability_type);
CREATE INDEX idx_exclusion_rule_type ON risk_exclusion_rule(rule_type);
CREATE INDEX idx_exclusion_rule_enabled ON risk_exclusion_rule(enabled);
CREATE INDEX idx_exclusion_rule_priority ON risk_exclusion_rule(priority);
CREATE INDEX idx_exclusion_artifact_id ON risk_exclusion(artifact_id);
CREATE INDEX idx_exclusion_danger_function_id ON risk_exclusion(danger_function_id);
CREATE INDEX idx_exclusion_status ON risk_exclusion(status);

-- 插入预置测试用例数据
INSERT INTO test_case (case_name, vulnerability_type, risk_level, applicable_components, applicable_danger_functions, detection_method, test_steps, enabled) VALUES
('SQL注入测试 - MyBatis', 'SQL_INJECTION', 'HIGH', '["mybatis", "mybatis-plus", "mybatis-spring"]', '["SQL_EXECUTION", "DYNAMIC_SQL"]', 'MANUAL', '1. 识别所有使用${}的SQL语句\n2. 构造测试payload\n3. 验证是否存在注入', TRUE),
('SQL注入测试 - JDBC', 'SQL_INJECTION', 'HIGH', '["jdbc", "mysql-connector", "oracle-jdbc"]', '["SQL_EXECUTION"]', 'MANUAL', '1. 识别字符串拼接SQL\n2. 构造注入payload\n3. 验证注入点', TRUE),
('XSS测试 - 存储型', 'XSS', 'MEDIUM', '["spring-mvc", "servlet", "thymeleaf", "freemarker"]', '[]', 'MANUAL', '1. 识别用户输入存储点\n2. 构造XSS payload\n3. 验证存储和回显', TRUE),
('命令注入测试', 'COMMAND_INJECTION', 'CRITICAL', '[]', '["COMMAND_EXECUTION", "PROCESS_BUILDER"]', 'MANUAL', '1. 识别Runtime.exec调用\n2. 分析参数来源\n3. 构造命令注入payload', TRUE),
('反序列化漏洞测试', 'DESERIALIZATION', 'CRITICAL', '[]', '["DESERIALIZATION"]', 'MANUAL', '1. 识别反序列化入口\n2. 分析序列化数据来源\n3. 构造恶意序列化对象', TRUE),
('文件上传漏洞测试', 'FILE_UPLOAD', 'HIGH', '["commons-fileupload", "spring-mvc"]', '["FILE_IO", "FILE_WRITE"]', 'MANUAL', '1. 识别文件上传接口\n2. 测试文件类型校验\n3. 尝试上传恶意文件', TRUE),
('SSRF测试', 'SSRF', 'HIGH', '["httpclient", "okhttp", "resttemplate"]', '["HTTP_REQUEST"]', 'MANUAL', '1. 识别外部请求接口\n2. 测试内网地址访问\n3. 验证协议限制', TRUE),
('敏感信息泄露测试', 'INFO_DISCLOSURE', 'MEDIUM', '[]', '["LOGGING", "DEBUG_INFO"]', 'MANUAL', '1. 检查错误页面\n2. 检查调试接口\n3. 检查日志输出', TRUE),
('认证绕过测试', 'AUTH_BYPASS', 'HIGH', '["spring-security", "shiro"]', '[]', 'MANUAL', '1. 分析认证机制\n2. 测试绕过方式\n3. 验证权限控制', TRUE),
('XXE测试', 'XXE', 'HIGH', '["jaxb", "dom4j", "xstream"]', '["XML_PARSER"]', 'MANUAL', '1. 识别XML解析入口\n2. 构造XXE payload\n3. 验证外部实体解析', TRUE);

-- 插入预置配置检查项数据
INSERT INTO config_check_item (item_name, category, config_key_pattern, check_rule, expected_value, unsafe_values, risk_level, description, remediation, standard_reference, enabled) VALUES
('调试模式检查', 'DEBUG', 'debug', 'BOOLEAN_CHECK', 'false', '["true"]', 'HIGH', '生产环境不应开启调试模式', '关闭调试模式: debug=false', 'OWASP-Config-001', TRUE),
('数据库密码明文检查', 'DATABASE', 'password', 'PLAINTEXT_CHECK', NULL, NULL, 'CRITICAL', '数据库密码不应明文存储', '使用加密配置或密钥管理服务', 'OWASP-Config-002', TRUE),
('密钥明文检查', 'SECURITY', 'secret', 'PLAINTEXT_CHECK', NULL, NULL, 'CRITICAL', '密钥不应明文存储', '使用加密配置或密钥管理服务', 'OWASP-Config-003', TRUE),
('JWT密钥检查', 'SECURITY', 'jwt.secret', 'LENGTH_CHECK', '>=256bits', NULL, 'HIGH', 'JWT密钥长度应足够', '使用至少256位的强密钥', 'OWASP-Config-004', TRUE),
('跨域配置检查', 'SECURITY', 'cors', 'CORS_CHECK', 'restricted', '["*"]', 'MEDIUM', 'CORS不应允许所有来源', '限制允许的来源域名', 'OWASP-Config-005', TRUE),
('SQL日志检查', 'DATABASE', 'show-sql', 'BOOLEAN_CHECK', 'false', '["true"]', 'MEDIUM', '生产环境不应开启SQL日志', '关闭SQL日志: show-sql=false', 'OWASP-Config-006', TRUE),
('Actuator端点检查', 'ACTUATOR', 'management.endpoints.web.exposure', 'ACTUATOR_CHECK', 'limited', '["*"]', 'HIGH', '不应暴露所有actuator端点', '限制暴露的端点列表', 'OWASP-Config-007', TRUE),
('文件上传大小检查', 'UPLOAD', 'max-file-size', 'SIZE_CHECK', '<=100MB', NULL, 'MEDIUM', '文件上传大小应有限制', '设置合理的文件大小限制', 'OWASP-Config-008', TRUE),
('Session超时检查', 'SESSION', 'session.timeout', 'TIMEOUT_CHECK', '<=30min', NULL, 'MEDIUM', 'Session超时时间应合理', '设置合理的session超时时间', 'OWASP-Config-009', TRUE),
('SSL配置检查', 'SSL', 'ssl.enabled', 'BOOLEAN_CHECK', 'true', '["false"]', 'HIGH', '生产环境应启用SSL', '启用SSL: ssl.enabled=true', 'OWASP-Config-010', TRUE);

-- 插入预置风险排除规则
INSERT INTO risk_exclusion_rule (rule_name, rule_type, applicable_components, applicable_function_types, exclusion_condition, exclusion_reason, confidence_level, requires_manual_review, priority, enabled) VALUES
('安全的日志输出', 'LOGGING', NULL, '["LOGGING"]', 'function_name LIKE "log.info" OR function_name LIKE "log.debug" OR function_name LIKE "logger.info"', '日志输出函数，无安全风险', 'HIGH', FALSE, 100, TRUE),
('测试代码中的危险函数', 'TEST_CODE', NULL, NULL, 'class_name LIKE "%Test" OR class_name LIKE "%Tests" OR file_path LIKE "%/test/%"', '测试代码中的危险函数不构成生产风险', 'HIGH', FALSE, 90, TRUE),
('静态工具方法调用', 'STATIC_METHOD', NULL, '["COMMAND_EXECUTION"]', 'class_name LIKE "Runtime" AND method_name NOT LIKE "exec"', '非exec方法的Runtime调用无风险', 'MEDIUM', TRUE, 80, TRUE),
('配置文件中的命令执行', 'CONFIG_FILE', NULL, '["COMMAND_EXECUTION"]', 'file_path LIKE "%.properties" OR file_path LIKE "%.yml"', '配置文件中的引用通常无注入风险', 'LOW', TRUE, 70, TRUE),
('已过滤的SQL执行', 'SQL_FILTERED', '["mybatis", "jdbc"]', '["SQL_EXECUTION"]', 'method_name LIKE "%PreparedStatement%" OR method_name LIKE "%NamedParameter%"', '使用预编译语句，SQL注入风险较低', 'MEDIUM', TRUE, 60, TRUE),
('安全的文件读取', 'FILE_READ_SAFE', NULL, '["FILE_IO"]', 'function_name LIKE "readLine" AND file_path NOT LIKE "%upload%"', '读取固定路径文件，风险较低', 'MEDIUM', TRUE, 50, TRUE),
('内部服务调用', 'INTERNAL_CALL', NULL, '["HTTP_REQUEST"]', 'url LIKE "%.internal%" OR url LIKE "%.local%"', '内部服务调用，SSRF风险较低', 'MEDIUM', TRUE, 40, TRUE),
('已知安全库使用', 'SAFE_LIBRARY', '["spring-security", "shiro"]', NULL, 'component_name IN ["spring-security-core", "shiro-core"]', '使用安全框架处理认证授权', 'HIGH', FALSE, 30, TRUE);