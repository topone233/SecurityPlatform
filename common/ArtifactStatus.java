package org.example.securityplatform.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 制品状态常量
 */
@Getter
@AllArgsConstructor
public class ArtifactStatus {
    public static final String UPLOADED = "UPLOADED";
    public static final String ANALYZING = "ANALYZING";
    public static final String ANALYZED = "ANALYZED";
    public static final String FAILED = "FAILED";
}