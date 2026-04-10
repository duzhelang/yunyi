package com.cucn.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cucn.springboot.entity.User;
import com.cucn.springboot.controller.dto.UserDTO;
import com.cucn.springboot.controller.dto.UserPasswordDTO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务?
 * </p>
 *
 * @author CUCN-cj
 * @since 2026-03-26
 */
public interface IUserService extends IService<User> {


    UserDTO login(UserDTO userDTO);

    User register(UserDTO userDTO);

    void updatePassword(UserPasswordDTO userPasswordDTO);

    Page<User> findPage(Page<User> objectPage, String username, String email, String address);

    void saveUpdateUser(User user);


}
