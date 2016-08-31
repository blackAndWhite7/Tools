package com.project.wei.smallsiri;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private ListView lvList;
    private StringBuffer mVoiceBuffer;

    private ArrayList<TalkBean> mTalkList = new ArrayList<TalkBean>();
    private VoiceAdapter mAdapter;

    private String[] mAnswers = new String[] { "约吗?", "等你哦!!!", "没有更多美女了",
            "这是最后一张了!", "不要再要了", "人家害羞嘛" };

    private int[] mAnswerPics = new int[] { R.drawable.p1, R.drawable.p2,
            R.drawable.p3, R.drawable.p4 };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 将“12345678”替换成您申请的APPID，申请地址：http://www.xfyun.cn //
        // 请勿在“=”与appid之间添加任务空字符或者转义符
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=57c58dcd");

        lvList = (ListView) findViewById(R.id.lv_list);
        mAdapter = new VoiceAdapter();
        lvList.setAdapter(mAdapter);
    }
    public void startVoice(View view) {
        // 1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, null);
        // 2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");

        mVoiceBuffer = new StringBuffer();

        // 3.设置回调接口
        mDialog.setListener(new RecognizerDialogListener() {

            @Override
            public void onResult(RecognizerResult results, boolean isLast) {
                System.out.println("isLast:" + isLast);
                String voiceStr = parseData(results.getResultString());
                // System.out.println(voiceStr);

                mVoiceBuffer.append(voiceStr);

                if (isLast) {
                    String askStr = mVoiceBuffer.toString();
                    System.out.println(askStr);

                    // 添加提问对象
                    TalkBean ask = new TalkBean(askStr, true, -1);
                    mTalkList.add(ask);

                    // 初始化回答对象
                    String answerStr = "没听清";
                    int imageId = -1;
                    if (askStr.contains("你好")) {
                        answerStr = "你好呀!!!";
                    } else if (askStr.contains("你是谁")) {
                        answerStr = "我是你的小猪手";
                    } else if (askStr.contains("美女")) {
                        Random random = new Random();
                        // 随机回答
                        int strPos = random.nextInt(mAnswers.length);
                        answerStr = mAnswers[strPos];

                        // 随机图片
                        int imagePos = random.nextInt(mAnswerPics.length);
                        imageId = mAnswerPics[imagePos];
                    } else if (askStr.contains("天王盖地虎")) {
                        answerStr = "小鸡炖蘑菇";
                        imageId = R.drawable.m;
                    }

                    TalkBean answer = new TalkBean(answerStr, false, imageId);
                    mTalkList.add(answer);

                    // 刷新listview
                    mAdapter.notifyDataSetChanged();

                    // 让listview定位到最后一个item
                    lvList.setSelection(mTalkList.size() - 1);

                    startSpeak(answerStr);
                }
            }

            @Override
            public void onError(SpeechError arg0) {

            }
        });

        // 4.显示dialog，接收语音输入
        mDialog.show();
    }

    // 解析语音json
    private String parseData(String json) {
        Gson gson = new Gson();
        VoiceBean voiceBean = gson.fromJson(json, VoiceBean.class);

        StringBuffer sb = new StringBuffer();

        ArrayList<VoiceBean.WsBean> ws = voiceBean.ws;
        for (VoiceBean.WsBean wsBean : ws) {
            String w = wsBean.cw.get(0).w;
            sb.append(w);
        }

        return sb.toString();
    }

    // 语音合成
    public void startSpeak(String content) {
        // 1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        SpeechSynthesizer mTts = SpeechSynthesizer
                .createSynthesizer(this, null);
        // 2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        // 设置发音人（更多在线发音人，用户可参见 附录12.2
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan"); // 设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");// 设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");// 设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); // 设置云端
        // 设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        // 保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
        // 仅支持保存为pcm和wav格式，如果不需要保存合成音频，注释该行代码
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");

        // 3.开始合成
        mTts.startSpeaking(content, null);
    }

    class VoiceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mTalkList.size();
        }

        @Override
        public TalkBean getItem(int position) {
            return mTalkList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.list_item, null);
                holder = new ViewHolder();
                holder.tvAsk = (TextView) convertView.findViewById(R.id.tv_ask);
                holder.tvAnswer = (TextView) convertView
                        .findViewById(R.id.tv_answer);
                holder.llAnswer = (LinearLayout) convertView
                        .findViewById(R.id.ll_answer);
                holder.ivPic = (ImageView) convertView
                        .findViewById(R.id.iv_pic);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            TalkBean item = getItem(position);
            if (item.isAsk) {
                // 提问
                holder.tvAsk.setVisibility(View.VISIBLE);
                holder.llAnswer.setVisibility(View.GONE);

                holder.tvAsk.setText(item.content);
            } else {
                // 回答
                holder.tvAsk.setVisibility(View.GONE);
                holder.llAnswer.setVisibility(View.VISIBLE);

                holder.tvAnswer.setText(item.content);

                // 有图片
                if (item.imageId > 0) {
                    holder.ivPic.setVisibility(View.VISIBLE);
                    holder.ivPic.setImageResource(item.imageId);
                } else {
                    // 没图片
                    holder.ivPic.setVisibility(View.GONE);
                }
            }

            return convertView;
        }

    }

    static class ViewHolder {
        public TextView tvAsk;
        public TextView tvAnswer;
        public ImageView ivPic;
        public LinearLayout llAnswer;
    }
}
