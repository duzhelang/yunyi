package com.cucn.springboot.service;

import com.cucn.springboot.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务?
 * </p>
 *
 * @author CUCN-cj
 * @since 2026-03-22
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> findMenus(String name);
}
