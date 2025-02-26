package com.kings.demo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 描述信息
 *
 * @author lun.wang
 * @date 2021/8/30 10:27 下午
 * @since v1.0
 */
@RestController
@RequiredArgsConstructor
public class MybatisPlusDemo {
    @Getter
    @Setter
    @TableName("users")
    public static class User {
        @TableId
        private Long id;
        private String name;
        private String email;
        private String address;
        private String phone;
        private Date birthday;
    }

    @Mapper
    interface UserMapper extends BaseMapper<User> {
    }

    private final UserMapper mapper;

    @GetMapping("/users")
    public List<User> users(@RequestParam("birthday") String birthday) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.le(User::getBirthday, birthday);
        return mapper.selectList(lambdaQueryWrapper);
    }
}
