package com.OMS.run.tool;

import io.kubernetes.client.openapi.ApiClient;

public interface k8sConfig {
    ApiClient k8sConfigManager();
}
