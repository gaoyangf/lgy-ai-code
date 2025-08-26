package com.lgy.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.lgy.model.entity.App;
import com.lgy.mapper.AppMapper;
import com.lgy.service.AppService;
import org.springframework.stereotype.Service;

/**
 * 应用 服务层实现。
 *
 * @author gy
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{

}
