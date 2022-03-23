package com.jemmy.framework.component.order.substance.postage;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.page.annotation.field.TableAttr;
import com.jemmy.framework.auto.page.annotation.field.Title;
import com.jemmy.framework.component.order.substance.postage.item.PostageItem;
import com.jemmy.framework.controller.EntityKey;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Postage extends EntityKey {

    @Title
    @FieldAttr(value = "名称", empty = false)
    private String name;

    @TableAttr(disable = true)
    @OneToMany
    @FieldAttr("地区")
    private List<PostageItem> items;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PostageItem> getItems() {
        return items;
    }

    public void setItems(List<PostageItem> items) {
        this.items = items;
    }
}
