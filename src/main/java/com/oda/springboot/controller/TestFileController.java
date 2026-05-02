package com.oda.springboot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oda.springboot.common.Constants;
import com.oda.springboot.common.Result;
import com.oda.springboot.entity.OnlineDate;
import com.oda.springboot.entity.TestFiles;
import com.oda.springboot.mapper.ResultMapper;
import com.oda.springboot.mapper.TestFileMapper;
import com.oda.springboot.utils.PropertyUtil;
import com.oda.springboot.utils.UsePythonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@RestController
@RequestMapping("/DataTest")
@Transactional
public class TestFileController {

    @Autowired
    private PropertyUtil propertyUtil;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TestFileMapper testFileMapper;

    @Autowired
    private ResultMapper resultMapper;

    @PostMapping("/upload")
    public Result upload(@RequestParam MultipartFile file) throws IOException, InterruptedException {
        String originalFilename = file.getOriginalFilename();
        // 防止文件名空指针
        if (StrUtil.isBlank(originalFilename)) {
            return Result.error("400", "文件名不能为空");
        }

        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();
        String uuid = "";
        synchronized (uuid) {
            uuid = (UUID.randomUUID().toString()).replace("-", "");
        }
        String fileUUID = uuid + StrUtil.DOT + type;

        String uploadDir = propertyUtil.getPythonDataTestUpload();
        if (StrUtil.isBlank(uploadDir)) {
            return Result.error("500", "服务器配置错误，上传路径未配置");
        }

        // 确保路径以分隔符结尾
        if (!uploadDir.endsWith("/") && !uploadDir.endsWith("\\")) {
            uploadDir += File.separator;
        }

        File uploadFile = new File(uploadDir + fileUUID);
        File parentFile = uploadFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        String url;
        // 注意:file.getInputStream() 被消费后不能再次读取,这里先做MD5 再transferTo 是对的
        // 注意:SecureUtil.md5(InputStream) 会关闭流吗? Hutool 通常不会关闭,但为了安全最好重新获取或确保顺序
        // 这里保持你原有逻辑,假设Hutool 处理得当
        byte[] bytes = file.getBytes();
        String md5 = SecureUtil.md5(Arrays.toString(bytes));

        // 写入文件
        FileUtil.writeBytes(bytes, uploadFile);

        url = "http://localhost:9090/DataTest/" + fileUUID;

        String userId = stringRedisTemplate.opsForValue().get("userId");
        System.out.println("---------------------------------- 上传用户 ID: " + userId);

        TestFiles saveFile = new TestFiles();
        saveFile.setName(originalFilename);
        saveFile.setFileName(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size / 1024);
        saveFile.setUrl(url);
        saveFile.setMd5(md5);
        // 防止 userId 为空或格式错误
        if (StrUtil.isBlank(userId)) {
            return Result.error("401", "用户未登录");
        }
        saveFile.setUserid(Integer.parseInt(userId));
        saveFile.setEnable(0);
        saveFile.setJsonUrl(null);

        testFileMapper.insert(saveFile);
        flushRedis(Constants.FILES_KEY);
        return Result.success();
    }

    /**
     * 预测接口 - 保持原有逻辑
     */
    @GetMapping("/getUrl/{url}")
    public Result debugging(@PathVariable String url) throws IOException {
        System.out.println("====== 开始预测流程 ======");
        long stime = System.currentTimeMillis();

        LambdaQueryWrapper<TestFiles> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TestFiles::getUrl, "http://localhost:9090/DataTest/" + url);
        TestFiles testFiles = testFileMapper.selectOne(queryWrapper);

        if (ObjectUtil.isEmpty(testFiles)) {
            return Result.error("404", "文件记录不存在");
        }

        if (!ObjectUtil.isEmpty(testFiles.getEnable()) && "1".equals(testFiles.getEnable())) {
            return Result.error("505", "已完成，请查看结果");
        }

        String csvDir = propertyUtil.getPythonDataTestUpload();
        if (StrUtil.isBlank(csvDir)) {
            return Result.error("500", "上传路径未配置");
        }
        if (!csvDir.endsWith("/") && !csvDir.endsWith("\\")) {
            csvDir += File.separator;
        }

        File uploadFile = new File(csvDir + url);
        if (!uploadFile.exists()) {
            return Result.error("506", "物理文件不存在:" + uploadFile.getAbsolutePath());
        }

