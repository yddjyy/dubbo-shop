package top.ingxx.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ingxx.manager.service.BrandService;
import top.ingxx.pojo.TbBrand;
import top.ingxx.untils.entity.PageResult;
import top.ingxx.untils.entity.PygResult;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    BrandService brandService;

    /**
     * 查询所有品牌
     */
    @RequestMapping("/findAll")
    public List<TbBrand> findAll() {
        List<TbBrand> brands = brandService.findAll();
        return brands;
    }

    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer rows) {
        PageResult result = brandService.findPage(page, rows);
        return result;
    }

    @RequestMapping("/add")
    public PygResult add(@RequestBody TbBrand brand) {
        try {
            brandService.add(brand);
            return new PygResult(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "添加失败");
        }
    }

    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
        TbBrand brand = brandService.findOne(id);
        return brand;
    }

    @RequestMapping("/update")
    public PygResult update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return new PygResult(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "修改失败");
        }
    }

    @RequestMapping("/delete")
    public PygResult delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new PygResult(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "删除失败");
        }
    }

    /**
     * 条件分页搜索
     * @param brand
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand brand,Integer page, Integer rows){
        PageResult result = brandService.findPage(brand, page, rows);
        return result;
    }

    /**
     * 获取品牌下拉列表
     * @return
     */
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
}
