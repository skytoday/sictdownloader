package cn.ac.sict.core;

import cn.ac.sict.constant.Constant;

import java.util.concurrent.atomic.LongAdder;

/**
 * 展示下载信息
 */
public class DownloadInfo implements Runnable{
    //下载文件总大小
    private long httpFileContentLength;

    //本地已下载文件的大小,LongAdder,AtomicLong
    public static LongAdder finishedSize = new LongAdder();

    //本次累计下载大小,volatile强制线程从主内存读数据,因为每秒累加要另一线程执行
    public static volatile LongAdder downSize = new LongAdder();

    //前一次下载大小
    public double prevSize;

    public DownloadInfo(long httpFileContentLength) {
        this.httpFileContentLength = httpFileContentLength;
    }

    @Override
    public void run() {
        //计算文件总大小
        String httpFileSize = String.format("%.2f", httpFileContentLength / Constant.MB);
        //计算每秒下载速度
        double speed = (downSize.doubleValue() - prevSize) / 1024d;
        prevSize = downSize.doubleValue();

        //计算剩余文件大小
        double remainSize = httpFileContentLength - finishedSize.doubleValue() - downSize.doubleValue();
        //估算剩余时间
        String remainTime = String.format("%.1f", remainSize / 1024d / speed);

        if ("Infinity".equalsIgnoreCase(remainTime)){
            remainTime = "-";
        }

        //已下载大小
        String currentFileSize = String.format("%.2f", (downSize.doubleValue() - finishedSize.doubleValue()) / Constant.MB);

        String downInfo = String.format("已下载 %smb/%smb,速度 %skb/s,剩余时间 %ss", currentFileSize, httpFileSize, speed, remainTime);

        System.out.print("\r");
        System.out.print(downInfo);
    }


}
