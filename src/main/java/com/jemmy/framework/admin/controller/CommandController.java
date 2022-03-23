package com.jemmy.framework.admin.controller;

import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.*;

@RestController
@RequestMapping("AdminAPI/Cmd")
public class CommandController {

    @GetMapping("Run")
    public Result<String> run(@RequestParam String cmd) {
        final ExecutorService exec = Executors.newFixedThreadPool(1);

        Callable<Result<String>> call = () -> {
            try {
                ArrayList<String> commands = new ArrayList<>(Arrays.asList(cmd.split(" ")));

                ProcessBuilder pb = new ProcessBuilder(commands);
                pb.redirectErrorStream(true);
                Process p = pb.start(); //启动进程

                BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream(), "gbk"));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = is.readLine()) != null) {
                    if (line.toLowerCase().startsWith("warning")) {
                        sb.append("WARNING: ").append(line).append("\n");
                    } else if (line.toLowerCase().startsWith("error")) {
                        sb.append("ERROR: ").append(line).append("\n");
                    } else if (line.toLowerCase().startsWith("fatal")) {
                        sb.append("FATAL ERROR: ").append(line).append("\n");
                    } else {
                        sb.append(line).append("\n");
                    }
                }

                try {
                    p.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return Result.<String>of(ResultCode.HTTP200).setData(sb.toString());
            } catch (IOException e) {
                return Result.<String>of(ResultCode.HTTP500).setMessage(e.getMessage());
            }
        };

        try {
            Future<Result<String>> future = exec.submit(call);
            return future.get(1000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            return Result.<String>of(ResultCode.HTTP500).setMessage("线程超时");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.<String>of(ResultCode.HTTP500).setMessage("线程超时");
        } finally {
            // 关闭线程池
            exec.shutdown();
        }
    }
}
