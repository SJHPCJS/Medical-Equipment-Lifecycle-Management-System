package com.example.melms.pojo;

import java.sql.Timestamp;

public class Log {
    private int log_id;
    private Timestamp log_time;
    private String log_action;
    private String log_user_id;

    public Log() {
    }

    public Log(int log_id, Timestamp log_time, String log_action, String log_user_id) {
        this.log_id = log_id;
        this.log_time = log_time;
        this.log_action = log_action;
        this.log_user_id = log_user_id;
    }

    /**
     * 获取
     * @return log_id
     */
    public int getLog_id() {
        return log_id;
    }

    /**
     * 设置
     * @param log_id
     */
    public void setLog_id(int log_id) {
        this.log_id = log_id;
    }

    /**
     * 获取
     * @return log_time
     */
    public Timestamp getLog_time() {
        return log_time;
    }

    /**
     * 设置
     * @param log_time
     */
    public void setLog_time(Timestamp log_time) {
        this.log_time = log_time;
    }

    /**
     * 获取
     * @return log_action
     */
    public String getLog_action() {
        return log_action;
    }

    /**
     * 设置
     * @param log_action
     */
    public void setLog_action(String log_action) {
        this.log_action = log_action;
    }

    /**
     * 获取
     * @return log_user_id
     */
    public String getLog_user_id() {
        return log_user_id;
    }

    /**
     * 设置
     * @param log_user_id
     */
    public void setLog_user_id(String log_user_id) {
        this.log_user_id = log_user_id;
    }

    public String toString() {
        return "Log{log_id = " + log_id + ", log_time = " + log_time + ", log_action = " + log_action + ", log_user_id = " + log_user_id + "}";
    }
}
