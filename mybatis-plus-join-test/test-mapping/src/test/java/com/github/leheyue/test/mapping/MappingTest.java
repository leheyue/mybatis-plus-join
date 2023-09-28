package com.github.leheyue.test.mapping;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.leheyue.test.mapping.entity.AddressDO;
import com.github.leheyue.test.mapping.entity.UserDO;
import com.github.leheyue.test.mapping.service.UserService;
import com.github.leheyue.wrapper.MPJLambdaWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * 注解一对一,一对多查询
 */
@SpringBootTest
@SuppressWarnings("unused")
class MappingTest {

    @Resource
    private UserService userService;

    @Test
    public void test() {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getPid, 5);
        List<UserDO> dos = userService.getRelation(m -> m.selectList(wrapper));
        assert dos.get(0).getPUser() != null && dos.get(0).getPUser().getPUser() == null;

        LambdaQueryWrapper<UserDO> wrapper1 = new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getPid, 5);
        List<UserDO> dos1 = userService.getRelation(m -> m.selectList(wrapper1), conf -> conf.loop(true).deep(3));
        assert dos1.get(0).getPUser() != null && dos1.get(0).getPUser().getPUser().getPUser() == null;

        LambdaQueryWrapper<UserDO> wrapper2 = new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getPid, 5);
        List<UserDO> dos2 = userService.getRelation(m -> m.selectList(wrapper2), conf -> conf.loop(true).deep(3).property(UserDO::getPUser));
        assert dos2.get(0).getPUser() != null && dos2.get(0).getPUser().getPUser().getPUser() == null;
        assert dos2.get(0).getPName() == null && dos2.get(0).getPUser().getPName() == null &&
                dos2.get(0).getPUser().getPUser().getPName() == null;
    }


    @Test
    public void testJoin() {
        MPJLambdaWrapper<UserDO> wrapper = new MPJLambdaWrapper<UserDO>()
                .selectAll(UserDO.class)
                .leftJoin(AddressDO.class, AddressDO::getId, UserDO::getAddressId);
        List<UserDO> dos = userService.listDeep(wrapper, conf -> conf.loop(true));
        System.out.println(1);
    }
}
