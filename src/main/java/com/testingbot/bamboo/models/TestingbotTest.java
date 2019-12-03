package com.testingbot.bamboo.models;

import java.util.ArrayList;
import java.util.Map;

public class TestingbotTest {
    private String created_at;
    private String completed_at;
    private String extra;
    private String name;
    private String session_id;
    private boolean success;
    private String status_message;
    private String state;
    private String browser;
    private String browser_version;
    private String os;
    private int duration;
    private String build;
    private String video;
    private ArrayList<String> thumbs;
    private Map<String, String> logs;
    private ArrayList<String> groups;
    private String type;

    /**
     * @return the extra
     */
    public String getExtra() {
        return extra;
    }

    /**
     * @param extra the extra to set
     */
    public void setExtra(String extra) {
        this.extra = extra;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the browser
     */
    public String getBrowser() {
        return browser;
    }

    /**
     * @param browser the browser to set
     */
    public void setBrowser(String browser) {
        this.browser = browser;
    }

    /**
     * @return the os
     */
    public String getOs() {
        return os;
    }

    /**
     * @param os the os to set
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * @return the duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * @return the build
     */
    public String getBuild() {
        return build;
    }

    /**
     * @param build the build to set
     */
    public void setBuild(String build) {
        this.build = build;
    }

    /**
     * @return the groups
     */
    public ArrayList<String> getGroups() {
        return groups;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the video
     */
    public String getVideo() {
        return video;
    }

    /**
     * @param video the video to set
     */
    public void setVideo(String video) {
        this.video = video;
    }

    /**
     * @return the thumbs
     */
    public ArrayList<String> getThumbs() {
        return thumbs;
    }

    /**
     * @param thumbs the thumbs to set
     */
    public void setThumbs(ArrayList<String> thumbs) {
        this.thumbs = thumbs;
    }

    /**
     * @return the logs
     */
    public Map<String, String> getLogs() {
        return logs;
    }

    /**
     * @param logs the logs to set
     */
    public void setLogs(Map<String, String> logs) {
        this.logs = logs;
    }

    /**
     * @return the session_id
     */
    public String getSession_id() {
        return session_id;
    }

    /**
     * @param session_id the session_id to set
     */
    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    /**
     * @return the created_at
     */
    public String getCreated_at() {
        return created_at;
    }

    /**
     * @param created_at the created_at to set
     */
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    /**
     * @return the completed_at
     */
    public String getCompleted_at() {
        return completed_at;
    }

    /**
     * @param completed_at the completed_at to set
     */
    public void setCompleted_at(String completed_at) {
        this.completed_at = completed_at;
    }

    /**
     * @return the status_message
     */
    public String getStatus_message() {
        return status_message;
    }

    /**
     * @param status_message the status_message to set
     */
    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    /**
     * @return the browser_version
     */
    public String getBrowser_version() {
        return browser_version;
    }

    /**
     * @param browser_version the browser_version to set
     */
    public void setBrowser_version(String browser_version) {
        this.browser_version = browser_version;
    }
}
