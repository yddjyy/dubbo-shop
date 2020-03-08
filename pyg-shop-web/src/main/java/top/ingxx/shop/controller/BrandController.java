package top.ingxx.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ingxx.manager.service.BrandService;
import top.ingxx.pojo.TbBrand;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    BrandService brandService;

    /**
     * 查询所有品牌
     */
    @CrossOrigin
    @RequestMapping("/findAll")
    public List<TbBrand> findAll() {
        List<TbBrand> brands = brandService.findAll();
        return brands;
    }
}
