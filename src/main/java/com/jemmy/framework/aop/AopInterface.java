package com.jemmy.framework.aop;

import com.jemmy.framework.exception.AopException;
import com.jemmy.framework.exception.ResultException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AopInterface {

    boolean advise(HttpServletRequest request, HttpServletResponse response, Object handler) throws AopException, IOException, ResultException;

}
