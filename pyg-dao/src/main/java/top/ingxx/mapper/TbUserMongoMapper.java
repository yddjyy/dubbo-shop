package top.ingxx.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import top.ingxx.pojo.TbUserMongo;

@Component
public class TbUserMongoMapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insertOneTbUser(TbUserMongo tbUserMongo) {
        mongoTemplate.insert(tbUserMongo);
    }
}
