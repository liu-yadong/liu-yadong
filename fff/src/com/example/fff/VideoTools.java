package com.example.fff;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/*
 原文地址:http://blog.csdn.net/amor2006/article/details/7055902

优酷的视频下载有以下几个特点：

    地址动态生成，每次请求返回的地址都不一样。
    有效时间短，得到的下载地址大约只有1小时的有效时间。
    视频地址经过加密，需在客户（用户）端进行解密。
    长视频会被分割成多段短视频。
    对视频下载没有限制，即用户A得到的下载地址，用户B也可以下载。

先来看一下解析后的视频地址：

    http://f.youku.com/player/getFlvPath/sid/130086939328910582812_00/st/flv/fileid/03000201004D8858360BD1047C4F5FF471CDD7-C742-8D74-3EED-90A9EF54EEC1?K=de1515a31372faac182698bc

以上三段红色部分分别代表sid、fileid和key。

我们来分析一下这个地址，除了固定的部分以外，整个地址由sid、fileid和key三部分组成，下面我们逐一来分析如何解析这三个值。

以普通的优酷视频播放地址为例，

    http://v.youku.com/v_show/id_XMjUyODAzNDg0.html

把其中的红色部分复制出来，拼在

    http://v.youku.com/player/getPlayList/VideoIDS/

后面，得到

    http://v.youku.com/player/getPlayList/VideoIDS/XMjUyODAzNDg0

访问该地址得到json格式的字符串，其中我们感兴趣的内容是：

    "seed":6302,
    "key1":"bd7c3d19",
    "key2":"de1515a31372faac",
    "fileid":"13*18*13*13*13*11*13*42*13*13*39*44*41*41*47*41*18*29*13*60*44*42*13*39*17*33*39*56*47*56*56*39*17*42*33*44*44*17*54*33*17*39*11*54*41*44*17*39*54*18*55*55*44*54*38*13*57*38*55*56*47*39*55*55*33*42*", 

我们的解析工作需要用到上面的这些内容。这里要说明一下，因为地址是动态生成的，每次请求返回的结果都不一样，所以你看到的和上面是不一样的，但是不影响解析的过程。

生成sid

sid是一个随机数，我们可以这样获得
[csharp] view plaincopy

    private String genSid() {  
      int i1 = (int)(1000+Math.floor(Math.random()*999));  
      int i2 = (int)(1000+Math.floor(Math.random()*9000));  
      return System.currentTimeMillis()+"" + i1+"" + i2;  
    }  

假设返回”130086939328910582812″。

生成fileid

优酷返回的fileid已经做了加密工作，好在并不难解，利用上面得到的fileid和seed
[java] view plaincopy

    private String getFileID(String fileid,double seed){  
      String mixed = getFileIDMixString(seed);  
      String[] ids= fileid.split("\\*");  
      StringBuilder realId = new StringBuilder();  
      int idx;  
      for (int i=0; i< ids.length; i++){  
        idx = Integer.parseInt(ids[i]);  
        realId.append(mixed.charAt(idx));  
      }  
      return realId.toString();  
    }  
      
    private String getFileIDMixString(double seed){  
      StringBuilder mixed = new StringBuilder();  
      StringBuilder source = new StringBuilder(  
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ/\\:._-1234567890");  
      int index, len = source.length();  
      for (int i=0; i< len;++i){  
        seed = (seed * 211 + 30031) % 65536;  
        index = (int)Math.floor(seed/65536 * source.length());  
        mixed.append(source.charAt(index));  
        source.deleteCharAt(index);  
      }  
      return mixed.toString();  
    }  

假设返回”03000201004D8858360BD1047C4F5FF471CDD7-C742-8D74-3EED-90A9EF54EEC1″。

生成key

利用上面得到的key1和key2
[java] view plaincopy

    private String genKey(String key1,String key2){  
      int key = Long.valueOf(key1,16).intValue();  
      key ^= 0xA55AA5A5;  
      return key2 + Long.toHexString(key);  
    }  

假设返回”de1515a31372faac182698bc”。

好了，把sid，fileid，key合并起来，就可以得到一开始的下载地址了。

接下来就是分段视频的问题了，如果一个视频分成几段，在返回的json对象中可以找到类似的内容：

[plain] view plaincopy

    "segs":{  
    "mp4":[{"no":"0","size":"39095085","seconds":"426"},{"no":"1","size":"22114342","seconds":"426"},{"no":"2","size":"23296715","seconds":"424"},{"no":"3","size":"18003234","seconds":"426"},{"no":"4","size":"31867294","seconds":"423"},{"no":"5","size":"14818514","seconds":"248"}],  
    "flv":[{"no":"0","size":"19739080","seconds":"425"},{"no":"1","size":"11506385","seconds":"426"},{"no":"2","size":"11821267","seconds":"426"},{"no":"3","size":"8988612","seconds":"426"},{"no":"4","size":"16078739","seconds":"425"},{"no":"5","size":"7634043","seconds":"245"}]}  

很明显，该视频分成了6段，而且有mp4和flv两种格式的视频。还记得一开始的视频地址中的蓝色部分吗，我们只要修改那一部分的数字就可以了，比如第二段，就把蓝色部分换成01（两个都要换），注意这是16进制的。

如果想下载mp4格式的，只要把下载地址中的/flv/换成/mp4/，当然你要确定该视频有mp4格式。
 */

public class VideoTools
{
 public static void a() throws Exception
 {
  
 }

 public String getVideoUrl(String htmlUrl) throws JSONException{
  //正则表达式解析地址，取id
  Pattern p = Pattern.compile(".*id_(\\w+)\\.html");
  String u = htmlUrl;
  Matcher m = p.matcher(u);
  String id = "";
  if (m.find()) {
   id = m.group(1);
  }
 
//  String id="XMTgzNDA5OTMy";
  String s = getContent("http://v.youku.com/player/getPlayList/VideoIDS/"+id);
  
  return s;
 }


 public static String getContent(String strUrl) {
  try {

   URL url = new URL(strUrl);
   BufferedReader br = new BufferedReader(new InputStreamReader(
     url.openStream()));
   String s = "";
   StringBuffer sb = new StringBuffer("");
   while ((s = br.readLine()) != null) {
    sb.append(s);
   }
   br.close();
   return sb.toString();
  } catch (Exception e) {
   return "error open url:" + strUrl;
  }

 }
}