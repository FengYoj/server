package com.jemmy.framework.admin.controller;

import com.jemmy.config.RequestPath;
import com.jemmy.framework.admin.dao.AdminAccount;
import com.jemmy.framework.admin.repository.AdminAccountRepository;
import com.jemmy.framework.auto.admin.Admin;
import com.jemmy.framework.auto.admin.AutoAdmin;
import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.auto.api.annotation.Get;
import com.jemmy.framework.auto.api.annotation.Post;
import com.jemmy.framework.auto.param.AutoParam;
import com.jemmy.framework.component.ali.oss.AliOssController;
import com.jemmy.framework.component.password.Password;
import com.jemmy.framework.component.password.PasswordController;
import com.jemmy.framework.component.resources.ResourceParamConfig;
import com.jemmy.framework.component.resources.image.ImageController;
import com.jemmy.framework.component.resources.image.ResourceImage;
import com.jemmy.framework.component.validate_code.ValidateCode;
import com.jemmy.framework.component.validate_code.ValidateCodeController;
import com.jemmy.framework.data.redis.RedisHash;
import com.jemmy.framework.controller.ErrorUtils;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.LockMap;
import com.jemmy.framework.utils.MD5Utils;
import com.jemmy.framework.utils.StringUtils;
import com.jemmy.framework.utils.Verify;
import com.jemmy.framework.utils.file.ClassPathResourceReader;
import com.jemmy.framework.utils.request.Cookie;
import com.jemmy.framework.utils.request.IpUtil;
import com.jemmy.framework.utils.request.RequestParam;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.validation.BindingResult;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@AutoAPI
public class AdminAccountController extends JpaController<AdminAccount, AdminAccountRepository> {

    @AutoAdmin("???????????????")
    public static class _ADMIN extends Admin<AdminAccount, AdminAccountController> {}

    private final TokenController tokenController;

    private final LockMap<String> keyLock = LockMap.forLargeKeySet();

    private final ImageController imageController;

    private final RedisHash<Set<String>> redisHash;

    private final ValidateCodeController validateCodeController;

    private final PasswordController passwordController;

    private final JavaMailSender javaMailSender;

    public AdminAccountController(TokenController tokenController, ImageController imageController, ValidateCodeController validateCodeController, RedisConnectionFactory redisConnectionFactory, PasswordController passwordController, JavaMailSender javaMailSender) {
        this.tokenController = tokenController;
        this.imageController = imageController;
        this.validateCodeController = validateCodeController;
        this.passwordController = passwordController;
        this.javaMailSender = javaMailSender;

        redisHash = new RedisHash<>("login_record", redisConnectionFactory);
    }

    /**
     * ??????????????????
     */
    @PostConstruct
    private void init() throws IOException {
        if (repository.count() <= 0) {
            AdminAccount adminAccount = new AdminAccount();

            ResourceParamConfig config = new ResourceParamConfig();

            config.setFolder("avatar");
            config.setMaterial(false);

            Result<ResourceImage> avatar = imageController.upload(ClassPathResourceReader.getFile("static/logo.png"), config);

            if (avatar.isBlank()) {
                throw new RuntimeException(avatar.getMessage());
            }

            adminAccount.setAvatar(avatar.getData());
            adminAccount.setGrade(0);
            adminAccount.setDescription("??????????????????");
            adminAccount.setUsername("admin");
            adminAccount.setPassword(passwordController.create(MD5Utils.encrypt("admin")));

            // ????????????????????????
            controller.onlySave(adminAccount);
        }
    }

    @Get
    public Result<Map<String, String>> getForgotAccountInfo(@AutoParam String username) {
        AdminAccount account = repository.findByUsername(username);

        if (account == null) {
            return Result.code("NOT_USERNAME", "User does not exist");
        }

        if (StringUtils.isBlank(account.getEmail())) {
            return Result.code("NO_EMAIL", "Did not fill in the email");
        }

        Map<String, String> res = new HashMap<>();

        res.put("email", account.getEmail().replaceAll("(\\w?)(\\w+)(\\w)@\\w+(\\w\\.[a-z]+(\\.[a-z]+)?)", "$1**$3@**$4"));
        res.put("username", account.getUsername());
        res.put("avatar", account.getAvatar().getUrl());

        return Result.<Map<String, String>>HTTP200().setData(res);
    }

