package com.wangsc.lovehome.service;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.wangsc.lovehome.model.DataContext;
import com.wangsc.lovehome.helper._Utils;
import com.wangsc.lovehome.model.DateTime;
import com.wangsc.lovehome.model.RimetClock;
import com.wangsc.lovehome.model.Setting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

public class MyListenerService extends AccessibilityService {

    private static final String TAG = "wangsc";
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
        }
    };


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
//
//        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
//        List<String> apps = _Utils.getAppInfos(getApplication());
//        info.packageNames = apps.toArray(new String[apps.size()]); //监听过滤的包名
//        for(String packageName : apps){
//            Log.e("wangsc","包名："+packageName);
//        }
////        info.packageNames = new String[]{"com.alibaba.android.rimet"}; //监听过滤的包名
//        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK; //监听哪些行为
//        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN; //反馈
//        info.notificationTimeout = 100; //通知的时间
//        setServiceInfo(info);

//        initTimePrompt();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            //
            mDataContext = new DataContext(getApplicationContext());
            if (!mDataContext.getSetting(Setting.KEYS.is_rimet_clock_running, false).getBoolean())
                return;

            String packageName = event.getPackageName().toString();
            String className = event.getClassName().toString();
            eventType = event.getEventType();


            List<RimetClock> rimetClockList = mDataContext.getRimetClocks();
            RimetClock rimetClock = null;
            for (RimetClock rc : rimetClockList) {
                DateTime clockTime = new DateTime(rc.getHour(), rc.getMinite());
                if (System.currentTimeMillis() >= clockTime.getTimeInMillis() && (System.currentTimeMillis() - clockTime.getTimeInMillis()) < 10 * 60 * 1000) {
                    rimetClock = rc;
                    break;
                }
            }


            if (rimetClock != null) {
                Log.e("wangsc", "clock: " + rimetClock.getHour() + " - " + rimetClock.getSummery());
                //
                if (!_Utils.rimetAppStartClockId.equals(rimetClock.getId())) {
                    mDataContext.addRunLog("钉钉已被启动", new DateTime().toLongDateTimeString());
                    Log.e("wangsc","钉钉已被启动："+ new DateTime().toLongDateTimeString());
                    _Utils.rimetAppStartClockId = rimetClock.getId();
                }

                //
                switch (eventType) {
                    case TYPE_WINDOW_STATE_CHANGED:
//                        Log.e("wangsc", "TYPE_WINDOW_STATE_CHANGED");
//                        Log.e("wangsc", "-------------------package: " + packageName + "  ---------------------className: " + className);
//                        printNodeInfo();

                        //
                        if (clickViewListByText("工作")) {
                            Thread.sleep(3000);
                        }

                        // “努力定位中”对话框退出
                        if (clickViewListByText("我知道了"))
                            return;

                        //
                        switch (className) {
//                            case "com.alibaba.android.rimet.biz.SplashActivity":
//                                // 主界面
//                                break;
//                            case "com.alibaba.lightapp.runtime.activity.CommonWebViewActivity":
//                                // 打卡界面
//                                break;
                            case "com.alibaba.android.rimet.biz.SlideActivity":
                                // 登录注册界面
                                clickViewByEqualsText("登录");
                                break;
                            case "com.alibaba.android.user.login.SignUpWithPwdActivity":
                                // 登录界面
                                LoginPwd();
                                break;
                        }

                        break;
                    case TYPE_WINDOW_CONTENT_CHANGED:
//                        Log.e("wangsc", "TYPE_WINDOW_CONTENT_CHANGED");
//                        Log.e("wangsc", "-------------------package: " + packageName + "  ---------------------className: " + className);
//                        printNodeInfo();

                        if (clickViewByEqualsText("暂不更新"))
                            return;
                        if (clickViewByEqualsDescription("确定"))
                            return;
                        if (clickViewByEqualsDescription("考勤打卡"))
                            return;

                        /**
                         * 点击上下班打卡按钮
                         */
                        clickCheckButton(rimetClock);

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

            try {
                //android>21 = 5.0时可以用ACTION_SET_TEXT
                // android>18 3.0.1可以通过复制的手段,先确定焦点，再粘贴ACTION_PASTE
                // 使用剪切板
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", phone);
                clipboard.setPrimaryClip(clip);

                Thread.sleep(500);
                nodePhone.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                Thread.sleep(500);
                nodePhone.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
                Thread.sleep(500);
//                        nodePhone.performAction(AccessibilityNodeInfo.ACTION_CLEAR_SELECTION);
                //焦点    （n是AccessibilityNodeInfo对象）
                //粘贴进入内容
                nodePhone.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                Thread.sleep(500);
                Log.e("wangsc", nodePhone.getText().toString());


                clip = ClipData.newPlainText("text", password);
                clipboard.setPrimaryClip(clip);

                nodePassword.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                Thread.sleep(500);
                nodePassword.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
                Thread.sleep(500);
//                        nodePassword.performAction(AccessibilityNodeInfo.ACTION_CLEAR_SELECTION);
                //焦点    （n是AccessibilityNodeInfo对象）
                //粘贴进入内容
                nodePassword.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                Log.e("wangsc", nodePassword.getText().toString());
                Thread.sleep(500);
            } catch (Exception e) {

            }

            // 点击登录
            clickView(nodeLogin);
        }
    }

    private void LoginPwd() {
        String password = mDataContext.getSetting(Setting.KEYS.password, "").getString();

        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        getAllNodesToList(nodeInfo);

        int nodeIndex = 0;
        AccessibilityNodeInfo nodePassword = null, nodeLogin = null;
        for (AccessibilityNodeInfo node : allNodesInActiveWindow) {
            if (node.getClassName().toString().equals("android.widget.EditText")) {
                switch (nodeIndex) {
                    case 0:
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
        if (nodePassword != null && nodeLogin != null && !password.isEmpty()) {

            try {
                //android>21 = 5.0时可以用ACTION_SET_TEXT
                // android>18 3.0.1可以通过复制的手段,先确定焦点，再粘贴ACTION_PASTE
                // 使用剪切板
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", password);
                clipboard.setPrimaryClip(clip);

                nodePassword.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                nodePassword.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
//                        nodePassword.performAction(AccessibilityNodeInfo.ACTION_CLEAR_SELECTION);
                //焦点    （n是AccessibilityNodeInfo对象）
                //粘贴进入内容
                nodePassword.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                Log.e("wangsc", nodePassword.getText().toString());
            } catch (Exception e) {

            }

            // 点击登录
            clickView(nodeLogin);
        }
    }

    private void clickCheckButton(RimetClock rc) {
        if (!_Utils.rimetCheckClockId.equals(rc.getId())) {
            String text = rc.getSummery() + "打卡";
            Log.e("wangsc", "button text : " + text);
            clickViewListByDescription(text);


            if (clickViewListByDescription("我知道了")) {
                mDataContext.addRunLog("常规打卡", new DateTime().toLongDateTimeString());
                _Utils.rimetCheckClockId = rc.getId();

            }
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

    /**
     * 点击Text = btnText的按钮，只点击一个view即返回。
     *
     * @param viewText
     * @return
     */
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
