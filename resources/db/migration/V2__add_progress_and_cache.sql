-- 分析进度追踪表
CREATE TABLE IF NOT EXISTS analysis_progress (
    id BIGSERIAL PRIMARY KEY,
    artifact_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    current_step VARCHAR(100),
    total_steps INTEGER DEFAULT 5,
    completed_steps INTEGER DEFAULT 0,
    progress_percentage INTEGER DEFAULT 0,
    message TEXT,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_progress_artifact FOREIGN KEY (artifact_id) REFERENCES artifact(id) ON DELETE CASCADE
);

-- 分析结果缓存表
CREATE TABLE IF NOT EXISTS analysis_cache (
    id BIGSERIAL PRIMARY KEY,
    artifact_md5 VARCHAR(64) NOT NULL UNIQUE,
    api_count INTEGER DEFAULT 0,
    component_count INTEGER DEFAULT 0,
    danger_count INTEGER DEFAULT 0,
    config_count INTEGER DEFAULT 0,
    url_count INTEGER DEFAULT 0,
    cache_data JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP
);

-- 创建索引
CREATE INDEX idx_progress_artifact_id ON analysis_progress(artifact_id);
CREATE INDEX idx_progress_status ON analysis_progress(status);
CREATE INDEX idx_cache_md5 ON analysis_cache(artifact_md5);
CREATE INDEX idx_cache_expires ON analysis_cache(expires_at);
