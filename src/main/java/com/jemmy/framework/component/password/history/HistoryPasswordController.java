package com.jemmy.framework.component.password.history;

import com.jemmy.framework.controller.JpaController;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HistoryPasswordController extends JpaController<HistoryPassword, HistoryPasswordRepository> {
}
