package top.ingxx.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import top.ingxx.entity.PageResult;
import top.ingxx.pojo.TbComments;

import java.util.List;

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
        System.out.println(spuId+":"+currentPage+":"+pageSize+"--------------------------------");
        Query query = new Query();
       query.with(new Sort(Sort.Direction.DESC, "publishtime")); // .and("isparent").is("0")
        Criteria c = Criteria.where("spuid").is(spuId);
        query.addCriteria(c);
        Long total = mongoTemplate.count(query,TbComments.class);
        System.out.println("total:"+total);
        query.skip((currentPage-1)*pageSize);
        query.limit(pageSize);
        List<TbComments> tbComments = mongoTemplate.find(query, TbComments.class);
        System.out.println("tbComments:"+tbComments.size());
        for(TbComments tbComments1 :tbComments){
            if(tbComments1.getVisits()==1){//如果已经被回复
                Query query1 = new Query();
                query1.with(new Sort(Sort.Direction.DESC, "publishtime"));
                Criteria c1 = Criteria.where("parentid").is(tbComments1.get_id());
                query1.addCriteria(c1);
                List<TbComments> tbComments2 = mongoTemplate.find(query1, TbComments.class);
                tbComments1.setList(tbComments2);
            }

        }
        return  new PageResult(total,tbComments);
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
    public PageResult findByName(String sellerid,int currentPage,int pageSize) {
        Criteria c = new Criteria();
        c.and("sellerid").is(sellerid);
        c.and("visits").is(0);
        Query query = new Query(c);
        Long total =mongoTemplate.count(query,TbComments.class);
        query.skip((currentPage-1)*pageSize);
        query.limit(pageSize);
        return new PageResult(total,mongoTemplate.find(query, TbComments.class));
    }


    public TbComments find(String id) {
        return mongoTemplate.findById(id, TbComments.class);
    }

    public void updateByid(String id,int status){
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("visits", status);
        mongoTemplate.updateFirst(query, update, TbComments.class);
    }
}
