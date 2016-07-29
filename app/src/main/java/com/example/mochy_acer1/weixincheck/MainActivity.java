package com.example.mochy_acer1.weixincheck;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mochy_acer1.weixincheck.Entity.LogTag;
import com.example.mochy_acer1.weixincheck.Entity.WxGetMoudle;
import com.example.mochy_acer1.weixincheck.Entity.WxShare;
import com.example.mochy_acer1.weixincheck.Entity.WxUrl;
import com.example.mochy_acer1.weixincheck.Util.CmdHelper;
import com.example.mochy_acer1.weixincheck.Util.WeChatController;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.common.entity.HttpResponse;
import cn.trinea.android.common.util.ShellUtils;

public class MainActivity extends AppCompatActivity {
    //Equipment.CurrentEquipment currentEquipment=null;
    Button btnStart;
    Button btnCheckUrl;
    String userID="wxid_0jotqdxrl16922";
    String epuipmentName;
    private String checkResult;
    public static final int STATE_CHECK_URL_MODE_ONE=1;
    public static final int STATE_CHECK_URL_MODE_TWO=2;
    public static final int STATE_CHECK_URL_MODE_DEFAULT=1;
    private int currentStare=STATE_CHECK_URL_MODE_DEFAULT;
    private static final String urlCheck="http://www.iloveguilin.com:9003/Question/BrowseDomain?opt=auto&t="+(int) ( 50 * Math.random() + 50);
    private static final String BROAST_URL="cn.bs.dwechat.tl";
    private static final String BROAST_SHARE="cn.bs.dwechat.wxshare";
    private static final String urlGet="http://119.97.160.86:9003//Monitor/index";
    private static final String urlPost="http://119.97.160.86:9003//Monitor/ImportData";
    private static final String urlCheckGet="http://www.iloveguilin.com:9003/Monitor/ImportDataByHost?host=r2.t2.com";
    private int sleepTime=500;
    private int baseTime=0;
    boolean go_stop=false;
    boolean getBandedUrl=true;
    boolean isLoading=false;
    boolean isStart=false;
    boolean isShared=false;
    List<WxShare> wxShareList;
    List<WxGetMoudle> getList;
    Handler mHandler;
    HttpResponse httpResponse;
    SharedPreferences sharedPreferences;
    TextView textViewInfo;
    TextView textViewDo;
    int count=0;
    BroadcastReceiver urlReceiver;
    BroadcastReceiver shareReceiver;
    WxUrl wxUrl;
    WxShare wxShare;
    WeChatController weChatController;
    private boolean goGet=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences=getSharedPreferences("main.xml", Context.MODE_WORLD_READABLE);
        init();
        registerReceiver();
    }

    private void registerReceiver() {
        IntentFilter broadcast_url_filter = new IntentFilter();
        broadcast_url_filter.addAction(BROAST_URL);			//添加动态广播的Action
        registerReceiver(urlReceiver, broadcast_url_filter);	// 注册自定义动态广播消息
        IntentFilter broadcast_share_filter = new IntentFilter();
        broadcast_share_filter.addAction(BROAST_SHARE);
        registerReceiver(shareReceiver, broadcast_share_filter);
    }

    private void init() {
        btnCheckUrl= (Button) findViewById(R.id.sendRequestBtn);
        btnStart= (Button) findViewById(R.id.autoStartBtn);
        weChatController=WeChatController.getInstance(mHandler);
        textViewInfo= (TextView) findViewById(R.id.textViewInfo);
        textViewDo= (TextView) findViewById(R.id.textViewCount);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        String strOpt =  dm.widthPixels + " × " + dm.heightPixels;
        textViewDo.setText(strOpt);
        //currentEquipment=Equipment.getCurrentEquipment(dm.widthPixels,dm.heightPixels);
        urlReceiver=new BroadcastReceiver() {


            @Override
            public void onReceive(Context context, Intent intent) {
                switch (currentStare){
                    case 1:
                        String url1=intent.getStringExtra("url");

                        boolean result1=
                                intent.getBooleanExtra("result",false);
                        Log.i(LogTag.WEB_URL,"广播接收成功");
                        if(wxUrl!=null&&!isShared){
                            wxUrl.setUrl(url1);
                            wxUrl.setResult(result1);
                        }
                        isLoading=false;
                        break;
                    case 2:
                        String url2=intent.getStringExtra("url");

                        boolean result2=
                                intent.getBooleanExtra("result",false);
                        Log.i(LogTag.WEB_URL,"广播接收成功");
                        if(url2.contains("rd.wechat.com")&&getBandedUrl){
                            try {
                                String urlBanded=parseToUrl(url2);
                                checkResult = urlCheckGet.split("=")[0] + "=" +urlBanded.split("http://")[1].split("/")[0];
                                goGet=true;
                                Log.i("TAG",checkResult);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            getBandedUrl=false;
                            isLoading=false;
                        }
                        break;
                }

            }

        };
        shareReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                try {

                    if(isShared){
                        String checkShare=URLDecoder.decode(intent.getStringExtra("SHARE_ITEM"),"UTF-8");
                        JSONObject obj=new JSONObject(checkShare);
                        WxShare wxShare=new WxShare();
                        wxShare.setCreateTime(obj.getLong("createTime"));
                        wxShare.setUsername(obj.getString("username"));
                        wxShare.setDesc(obj.getString("desc"));
                        wxShare.setUrl(obj.getString("url"));
                        wxShare.setText(obj.getString("text"));
                        wxShareList.add(wxShare);
//                    Log.i("TAG_BROADCAST_SHARE",wxShareList.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what){
                    //向服务器发送请求
                    case 1:
                        if(isStart){
                            isStart=false;
                            getHttpResponse();
                        }
                        break;
                    //URL载入完毕 已收到后台广播结果 向服务器发送JSON结果
                    case 2:
                        break;
                    case 3:
                        textViewInfo.append(msg.obj.toString()+"\n");
                        break;
                    case 4:
                        break;
                    case 7:
                        weChatController.openWebView(msg.obj.toString());
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };


    }

    private String parseToUrl(String url2) throws UnsupportedEncodingException {
        String result=url2.split("url=")[1].split("%3Fc%3D")[0];
        result.replace("%3A",":");
        result.replace("%2F","/");
        String url=URLDecoder.decode(result,"utf-8");
        return url;
    }

    private void sendMessages(int state) {
        Message msg=Message.obtain();
        msg.what=state;
        mHandler.sendMessage(msg);
    }

    private void
    sendJsonToServer(List<WxGetMoudle> list) throws IOException, JSONException {
        HttpPost post=new HttpPost(urlPost);
        DefaultHttpClient client=new DefaultHttpClient();
        StringBuffer buffer=new StringBuffer();
        buffer.append("[");
        JSONObject obj;
        for(int i=0;i<list.size();i++){
            obj=new JSONObject();
            WxGetMoudle moudle=list.get(i);
            obj.put("Id",moudle.getId());
            obj.put("Did",moudle.getDid());
            obj.put("Type",moudle.getType());
            obj.put("OperateTime",moudle.getoperateTime());
            obj.put("CreateDate",moudle.getCreateDate());
            obj.put("EquipmentName",moudle.getEquipmentName());
            obj.put("Date",moudle.getDate());
            obj.put("Url",moudle.getUrl());
            obj.put("UpdateTime",moudle.getUpdateTime());
            obj.put("State",moudle.getState());
            Log.i("TAG",obj.toString());
            buffer.append(obj.toString()+",");

        }
        buffer.deleteCharAt(buffer.length()-1);
        buffer.append("]");
        Log.i("TAG_BUFFER", buffer.toString());
        StringEntity entity=new StringEntity(buffer.toString(),"utf-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        post.setEntity(entity);
        org.apache.http.HttpResponse response=client.execute(post);
        String resData = EntityUtils.toString(response.getEntity());
        Log.i("TAG_SERVER_RESULT",resData.toString());

        isStart=true;
        sendMessages(1);
    }


    private void getHttpResponse() {
        AsyncHttpClient client = new AsyncHttpClient();
//        Toast.makeText(this, "发送GET请求到服务器", Toast.LENGTH_SHORT).show();
        RequestParams params=new RequestParams();
        ShellUtils.CommandResult cr=ShellUtils.execCommand("cat /sys/class/android_usb/android0/iSerial\n",true);
        epuipmentName=cr.successMsg;
        params.add("EquipmentName",epuipmentName);
        client.get(urlGet, params,new JsonHttpResponseHandler() {
            //返回JSONObject对象|JSONOArray对象
            @Override
            public void onSuccess(final int statusCode, Header[] headers,
                                  final JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        resoleHttpResponse(statusCode, response);
                    }
                }).start();
            }
        });
    }

    private void resoleHttpResponse(int statusCode, final JSONArray response) {

        final int responseLength=response.length();
        getList=new ArrayList<WxGetMoudle>();
        if (statusCode == 200) {
            //遍历json数组
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(response.length()==0){
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    for (int i = 0; i < response.length(); i++) {

                        try {
                            // 获取具体的一个JSONObject对象
                            JSONObject obj = null;
                            obj = response.getJSONObject(i);
                            final WxGetMoudle moudle = getWxGetMoudle(obj);
                            switch (obj.getInt("Type")){
//                                case 1:
//                                {
//
//                                    isShared=false;
//                                    baseTime=0;
//                                    String checkResult="";
//                                    Message msg1=Message.obtain();
//                                    msg1.what=3;
//                                    msg1.obj="当前正在检测域名"+obj.get("Url").toString();
//                                    mHandler.sendMessage(msg1);
//                                    Thread.sleep(2000);
//                                    Message msg=Message.obtain();
//                                    msg.obj=obj.get("Url");
//                                    msg.what=7;
//                                    mHandler.sendMessage(msg);
//                                    isLoading=true;
//                                    wxUrl=new WxUrl();
//                                    if(wxUrl.getUrl()==null){
//                                        Log.i("TAG","sleep");
//                                        while(isLoading&&baseTime<20000){
//                                            Thread.sleep(sleepTime);
//                                            baseTime+=sleepTime;
//                                        }
//                                        if(wxUrl.getUrl()==null){
//                                            checkResult="域名无效";
//                                            break;
//                                        }
//                                        if(wxUrl.getUrl()!=null&&wxUrl.getResult()==false){
//                                            moudle.setState(2);
//                                            Log.i(LogTag.WEBBIEW,wxUrl.getUrl()+"域名出错！");
//                                            checkResult="域名被封禁";
//                                        }
//                                        checkResult="域名可以使用";
//                                        Log.i(LogTag.WEB_URL,wxUrl.toString());
//                                        Log.i(LogTag.WEB_URL,moudle.toString());
//                                    }
//                                    getList.add(moudle);
//                                    mHandler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Log.i(LogTag.WEBBIEW, "触摸指令-关闭webview");
//                                            WeChatController.closeWeiXin();
//                                        }
//                                    }, 2000);
//                                    Thread.sleep(4000);
//                                    moveTaskToFront();
//                                    Message msg2=Message.obtain();
//                                    msg2.what=3;
//                                    msg2.obj="域名:"+wxUrl.getUrl()+"检查完毕，结果为"+checkResult;
//                                    mHandler.sendMessage(msg2);
//                                    Thread.sleep(2000);
//
//                                }
//                                break;
                                case 2:
//                                    textViewInfo.setText("分享链接中....");
                                    doShare(obj, moudle);
                                    break;
                                case 3:
//                                    textViewInfo.setText("检查分享中....");
                                    doCheckShare(moudle);
                                    break;
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(responseLength==getList.size()){
                                    Log.i("TAG_SEND_TOSERVET", getList.toString());
                                    sendJsonToServer(getList);
                                }else{
                                    isStart=true;
                                    sendMessages(1);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }

            }).start();

        }
    }

    private void doCheckUrl() throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("TAG","检查开始");

                weChatController.openWebView(urlCheck);
                isLoading=true;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int sleepCount=0;
                while(isLoading){
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sleepCount+=sleepTime;
                    if(sleepCount>60000)break;
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(LogTag.WEBBIEW, "触摸指令-关闭webview");
                        CmdHelper.sendTap(0x57,0x96);
                    }
                }, 4000);
                isLoading=true;

                if(goGet){
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            weChatController.openWebView(checkResult);
                        }
                    }, 7000);
                    sleepCount=0;
                    while(isLoading){
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sleepCount+=sleepTime;
                        if(sleepCount>10000)break;
                    }
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(LogTag.WEBBIEW, "触摸指令-关闭");
                            CmdHelper.sendTap(0x57,0x96);
                        }
                    }, 2000);
                    goGet=false;


                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getBandedUrl=true;
                            doCheckUrl();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                },5000);

            }
        }).start();

    }

    private void doCheckShare(final WxGetMoudle moudle) throws InterruptedException {
        wxShareList=new ArrayList<WxShare>();
        isShared=true;
        weChatController.openMoments();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CmdHelper.exec("input swipe 600 400 550 1830");
            }
        },8000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CmdHelper.exec("input swipe 600 400 550 1830");
            }
        },10000);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CmdHelper.exec("input swipe 600 400 550 1830");
            }
        },12000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CmdHelper.exec("input swipe 800 1800 600 277");
            }
        },15000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CmdHelper.exec("input swipe 822 1750 550 300");
            }
        },18000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CmdHelper.sendTap(0x4e,0xa0);
                try {
                    Thread.sleep(2000);
                    Log.i("TAG","sleep");
                    WxShare wxShare;
                    moudle.setState(3);
                    for(int j=0;j<wxShareList.size();j++){
                        wxShare=wxShareList.remove(0);
                        String urlWxShare=wxShare.getUrl();
                        String urlGetMoudle=moudle.getUrl();
                        Long WxShareCreateTime=wxShare.getCreateTime();
                        Long GetMoudleCreateTime=System.currentTimeMillis();
                        Log.i("TAG_TIME",String.valueOf(GetMoudleCreateTime));

                        Log.i("TAG_TIME",String.valueOf(WxShareCreateTime));
                        Long timeDifference=(GetMoudleCreateTime/1000-WxShareCreateTime);
                        Log.i("TAG_TIME",String.valueOf(timeDifference));
                        String userIDWxShare=wxShare.getUsername();
                        if(!urlGetMoudle.contains("http")){
                            urlGetMoudle="http://"+urlGetMoudle;
                        }
                        if(urlWxShare.equals(urlGetMoudle)&&userIDWxShare.equals(userID)&&timeDifference>0&&timeDifference<30000){
                            moudle.setState(1);
                            Log.i("TAG3","当前URL分享存在！");
                            break;
                        }else{
//                            textViewInfo.append(urlGetMoudle+">>>分享不可见\n");
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        },21000);

        getList.add(moudle);
        Log.i("TAG_CASE3_GETLIST",getList.toString());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                moveTaskToFront();
            }
        },24000);
        Thread.sleep(28000);
    }



    private void doShare(JSONObject obj, final WxGetMoudle moudle) throws JSONException, InterruptedException {
        wxShareList=new ArrayList<WxShare>();
        isShared=true;
        final String url=obj.getString("Url");
        final String shareResult="";
        textViewInfo.append("当前正在分享"+url);
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                weChatController.openWebView(url);
            }
        }, 5000);
        isLoading=true;
        baseTime=0;
        while(isLoading&&baseTime<20000){
            Thread.sleep(sleepTime);
            baseTime+=sleepTime;
        }

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Log.i(LogTag.WEBBIEW, "触摸指令--点击右上menu");
                CmdHelper.sendTap(0x3e8,0x96);
            }
        }, baseTime+3000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(LogTag.WEBBIEW, "触摸指令-分享");
                CmdHelper.sendTap(0x2e4,0x1bc);
            }
        }, baseTime+6000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(LogTag.WEBBIEW, "触摸指令-确认分享");
                CmdHelper.sendTap(0x3b6,0x96);
            }
        }, baseTime+9000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(LogTag.WEBBIEW, "触摸指令-关闭");
                CmdHelper.sendTap(0x57,0x96);
            }
        }, baseTime+12000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                weChatController.openMoments();
            }
        },baseTime+15000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.i(LogTag.WEBBIEW, "触摸指令-上拉刷新");
                CmdHelper.exec("input swipe 550 300 822 1750");
                Log.i("TAG","sleep");
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for(int j=0;j<wxShareList.size();j++){
                    wxShare=wxShareList.get(j);
                    Log.i("TAG","wxShareList长度="+wxShareList.size()+"");
                    String urlWxShare=wxShare.getUrl();
                    String urlGetMoudle=moudle.getUrl();
                    if(!urlGetMoudle.contains("http")){
                        urlGetMoudle="http://"+urlGetMoudle;
                    }
                    Log.i("URL_COMPARE",urlWxShare+"============"+urlGetMoudle);
                    Long WxShareCreateTime=wxShare.getCreateTime();
                    Long GetMoudleCreateTime=System.currentTimeMillis();
                    Log.i("TAG_TIME",String.valueOf(GetMoudleCreateTime));
                    Log.i("TAG_TIME",String.valueOf(WxShareCreateTime));
                    Long timeDifference=(GetMoudleCreateTime/1000-WxShareCreateTime);
                    Log.i("TAG_TIME",String.valueOf(timeDifference));
                    String userIDWxShare=wxShare.getUsername();
                    if(urlWxShare.equals(urlGetMoudle)&&userIDWxShare.equals(userID)){
                        if(timeDifference>0&&timeDifference<300){

                            Log.i("TAG","分享成功");
                            getList.add(moudle);
                            break;
                        }
                    }
                }

            }
        }, baseTime+18000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(LogTag.WEBBIEW, "触摸指令-关闭");
                CmdHelper.sendTap(0x57,0x96);

            }
        }, baseTime+28000);


        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                moveTaskToFront();
            }
        }, 50000);
        Thread.sleep(baseTime+31000);
    }

    @NonNull
    private WxGetMoudle getWxGetMoudle(JSONObject obj) throws JSONException {
        WxGetMoudle moudle=new WxGetMoudle();
        moudle.setId(obj.getInt("Id"));
        moudle.setDid(obj.getInt("Did"));
        moudle.setType(obj.getInt("Type"));
        moudle.setoperateTime(obj.getString("OperateTime"));
        moudle.setCreateDate(obj.getString("CreateDate"));
        moudle.setEquipmentName(epuipmentName);
        moudle.setDate(obj.getString("Date"));
        moudle.setUrl(obj.getString("Url"));
        moudle.setUpdateTime(obj.getString("UpdateTime"));
        moudle.setState(obj.getInt("State"));
        Log.i("TAG_GET_MDOUDLE",moudle.toString());
        return moudle;
    }

    private Long getDate(String createDate) {
        String l=createDate;
        String r1=l.split("\\(")[1];
        String r2=r1.split("\\)")[0];
        long result=Long.parseLong(r2);
        return result;
    }


    public void moveTaskToFront(){
        ActivityManager am = (ActivityManager) AppBase.getContext().getSystemService(Activity.ACTIVITY_SERVICE);
        am.moveTaskToFront(getTaskId(),0);
    }

    public void doClick(View v){
        switch (v.getId()){
//            case R.id.autoStartBtn:
//                isStart=true;
//                btnStart.setEnabled(false);
//                sendMessages(1);
//                break;
            case R.id.stopBtn:
                currentStare=STATE_CHECK_URL_MODE_DEFAULT;
                btnStart.setEnabled(true);
                btnCheckUrl.setEnabled(true);
                go_stop=false;
                finish();
                System.exit(0);
                break;
            case R.id.sendRequestBtn:
                currentStare=STATE_CHECK_URL_MODE_TWO;
                btnCheckUrl.setEnabled(false);
                go_stop=true;
                getBandedUrl=true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            doCheckUrl();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
        }
    }



}

