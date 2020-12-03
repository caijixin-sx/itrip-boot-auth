package com.service;

import com.po.ItripUser;

public interface ItripUserService {

    public boolean insert(ItripUser itripUser);

    public boolean findByUserCode(ItripUser itripUser);

    public boolean updateActivated(ItripUser itripUser);

    public Integer findActivatedbyUserCode(String usercode);
}
