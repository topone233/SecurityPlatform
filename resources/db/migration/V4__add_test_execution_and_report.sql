-- 测试执行记录表
CREATE TABLE IF NOT EXISTS test_execution (
    id BIGSERIAL PRIMARY KEY,
    artifact_id BIGINT NOT NULL,
    checklist_id BIGINT,
    test_case_id BIGINT,
    execution_name VARCHAR(255) NOT NULL,
    vulnerability_type VARCHAR(100),
    execution_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    execution_type VARCHAR(20) NOT NULL DEFAULT 'MANUAL',
    executor VARCHAR(100),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration_ms BIGINT,
    test_result VARCHAR(20),
    severity VARCHAR(20),
    reproducible BOOLEAN,
    environment_info TEXT,
    test_data TEXT,
    actual_result TEXT,
    expected_result TEXT,
    evidence_files JSONB,
    log_output TEXT,
    error_message TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_execution_artifact FOREIGN KEY (artifact_id) REFERENCES artifact(id) ON DELETE CASCADE,
    CONSTRAINT fk_execution_checklist FOREIGN KEY (checklist_id) REFERENCES test_checklist(id) ON DELETE SET NULL,
    CONSTRAINT fk_execution_test_case FOREIGN KEY (test_case_id) REFERENCES test_case(id) ON DELETE SET NULL
);

-- 测试报告表
CREATE TABLE IF NOT EXISTS test_report (
    id BIGSERIAL PRIMARY KEY,
    artifact_id BIGINT NOT NULL,
    report_name VARCHAR(255) NOT NULL,
    report_type VARCHAR(20) NOT NULL DEFAULT 'FULL',
    report_status VARCHAR(20) NOT NULL DEFAULT 'GENERATING',
    generated_by VARCHAR(100),
    generated_at TIMESTAMP,
    -- 统计信息
    total_test_cases INTEGER DEFAULT 0,
    executed_count INTEGER DEFAULT 0,
    pass_count INTEGER DEFAULT 0,
    fail_count INTEGER DEFAULT 0,
    skip_count INTEGER DEFAULT 0,
    error_count INTEGER DEFAULT 0,
    -- 风险统计
    critical_count INTEGER DEFAULT 0,
    high_count INTEGER DEFAULT 0,
    medium_count INTEGER DEFAULT 0,
    low_count INTEGER DEFAULT 0,
    -- 执行时间
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    total_duration_ms BIGINT,
    -- 报告内容
    summary TEXT,
    vulnerability_summary JSONB,
    component_summary JSONB,
    recommendations TEXT,
    -- 文件信息
    report_file_path VARCHAR(500),
    report_format VARCHAR(20) DEFAULT 'JSON',
    file_size_bytes BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_report_artifact FOREIGN KEY (artifact_id) REFERENCES artifact(id) ON DELETE CASCADE
);

-- 测试环境表
CREATE TABLE IF NOT EXISTS test_environment (
    id BIGSERIAL PRIMARY KEY,
    env_name VARCHAR(100) NOT NULL,
    env_type VARCHAR(50) NOT NULL,
    description TEXT,
    config_data JSONB,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX idx_execution_artifact_id ON test_execution(artifact_id);
CREATE INDEX idx_execution_checklist_id ON test_execution(checklist_id);
CREATE INDEX idx_execution_status ON test_execution(execution_status);
CREATE INDEX idx_execution_result ON test_execution(test_result);
CREATE INDEX idx_execution_vulnerability_type ON test_execution(vulnerability_type);
CREATE INDEX idx_report_artifact_id ON test_report(artifact_id);
CREATE INDEX idx_report_status ON test_report(report_status);
CREATE INDEX idx_report_type ON test_report(report_type);
CREATE INDEX idx_environment_type ON test_environment(env_type);
CREATE INDEX idx_environment_active ON test_environment(is_active);

-- 插入预置测试环境数据
INSERT INTO test_environment (env_name, env_type, description, config_data, is_active) VALUES
('默认测试环境', 'DEFAULT', '默认测试环境配置', '{"jdk": "11", "os": "linux", "memory": "4G"}', TRUE),
('Docker测试环境', 'DOCKER', 'Docker容器测试环境', '{"image": "openjdk:11", "network": "bridge"}', TRUE),
('Kubernetes测试环境', 'K8S', 'Kubernetes集群测试环境', '{"namespace": "test", "replicas": 1}', FALSE);
