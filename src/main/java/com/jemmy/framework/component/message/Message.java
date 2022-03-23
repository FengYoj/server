package com.jemmy.framework.component.message;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.page.type.FieldType;
import com.jemmy.framework.auto.page.type.select.SelectField;
import com.jemmy.framework.controller.EntityKey;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity
public class Message extends EntityKey {

    @FieldAttr("消息类型")
    private MessageType type;

    @Column(columnDefinition = "text")
    @FieldAttr(value = "内容", type = FieldType.Textarea)
    private String message;

    @Column(columnDefinition = "text")
    @FieldAttr(value = "附加信息", type = FieldType.Textarea)
    private String additional;

    @FieldAttr("消息来源")
    private MessageSource source;

    @FieldAttr("消息状态")
    @SelectField(fixed = { "未读", "已读" })
    private Integer readStatus = 0;

    public Message() {
    }

    public Message(MessageType type, MessageSource source, String message) {
        this.type = type;
        this.message = message;
        this.source = source;
    }

    public Message(MessageType type, MessageSource source, String message, String additional) {
        this.type = type;
        this.message = message;
        this.additional = additional;
        this.source = source;
    }


    public static void info(MessageSource source, String message, String additional) {
        save(MessageType.INFO, source, message, additional);
    }

    public static void info(MessageSource source, String message) {
        save(MessageType.INFO, source, message);
    }

    public static void info(String message) {
        info(MessageSource.SYSTEM, message);
    }

    public static void info(String message, String additional) {
        info(MessageSource.SYSTEM, message, additional);
    }


    public static void error(MessageSource source, String message, String additional) {
        save(MessageType.ERROR, source, message, additional);
    }

    public static void error(MessageSource source, String message) {
        save(MessageType.ERROR, source, message);
    }

    public static void error(String message) {
        info(MessageSource.SYSTEM, message);
    }

    public static void error(String message, String additional) {
        info(MessageSource.SYSTEM, message, additional);
    }


    private static void save(MessageType type, MessageSource source, String message, String additional) {
        Message m = new Message(type, source, message, additional);

        // 获取控制器
        MessageController controller = m.getController();

        // 保存
        controller.save(m);
    }

    private static void save(MessageType type, MessageSource source, String message) {
        Message m = new Message(type, source, message);

        // 获取控制器
        MessageController controller = m.getController();

        // 保存
        controller.save(m);
    }

}
