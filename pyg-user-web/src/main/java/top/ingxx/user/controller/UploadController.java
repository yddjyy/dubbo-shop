package top.ingxx.user.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.ingxx.untils.entity.PygResult;
import top.ingxx.untils.until.OssUtils;


@RestController
@RequestMapping("/file")
public class UploadController {

    //@Value("${FILE_SERVER_URL}")
    //private String file_server_url;

    @RequestMapping("/upload")
    public PygResult upload(MultipartFile file) {
        System.out.println("图片----------------------------------------------------------------------------------");
        String filename = file.getOriginalFilename();
        String extName = filename.substring(filename.lastIndexOf(".") + 1);//获得扩展名
        try {
            OssUtils ossUtils = new OssUtils();
           return  ossUtils.upLoadFile(file.getInputStream(),filename);
//            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
//            String fileId = client.uploadFile(file.getBytes(), extName);
//            String url = file_server_url + fileId;//图片完整地址
//            return new PygResult(true,url);
        } catch (Exception e) {
           e.printStackTrace();
           return new PygResult(false,"上传失败");
        }
    }
}
