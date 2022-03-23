package com.jemmy.framework.aop.token;

import com.jemmy.framework.admin.controller.TokenController;
import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.utils.request.RequestUtils;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import com.jemmy.framework.page.ErrorPage;
import com.jemmy.framework.utils.request.CookieUtils;
import com.jemmy.framework.utils.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
public class HttpAopCheckerToken {

    private final TokenController tokenController;

    @Autowired
    public HttpAopCheckerToken(TokenController tokenController) {
        this.tokenController = tokenController;
    }

    // 定义一个 Pointcut, 使用 切点表达式函数 来描述对哪些 Join point 使用 advise
    @Pointcut("@annotation(com.jemmy.framework.aop.token.AuthCheckerToken)")
    public void pointcut() {
    }

    // 定义 advise
    @Around("pointcut() && @annotation(act)")
    public Object checkAuth(ProceedingJoinPoint joinPoint, AuthCheckerToken act) {
        String name = null;

        if (act.isAuto()) {
            // 获取切入的 Method
            MethodSignature joinPointObject = (MethodSignature) joinPoint.getSignature();
            AutoAPI autoApi = joinPointObject.getMethod().getDeclaringClass().getAnnotation(AutoAPI.class);

            if (autoApi != null) {
                name = autoApi.token();
            }
        } else {
            name = act.value();
        }

        boolean isStatus = act.status();

        HttpServletRequest request = RequestUtils.getServlet();

        // 获取 token
        CookieUtils cookies = new CookieUtils(request);

        // 检查用户所传递的 token 是否合法
        if (StringUtils.isBlank(act.check()) ?
                !tokenController.getStatus(cookies.get(act.value()), name) :
                !tokenController.getStatus(cookies.get(act.value()), name, cookies.get(act.check()))) {

            return get400Error(isStatus, act.page());
        }

        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return get500Error(isStatus);
        }
    }

    private Object get500Error(Boolean isStatus) {
        if (isStatus) {
            return Result.of(ResultCode.HTTP500);
        }

        return ErrorPage.error(500);
    }

    /**
     * 返回错误信息
     * @param isStatus 是否为状态对象
     * @return 对象或页面
     */
    private Object get400Error(Boolean isStatus, String page) {
        if (isStatus) {
            return Result.of(ResultCode.HTTP400).putMessage("Token has expired");
        }

        return page;
    }
}
