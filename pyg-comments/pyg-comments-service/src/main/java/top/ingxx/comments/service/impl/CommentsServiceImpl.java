package top.ingxx.comments.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import top.ingxx.comments.service.CommentsService;
import top.ingxx.entity.PageResult;
import top.ingxx.mapper.TbCommentsMapper;
import top.ingxx.pojo.TbComments;
import top.ingxx.untils.until.IdWorker;

@Service
public class CommentsServiceImpl implements CommentsService {
    @Autowired
    TbCommentsMapper tbCommentsMapper;

    @Autowired
    IdWorker idWorker;
    @Override
    public PageResult findAll(int currentPage, int pageSize) {
        return tbCommentsMapper.findAll( currentPage, pageSize);
    }

    @Override
    public void insertOneTbComments(TbComments tbComments) {
        tbComments.set_id(idWorker.nextId()+"");
         tbCommentsMapper.insertOneTbComments(tbComments);
    }

    @Override
    public PageResult queryTbCommentsBySpuId(String spuid, int currentPage, int PageSize) {
        return tbCommentsMapper.queryTbCommentsBySpuId(spuid,currentPage,PageSize);
    }

    @Override
    public PageResult queryTbCommentsByParentsId(String parentsid, int currentPage, int pageSize) {
        return tbCommentsMapper.queryTbCommentsByParentsId(parentsid,currentPage,pageSize);
    }

    @Override
    public void deleteOneTbComments(String id) {
        tbCommentsMapper.deleteOneTbComments(id);
    }

    @Override
    public PageResult findByName(String nickname, int currentPage, int pageSize) {
        return tbCommentsMapper.findByName(nickname,currentPage,pageSize);
    }

    @Override
    public TbComments find(String id) {
        return tbCommentsMapper.find(id);
    }
}
