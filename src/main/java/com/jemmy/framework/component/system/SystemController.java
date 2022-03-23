package com.jemmy.framework.component.system;

import com.jemmy.framework.Framework;
import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.auto.api.annotation.Get;
import com.jemmy.framework.utils.result.Result;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@AutoAPI("System")
public class SystemController {

    private final SystemInfo si = new SystemInfo();

//    SystemController() {
//        SystemInfo si = new SystemInfo();
//        HardwareAbstractionLayer hal = si.getHardware();
//        CentralProcessor cpu = hal.getProcessor();
//
//        System.out.println(cpu.getMaxFreq());
//    }

    @Get
    public Result<String> getFrameworkVersion() {
        return Result.<String>HTTP200().setData(Framework.VERSION);
    }

    @Get
    public Result<?> getInfo() throws InterruptedException {
        Map<String, Object> res = new HashMap<>();

        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();

        res.put("cpu", this.getCPUInfo(hal.getProcessor()));
        res.put("memory", this.getMemoryInfo(hal.getMemory()));

        return Result.HTTP200().setData(res);
    }

    private Map<String, Object> getCPUInfo(CentralProcessor processor) throws InterruptedException {
        Map<String, Object> res = new HashMap<>();

        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 睡眠1s
        TimeUnit.SECONDS.sleep(1);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
        long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
        long cSys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
        long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        System.out.println("----------------cpu信息----------------");
        System.out.println("cpu核数:" + processor.getLogicalProcessorCount());
        System.out.println("cpu系统使用率:" + new DecimalFormat("#.##%").format(cSys * 1.0 / totalCpu));
        System.out.println("cpu用户使用率:" + new DecimalFormat("#.##%").format(user * 1.0 / totalCpu));
        System.out.println("cpu当前等待率:" + new DecimalFormat("#.##%").format(iowait * 1.0 / totalCpu));
        System.out.println("cpu当前使用率:" + new DecimalFormat("#.##%").format(1.0-(idle * 1.0 / totalCpu)));

        res.put("used", 1.0 - (idle * 1.0 / totalCpu));
        res.put("user", user * 1.0 / totalCpu);
        res.put("system", cSys * 1.0 / totalCpu);

        return res;
    }

    private Map<String, Object> getMemoryInfo(GlobalMemory memory) {
        Map<String, Object> res = new HashMap<>();

        res.put("total", memory.getTotal());
        res.put("used", memory.getTotal() - memory.getAvailable());
        res.put("free", memory.getAvailable());

        return res;
    }
}
