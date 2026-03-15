package org.example.securityplatform.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务状态常量
 */
@Getter
@AllArgsConstructor
public class TaskStatus {
    public static final String CREATED = "CREATED";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String COMPLETED = "COMPLETED";
    public static final String FAILED = "FAILED";
}