package com.leheyue.test.springboot3jdk17.dto;

import com.leheyue.test.springboot3jdk17.entity.UserDO;
import com.leheyue.test.springboot3jdk17.enums.Sex;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;


/**
 * userDTO
 */
@Data
@ToString
public class UserDTO {
    /** user */
    private Integer id;
    /** user */
    private String name;
    /** user */
    private Map<String,String> json;
    /** user */
    private Sex sex;
    /** user */
    private String headImg;
    /** user */
    private String userHeadImg;//同 headImg 别名测试
    /** user_address */
    private String tel;
    /** user_address */
    private String address;
    /** user_address */
    private String userAddress;
    /** area */
    private String province;
    /** area */
    private String city;
    /** area */
    private Map<String, String> area;

    private List<AddressDTO> addressList;

    private List<Integer> addressIds;

    private List<UserDO> children;
}
