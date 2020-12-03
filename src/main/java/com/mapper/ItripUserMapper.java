package com.mapper;

import com.po.ItripUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ItripUserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ItripUser record);

    int insertSelective(ItripUser record);

    ItripUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ItripUser record);

    int updateByPrimaryKey(ItripUser record);

    /**
     * 根据用户码查询该用户是否存在
     * @param itripUser
     * @return
     */
    public ItripUser findByUserCode(ItripUser itripUser);

    /**
     * 通过用户账号或者邮箱，激活账号
     * @param itripUser
     * @return
     */
    public Integer updateActivated(ItripUser itripUser);

    /**
     * 根据用户码查看用户状态
     * @param usercode
     * @return
     */
    public Integer findActivatedbyUserCode(String usercode);
}