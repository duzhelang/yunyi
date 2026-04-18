package com.oda.springboot.service;

import com.oda.springboot.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务?
 * </p>
 *
 * @author ODA-cj
 * @since 2026-03-22
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> findMenus(String name);
}
