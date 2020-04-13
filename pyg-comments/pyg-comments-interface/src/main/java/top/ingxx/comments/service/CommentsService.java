package top.ingxx.comments.service;


import top.ingxx.entity.PageResult;
import top.ingxx.pojo.TbComments;


public interface CommentsService {
    public PageResult findAll(int currentPage, int pageSize);
    public void insertOneTbComments(TbComments tbComments);
    public PageResult queryTbCommentsBySpuId(String spuid, int currentPage, int PageSize);
    public PageResult queryTbCommentsByParentsId(String parentsid, int currentPage, int pageSize);
    public void  deleteOneTbComments(String id);
    public PageResult findByName(String sellerid, int currentPage, int pageSize);
    public TbComments find(String id);
    public void updateByid(String id,int content);
}
