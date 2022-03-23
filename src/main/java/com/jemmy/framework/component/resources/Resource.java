package com.jemmy.framework.component.resources;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.page.annotation.field.CreateAttr;
import com.jemmy.framework.auto.page.annotation.field.TableAttr;
import com.jemmy.config.AliOssConfig;
import com.jemmy.framework.auto.page.annotation.field.Title;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.component.upload.UploadInfo;
import com.jemmy.framework.config.Setting;
import com.jemmy.framework.controller.EntityKey;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Resource extends EntityKey {

    @Title
    @FieldAttr("名称")
    private String name;

    @FieldAttr("文件内容类型")
    private String type;

    @FieldAttr("路径")
    private String src;

    @FieldAttr("文件大小")
    private Long size;

    @FieldAttr("素材库可见")
    private Boolean material = true;

    @FieldAttr("上传至 OSS")
    private Boolean oss = false;

    @TableAttr(disable = true)
    @CreateAttr(disable = true)
    private final String domain = getDomain();

    @FieldAttr("链接")
    private String url;

    public Resource() {
    }

    public Resource(UploadInfo info) {
        // 拷贝对象
        BeanUtils.copyProperties(info, this);
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDomain() {

        if (src == null) {
            return "";
        }

        if (!src.contains("http")) {
            return this.oss ? AliOssConfig.bucketDomain : Setting.DOMAIN;
        }

        return "";
    }

    public String getUrl() {
        return getDomain() + this.src;
    }

    public Boolean getOss() {
        return oss;
    }

    public void setOss(Boolean oss) {
        this.oss = oss;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Boolean getMaterial() {
        return material;
    }

    public void setMaterial(Boolean material) {
        this.material = material;
    }

    @Override
    public String toString() {
        return JemmyJson.toJSONString(this);
    }
}