    @Get
    public Result<Map<String, String>> getAccountInfo(@AutoParam String username) {
        AdminAccount account = repository.findByUsername(username);

        if (account == null) {
            return Result.code("NOT_USERNAME");
        }

        Map<String, String> res = new HashMap<>();

        res.put("username", account.getUsername());
        res.put("avatar", account.getAvatar().getUrl());

        return Result.<Map<String, String>>HTTP200().setData(res);
    }

    @Post
    public Result<String> sendEmailCode(@AutoParam String username, @AutoParam String email) throws MessagingException {
        AdminAccount account = repository.findByUsername(username);

        if (account == null) {
            return Result.code("NOT_USERNAME");
        }

        if (StringUtils.isBlank(account.getEmail()) || !account.getEmail().equals(email)) {
            return Result.code("EMAIL_WRONG", "E-mail is wrong");
        }

        String codeHtml = ClassPathResourceReader.of("templates/email/code.html").getContent();

        ValidateCode code = validateCodeController.get(30 * 60 * 1000L, account.getEmail());

        codeHtml = codeHtml.replaceAll("\\$\\{code}", code.getCode());

        //????????????SimpleMailMessage??????
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        //??????????????????MimeMessageHelper??????????????????????????????????????????
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        // ??????????????????
        helper.setSubject("????????????");
        // ?????????????????????????????????application.yml?????????????????????
        helper.setFrom("shltds@outlook.com");
        // ?????????????????????
        helper.setTo(account.getEmail());
        // ????????????????????????
        helper.setSentDate(new Date());
        // ?????????????????????
        helper.setText(codeHtml, true);
        // ????????????
        javaMailSender.send(mimeMessage);

        return Result.<String>HTTP200().setData(code.getId());
    }

    @Post
    public Result<?> verifyEmailCode(@AutoParam String username, @AutoParam String email, @AutoParam String codeId, @AutoParam String code) {
        AdminAccount account = repository.findByUsername(username);

        if (account == null) {
            return Result.code("NOT_USERNAME");
        }

        if (StringUtils.isBlank(account.getEmail()) || !account.getEmail().equals(email)) {
            return Result.code("EMAIL_WRONG", "E-mail is wrong");
        }

        Verify verify = validateCodeController.verify(codeId, code, account.getEmail());

        if (verify.isInvalid()) {
            return verify.toStatus();
        }

        return Result.HTTP200();
    }

    /**
     * ????????????
     * @param username ?????????
     * @param email ????????????
     * @param codeId ????????? ID
     * @param code ?????????
     * @param password ??????
     */
    @Post
    public Result<?> resetPassword(@AutoParam String username, @AutoParam String email, @AutoParam String codeId, @AutoParam String code, @AutoParam String password) {
        AdminAccount account = repository.findByUsername(username);

        if (account == null) {
            return Result.code("NOT_USERNAME");
        }

        if (StringUtils.isBlank(account.getEmail()) || !account.getEmail().equals(email)) {
            return Result.code("EMAIL_WRONG", "E-mail is wrong");
        }

        Verify verify = validateCodeController.verify(codeId, code, account.getEmail());

        if (verify.isInvalid()) {
            return verify.toStatus();
        }

        // ????????????
        account.getPassword().change(password);

        return Result.HTTP200();
    }

    /**
     * ????????????
     *
     * @return Status
     */
    @Post(path = RequestPath.ADMIN)
    public Result<?> autoLogin(HttpServletRequest request, HttpServletResponse response) {

        RequestParam param = new RequestParam(request);

        String auto = param.getCookie("admin_auto_login");
        String admin_id = param.getCookie("admin_id");

        if (StringUtils.isBlank(auto) || StringUtils.isBlank(admin_id) || !tokenController.getStatus(auto, "admin_auto_login", admin_id)) {
            return Result.HTTP403();
        }

        response.addCookie(new Cookie("admin_token", tokenController.getTokenStr("admin_token", admin_id), "/"));

        return Result.HTTP200();
    }

