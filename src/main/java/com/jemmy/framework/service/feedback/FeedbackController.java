package com.jemmy.framework.service.feedback;

import com.jemmy.framework.admin.dao.Exception;
import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.config.ServiceSupport;
import com.jemmy.framework.utils.request.Request;

@AutoAPI
public class FeedbackController {

    private static final String URL = ServiceSupport.domain + "/WebAPI/Feedback/Receive";

    private static final Request request = new Request();

    /**
     * 发送异常反馈
     * @param exception 异常实体
     */
    public static void send(Exception exception) {
        // 是否开启异常反馈
        if (ServiceSupport.exceptionFeedback) {
            Feedback<Exception> feedback = new Feedback<>();

            feedback.setType("Exception");
            feedback.setMessage(exception.getMessage());
            feedback.setData(exception);

            request.post(URL, feedback);
        }
    }

    public static <T> void send(Feedback<T> feedback) {
        // 是否开启异常反馈
        if (ServiceSupport.exceptionFeedback) {
            request.post(URL, feedback);
        }
    }
}