        String jsonFileNameNoExt = UUID.randomUUID().toString();
        String jsonFileName = jsonFileNameNoExt + ".json";

        String predictTitle = "糖尿病筛查预测_" + System.currentTimeMillis();

        String modelPath = propertyUtil.getPythonModelPath();
        if (StrUtil.isBlank(modelPath)) {
            modelPath = propertyUtil.getPythonDownload() + "diabetes_model.pth";
        }
        File modelFile = new File(modelPath);

        if (!modelFile.exists()) {
            System.err.println("!!! 模型文件缺失!" + modelPath);
            return Result.error("508", "模型文件不存在");
        }

        String jsonDir = propertyUtil.getJsonDownload();
        if (StrUtil.isBlank(jsonDir)) {
            return Result.error("500", "服务器配置错误，JSON 下载路径未配置");
        }
        File jsonDirFile = new File(jsonDir);
        if (!jsonDirFile.exists()) {
            jsonDirFile.mkdirs();
        }

        String csvAbsolutePath = uploadFile.getAbsolutePath();
        String pythonArgJsonName = jsonFileName;

        System.out.println("正在调用 Python... 目标文件:" + jsonFileName);
        String userDir = System.getProperty("user.dir");
        System.out.println("Java 工作目录: " + userDir);
        System.out.println("Python 脚本路径: " + propertyUtil.getPythonPredictMain());

        int exitCode = -1;
        try {
            exitCode = UsePythonUtils.callPython(new String[]{
                    propertyUtil.getPythonExe(),
                    propertyUtil.getPythonPredictMain(),
                    csvAbsolutePath,
                    pythonArgJsonName,
                    predictTitle,
                    modelFile.getAbsolutePath()
            });
            System.out.println("Python 退出码:" + exitCode);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("509", "调用 Python 异常:" + e.getMessage());
        }

        if (exitCode != 0) {
            System.err.println("Python 执行失败!");
            return Result.error("507", "预测算法执行失败 (Exit Code: " + exitCode + ")");
        }

        // --- 核心修复:全局搜索文件 ---
        // 确保 jsonDir 格式正确
        if (!jsonDir.endsWith("/") && !jsonDir.endsWith("\\")) {
            jsonDir += File.separator;
        }

        File targetConfigFile = new File(jsonDir + jsonFileName);
        File foundFile = null;

        if (targetConfigFile.exists()) {
            System.out.println("文件已在正确位置生成!");
            foundFile = targetConfigFile;
        } else {
            System.out.println("⚠️ 文件未在预期位置找到,启动全局搜索...");
            System.out.println("   搜索文件名:" + jsonFileName);
            System.out.println("   预期路径:" + targetConfigFile.getAbsolutePath());

            Path startPath = Paths.get(userDir);

            try (Stream<Path> paths = Files.walk(startPath)) {
                foundFile = paths
                        .filter(Files::isRegularFile)
                        .filter(p -> p.getFileName().toString().equals(jsonFileName))
                        .filter(p -> !p.toString().contains("\\target\\") && !p.toString().contains("\\.git\\"))
                        .map(Path::toFile)
                        .findFirst()
                        .orElse(null);
            } catch (IOException e) {
                System.err.println("搜索文件时发生IO 异常:" + e.getMessage());
            }

            if (foundFile != null) {
                System.out.println("🎉 找到文件，文件位于:" + foundFile.getAbsolutePath());
                System.out.println("   正在移动至正确目录.");

                try {
                    if (!jsonDirFile.exists()) jsonDirFile.mkdirs();
                    boolean moved = foundFile.renameTo(targetConfigFile);
                    if (!moved) {
                        FileUtil.copy(foundFile, targetConfigFile, true);
                        foundFile.delete();
                        System.out.println("   (通过复制 + 删除方式移动成功)");
                    } else {
                        System.out.println("   (移动成功)");
                    }
                    foundFile = targetConfigFile;
                } catch (Exception e) {
                    System.err.println("移动文件失败:" + e.getMessage());
                }
            } else {
                System.err.println("严重错误:全局搜索也未找到文件!" + jsonFileName);
                // 列出 jsonDir 下的文件看看
                if (jsonDirFile.exists()) {
                    System.out.println("   配置目录下的文件列表:");
                    for (String f : jsonDirFile.list()) {
                        System.out.println("      - " + f);
                    }
                }
                return Result.error("510", "预测完成但文件丢失，请联系管理员检查后台日志");
            }
        }
        // --- 搜索结束 ---

