-- 项目表
CREATE TABLE IF NOT EXISTS project (
    id BIGSERIAL PRIMARY KEY,
    project_name VARCHAR(200) NOT NULL,
    product_name VARCHAR(200),
    description TEXT,
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 测试任务表
CREATE TABLE IF NOT EXISTS task (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    task_name VARCHAR(200) NOT NULL,
    test_version VARCHAR(100),
    test_time DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'CREATED',
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);

-- 制品表
CREATE TABLE IF NOT EXISTS artifact (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    artifact_name VARCHAR(255) NOT NULL,
    artifact_type VARCHAR(50) NOT NULL,
    file_path VARCHAR(500),
    file_size BIGINT,
    md5 VARCHAR(64),
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'UPLOADED',
    CONSTRAINT fk_artifact_task FOREIGN KEY (task_id) REFERENCES task(id) ON DELETE CASCADE
);

-- API信息表
CREATE TABLE IF NOT EXISTS api_info (
    id BIGSERIAL PRIMARY KEY,
    artifact_id BIGINT NOT NULL,
    url VARCHAR(500) NOT NULL,
    http_method VARCHAR(10) NOT NULL,
    controller_class VARCHAR(255),
    method_name VARCHAR(255),
    parameters JSONB,
    framework VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_api_artifact FOREIGN KEY (artifact_id) REFERENCES artifact(id) ON DELETE CASCADE
);

-- 组件信息表
CREATE TABLE IF NOT EXISTS component_info (
    id BIGSERIAL PRIMARY KEY,
    artifact_id BIGINT NOT NULL,
    component_name VARCHAR(255) NOT NULL,
    component_type VARCHAR(50) NOT NULL,
    version VARCHAR(100),
    detection_source VARCHAR(50) NOT NULL,
    details JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_component_artifact FOREIGN KEY (artifact_id) REFERENCES artifact(id) ON DELETE CASCADE
);

-- 危险函数表
CREATE TABLE IF NOT EXISTS danger_function (
    id BIGSERIAL PRIMARY KEY,
    artifact_id BIGINT NOT NULL,
    function_name VARCHAR(255) NOT NULL,
    function_type VARCHAR(100) NOT NULL,
    class_name VARCHAR(255),
    line_number INTEGER,
    file_path VARCHAR(500),
    method_name VARCHAR(255),
    risk_level VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_danger_artifact FOREIGN KEY (artifact_id) REFERENCES artifact(id) ON DELETE CASCADE
);

-- 配置信息表
CREATE TABLE IF NOT EXISTS config_info (
    id BIGSERIAL PRIMARY KEY,
    artifact_id BIGINT NOT NULL,
    config_key VARCHAR(255) NOT NULL,
    config_value TEXT,
    config_source VARCHAR(100),
    file_path VARCHAR(500),
    category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_config_artifact FOREIGN KEY (artifact_id) REFERENCES artifact(id) ON DELETE CASCADE
);

-- 外部URL表
CREATE TABLE IF NOT EXISTS external_url (
    id BIGSERIAL PRIMARY KEY,
    artifact_id BIGINT NOT NULL,
    url VARCHAR(500) NOT NULL,
    protocol VARCHAR(20) NOT NULL,
    usage_context VARCHAR(255),
    file_path VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_url_artifact FOREIGN KEY (artifact_id) REFERENCES artifact(id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX idx_project_created_at ON project(created_at);
CREATE INDEX idx_task_project_id ON task(project_id);
CREATE INDEX idx_task_status ON task(status);
CREATE INDEX idx_artifact_task_id ON artifact(task_id);
CREATE INDEX idx_artifact_status ON artifact(status);
CREATE INDEX idx_api_artifact_id ON api_info(artifact_id);
CREATE INDEX idx_api_url ON api_info(url);
CREATE INDEX idx_component_artifact_id ON component_info(artifact_id);
CREATE INDEX idx_component_type ON component_info(component_type);
CREATE INDEX idx_danger_artifact_id ON danger_function(artifact_id);
CREATE INDEX idx_danger_risk_level ON danger_function(risk_level);
CREATE INDEX idx_config_artifact_id ON config_info(artifact_id);
CREATE INDEX idx_config_category ON config_info(category);
CREATE INDEX idx_url_artifact_id ON external_url(artifact_id);