package com.wangsc.lovehome;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

public class MyListenerService extends AccessibilityService {

    private static final String TAG = "wangsc";
    private TextToSpeech textToSpeech;//创建自带语音对象
    private DataContext mDataContext;


    private int eventType;

    private void initTimePrompt() {
        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mTimeReceiver, timeFilter);
    }

    private BroadcastReceiver mTimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar cal = Calendar.getInstance();
            int min = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);
            textToSpeech.speak(min + "分" + second + "秒",//输入中文，若不支持的设备则不会读出来
                    TextToSpeech.QUEUE_FLUSH, null);
        }
    };


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
//
//        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
////        info.packageNames = apps.toArray(new String[apps.size()]); //监听过滤的包名
////        for(String packageName : apps){
////            Log.e("wangsc","包名："+packageName);
////        }
//        info.packageNames = new String[]{"com.alibaba.android.rimet"}; //监听过滤的包名
//        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK; //监听哪些行为
//        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN; //反馈
//        info.notificationTimeout = 100; //通知的时间
//        setServiceInfo(info);


        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == textToSpeech.SUCCESS) {
                    // Toast.makeText(MainActivity.this,"成功输出语音",
                    // Toast.LENGTH_SHORT).show();
                    // Locale loc1=new Locale("us");
                    // Locale loc2=new Locale("china");

                    textToSpeech.setPitch(1.0f);//方法用来控制音调
                    textToSpeech.setSpeechRate(1.0f);//用来控制语速

                    //判断是否支持下面两种语言
                    int result1 = textToSpeech.setLanguage(Locale.US);
                    int result2 = textToSpeech.setLanguage(Locale.
                            SIMPLIFIED_CHINESE);
                    boolean a = (result1 == TextToSpeech.LANG_MISSING_DATA || result1 == TextToSpeech.LANG_NOT_SUPPORTED);
                    boolean b = (result2 == TextToSpeech.LANG_MISSING_DATA || result2 == TextToSpeech.LANG_NOT_SUPPORTED);


                } else {
                    Toast.makeText(getApplicationContext(), "数据丢失或不支持", Toast.LENGTH_SHORT).show();
                }

            }
        });