        if (foundFile == null || !foundFile.exists()) {
            return Result.error("510", "文件最终确认失败");
        }

        System.out.println("确认文件已就绪:" + foundFile.getAbsolutePath());

        testFiles.setEnable(1);
        testFiles.setJsonUrl(jsonFileName);
        testFileMapper.updateById(testFiles);
        flushRedis(Constants.FILES_KEY);

        long etime = System.currentTimeMillis();
        System.out.println("预测全流程结束，耗时:" + (etime - stime) + "ms");
        return Result.success();
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam(defaultValue = "") String name) {
        try {
            QueryWrapper<TestFiles> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("is_delete", false);
            queryWrapper.orderByDesc("id");
            if (!"".equals(name)) {
                queryWrapper.like("name", name);
            }
            return Result.success(testFileMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("500", "查询失败: " + e.getMessage());
        }
    }

    private void flushRedis(String key) {
        stringRedisTemplate.delete(key);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        LambdaQueryWrapper<OnlineDate> onlineDateLambdaQueryWrapper = new LambdaQueryWrapper<>();
        onlineDateLambdaQueryWrapper.eq(OnlineDate::getTestfileid, id);
        resultMapper.delete(onlineDateLambdaQueryWrapper);
        testFileMapper.deleteById(id);
        flushRedis(Constants.FILES_KEY);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        QueryWrapper<OnlineDate> onlineDateQueryWrapper = new QueryWrapper<>();
        onlineDateQueryWrapper.in("testfile_id", ids);
        List<OnlineDate> onlinedates = resultMapper.selectList(onlineDateQueryWrapper);
        for (OnlineDate onlinedate : onlinedates) {
            resultMapper.deleteById(onlinedate);
        }
        QueryWrapper<TestFiles> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<TestFiles> testfiles = testFileMapper.selectList(queryWrapper);
        for (TestFiles file : testfiles) {
            testFileMapper.deleteById(file);
        }
        return Result.success();
    }

    @GetMapping("/detail/{id}")
    public Result getById(@PathVariable Integer id) {
        return Result.success(testFileMapper.selectById(id));
    }

    @GetMapping("/totle")
    public Result totle() {
        List<TestFiles> testfiles = testFileMapper.selectList(new QueryWrapper<TestFiles>());
        String today = DateUtil.today();
        Integer totle = 0;
        for (TestFiles file : testfiles) {
            Date createTime = file.getCreateTime();
            if (createTime == null) continue;
            String format = DateUtil.format(createTime, "yyyy-MM-dd");
            if (format.equals(today)) {
                totle++;
            }
        }
        return Result.success(totle);
    }

    @GetMapping("/members/{id}")
    public Result members(@PathVariable Integer id) {
        ArrayList<Long> integers = new ArrayList<>();
        for (int i = 0; i <= 1; i++) {
            LambdaQueryWrapper<OnlineDate> dateLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dateLambdaQueryWrapper.eq(OnlineDate::getTestfileid, id);
            dateLambdaQueryWrapper.eq(OnlineDate::getResult, i);
            Long aLong = resultMapper.selectCount(dateLambdaQueryWrapper);
            integers.add(aLong);
        }
        return Result.success(integers);
    }

    @GetMapping("/totle/{id}")
    public Result totle(@PathVariable Integer id) {
        LambdaQueryWrapper<OnlineDate> dateLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dateLambdaQueryWrapper.eq(OnlineDate::getTestfileid, id);
        Long aLong = resultMapper.selectCount(dateLambdaQueryWrapper);
        return Result.success(aLong);
    }

    /**
     * ⬇️️⬇️️⬇️ 重点修改:下载接口 (微调) ⬇️️⬇️️⬇️
     */
    @GetMapping("/{jsonUrl}")
    public void download(@PathVariable String jsonUrl, HttpServletResponse response) throws IOException {
        System.out.println("========== [下载请求] 开始 ==========");
        System.out.println("1. 接收到的文件名参数: [" + jsonUrl + "]");

        if (StrUtil.isBlank(jsonUrl)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":400,\"msg\":\"文件名不能为空\"}");
            return;
        }

        // 1. 数据库校验
        LambdaQueryWrapper<TestFiles> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TestFiles::getJsonUrl, jsonUrl);
        TestFiles fileRecord = testFileMapper.selectOne(queryWrapper);

        // 兼容:如果没查到且没后缀,尝试补全.json
        if (ObjectUtil.isEmpty(fileRecord) && !jsonUrl.endsWith(".json")) {
            String tempUrl = jsonUrl + ".json";
            queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TestFiles::getJsonUrl, tempUrl);
            fileRecord = testFileMapper.selectOne(queryWrapper);
            if(ObjectUtil.isNotEmpty(fileRecord)){
                jsonUrl = tempUrl;
                System.out.println("2. 自动补全后缀为: [" + jsonUrl + "]");
            }
        }

        if (ObjectUtil.isEmpty(fileRecord)) {
            System.out.println("3. 数据库未找到记录");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":404,\"msg\":\"数据库中未找到该文件记录\"}");
            return;
        }

        if (ObjectUtil.isEmpty(fileRecord.getEnable()) || !"1".equals(fileRecord.getEnable().toString())) {
            System.out.println("3. 文件尚未预测完成 (enable=" + fileRecord.getEnable() + ")");
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":409,\"msg\":\"预测尚未完成\"}");
            return;
        }

        // 2. 获取并处理路径
        String basePath = propertyUtil.getJsonDownload();
        System.out.println("4. 读取配置路径: [" + basePath + "]");

        if (StrUtil.isBlank(basePath)) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"code\":500,\"msg\":\"服务器路径配置错误\"}");
            return;
        }

        // [关键]统一使用 File.separator 确保跨平台兼容
        if (!basePath.endsWith("/") && !basePath.endsWith("\\")) {
            basePath += File.separator;
        }
        System.out.println("5. 修正后基础路径: [" + basePath + "]");

        File targetFile = new File(basePath + jsonUrl);
        System.out.println("6. 尝试访问绝对路径: [" + targetFile.getAbsolutePath() + "]");
        System.out.println("7. 文件是否存在? " + targetFile.exists());

        // 3. 容错:全局搜索
        if (!targetFile.exists()) {
            System.out.println("⚠️ [下载容错] 文件不在配置目录,尝试全局搜索...");
            Path startPath = Paths.get(System.getProperty("user.dir"));
            try (Stream<Path> paths = Files.walk(startPath)) {
                String finalJsonUrl = jsonUrl;
                File found = paths
                        .filter(Files::isRegularFile)
                        .filter(p -> p.getFileName().toString().equals(finalJsonUrl))
                        .filter(p -> !p.toString().contains("\\target\\") && !p.toString().contains("\\.git\\"))
                        .map(Path::toFile)
                        .findFirst()
                        .orElse(null);
                if (found != null) {
                    System.out.println("🎉 [下载容错] 找到文件:" + found.getAbsolutePath());
                    targetFile = found;
                } else {
                    System.out.println("⚠️ [下载容错] 全局搜索也未找到!");
                    // 打印配置目录下的文件列表辅助调试
                    File dir = new File(basePath);
                    if (dir.exists()) {
                        System.out.println("   配置目录下的文件列表:");
                        String[] list = dir.list();
                        if (list != null) {
                            for (String f : list) {
                                System.out.println("      - " + f);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("搜索时出错:" + e.getMessage());
            }
        }

        if (!targetFile.exists()) {
            System.out.println("⚠️ 最终确认文件不存在");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":404,\"msg\":\"文件未在服务器找到，请查看控制台日志\"}");
            return;
        }

        // 4. 执行下载
        System.out.println("📤 开始传输文件.");
        try (FileInputStream fis = new FileInputStream(targetFile);
             ServletOutputStream os = response.getOutputStream()) {

            response.setContentType("application/octet-stream");

            // [关键]标准文件名编码方式，兼容 Chrome/Firefox/Edge
            String encodedFileName = URLEncoder.encode(jsonUrl, "UTF-8").replace("+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

            response.setContentLengthLong(targetFile.length());

            byte[] buffer = new byte[4096]; // 增大缓冲区提高效率
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
            System.out.println("✅ 文件传输完成");
        } catch (Exception e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"code\":500,\"msg\":\"下载失败:" + e.getMessage() + "\"}");
            }
        } finally {
            System.out.println("========== [下载请求] 结束 ==========");
        }
    }
}