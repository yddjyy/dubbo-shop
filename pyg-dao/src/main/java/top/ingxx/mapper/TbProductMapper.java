package top.ingxx.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import top.ingxx.pojo.TbProductMongo;

@Component
public class TbProductMapper {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insertOneTbUser(TbProductMongo tbProductMongo) {
        mongoTemplate.insert(tbProductMongo);
    }
}