    /**
     * ??????????????????
     *
     * @param username ?????????
     * @param password ??????
     * @return Status
     */
    @Post(path = { RequestPath.WEB, RequestPath.ADMIN })
    public Result<Map<String, Object>> login(@AutoParam String username, @AutoParam String password, @AutoParam(required = false) String code, @AutoParam(required = false) String codeId, HttpServletRequest request) {
        Result<Map<String, Object>> result = this.verificationAccount(username, password);

        if (result.isBlank()) {
            return result;
        }

        String ip = IpUtil.getIpAddr(request);

        // ??????????????? IP + ??????????????????????????????
        if ((!redisHash.exists(ip) || !redisHash.get(ip).contains(username))) {
            if (StringUtils.isBlank(code) || StringUtils.isBlank(codeId)) {
                return Result.code("ENTER_VERIFICATION_CODE", "Please enter verification code");
            }

            Verify verify = validateCodeController.verify(codeId, code);

            // ????????????
            if (verify.isInvalid()) {
                return Result.code(verify.getCode(), verify.getMessage());
            }

            Set<String> set;

            if (redisHash.exists(ip)) {
                set = redisHash.get(ip);
            } else {
                set = new HashSet<>();
            }

            // ???????????????
            set.add(username);
            // ?????? Redis
            redisHash.put(ip, set);
        }

        return Result.<Map<String, Object>>HTTP200().setData(result.getData());
    }

    /**
     * ?????????????????????
     *
     * @param adminAccount  ?????????????????????
     * @param bindingResult ??????
     * @return ?????????????????????
     */
    public Result<?> saveAccount(AdminAccount adminAccount, BindingResult bindingResult) {
        if (ErrorUtils.hasErrors(bindingResult)) {
            return Result.of(ResultCode.HTTP400).putMessage(ErrorUtils.getErrorStr(bindingResult));
        }

        try {
            keyLock.lock("SaveAdminAccount");

            if ((adminAccount.getUuid() == null || adminAccount.getUuid().isEmpty()) && repository.existsByUsername(adminAccount.getUsername())) {
                return Result.of(ResultCode.HTTP400).putMessage("?????????????????????");
            }

            repository.save(adminAccount);

            return new Result<AdminAccount>(ResultCode.HTTP200).setData(adminAccount);
        } catch (InterruptedException e) {
            return new Result<>(ResultCode.HTTP500).setMessage("???????????????").putMessage(e.getMessage());
        } finally {
            keyLock.unlock("SaveAdminAccount");
        }
    }

    public Result<Map<String, Object>> verificationAccount(String username, String password) {
        AdminAccount adminAccount = repository.findByUsername(username);

        if (adminAccount == null) {
            return Result.<Map<String, Object>>code("NOT_USERNAME").putMessage("???????????????");
        }

        Password p = adminAccount.getPassword();

        if (p == null) {
            return Result.code("PASSWORD_NULL", "???????????????????????????");
        }

        if (!p.isMatch(password)) {
            return Result.<Map<String, Object>>code("PASSWORD_WRONG").putMessage("????????????");
        }

        String id = adminAccount.getUuid();

        Map<String, Object> map = new HashMap<>();

        map.put("userinfo", adminAccount);
        map.put("admin_id", id);
        map.put("admin_token", tokenController.getTokenStr("admin_token", id));

        return Result.<Map<String, Object>>HTTP200().setData(map);
    }

    @Get(path = RequestPath.ADMIN)
    public Result<AdminAccount> getUserinfo(HttpServletRequest request) {
        String id = request.getHeader("Authorization-Id");

        if (StringUtils.isBlank(id)) {
            return Result.HTTP404();
        }

        return controller.examineFind(repository.findByUuid(id));
    }
}
