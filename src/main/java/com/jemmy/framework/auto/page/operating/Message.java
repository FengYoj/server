package com.jemmy.framework.auto.page.operating;

import com.jemmy.framework.component.json.JemmyJson;

public class Message {

    private String content;

    private String confirm = "确认";

    private String cancel = "取消";

    public Message() {
    }

    public Message(String content) {
        this.content = content;
    }

    public Message(String content, String confirm, String cancel) {
        this.content = content;
        this.confirm = confirm;
        this.cancel = cancel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    @Override
    public String toString() {
        return JemmyJson.toJSONString(this);
    }
}