//        initTimePrompt();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            mDataContext = new DataContext(getApplicationContext());
            Log.e("wangsc",!mDataContext.getSetting(Setting.KEYS.listener,false).getBoolean()+"");
            if(!mDataContext.getSetting(Setting.KEYS.listener,false).getBoolean())
                return;

            eventType = event.getEventType();

            if (eventType == TYPE_WINDOW_STATE_CHANGED) {
                String packageName = event.getPackageName().toString();
                String className = event.getClassName().toString();
                Log.e("wangsc", "-------------------package: " + packageName + "  ---------------------className: " + className);

                if (packageName.equals("com.alibaba.android.rimet") && className.equals("com.alibaba.android.rimet.biz.SplashActivity")) {
                    // 主界面
                    clickViewListByText("工作");
                    Thread.sleep(5000);
                } else if (packageName.equals("com.alibaba.android.rimet") && className.equals("com.alibaba.lightapp.runtime.activity.CommonWebViewActivity")) {
                    // 打卡界面
                } else if (packageName.equals("com.alibaba.android.rimet") && className.equals("com.alibaba.android.user.login.SignUpWithPwdActivity")) {
                    // 登录界面
                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
//                    if (hour == 8 || hour == 12 || hour == 13 || hour == 18) {
                        Login();
//                    }
                }
            } else if (eventType == TYPE_WINDOW_CONTENT_CHANGED) {
//                printNodeInfo();
//               if(clickViewListByDescription("我知道了"))
//                   return;
                if (clickViewByEqualsDescription("暂不升级"))
                    return;
                if (clickViewByEqualsDescription("确定"))
                    return;
                if (clickViewByEqualsDescription("考勤打卡"))
                    return;

                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                switch (hour) {
                    case 8:
                        rimetClockOn(hour);
                        break;
                    case 12:
                        rimetClockOff(hour);
                        break;
                    case 13:
                        rimetClockOn(hour);
                        break;
                    case 18:
                        rimetClockOff(hour);
                        break;


                    // TODO: 2019/4/15 测试代码，用完删除。
                    case 22:
                        rimetClockOn(hour);
                        break;
                    case 23:
                        rimetClockOff(hour);
                        break;

                }
            }

        } catch (Exception e) {
            _Utils.printException(getBaseContext(), e);
        }
    }

    private void Login() {
        String phone = mDataContext.getSetting(Setting.KEYS.phone, "").getString();
        String password = mDataContext.getSetting(Setting.KEYS.password, "").getString();

        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        getAllNodesToList(nodeInfo);


        int nodeIndex = 0;
        AccessibilityNodeInfo nodePhone = null, nodePassword = null, nodeLogin = null;
        for (AccessibilityNodeInfo node : allNodesInActiveWindow) {
            if (node.getClassName().toString().equals("android.widget.EditText")) {
                switch (nodeIndex) {
                    case 0:
                        nodePhone = node;
                        nodeIndex++;
                        break;
                    case 1:
                        nodePassword = node;
                        break;
                }

            } else if (node.getClassName().toString().equals("android.widget.Button")) {
                nodeLogin = node;
            }
        }
        if (nodePhone != null && nodePassword != null && nodeLogin != null && !phone.isEmpty() && !password.isEmpty()) {

            //android>21 = 5.0时可以用ACTION_SET_TEXT
            // android>18 3.0.1可以通过复制的手段,先确定焦点，再粘贴ACTION_PASTE
            // 使用剪切板
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text", phone);
            clipboard.setPrimaryClip(clip);

            nodePhone.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
//                        nodePhone.performAction(AccessibilityNodeInfo.ACTION_CLEAR_SELECTION);
            //焦点    （n是AccessibilityNodeInfo对象）
            nodePhone.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            //粘贴进入内容
            nodePhone.performAction(AccessibilityNodeInfo.ACTION_PASTE);


            clip = ClipData.newPlainText("text", password);
            clipboard.setPrimaryClip(clip);

            nodePassword.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
//                        nodePassword.performAction(AccessibilityNodeInfo.ACTION_CLEAR_SELECTION);
            //焦点    （n是AccessibilityNodeInfo对象）
            nodePassword.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            //粘贴进入内容
            nodePassword.performAction(AccessibilityNodeInfo.ACTION_PASTE);


            // 点击登录
            clickView(nodeLogin);
        }
    }

    private void rimetClockOff(int hour) {
        if (_Utils.rimetClockOnHour != hour) {
            if (clickViewListByDescription("下班打卡")) {
                _Utils.rimetClockOnHour = hour;
                mDataContext.addRunLog("下班打卡成功", new DateTime().toLongDateTimeString());
            }
        }
        if (_Utils.rimetIKnowHour != hour) {
            clickViewListByDescription("我知道了");
            _Utils.rimetIKnowHour = hour;
        }
    }

    private void rimetClockOn(int hour) {
        if (_Utils.rimetClockOnHour != hour) {
            if (clickViewListByDescription("上班打卡")) {
                _Utils.rimetClockOnHour = hour;
                mDataContext.addRunLog("上班打卡成功", new DateTime().toLongDateTimeString());
            }
        }
        if (_Utils.rimetIKnowHour != hour) {
            clickViewListByDescription("我知道了");
            _Utils.rimetIKnowHour = hour;
        }
    }

    private boolean clickView(AccessibilityNodeInfo node) {
        if (node.isClickable()) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        } else {
            AccessibilityNodeInfo parent = node.getParent();
            while (parent != null) {
                if (parent.isClickable()) {
                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
                parent = parent.getParent();
            }
        }
        return false;
    }

    /**
     * 查找到
     */
    List<AccessibilityNodeInfo> allNodesInActiveWindow = new ArrayList<AccessibilityNodeInfo>();

    /**
     * 得到当前屏幕中所有节点数量。
     *
     * @return
     */
    private int nodesNumInActiveWindow;

    private int getNodesNum() {
        nodesNumInActiveWindow = 0;
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            recursionNodeForCount(rootNode);
            return nodesNumInActiveWindow;
        } else {
            return 0;
        }
    }

    private boolean recursionNodeForCount(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            nodesNumInActiveWindow++;
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    recursionNodeForCount(info.getChild(i));
                }
            }
        }
        return false;
    }


    /**
     * 点击Text = btnText的按钮，只点击一个view即返回。
     *
     * @param viewText
     * @return
     */
    private boolean clickViewByEqualText(String viewText) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(viewText);

        if (nodeInfo != null) {
            AccessibilityNodeInfo node = getNodeByEqualsText(nodeInfo, viewText);
            if (node != null) {
                return clickView(node);
            }
        }
        return false;
    }

    private boolean clickViewByEqualText1(String viewText) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(viewText);
        if (!list.isEmpty()) {
            boolean ret = click(viewText);
        }
        return false;
    }

    private void kaoqin() throws Exception {
        String resId = "com.alibaba.android.rimet:id/oa_fragment_gridview";
        AccessibilityNodeInfo info = getRootInActiveWindow();
        List<AccessibilityNodeInfo> list = info.findAccessibilityNodeInfosByViewId(resId);
        if (list != null || list.size() != 0) {
            AccessibilityNodeInfo node = list.get(0);
            if (node != null || node.getChildCount() >= 8) {
                node = node.getChild(7);
                if (node != null) {  //已找到考勤打卡所在节点,进行点击操作
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                } else {
                    throw new Exception("已进入工作页,但未找到考勤打卡节点");
                }
            } else {
                throw new Exception("已进入工作页,但未找到考勤打卡节点");
            }
        } else {
            throw new Exception("已进入工作页,但未找到相关节点");
        }
    }

    //通过文字点击
    private boolean click(String viewText) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.w(TAG, "点击失败，rootWindow为空");
            return false;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(viewText);
        if (list.isEmpty()) {
            //没有该文字的控件
            Log.w(TAG, "点击失败，" + viewText + "控件列表为空");
            return false;
        } else {
            //有该控件
            //找到可点击的父控件
            Log.e("wangsc", "size: " + list.size());
            AccessibilityNodeInfo view = list.get(0);
            return onclick(view);  //遍历点击
        }

    }

    private boolean onclick(AccessibilityNodeInfo view) {
        if (view.isClickable()) {
            view.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Log.w(TAG, "点击成功");
            return true;
        } else {

            AccessibilityNodeInfo parent = view.getParent();
            if (parent == null) {
                return false;
            }
            onclick(parent);
        }
        return false;
    }


    /**
     * 点击Text = btnText的按钮，只点击一个view即返回。
     *
     * @param viewText
     * @return
     */
    private boolean clickViewByContainsText(String viewText) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            AccessibilityNodeInfo node = getNodeByContains(nodeInfo, viewText);
            if (node != null) {
                return clickView(node);
            }
        }
        return false;
    }

    private String getContentByContainsText(String viewText) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            AccessibilityNodeInfo node = getNodeByContains(nodeInfo, viewText);
            if (node != null) {
                return node.getText().toString();
            }
        }
        return null;
    }

    private boolean clickViewByEqualsText(String viewText) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            AccessibilityNodeInfo node = getNodeByEquals(nodeInfo, viewText);
            if (node != null) {
                return clickView(node);
            }
        }
        return false;
    }


    /**
     * 循环点击指定view之后的第after个view;用于指定的viewText存在多个，例如滴滴的“系统消息”。
     *
     * @param viewText
     * @return
     */
    private boolean clickViewListByText(String viewText) {
        boolean result = false;
        String text = "";
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        AccessibilityNodeInfo node = null;
        if (nodeInfo != null) {
            allNodesInActiveWindow.clear();
            getAllNodesToList(nodeInfo);
            for (int i = 0; i < allNodesInActiveWindow.size(); i++) {
                node = allNodesInActiveWindow.get(i);
                if (node.getText() != null) {
                    text = node.getText().toString();
                    if (text.equals(viewText)) {
                        if (clickView(node))
                            result = true;
                    }
                }
            }
        }
        return result;
    }

    private boolean clickViewListByDescription(String... viewText) {
        boolean result = false;
        String desc = "";
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        AccessibilityNodeInfo node = null;
        if (nodeInfo != null) {
            allNodesInActiveWindow.clear();
            getAllNodesToList(nodeInfo);
            for (int i = 0; i < allNodesInActiveWindow.size(); i++) {
                node = allNodesInActiveWindow.get(i);
                if (node.getContentDescription() != null) {
                    desc = node.getContentDescription().toString();
                    for (String v : viewText) {
                        if (desc.equals(v)) {
                            if (clickView(node))
                                result = true;
                        }
                    }
                }
            }
        }
        return result;
    }


    /**
     * 点击ContentDescription = viewDescription
     *
     * @param viewDescription
     * @return
     */
    private boolean clickViewByEqualsDescription(String viewDescription) {

        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            AccessibilityNodeInfo node = getNodeByEqualsDescription(nodeInfo, viewDescription);
            if (node != null) {
                return clickView(node);
            }
        }
        return false;
    }


    /**
     * 点击“通话”按钮
     *
     * @return
     */
    private boolean clickLeftDialButton() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> nodes = new ArrayList<>();
            getAllNodesToListByEqualsDescription(nodeInfo, "通话", nodes);

            // 执行点击
            if (nodes.size() == 1) {
                return clickView(nodes.get(0));
            } else if (nodes.size() == 2) {
                Rect rect0 = new Rect();
                Rect rect1 = new Rect();
                nodes.get(0).getBoundsInScreen(rect0);
                nodes.get(1).getBoundsInScreen(rect1);
                if (rect0.left < rect1.left) {
                    return clickView(nodes.get(0));
                } else {
                    return clickView(nodes.get(1));
                }
            }

        }
        return false;
    }


    /**
     * 检查当前窗口中是否有某按钮。
     *
     * @param viewText
     * @return
     */
    private boolean isExsitNodeByContainsText(String viewText) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            if (recursionNodeForExsitNodeByContainsText(nodeInfo, viewText)) {
                return true;
            }
        }
        return false;
    }

    public boolean recursionNodeForExsitNodeByContainsText(AccessibilityNodeInfo node, String viewText) {
        if (node.getChildCount() == 0) {
            String text = "";
            if (node.getText() != null) {
                text = node.getText().toString();
            }
            if (text.contains(viewText)) {
                return true;
            }
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChild(i) != null) {
                    if (recursionNodeForExsitNodeByContainsText(node.getChild(i), viewText)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 检查当前窗口中是否有某按钮。
     *
     * @param viewText
     * @return
     */
    private boolean isExsitNodeByEqualText(String viewText) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            if (recursionNodeForExsitNodeByEqualText(nodeInfo, viewText)) {
                return true;
            }
        }
        return false;
    }

    public boolean recursionNodeForExsitNodeByEqualText(AccessibilityNodeInfo node, String viewText) {
        if (node.getChildCount() == 0) {
            String text = "";
            if (node.getText() != null) {
                text = node.getText().toString();
            }
            if (text.equals(viewText)) {
                return true;
            }
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChild(i) != null) {
                    if (recursionNodeForExsitNodeByEqualText(node.getChild(i), viewText)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isExsitNodeByEqualDesc(String viewText) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            if (recursionNodeForExsitNodeByEqualDesc(nodeInfo, viewText)) {
                return true;
            }
        }
        return false;
    }

    public boolean recursionNodeForExsitNodeByEqualDesc(AccessibilityNodeInfo node, String viewText) {
        if (node.getChildCount() == 0) {
            String text = "";
            if (node.getContentDescription() != null) {
                text = node.getContentDescription().toString();
            }
            if (text.equals(viewText)) {
                return true;
            }
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChild(i) != null) {
                    if (recursionNodeForExsitNodeByEqualDesc(node.getChild(i), viewText)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void printNodeInfo() {
        StringBuilder texts = new StringBuilder();
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        getAllNodesPrintInfo(nodeInfo, texts);
        Log.e("wangsc", texts.toString());
    }

    public void getAllNodesPrintInfo(AccessibilityNodeInfo info, StringBuilder texts) {
        if (info != null) {
            if (info.getChildCount() == 0) {
                texts.append("【");
                if (info.getText() != null) {
                    texts.append(info.getText().toString());
                }
                if (info.getContentDescription() != null) {
                    texts.append("*" + info.getContentDescription().toString());
                }
                texts.append("】");
            } else {
                for (int i = 0; i < info.getChildCount(); i++) {
                    if (info.getChild(i) != null) {
                        getAllNodesPrintInfo(info.getChild(i), texts);
                    }
                }
            }
        }
    }

    public void getAllNodesToList(AccessibilityNodeInfo info) {
        if (info != null) {
            if (info.getChildCount() == 0) {
                allNodesInActiveWindow.add(info);
            } else {
                for (int i = 0; i < info.getChildCount(); i++) {
                    if (info.getChild(i) != null) {
                        getAllNodesToList(info.getChild(i));
                    }
                }
            }
        }
    }

    private void getAllNodesToListByEqualsDescription(AccessibilityNodeInfo nodeInfo, String viewDescription, List<AccessibilityNodeInfo> nodeInfoList) {
        if (nodeInfo.getChildCount() == 0) {
            if (nodeInfo.getContentDescription() != null) {
                String description = nodeInfo.getContentDescription().toString();
                if (description.equals(viewDescription)) {
                    nodeInfoList.add(nodeInfo);
                }
            }
        } else {
            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                if (nodeInfo.getChild(i) != null) {
                    getAllNodesToListByEqualsDescription(nodeInfo.getChild(i), viewDescription, nodeInfoList);
                }
            }
        }
    }

    /**
     * 找到与viewText的view就返回。
     *
     * @param info
     * @param viewText
     * @return
     */
    public AccessibilityNodeInfo getNodeByEqualsText(AccessibilityNodeInfo info, String viewText) {
        if (info != null) {
            if (info.getChildCount() == 0) {
                if (info.getText() != null) {
                    String text = info.getText().toString();
                    if (text.equals(viewText))
                        return info;
                }
            } else {
                for (int i = 0; i < info.getChildCount(); i++) {
                    if (info.getChild(i) != null) {
                        AccessibilityNodeInfo node = getNodeByEqualsText(info.getChild(i), viewText);
                        if (node != null) {
                            return node;
                        }
                    }
                }
            }
        }
        return null;
    }


    /**
     * 找到与viewText的view就返回。
     *
     * @param info
     * @param viewText
     * @return
     */
    public AccessibilityNodeInfo getNodeByContains(AccessibilityNodeInfo info, String viewText) {
        if (info != null) {
            if (info.getChildCount() == 0) {
                if (info.getText() != null) {
                    String text = info.getText().toString();
                    if (text.contains(viewText))
                        return info;
                }
            } else {
                for (int i = 0; i < info.getChildCount(); i++) {
                    if (info.getChild(i) != null) {
                        AccessibilityNodeInfo node = getNodeByContains(info.getChild(i), viewText);
                        if (node != null) {
                            return node;
                        }
                    }
                }
            }
        }
        return null;
    }

    public AccessibilityNodeInfo getNodeByEquals(AccessibilityNodeInfo info, String viewText) {
        if (info != null) {
            if (info.getChildCount() == 0) {
                if (info.getText() != null) {
                    String text = info.getText().toString();
                    if (text.equals(viewText))
                        return info;
                }
            } else {
                for (int i = 0; i < info.getChildCount(); i++) {
                    if (info.getChild(i) != null) {
                        AccessibilityNodeInfo node = getNodeByEquals(info.getChild(i), viewText);
                        if (node != null) {
                            return node;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 找到与viewText的view就返回。
     *
     * @param info
     * @param viewDescription
     * @return
     */
    public AccessibilityNodeInfo getNodeByEqualsDescription(AccessibilityNodeInfo info, String viewDescription) {
        if (info != null) {
            if (info.getChildCount() == 0) {
                if (info.getContentDescription() != null) {
                    String description = info.getContentDescription().toString();
                    if (description.equals(viewDescription))
                        return info;
                }
            } else {
                for (int i = 0; i < info.getChildCount(); i++) {
                    if (info.getChild(i) != null) {
                        AccessibilityNodeInfo node = getNodeByEqualsDescription(info.getChild(i), viewDescription);
                        if (node != null) {
                            return node;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void onInterrupt() {
    }


}
