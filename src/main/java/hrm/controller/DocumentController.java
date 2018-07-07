package hrm.controller;

import hrm.domain.Document;
import hrm.domain.User;
import hrm.service.OtherServiceInterface;
import hrm.util.tag.PageModel;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @version V1.0
 * @Description: 处理上传下载文件请求控制器
 */

@Controller
public class DocumentController {

    @Autowired
    @Qualifier("hrmService")
    private OtherServiceInterface otherServiceInterface;


    @RequestMapping("/document/selectDocument")
    public ModelAndView selectDocument(Integer pageIndex, Document document, HttpServletRequest request) {
        //如果没有得到 title 就表明是从左边点击来的，那么就分页显示所有的
        String title = document.getTitle();

        //从request中取user，放入document中
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user_session");
        document.setUser(user);

        ModelAndView modelAndView = new ModelAndView();
        PageModel pageModel = new PageModel();

        //表示点击查询过来的
        if (pageIndex == null) {
            pageIndex = 1;
        }
        //在service层判断是否username和status为null，这里返回的userlike最好不要分页，全部返回就行了
        List<Document> documents = otherServiceInterface.findDocument(document, pageModel);

        if (documents == null || documents.size() == 0) {
            modelAndView.setViewName("document/document");
            return modelAndView;
        }

        //要在这层告诉页面 总页数
        pageModel.setPageSize(pageModel.getPageSize());
        pageModel.setRecordCount(documents.size());
        pageModel.setPageIndex(pageIndex);

        List<Document> retDocument = new ArrayList<>();
        //然后根据当前页数来选择List中返回那几条
        if (pageModel.getTotalSize() - 1 == 0) {
            //只有一页
            for (int i = 0; i < documents.size(); i++) {
                retDocument.add(documents.get(i));
            }
            //index从1开始
        } else if (pageModel.getTotalSize() == pageIndex) {
            //最后一页
            for (int i = pageModel.getPageSize() * (pageModel.getTotalSize() - 1); i < documents.size(); i++) {
                retDocument.add(documents.get(i));
            }
        } else {
            //中间页
            for (int i = (pageIndex - 1) * pageModel.getPageSize();
                 i < ((pageIndex - 1) * pageModel.getPageSize() + pageModel.getPageSize());
                 i++) {
                retDocument.add(documents.get(i));
            }
        }
        System.out.println("pageModel.getPageIndex()======" + pageModel.getPageIndex());
        System.out.println("pageModel.getTotalSize()======" + pageModel.getTotalSize());
        //将分页信息塞入
        modelAndView.addObject("pageModel", pageModel);

        modelAndView.addObject("documents", retDocument);

        modelAndView.setViewName("document/document");

        return modelAndView;

    }


    @RequestMapping("/document/addDocument")
    public ModelAndView addDocument(@ModelAttribute Document document, Integer flag,
                                    HttpServletRequest request, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        //得到是谁创建的
        User user = (User) request.getSession().getAttribute("user_session");

        //这里是上传界面的跳转操作
        if (flag == 1) {
            document.setUser(user);
            model.addAttribute("document", document);
            modelAndView.setViewName("document/showAddDocument");
            return modelAndView;
        }

        //这里是上传文件的操作
        else if (flag == 2) {
            //处理文件名fileName和文件路径file
            if (!document.getFile().isEmpty()) {
                //通过Multipart 处理上传文件
                MultipartFile file = document.getFile();
                //设置视图为json视图
                modelAndView.setView(new MappingJackson2JsonView());

                //获取原始文件名,原本的document中已经封装了一个MultipartFile
                String fileName = file.getOriginalFilename();
                System.out.println("+++++++++++++++++++++++++++++++++++" + fileName);


                //接收的文件放在/images目录下，并获得文件系统目录
                String path = request.getServletContext().getRealPath("/upload/");
                System.out.println("============================================================path=" + path);


                //把文件名塞入document
                UUID uuid = UUID.randomUUID();
                String newFileName = uuid + fileName;
                document.setFileName(newFileName);

                File filepath=new File(path,newFileName);
                if(!filepath.getParentFile().exists()){
                    filepath.getParentFile().mkdirs();
                }

                //目标文件
                File dest = new File(path + File.separator + newFileName);
                try {
                    //将文件存储到目标位置
                    file.transferTo(dest);

                    //成功，那么就存入数据库
                    document.setUser(user);
                    model.addAttribute("document", document);
                    otherServiceInterface.addDocument(document);
                    //返回视图
                    modelAndView.setViewName("redirect:/document/selectDocument");
                    return modelAndView;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            modelAndView.setViewName("document/document");
            return modelAndView;
        }

        modelAndView.setViewName("redirect:/document/selectDocument");
        return modelAndView;

    }


    @RequestMapping("/document/downLoad")
    public ResponseEntity<byte[]> downLoad(HttpServletRequest request, @RequestParam("id") Integer id,
                                           Model model) throws IOException {

        String path = request.getServletContext().getRealPath("/upload/");  //获取文件所在路径
        System.out.println("--------------------------------path==" + path);

        String fileName = otherServiceInterface.findDocumentById(id).getFileName();

        String UUIDFileName = new String(fileName.getBytes("UTF-8"), "UTF-8");

        File file=new File(path + File.separator + UUIDFileName);

        HttpHeaders headers = new HttpHeaders();
        //少了这句，可能导致下载中文文件名的文档，只有后缀名的情况
        String downloadFileName = new String(UUIDFileName.getBytes("UTF-8"),"UTF-8");
        //告知浏览器以下载方式打开
        headers.setContentDispositionFormData("attachment", downloadFileName);
        //设置MIME类型
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        //用FileUpload组件的FileUtils读取文件，并构建成ResponseEntity<byte[]>返回给浏览器
        //HttpStatus.CREATED是HTTP的状态码201

        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
    }


    @RequestMapping("/document/updateDocument")
    public ModelAndView updateDocument(HttpServletRequest request, Model model, Integer flag, Integer id) {
        //得到是哪个鬼修改我的文件的
        User user = (User) request.getSession().getAttribute("user_session");
        model.addAttribute("user", user);

        ModelAndView modelAndView = new ModelAndView();

        Document documentById = otherServiceInterface.findDocumentById(id);

        //这里是查询界面的跳转操作
        if (flag == 1) {
            model.addAttribute("document", documentById);
            modelAndView.setViewName("document/showUpdateDocument");
            return modelAndView;
        }
        //这里是修改页面的修改操作
        else if (flag == 2) {
            documentById.setUser(user);
            model.addAttribute("document", documentById);
            otherServiceInterface.modifyDocument(documentById);
            modelAndView.setViewName("redirect:/document/selectDocument");
        }


        return modelAndView;
    }
}
