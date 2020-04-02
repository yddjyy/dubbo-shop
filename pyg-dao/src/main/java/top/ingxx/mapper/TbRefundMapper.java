package top.ingxx.mapper;

import org.apache.ibatis.annotations.Param;
import top.ingxx.pojo.TbRefund;
import top.ingxx.pojo.TbRefundExample;

import java.util.List;

public interface TbRefundMapper {
    long countByExample(TbRefundExample example);

    int deleteByExample(TbRefundExample example);

    int deleteByPrimaryKey(String id);

    int insert(TbRefund record);

    int insertSelective(TbRefund record);

    List<TbRefund> selectByExample(TbRefundExample example);

    TbRefund selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") TbRefund record, @Param("example") TbRefundExample example);

    int updateByExample(@Param("record") TbRefund record, @Param("example") TbRefundExample example);

    int updateByPrimaryKeySelective(TbRefund record);

    int updateByPrimaryKey(TbRefund record);
}