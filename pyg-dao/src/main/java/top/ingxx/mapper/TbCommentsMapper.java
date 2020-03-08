package top.ingxx.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import top.ingxx.entity.PageResult;
import top.ingxx.pojo.TbComments;

@Component
public class TbCommentsMapper {
    @Autowired
    private MongoTemplate mongoTemplate;


    /*
    查询所有评论
     */
    public PageResult findAll(int currentPage, int pageSize) {
        Query query = new Query();
        Long total = mongoTemplate.count(query, TbComments.class);
        query.skip((currentPage-1)*pageSize);
        query.limit(pageSize);
        return  new PageResult(total,mongoTemplate.find(query, TbComments.class));
    }

    /**
     * 插入一条评论
     * @param TbComments
     */
    public void insertOneTbComments(TbComments TbComments) {
          mongoTemplate.insert(TbComments);
    }

    /**
     * 根据商品spuid分页查询顶级评论
     * @param spuId
     */
    public PageResult queryTbCommentsBySpuId(String spuId, int currentPage, int pageSize) {
        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "publishtime"));
        Criteria c = Criteria.where("spuid").is(spuId).and("parentid").is("0");
        query.addCriteria(c);
        Long total = mongoTemplate.count(query,TbComments.class);
        query.skip((currentPage-1)*pageSize);
        query.limit(pageSize);
       return  new PageResult(total,mongoTemplate.find(query, TbComments.class));
    }
    /**
     * 通过父评论id查找评论并分页
     */
    public PageResult queryTbCommentsByParentsId(String parentsId,int currentPage,int pageSize){

        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "publishtime"));
        Criteria c = Criteria.where("parentid").is(parentsId);
        query.addCriteria(c);
        Long total = mongoTemplate.count(query,TbComments.class);
        query.skip((currentPage-1)*pageSize);
        query.limit(pageSize);
        return  new PageResult(total,mongoTemplate.find(query, TbComments.class));
    }
    /**
     * 通过评论id删除评论
     * @param id
     */
    public void deleteOneTbComments(String id) {
        Criteria c = new Criteria();
        c.and("_id").is(id);
        Query query = new Query(c);
        mongoTemplate.remove(query, TbComments.class);
    }

    /*
     通过评论人昵称查找评论分页
     */
    public PageResult findByName(String nickname,int currentPage,int pageSize) {
        Criteria c = new Criteria();
        c.and("nickname").is(nickname);
        Query query = new Query(c);
        Long total =mongoTemplate.count(query,TbComments.class);
        query.skip((currentPage-1)*pageSize);
        query.limit(pageSize);
        return new PageResult(total,mongoTemplate.find(query, TbComments.class));
    }


    public TbComments find(String id) {
        return mongoTemplate.findById(id, TbComments.class);
    }
}
