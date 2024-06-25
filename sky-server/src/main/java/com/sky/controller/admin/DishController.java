package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result<String> saveWithFlavor(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品...dishDTO:{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        cleanCache("dish_" + dishDTO.getCategoryId());
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dto
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> pageQuery(DishPageQueryDTO dto) {
        log.info("菜品分页查询: dto:{}", dto);
        PageResult pageResult = dishService.pageQuery(dto);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result<String> deleByIds(@RequestParam List<Long> ids) {
        log.info("批量删除菜品 ids:{}", ids);
        dishService.deleteByIds(ids);
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据id查询菜品以及口味
     * @param id
     * @return
     */
    @GetMapping({"/{id}"})
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        DishVO vo = dishService.getByIdWithFlavor(id);
        return Result.success(vo);
    }

    /**
     * 修改菜品
     * @param dto
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result<String> update(@RequestBody DishDTO dto) {
        dishService.update(dto);
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 菜品的起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品的起售停售")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        dishService.startOrStop(status,id);
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 清理redis缓存
     * @param keyPattern
     */
    private void cleanCache(String keyPattern) {
        // 匹配符合条件的key
        Set keys = redisTemplate.keys(keyPattern);
        // 删除key
        redisTemplate.delete(keys);
    }
}
