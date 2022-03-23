package com.jemmy.framework.component.location;

import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.auto.api.annotation.Get;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.utils.request.Request;
import com.jemmy.framework.utils.request.RequestResult;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.http.HttpRequest;

@AutoAPI("Location")
public class LocationController {

    private static final Request request = new Request("status", "1", "info");

    @Get(value = "GetAddressByCoordinate")
    public Result<String> getAddressByCoordinate(@RequestParam Double lng, @RequestParam Double lat) {
        Result<JemmyJson> result = getLocationData(lng, lat);

        if (result.isBlank()) {
            return result.toObject();
        }

        JemmyJson json = result.getData();

        String name = null;

        try {
            if (json.containsKey("aois") && json.getJemmyArray("aois").size() > 0) {
                name = json.getJemmyArray("aois").getJemmyJson(0).getString("name");
            } else if (json.containsKey("pois") && json.getJemmyArray("pois").size() > 0) {
                name = json.getJemmyArray("pois").getJemmyJson(0).getString("name");
            } else {
                name = json.getString("formatted_address");
            }
        } catch (Exception ignored) {}

        return Result.<String>of(ResultCode.HTTP200).setData(name);
    }

    @Get(value = "GetCityByCoordinate")
    public Result<String> getCityByCoordinate(@RequestParam Double lng, @RequestParam Double lat) {
        Result<JemmyJson> result = getLocationData(lng, lat);

        if (result.isBlank()) {
            return result.toObject();
        }

        JemmyJson json = result.getData();

        String city = null;

        try {
            city = json.getJemmyJson("addressComponent").getString("city").replace("市", "");
        } catch (Exception ignored) {}

        return Result.<String>of(ResultCode.HTTP200).setData(city);
    }

    public static Result<JemmyJson> getIpLocationData(String ip) {
        var httpRequest = HttpRequest.newBuilder()
                .uri(AMapUri.ip(ip))
                .GET()
                .build();

        RequestResult res = request.send(httpRequest);

        if (res.isBlank()) {
            return res.toStatus();
        }

        return res.toJsonStatus();
    }

    public static Result<JemmyJson> getLocationData(Double lng, Double lat) {
        var httpRequest = HttpRequest.newBuilder()
                .uri(AMapUri.geography(lng, lat))
                .GET()
                .build();

        RequestResult res = request.send(httpRequest);

        if (res.isBlank()) {
            return res.toStatus();
        }

        return Result.<JemmyJson>HTTP200().setData(res.toJson().getJemmyJson("regeocode"));
    }
}
