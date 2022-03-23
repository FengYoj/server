package com.jemmy.framework.interceptor;

import com.jemmy.framework.admin.controller.ExceptionController;
import com.jemmy.framework.admin.dao.Exception;
import com.jemmy.framework.admin.ordersException.OrdersException;
import com.jemmy.framework.admin.ordersException.OrdersExceptionController;
import com.jemmy.framework.exception.AopException;
import com.jemmy.framework.exception.OrderException;
import com.jemmy.framework.exception.ResultException;
import com.jemmy.framework.service.feedback.FeedbackController;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionInterceptor {

    private final ExceptionController exceptionController;

    private final OrdersExceptionController ordersExceptionController;

    public ExceptionInterceptor(ExceptionController exceptionController, OrdersExceptionController ordersExceptionController) {
        this.exceptionController = exceptionController;
        this.ordersExceptionController = ordersExceptionController;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Result<?> runtimeException(RuntimeException e, HttpServletRequest request) {

        // 打印至控制台
        e.printStackTrace();

        return Result.of(ResultCode.HTTP500).putMessage(e.getMessage()).putMessage("Message ID : " + save(e, request));
    }

    /**
     * 参数异常
     * @param e 异常事件
     * @return 状态值
     */
    @ExceptionHandler(ResultException.class)
    @ResponseBody
    public Result<?> paramException(ResultException e) {

        if (e.getStatus() != null) {
            return e.getStatus();
        }

        return Result.of(ResultCode.HTTP400).putMessage(e.getMessage());
    }

    /**
     * AOP 异常
     * @param e 异常事件
     * @return 状态值
     */
    @ExceptionHandler(AopException.class)
    @ResponseBody
    public Result<?> aopException(AopException e) {
        return Result.HTTP403().putMessage(e.getMessage());
    }

    private String save(RuntimeException e, HttpServletRequest request) {
        Exception exception = new Exception();

        StackTraceElement[] stackTraceElements = e.getStackTrace();

        if (stackTraceElements.length > 0) {
            StackTraceElement stackTraceElement = stackTraceElements[0];

            exception.setClassName(stackTraceElement.getClassName());
            exception.setFileName(stackTraceElement.getFileName());
            exception.setLineNumber(stackTraceElement.getLineNumber());
            exception.setMethodName(stackTraceElement.getMethodName());
        }

        exception.setMessage(e.getMessage());
        exception.setPath(request.getServletPath());
        exception.setType(e.getClass().getName());

        // Save To Database
        exceptionController.controller.save(exception);

        FeedbackController.send(exception);

        return exception.getUuid();
    }

    /**
     * 订单异常
     * @param e 异常事件
     * @return 状态值
     */
    @ExceptionHandler(OrderException.class)
    @ResponseBody
    public Object orderException(OrderException e) {

        OrdersException ordersException = e.getException();

        // 保存订单异常实体
        ordersExceptionController.controller.onlySave(ordersException);

        // 返回微信支付正常接收字符串
        switch (ordersException.getPaymentPlatform()) {
            case "wxPay":
                return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        }

        return null;
    }
}
