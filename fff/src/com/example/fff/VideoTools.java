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
 ԭ�ĵ�ַ:http://blog.csdn.net/amor2006/article/details/7055902

�ſ����Ƶ���������¼����ص㣺

    ��ַ��̬���ɣ�ÿ�����󷵻صĵ�ַ����һ����
    ��Чʱ��̣��õ������ص�ַ��Լֻ��1Сʱ����Чʱ�䡣
    ��Ƶ��ַ�������ܣ����ڿͻ����û����˽��н��ܡ�
    ����Ƶ�ᱻ�ָ�ɶ�ζ���Ƶ��
    ����Ƶ����û�����ƣ����û�A�õ������ص�ַ���û�BҲ�������ء�

������һ�½��������Ƶ��ַ��

    http://f.youku.com/player/getFlvPath/sid/130086939328910582812_00/st/flv/fileid/03000201004D8858360BD1047C4F5FF471CDD7-C742-8D74-3EED-90A9EF54EEC1?K=de1515a31372faac182698bc

�������κ�ɫ���ֱַ����sid��fileid��key��

����������һ�������ַ�����˹̶��Ĳ������⣬������ַ��sid��fileid��key��������ɣ�����������һ��������ν���������ֵ��

����ͨ���ſ���Ƶ���ŵ�ַΪ����

    http://v.youku.com/v_show/id_XMjUyODAzNDg0.html

�����еĺ�ɫ���ָ��Ƴ�����ƴ��

    http://v.youku.com/player/getPlayList/VideoIDS/

���棬�õ�

    http://v.youku.com/player/getPlayList/VideoIDS/XMjUyODAzNDg0

���ʸõ�ַ�õ�json��ʽ���ַ������������Ǹ���Ȥ�������ǣ�

    "seed":6302,
    "key1":"bd7c3d19",
    "key2":"de1515a31372faac",
    "fileid":"13*18*13*13*13*11*13*42*13*13*39*44*41*41*47*41*18*29*13*60*44*42*13*39*17*33*39*56*47*56*56*39*17*42*33*44*44*17*54*33*17*39*11*54*41*44*17*39*54*18*55*55*44*54*38*13*57*38*55*56*47*39*55*55*33*42*", 

���ǵĽ���������Ҫ�õ��������Щ���ݡ�����Ҫ˵��һ�£���Ϊ��ַ�Ƕ�̬���ɵģ�ÿ�����󷵻صĽ������һ���������㿴���ĺ������ǲ�һ���ģ����ǲ�Ӱ������Ĺ��̡�

����sid

sid��һ������������ǿ����������
[csharp] view plaincopy

    private String genSid() {  
      int i1 = (int)(1000+Math.floor(Math.random()*999));  
      int i2 = (int)(1000+Math.floor(Math.random()*9000));  
      return System.currentTimeMillis()+"" + i1+"" + i2;  
    }  

���践�ء�130086939328910582812�塣

����fileid

�ſ᷵�ص�fileid�Ѿ����˼��ܹ��������ڲ����ѽ⣬��������õ���fileid��seed
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

���践�ء�03000201004D8858360BD1047C4F5FF471CDD7-C742-8D74-3EED-90A9EF54EEC1�塣

����key

��������õ���key1��key2
[java] view plaincopy

    private String genKey(String key1,String key2){  
      int key = Long.valueOf(key1,16).intValue();  
      key ^= 0xA55AA5A5;  
      return key2 + Long.toHexString(key);  
    }  

���践�ء�de1515a31372faac182698bc����

���ˣ���sid��fileid��key�ϲ��������Ϳ��Եõ�һ��ʼ�����ص�ַ�ˡ�

���������Ƿֶ���Ƶ�������ˣ����һ����Ƶ�ֳɼ��Σ��ڷ��ص�json�����п����ҵ����Ƶ����ݣ�

[plain] view plaincopy

    "segs":{  
    "mp4":[{"no":"0","size":"39095085","seconds":"426"},{"no":"1","size":"22114342","seconds":"426"},{"no":"2","size":"23296715","seconds":"424"},{"no":"3","size":"18003234","seconds":"426"},{"no":"4","size":"31867294","seconds":"423"},{"no":"5","size":"14818514","seconds":"248"}],  
    "flv":[{"no":"0","size":"19739080","seconds":"425"},{"no":"1","size":"11506385","seconds":"426"},{"no":"2","size":"11821267","seconds":"426"},{"no":"3","size":"8988612","seconds":"426"},{"no":"4","size":"16078739","seconds":"425"},{"no":"5","size":"7634043","seconds":"245"}]}  

�����ԣ�����Ƶ�ֳ���6�Σ�������mp4��flv���ָ�ʽ����Ƶ�����ǵ�һ��ʼ����Ƶ��ַ�е���ɫ����������ֻҪ�޸���һ���ֵ����־Ϳ����ˣ�����ڶ��Σ��Ͱ���ɫ���ֻ���01��������Ҫ������ע������16���Ƶġ�

���������mp4��ʽ�ģ�ֻҪ�����ص�ַ�е�/flv/����/mp4/����Ȼ��Ҫȷ������Ƶ��mp4��ʽ��
 */

public class VideoTools
{
 public static void a() throws Exception
 {
  
 }

 public String getVideoUrl(String htmlUrl) throws JSONException{
  //������ʽ������ַ��ȡid
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