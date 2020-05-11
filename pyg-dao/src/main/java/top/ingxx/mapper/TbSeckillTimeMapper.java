package top.ingxx.mapper;

import org.apache.ibatis.annotations.Param;
import top.ingxx.pojo.TbSeckillTime;
import top.ingxx.pojo.TbSeckillTimeExample;

import java.util.List;

public interface TbSeckillTimeMapper {
    long countByExample(TbSeckillTimeExample example);

    int deleteByExample(TbSeckillTimeExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TbSeckillTime record);

    int insertSelective(TbSeckillTime record);

    List<TbSeckillTime> selectByExample(TbSeckillTimeExample example);

    TbSeckillTime selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TbSeckillTime record, @Param("example") TbSeckillTimeExample example);

    int updateByExample(@Param("record") TbSeckillTime record, @Param("example") TbSeckillTimeExample example);

    int updateByPrimaryKeySelective(TbSeckillTime record);

    int updateByPrimaryKey(TbSeckillTime record);
}