package com.controller;

import com.po.Dto;
import com.po.ItripUser;
import com.service.ItripUserService;
import com.utils.DtoUtil;
import com.utils.EmailUtil;
import com.utils.ErrorCode;
import com.utils.MD5Util;
import com.utils.vo.ItripUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api")
public class ItripUserController {

    private Jedis jedis = new Jedis("127.0.0.1", 6379);

    @Autowired
    private ItripUserService itripUserService;

    public ItripUserService getItripUserService() {
        return itripUserService;
    }

    public void setItripUserService(ItripUserService itripUserService) {
        this.itripUserService = itripUserService;
    }

    /**
     * 手机注册
     *
     * @param request
     * @param response
     * @param itripUserVO 注册用户对象
     * @return Dto<T> 数据传输对象
     */
    @RequestMapping(value = "/registerbyphone")
    public Dto registerbyphone(HttpServletRequest request, HttpServletResponse response, @RequestBody ItripUserVO itripUserVO) {
        System.out.println("registerbyphone...............");
        System.out.println(itripUserVO.toString());
        try {
            if (itripUserVO != null) {

                ItripUser itripUser = new ItripUser();
                itripUser.setUsercode(itripUserVO.getUserCode());
                itripUser.setUsername(itripUserVO.getUserName());
                //使用MD5对用户密码进行加密
                itripUser.setUserpassword(MD5Util.getMd5(itripUserVO.getUserPassword(), 32));
                itripUser.setUsertype(0);
                System.out.println("controller：" + itripUser.toString());
                if (itripUserService.findByUserCode(itripUser)) {
                    //用户不存在
                    if (itripUserService.insert(itripUser)) {
                        //String code = SMSUtil.sendCode(itripUserVO.getUserCode());
                        jedis.setex(itripUserVO.getUserCode(), 120, "123456");
                        System.out.println("验证码：" + jedis.get(itripUserVO.getUserCode()));
                        return DtoUtil.returnSuccess();
                    } else {
                        return DtoUtil.returnFail("注册失败", ErrorCode.AUTH_USER_ALREADY_EXISTS);
                    }
                } else {
                    //用户存在
                    if (itripUserService.findActivatedbyUserCode(itripUserVO.getUserCode()) == 0 && jedis.get(itripUserVO.getUserCode()) == null) {
                        //String code = SMSUtil.sendCode(itripUserVO.getUserCode());
                        jedis.setex(itripUserVO.getUserCode(), 120, "123456");
                        System.out.println("验证码：" + jedis.get(itripUserVO.getUserCode()));
                        return DtoUtil.returnFail("该账号已注册,但未激活,请激活", ErrorCode.AUTH_USER_ALREADY_EXISTS);
                    } else {
                        return DtoUtil.returnFail("该账号已注册激活成功，请登录", ErrorCode.AUTH_USER_ALREADY_EXISTS);
                    }
                }
            } else {
                return DtoUtil.returnFail("注册失败", ErrorCode.AUTH_UNKNOWN);
            }
        } catch (Exception e) {
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_UNKNOWN);
        }
    }

    /**
     * 邮箱验证
     *
     * @param request
     * @param response
     * @param name
     * @return
     */
    @RequestMapping(value = "/ckusr")
    public Dto ckusr(HttpServletRequest request, HttpServletResponse response, String name) {
        System.out.println("ckusr..........");
        ItripUser itripUser = new ItripUser();
        itripUser.setUsercode(name);
        if (itripUserService.findByUserCode(itripUser)) {
            return DtoUtil.returnSuccess("邮箱可以使用");
        }else {
            return DtoUtil.returnFail("邮箱已存在", ErrorCode.AUTH_USER_ALREADY_EXISTS);
        }

    }

    @RequestMapping(value = "/doregister")
    public Dto doregister(HttpServletRequest request,HttpServletResponse response,@RequestBody ItripUserVO itripUserVO){
        System.out.println("doregister........");
        System.out.println(itripUserVO.toString());
        if(null!=itripUserVO){
            ItripUser itripUser = new ItripUser();
            itripUser.setUsercode(itripUserVO.getUserCode());
            itripUser.setUsername(itripUserVO.getUserName());
            itripUser.setUserpassword(MD5Util.getMd5(itripUserVO.getUserPassword(),32));
            itripUser.setUsertype(1);

            if(itripUserService.findByUserCode(itripUser)){
                //邮箱未注册
                if(itripUserService.insert(itripUser)){
                    String emailregister = EmailUtil.emailregister(itripUser);
                    jedis.setex(itripUserVO.getUserCode(),120,emailregister);
                    System.out.println("邮箱验证码："+emailregister);
                    return DtoUtil.returnSuccess();
                }else {
                    return DtoUtil.returnFail("注册失败",ErrorCode.AUTH_USER_ALREADY_EXISTS);
                }
            }else {
                //邮箱已注册
                if(itripUserService.findActivatedbyUserCode(itripUserVO.getUserCode())==0&&null==jedis.get(itripUserVO.getUserCode())){
                    String emailregister = EmailUtil.emailregister(itripUser);
                    jedis.setex(itripUserVO.getUserCode(),120,emailregister);
                    System.out.println("邮箱验证码："+emailregister);
                    return DtoUtil.returnFail("邮箱已注册，验证码失效，请重新激活",ErrorCode.AUTH_AUTHENTICATION_FAILED);
                }else {
                    return DtoUtil.returnFail("激活码错误",ErrorCode.AUTH_USER_ALREADY_EXISTS);
                }
            }
        }
        return DtoUtil.returnFail("注册失败",ErrorCode.AUTH_USER_ALREADY_EXISTS);

    }
    /**
     * 账号激活
     *
     * @param request
     * @param response
     * @param user     激活的手机或者邮箱号
     * @param code     密码
     * @return
     */
    @RequestMapping(value = "/validatephone")
    public Dto validatephone(HttpServletRequest request, HttpServletResponse response, String user, String code) {
        /*
         * 账号激活
         *   1.查询redis数据库中有没有激活码。（有-激活，没有-失败）
         *       2.有-
         *           判断激活码是否相等（=成功，修改。   !=失败。）
         * */
        System.out.println("validatephone..........");

        if (null != jedis.get(user)) {
            if (jedis.get(user).equals(code)) {
                ItripUser itripUser = new ItripUser();
                itripUser.setUsercode(user);
                itripUser.setActivated(1);
                itripUserService.updateActivated(itripUser);
                return DtoUtil.returnSuccess("激活成功");
            }
            return DtoUtil.returnFail("验证码错误，激活失败",ErrorCode.AUTH_ACTIVATE_FAILED);
        } else {
            return DtoUtil.returnFail("激活失败", ErrorCode.AUTH_AUTHENTICATION_FAILED);
        }
    }

    /**
     * 单点激活
     *
     * @param request
     * @param response
     * @param user
     * @param code
     * @return
     */
    @RequestMapping(value = "/activate")
    public Dto activate(HttpServletRequest request, HttpServletResponse response, String user, String code) {
        System.out.println("activate..........");
        if(user.lastIndexOf(".") == -1){
            if (null != jedis.get(user)) {
                if (jedis.get(user).equals(code)) {
                    ItripUser itripUser = new ItripUser();
                    itripUser.setUsercode(user);
                    itripUser.setActivated(1);
                    itripUserService.updateActivated(itripUser);
                    return DtoUtil.returnSuccess("激活成功");
                }
                return DtoUtil.returnFail("验证码错误，激活失败",ErrorCode.AUTH_ACTIVATE_FAILED);
            } else {
                //String sendCode = SMSUtil.sendCode(user);
                jedis.setex(user, 120, "123456");
                System.out.println("验证码：" + jedis.get(user));
                return DtoUtil.returnFail("验证码失效，激活失败", ErrorCode.AUTH_AUTHENTICATION_FAILED);
            }
        }else {
            if (null != jedis.get(user)) {
                if (jedis.get(user).equals(code)) {
                    ItripUser itripUser = new ItripUser();
                    itripUser.setUsercode(user);
                    itripUser.setActivated(1);
                    itripUserService.updateActivated(itripUser);
                    return DtoUtil.returnSuccess("激活成功");
                }
                return DtoUtil.returnFail("验证码错误，激活失败",ErrorCode.AUTH_ACTIVATE_FAILED);
            } else {
                ItripUser itripUser = new ItripUser();
                itripUser.setUsercode(user);
                itripUser.setUsername(code);
                String emailregister = EmailUtil.emailregister(itripUser);
                jedis.setex(user,120,emailregister);
                System.out.println("验证码：" + jedis.get(user));
                return DtoUtil.returnFail("验证码失效，激活失败", ErrorCode.AUTH_AUTHENTICATION_FAILED);
            }
        }





    }


}
