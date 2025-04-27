package com.OMS.run.tool.impl;

import com.OMS.run.tool.k8sConfig;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class k8sConfigImpl implements k8sConfig {

    @Bean
    public ApiClient k8sConfigManager() {
        String master = "https://127.0.0.1:51260";
        String oauthToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6InE3R3RQN2xvb1FFc191a3U1Z20xcXZDS3RzYjVNQzJHZllRa28zWGstMHcifQ.eyJhdWQiOlsiaHR0cHM6Ly9rdWJlcm5ldGVzLmRlZmF1bHQuc3ZjLmNsdXN0ZXIubG9jYWwiXSwiZXhwIjoxNzQ4MzU0ODM1LCJpYXQiOjE3NDU3NjI4MzUsImlzcyI6Imh0dHBzOi8va3ViZXJuZXRlcy5kZWZhdWx0LnN2Yy5jbHVzdGVyLmxvY2FsIiwianRpIjoiNGUzYzFiMmItODBlNC00NmFjLTgwMWUtODQ2ZjgwNjQwOGJjIiwia3ViZXJuZXRlcy5pbyI6eyJuYW1lc3BhY2UiOiJrdWJlcm5ldGVzLWRhc2hib2FyZCIsInNlcnZpY2VhY2NvdW50Ijp7Im5hbWUiOiJrdWJlcm5ldGVzLWRhc2hib2FyZCIsInVpZCI6IjVmOGQwZThmLWVlNGQtNDAzOS04Y2QyLTA5OGU2NTg0ZDQxMiJ9fSwibmJmIjoxNzQ1NzYyODM1LCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZXJuZXRlcy1kYXNoYm9hcmQ6a3ViZXJuZXRlcy1kYXNoYm9hcmQifQ.aVlKWlzp6cHA4ITupcTS3_O-3Xea4zAPtxn8rtMJcb1u1VWnzpIFxMLTnJ9m839t9UG_5IqXtKuYkUKRE6d76wo2sNsBwbvAodFzXK2xFvDff6mQ3b7aYXTwv6WMuzNvUcm-zSey7B0Fcs9fJV2f76sRGmhflY1N-l7mDLsQBv22YoAEyHECZyzHXDOXWfUwhYcrizGDafaKE40Fnxviup6mypaQEfCPHAHeMgSAJawJKU0W5Jx25QqraU10GkJFeHVMDVTV6D12YI13LhC49KACG0xWD-fvsZ7TiB-yu_gNMkmvbxo4hy7MMMQUCf-bAvNrtZCyhzFnEy7wWxOb_g";

        ApiClient apiClient = new ClientBuilder()
                .setBasePath(master)
                .setVerifyingSsl(false)
                .setAuthentication(new AccessTokenAuthentication(oauthToken))
                .build();
        Configuration.setDefaultApiClient(apiClient);

        return apiClient;
    }
}
