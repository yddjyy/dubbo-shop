package top.ingxx.untils.until;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import org.springframework.stereotype.Component;
import top.ingxx.untils.entity.PygResult;

import java.io.InputStream;
import java.util.Calendar;
import java.util.UUID;

@Component
public  class OssUtils {
    /**
     * 地域节点
     */
    private static final String endpoint="http://oss-cn-shanghai.aliyuncs.com";
    /**
     * bucket域名
     */
    private static final String bucketpoint = "http://baiyouji.oss-cn-shanghai.aliyuncs.com/";
    /**
     * 账户
     */
    private static  final   String accessKeyId = "LTAIZm6jDKDcb4uJ";
    /**
     * 密码
     */
    private static  final   String accessKeySecret = "Ggc9bsJMGh4FcGBxXSVp4YB69bQ1PX";
    /**
     * bucket名称
     */
    private static final String bucketName ="baiyouji";

    /**
     * 文件存储目录
     */
    private static  final String filedir ="baiyou/";

    /**
     * 上传图片
     * @param inputStream
     * @param filename
     * @return
     */
    public PygResult upLoadFile(InputStream inputStream, String filename){
        //获得扩展名
        String extName = filename.substring(filename.lastIndexOf(".") + 1);
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        String pathName="";
        Calendar cal = Calendar.getInstance();
        pathName=filedir+cal.get(Calendar.YEAR)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+ UUID.randomUUID().toString()+"."+extName;
        try{
            ossClient.putObject(bucketName,pathName,inputStream);
        }catch (OSSException e){
            return new PygResult(false,"上传失败:"+e.getErrorMessage());
        }
        String url=bucketpoint+pathName;
        ossClient.shutdown();
        return new PygResult(true,url);
    }
    public void deleteObject(String path){
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        ossClient.deleteObject(bucketName,path.replace(bucketpoint,""));
        ossClient.shutdown();
    }
}
