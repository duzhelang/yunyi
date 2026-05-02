package com.oda.springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.oda.springboot.entity.Menu;
import com.oda.springboot.mapper.MenuMapper;
import com.oda.springboot.service.IMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author
 * @since 2026-03-22
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    @Override
    public List<Menu> findMenus(String name) {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort_num");
        if (StrUtil.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        List<Menu> list = list(queryWrapper);
        List<Menu> parentNodes = list.stream().filter(menu -> menu.getPid() == null || menu.getPid().equals(0)).collect(Collectors.toList());
        for (Menu menu : parentNodes) {
            List<Menu> children = list.stream().filter(m -> m.getPid() != null && menu.getId().equals(m.getPid())).collect(Collectors.toList());
            menu.setChildren(children);
        }
        return parentNodes;
    }
}
