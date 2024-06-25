package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺营业状态")
@Slf4j
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String KEY = "SHOP_STATUS";

    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result<String> setStatus(@PathVariable Integer status) {
        log.info("设置店铺的营业状态:{}", status);
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("获取店铺的营业状态")
    public Result<Object> getStatus(){
        Object status = redisTemplate.opsForValue().get(KEY);
        return Result.success(status);
    }
}
