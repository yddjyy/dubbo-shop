package top.ingxx.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import top.ingxx.mapper.TbProvincesMapper;
import top.ingxx.pojo.TbProvinces;
import top.ingxx.pojo.TbProvincesExample;
import top.ingxx.untils.entity.PageResult;
import top.ingxx.user.service.ProvincesService;

import java.util.List;
@Service
public class ProvincesServiceImpl implements ProvincesService {
    @Autowired
    TbProvincesMapper tbProvincesMapper;
    @Override
    public List<TbProvinces> findAll() {
        return tbProvincesMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbProvinces> page=   (Page<TbProvinces>) tbProvincesMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void add(TbProvinces tbProvinces) {
        tbProvincesMapper.insert(tbProvinces);
    }

    @Override
    public void update(TbProvinces tbProvinces) {
        tbProvincesMapper.updateByPrimaryKey(tbProvinces);
    }

    @Override
    public TbProvinces findOne(Long id) {
        return tbProvincesMapper.selectByPrimaryKey(Integer.valueOf(String.valueOf(id)));
    }

    @Override
    public void delete(Long[] ids) {
        for(Long id:ids){
            tbProvincesMapper.deleteByPrimaryKey(Integer.valueOf(String.valueOf(id)));
        }
    }

    @Override
    public PageResult findPage(TbProvinces tbProvinces, int pageNum, int pageSize) {

        return null;
    }

    @Override
    public TbProvinces findProvinceidId(String provinceid) {
        TbProvincesExample tbProvincesExample = new TbProvincesExample();
        TbProvincesExample.Criteria criteria = tbProvincesExample.createCriteria();
        criteria.andProvinceidEqualTo(provinceid);
        List<TbProvinces> tbProvinces = tbProvincesMapper.selectByExample(tbProvincesExample);
        if(tbProvinces.size()>0){
            return tbProvinces.get(0);
        }
        return null;
    }
}
