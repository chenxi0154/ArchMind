package com.example.archmind.common.constant;

public final class TaskStatus {

    public static final String PENDING = "PENDING";
    public static final String RUNNING = "RUNNING";
    public static final String SUCCESS = "SUCCESS";
    public static final String PARTIAL = "PARTIAL";
    public static final String FAILED  = "FAILED";

    /** 终态：前端看到这几个就停止轮询 */
    public static boolean isTerminal(String status) {
        return SUCCESS.equals(status) || PARTIAL.equals(status) || FAILED.equals(status);
    }
    private TaskStatus() {}
}
