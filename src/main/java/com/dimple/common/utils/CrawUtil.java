package com.dimple.common.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class CrawUtil {

    private static RedisUtil redisUtil = new RedisUtil();

    /***
     *
     * @param args fdgf
     * @throws IOException gfds
     */
    public static void main(String[] args) throws IOException {
        String novelName = "十方乾坤";
        //设置下载文件存放磁盘的位置
        File file = new File("d:\\" + novelName);
        //判断文件夹是否存在，不存在就创建
        if (!file.exists()) {
            file.mkdirs();
        }
        File f = new File(file, novelName + ".txt");
//        if (file.exists()) {
//            Writer out = new FileWriter(f);
//            out.write("");
//            out.close();
//            System.out.println("源内容清除成功");
//        }
        //规定要爬取的网页
        String url = "https://www.bswtan.com/24/24695/";
        Connection conn = Jsoup.connect(url);
        //伪装成为浏览器，有的网站爬取数据会阻止访问，伪装成浏览器可以访问，这里我伪装成Google浏览器
        Document doc = conn.header("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit"
                        + "/537.36 (KHTML, like Gecko) Chrome/89.0.4389.128 Safari/537.36").get();
        //寻找名叫panel-body的div，接着再在里面寻找叫list-group.list-charts的ul，再在里面寻找li，再在li里寻找a
        Elements as = doc.select("div#list dl dd a");
        ArrayList<NodeObject> list = new ArrayList<>();
        for (Element a : as) {
            //对于每一个元素取出拿到href和小说名字
            String href = a.attr("href");
            String title = a.text();
            NodeObject nodeObject = new NodeObject(href, title);
            list.add(nodeObject);

        }

        saveNovel(url, f, list);
        System.out.println(novelName + "爬虫成功！");
    }

    public static void saveNovel(String url, File f, ArrayList<NodeObject> list) {
        String title1 = redisUtil.getRedisCache("chapters");

        if (title1 == null) {
            for (NodeObject nodeObject : list) {
                //对于每一个元素取出拿到href和小说名字
                String href = nodeObject.getHref();
                String title = nodeObject.getTitle();
                //file:表示存放文件的位置
                //href:表示要读取内容的页面
                //title:表示存放文件名称
                try {
                    save(url, f, href, title);
                } catch (SocketTimeoutException e) {
                    System.out.println("读取数据超时，重新读取。。。");
                    saveNovel(url, f, list);
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        } else {
            int num = 0;
            for (int i = 0; i < list.size(); i++) {
                //对于每一个元素取出拿到href和小说名字
                String title = list.get(i).getTitle();
                if (_title.equals(title)) {
                    num = i + 1;
                    break;
                }
            }
            for (int i = num; i < list.size(); i++) {
                String href = list.get(i).getHref();
                String title = list.get(i).getTitle();
                //file:表示存放文件的位置
                //href:表示要读取内容的页面
                //title:表示存放文件名称
                try {
                    save(url, f, href, title);
                } catch (SocketTimeoutException e) {
                    System.out.println("读取数据超时，重新读取。。。");
                    saveNovel(url, f, list);
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        }
    }

    private static void save(String shortUrl, File file, String href, String title) throws SocketTimeoutException,
            IOException {
        //在file目录下创建每一个章命名的txt文件
        //构建输出流对象，因为小说的内容是字符类型的数据
        Writer out = new FileWriter(file, true);
        //构建读取页面的url
        String url = shortUrl + href;
        Connection conn = Jsoup.connect(url);
        //伪装成为浏览器
        Document doc = conn.get();

        //获取小说的正文
        String content = doc.select("div#content").html();
        //处理特殊数据
        content = content.replace("//本站百度搜23文学网即可找到本站.//", "");
        content = content.replace("&#xFEFF;", "");
        content = content.replace("<br>", "");
        content = content.replace("&nbsp;", " ");
        content = "\r\n" + title + "\r\n" + content;
        out.write(content);
        out.close();

        redisUtil.setRedisCache("chapters", title);
        System.out.println(title + "  写入成功");
        //需要使用休眠，为了防止网站检测为蓄意攻击，停止我们的IP访问

        int n = (int) (Math.random() * 1000 + 100);
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
