package com.jemmy.framework.component.validate_code;

import com.jemmy.framework.aop.AopInterface;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.exception.ResultException;
import com.jemmy.framework.utils.StringUtils;
import com.jemmy.framework.utils.Verify;
import com.jemmy.framework.utils.request.RequestParam;
import com.jemmy.framework.utils.result.Result;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ValidateCodeAop implements AopInterface {

    private final ValidateCodeController codeController;

    public ValidateCodeAop(ValidateCodeController codeController) {
        this.codeController = codeController;
    }

    @Override
    public boolean advise(HttpServletRequest request, HttpServletResponse response, Object handler) throws ResultException {
        RequestParam requestParam = new RequestParam(request);

        JemmyJson param = requestParam.getParam();

        String id = param.getString("validateCodeId");
        String value = param.getString("validateCodeValue");

        if (StringUtils.isBlank(id) || StringUtils.isBlank(value)) {
            throw new ResultException(Result.HTTP400().putMessage("Verification code must be filled").setCode("CODE_NOT_EXIST"));
        }

        Verify verify = codeController.verify(id, value);

        if (verify.isInvalid()) {
            throw new ResultException(verify.toStatus());
        }

        return true;
    }

}
