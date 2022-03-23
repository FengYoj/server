package com.jemmy.framework.admin.config.href;

import com.jemmy.framework.admin.config.href.entity.Menu;
import com.jemmy.framework.admin.config.href.entity.Page;
import com.jemmy.framework.auto.page.enums.MenuIcon;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.utils.StringUtils;
import com.jemmy.framework.utils.file.ClassPathResourceReader;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/AdminAPI/Config/HrefConfig")
public class AdminHrefController {

    private final static List<Menu> data = new ArrayList<>();

    private final static List<Menu> setting = new ArrayList<>();

    private final static List<Page> pages = new ArrayList<>();

    private AdminHref href;

    @PostConstruct
    public void init() {
        href = JemmyJson.toJavaObject(new ClassPathResourceReader("static/admin/config/href.json").getContent(), AdminHref.class);

        // 添加所有初始页面配置
        pages.addAll(href.getPages());

        // 将数组栈指向 pages 属性
        href.setPages(pages);

        href.getMenus()
                .stream()
                .filter(v -> v.getRoot().equals("data"))
                .findAny()
                .ifPresent(menu -> menu.setChild(data));

        href.getSubmenus()
                .stream()
                .filter(v -> v.getRoot().equals("setting"))
                .findAny()
                .ifPresent(menu -> menu.setChild(setting));
    }

    @GetMapping("Get")
    public Result<AdminHref> get() {
        return Result.<AdminHref>of(ResultCode.HTTP200).setData(href);
    }

    /**
     * 添加至数据菜单
     * @param keys menu 键值
     * @param menu menu 实体
     * @param page page 实体
     */
    public static void addData(List<String> keys, Menu menu, Page page) {
        add(keys, menu, data);

        // 设置标题
        page.setTitle(getTitle(keys) + page.getTitle());

        // 添加至页面数组
        pages.add(page);
    }

    public static void addData(List<String> keys, Menu menu) {
        add(keys, menu, data);
    }

    /**
     * 添加至设置菜单
     * @param keys menu 键值
     * @param menu menu 实体
     */
    public static void addSetting(List<String> keys, Menu menu, Page page) {
        add(keys, menu, setting);

        // 设置标题
        page.setTitle(getTitle(keys) + page.getTitle());

        // 添加至页面数组
        pages.add(page);
    }

    private static void add(List<String> keys, Menu menu, List<Menu> list) {
        for (String key : keys) {
            if (StringUtils.isExist(key)) {
                // 根据 key 查找是否存在相同名称的子菜单
                var o = list.stream().filter(v -> v.getName().equals(key)).findAny();

                Menu m;

                if (o.isPresent()) {
                    m = o.get();
                } else {
                    m = new Menu();

                    m.setName(key);
                    // 菜单图标为
                    m.setIcon(MenuIcon.MENU.getIcon());

                    list.add(m);
                }

                if (m.getChild() == null) {
                    m.setChild(new ArrayList<>());
                }

                list = m.getChild();
            }
        }

        list.add(menu);
    }

    private static String getTitle(List<String> keys) {
        var j = String.join(" - ", keys);

        return StringUtils.isExist(j) ? j + " - " : "";
    }
}