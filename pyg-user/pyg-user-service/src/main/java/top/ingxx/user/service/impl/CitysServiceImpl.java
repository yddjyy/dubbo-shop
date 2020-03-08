package top.ingxx.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import top.ingxx.mapper.TbCitiesMapper;
import top.ingxx.pojo.TbCities;
import top.ingxx.pojo.TbCitiesExample;
import top.ingxx.untils.entity.PageResult;
import top.ingxx.user.service.CitysService;

import java.util.List;
@Service
public class CitysServiceImpl implements CitysService {
    @Autowired
    TbCitiesMapper tbCitiesMapper;
    @Override
    public List<TbCities> findAll() {
            return tbCitiesMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbCities> page=   (Page<TbCities>) tbCitiesMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void add(TbCities tbCities) {
        tbCitiesMapper.insert(tbCities);
    }

    @Override
    public void update(TbCities tbCities) {
        tbCitiesMapper.updateByPrimaryKey(tbCities);
    }

    @Override
    public TbCities findOne(Long id) {
        return tbCitiesMapper.selectByPrimaryKey(Integer.valueOf(String.valueOf(id)));
    }

    @Override
    public void delete(Long[] ids) {
        for(Long id:ids){
            tbCitiesMapper.deleteByPrimaryKey(Integer.valueOf(String.valueOf(id)));
        }
    }

    @Override
    public PageResult findPage(TbCities tbCities, int pageNum, int pageSize) {
        return null;
    }

    @Override
    public List<TbCities> findListByProvinceidId(String provinceid) {
        TbCitiesExample example=new TbCitiesExample();
        TbCitiesExample.Criteria criteria = example.createCriteria();
        criteria.andProvinceidEqualTo(provinceid);
        return tbCitiesMapper.selectByExample(example);
    }
}
