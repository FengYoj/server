package com.jemmy.framework.admin.controller;

import com.jemmy.framework.admin.repository.SuperAuthorityRepository;
import com.jemmy.framework.utils.StringUtils;
import com.jemmy.framework.utils.request.IpUtil;
import com.jemmy.framework.utils.request.RequestUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class SuperAuthorityController {

    private final SuperAuthorityRepository repository;

    public SuperAuthorityController(SuperAuthorityRepository repository) {
//        if (repository.findAll().size() <= 0) {
//            SuperAuthority superAuthority = new SuperAuthority();
//
//            superAuthority.setIp("183.20.117.114");
//            superAuthority.setKey("TestSuperAuthorityKey");
//            superAuthority.setRemark("test");
//
//            repository.save(superAuthority);
//        }

        this.repository = repository;
    }

    public boolean allow() {
        HttpServletRequest request = RequestUtils.getServlet();

        if (request == null) {
            return false;
        }

        // get cookie to key
        String key = request.getHeader("Super-Authority");

        if (StringUtils.isBlank(key)) {
            return false;
        }

        // get ip address
        String ip = IpUtil.getIpAddr(request);

        return repository.existsByKeyAndIp(key, ip);
    }
}
