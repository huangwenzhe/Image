package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.Image;
import dao.ImageDao;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ImageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String imageId = req.getParameter("imageId");
        if(imageId == null || imageId.equals("")){
            selectAll(req,resp);
        }else {
            selectOne(imageId,req,resp);
        }
    }

    private void selectAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        ImageDao imageDao = new ImageDao();
        List<Image>list = imageDao.selectAll();
        Gson gson = new GsonBuilder().create();
        String jsonData = gson.toJson(list);
        resp.getWriter().write(jsonData);
    }
    private void selectOne(String imageId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        ImageDao imageDao = new ImageDao();

        Image  image = imageDao.selectOne(Integer.parseInt(imageId));
        Gson gson = new GsonBuilder().create();
        String jsonData = gson.toJson(image);
        resp.getWriter().write(jsonData);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<FileItem> items = null;
        try {
            items = upload.parseRequest(req);
        } catch (FileUploadException e) {
            // 出现异常说明解析出错!
            e.printStackTrace();
            // 告诉客户端出现的具体的错误是啥
            resp.setContentType("application/json; charset=utf-8");
            resp.getWriter().write("{ \"ok\": false, \"reason\": \"请求解析失败\" }");
            return;
        }
        FileItem fileItem = items.get(0);
        Image image = new Image();
        image.setImageName(fileItem.getName());
        image.setSize((int)fileItem.getSize());
        // 手动获取一下当前日期, 并转成格式化日期, yyMMdd => 20200218
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        image.setUploadTime(simpleDateFormat.format(new Date()));
        image.setContentType(fileItem.getContentType());
        // MD5 暂时先不去计算
        image.setMd5(DigestUtils.md5Hex(fileItem.get()));
        // 自己构造一个路径来保存, 引入时间戳是为了让文件路径能够唯一
        image.setPath("./image/" + image.getMd5());
        // 存到数据库中
        ImageDao imageDao = new ImageDao();

        // 看看数据库中是否存在相同的 MD5 值的图片, 不存在, 返回 null
        Image existImage = imageDao.selectByMd5(image.getMd5());

        imageDao.insert(image);

        // 2. 获取图片的内容信息, 并且写入磁盘文件
        if (existImage == null) {
            File file = new File(image.getPath());
            try {
                fileItem.write(file);
            } catch (Exception e) {
                e.printStackTrace();

                resp.setContentType("application/json; charset=utf-8");
                resp.getWriter().write("{ \"ok\": false, \"reason\": \"写磁盘失败\" }");
                return;
            }
        }

        // 3. 给客户端返回一个结果数据
//        resp.setContentType("application/json; charset=utf-8");
//        resp.getWriter().write("{ \"ok\": true }");
        resp.sendRedirect("index.html");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");
        // 1. 先获取到请求中的 imageId
        String imageId = req.getParameter("imageId");
        if (imageId == null || imageId.equals("")) {
            resp.setStatus(200);
            resp.getWriter().write("{ \"ok\": false, \"reason\": \"解析请求失败\" }");
            return;
        }
        // 2. 创建 ImageDao 对象, 查看到该图片对象对应的相关属性(这是为了知道这个图片对应的文件路径)
        ImageDao imageDao = new ImageDao();
        Image image = imageDao.selectOne(Integer.parseInt(imageId));
        if (image == null) {
            // 此时请求中传入的 id 在数据库中不存在.
            resp.setStatus(200);
            resp.getWriter().write("{ \"ok\": false, \"reason\": \"imageId 在数据库中不存在\" }");
            return;
        }
        // 3. 删除数据库中的记录
        imageDao.deleteOne(Integer.parseInt(imageId));
        // 4. 删除本地磁盘文件
        File file = new File(image.getPath());
        file.delete();
        resp.setStatus(200);
        resp.getWriter().write("{ \"ok\": true }");
    }
}