package cn.ac.sict.core;

import cn.ac.sict.constant.Constant;
import cn.ac.sict.util.HttpUtils;
import cn.ac.sict.util.LogUtils;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * 分块下载任务
 */
public class DownloaderTask implements Callable<Boolean> {

    private String url;
    //下载起始位置
    private long startPos;
    //下载结束位置
    private long endPos;
    //标识当前是哪一部分
    private int part;

    private CountDownLatch countDownLatch;

    public DownloaderTask(String url, long startPos, long endPos, int part, CountDownLatch countDownLatch) {
        this.url = url;
        this.startPos = startPos;
        this.endPos = endPos;
        this.part = part;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Boolean call() throws Exception {
        //获取文件名
        String httpFileName = HttpUtils.getHttpFileName(url);
        //分块的文件名
        httpFileName = httpFileName + ".temp" + part;
        //下载路径
        httpFileName = Constant.PATH + httpFileName;
        //获取分块下载链接
        HttpURLConnection httpURLConnection = HttpUtils.getHttpURLConnection(url, startPos, endPos);

        try (
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                RandomAccessFile accessFile = new RandomAccessFile(httpFileName, "rw");
        ){
            byte[] buffer = new byte[Constant.BYTE_SIZE];
            int len = -1;
            //循环读取数据
            while ((len = bufferedInputStream.read(buffer)) != -1){
                //1秒内下载数据之和,通过原子类进行操作
                DownloadInfo.downSize.add(len);
                accessFile.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e){
            LogUtils.error("下载文件不存在！{}", url);
            return false;
        } catch (Exception e){
            LogUtils.error("下载异常！");
            return false;
        } finally {
            httpURLConnection.disconnect();
            //减一操作，都完成置零
            countDownLatch.countDown();
        }

        return true;
    }
}
