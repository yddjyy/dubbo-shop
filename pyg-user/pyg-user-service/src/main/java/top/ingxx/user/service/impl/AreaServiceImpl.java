package top.ingxx.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import top.ingxx.mapper.TbAreasMapper;
import top.ingxx.pojo.TbAreas;
import top.ingxx.pojo.TbAreasExample;
import top.ingxx.untils.entity.PageResult;
import top.ingxx.user.service.AreasService;

import java.util.List;
@Service
public class AreaServiceImpl implements AreasService {
    @Autowired
    TbAreasMapper tbAreasMapper;
    @Override
    public List<TbAreas> findAll() {
        return tbAreasMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbAreas> page=   (Page<TbAreas>) tbAreasMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void add(TbAreas tbAreas) {
        tbAreasMapper.insert(tbAreas);
    }

    @Override
    public void update(TbAreas tbAreas) {
        tbAreasMapper.updateByPrimaryKey(tbAreas);
    }

    @Override
    public TbAreas findOne(Long id) {
        return tbAreasMapper.selectByPrimaryKey(Integer.valueOf(String.valueOf(id)));
    }

    @Override
    public void delete(Long[] ids) {
        for(Long id:ids){
            tbAreasMapper.deleteByPrimaryKey(Integer.valueOf(String.valueOf(id)));
        }
    }

    @Override
    public PageResult findPage(TbAreas tbAreas, int pageNum, int pageSize) {
        return  null;
    }

    @Override
    public List<TbAreas> findListByCitiesId(String cities) {
        TbAreasExample example=new TbAreasExample();
        TbAreasExample.Criteria criteria = example.createCriteria();
        criteria.andCityidEqualTo(cities);
        return tbAreasMapper.selectByExample(example);
    }
}
