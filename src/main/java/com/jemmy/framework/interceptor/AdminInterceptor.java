package com.jemmy.framework.interceptor;

import com.jemmy.framework.admin.controller.TokenController;
import com.jemmy.framework.utils.result.Result;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminInterceptor extends HandlerInterceptorAdapter {

    private final TokenController tokenController;

    public AdminInterceptor(TokenController tokenController) {
        this.tokenController = tokenController;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws IOException {

        // 获取 token
        String admin_token = request.getHeader("Authorization-Token");

        // 判断 token 是否有效
        if (admin_token == null || admin_token.isEmpty() || !tokenController.getStatus(admin_token, "admin_token")) {

            if (handler instanceof HandlerMethod) {
                HandlerMethod method = (HandlerMethod) handler;

                // 判断返回类型是否为 Status 类
                if (Result.class.isAssignableFrom(method.getMethod().getReturnType())) {
                    // 返回参数异常状态
                    response.sendError(403, "token does not exist or expires");

                    return false;
                }
            }

//            System.out.println(request.getRequestURI());
//            System.out.println(request.getRequestURL());
//
//            // 返回登录页面
//            response.sendRedirect("/admin/login?oauth_callback=" + request.getRequestURL());
            response.sendRedirect("/admin/login");

            return false;
        }

        return true;
    }
}
