package com.service.impl;

import com.mapper.ItripUserMapper;
import com.po.ItripUser;
import com.service.ItripUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ItripUserServiceImpl implements ItripUserService {
    @Autowired
    private ItripUserMapper itripUserMapper;

    public ItripUserMapper getItripUserMapper() {
        return itripUserMapper;
    }

    public void setItripUserMapper(ItripUserMapper itripUserMapper) {
        this.itripUserMapper = itripUserMapper;
    }

    @Override
    public boolean insert(ItripUser itripUser) {
        if (itripUserMapper.insert(itripUser)>0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean findByUserCode(ItripUser itripUser) {
        ItripUser user = itripUserMapper.findByUserCode(itripUser);
        if (user == null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateActivated(ItripUser itripUser) {
        if (itripUserMapper.updateActivated(itripUser)>0){
            return true;
        }
        return false;
    }

    @Override
    public Integer findActivatedbyUserCode(String usercode) {
        return itripUserMapper.findActivatedbyUserCode(usercode);
    }
}
