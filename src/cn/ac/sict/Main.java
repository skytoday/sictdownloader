package cn.ac.sict;

import cn.ac.sict.core.Downloader;
import cn.ac.sict.util.LogUtils;

import java.util.Scanner;

/*
* 主类
* */
public class Main {
    public static void main(String[] args) {
        //下载地址
        String url = null;
        if (args == null || args.length == 0) {
            do {
                //System.out.println("请输入下载链接！");
                LogUtils.info("请输入下载链接地址！");
                Scanner scanner = new Scanner(System.in);
                url = scanner.next();
            } while (url == null);
        }else {
            url = args[0];
        }

        Downloader downloader = new Downloader();
        downloader.download(url);
    }
}
