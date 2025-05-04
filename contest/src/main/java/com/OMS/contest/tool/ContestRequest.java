package com.OMS.contest.tool;

import com.OMS.contest.model.contest;
import lombok.Data;

@Data
public class ContestRequest {
    private contest contest; // 复杂对象
    private int[] problemIds; // 数组
}
