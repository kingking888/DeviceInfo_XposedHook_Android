package com.my.deviceinfohook;

import android.net.wifi.WifiInfo;
import android.telephony.TelephonyManager;

import java.net.InetAddress;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Arrow implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        // TODO Auto-generated method stub
        XposedBridge.log("Loaded app ==== Congratulations! hook is go! =====");
        // 不是需要 Hook 的包直接返回
        if (!loadPackageParam.packageName.equals("com.my.deviceinfo"))
            return;

        XposedBridge.log("app包名：" + loadPackageParam.packageName);

        XposedHelpers.findAndHookMethod("com.my.deviceinfo.MainActivity", // 被Hook函数所在的类(包名+类名)
                loadPackageParam.classLoader,
                "testFunc", // 被Hook函数的名称ordinaryFunc
                String.class, // 被Hook函数的第一个参数String
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.beforeHookedMethod(param);
                        // Hook函数之前执行的代码
                        // 打印方法的参数信息
                        XposedBridge.log("param1：" + param.args[0]);
                        // 打印堆栈查看调用关系
                        StackTraceElement[] mylogs = new Throwable("mylogs")
                                .getStackTrace();
                        for (int i = 0; i < mylogs.length; i++) {
                            XposedBridge.log("查看堆栈：" + mylogs[i].toString());
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
                        // Hook函数之后执行的代码
                        // 获取类
                        Class<?> clazz = param.thisObject.getClass();
                        XposedBridge.log("要hook的方法所在的类：" + clazz.getName());

                        // 获取方法的返回值
                        Object resultString = param.getResult();
                        XposedBridge.log("返回值：" + resultString.toString());
                        // 修改方法的返回值
                        param.setResult("Target was hooked!");
                    }
                });

        /**
         * 拦截系统方法 篡改IMEI设备号
         * */
        XposedBridge.hookAllMethods(TelephonyManager.class, "getImei",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.afterHookedMethod(param);
                        XposedBridge.log("imei：" + param.getResult());
                        param.setResult("999999");
                    }
                });

        /**
         * 拦截系统方法 获取序列号
         * */
        XposedHelpers.findAndHookMethod("android.os.SystemProperties", // 被Hook函数所在的类(包名+类名)
                loadPackageParam.classLoader,
                "get", // 被Hook函数的名称ordinaryFunc
                String.class, // 被Hook函数的第一个参数String
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("SerialNumber ：" + param.getResult());
                        param.setResult("6666666");
                    }
                });

        //拦截流量上网IP地址
        XposedHelpers.findAndHookMethod(InetAddress.class, "getHostAddress",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("流量 IP地址：" + param.getResult());
                        param.setResult("99.99.99.99 (is hooked)");
                    }
                });


        //拦截WiFi上网IP地址
        XposedHelpers.findAndHookMethod(WifiInfo.class, "getIpAddress",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("WIFI IP地址：" + param.getResult());
                        // 分割字符串
                        String[] str = "192.168.99.99".split("\\.");
                        // 定义一个字符串，用来存储反转后的IP地址
                        String ipAdress = "";
                        // for循环控制IP地址反转
                        for (int i = 3; i >= 0; i--) {
                            ipAdress = ipAdress + str[i] + ".";
                        }
                        // 去除最后一位的"."
                        ipAdress = ipAdress.substring(0, ipAdress.length() - 1);
                        // 返回新的整形IP地址
                        param.setResult((int) ipToLong(ipAdress));
                    }
                });

        /**
         * 拦截系统方法 篡改MEID设备号
         * */
        XposedBridge.hookAllMethods(TelephonyManager.class, "getMeid",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.afterHookedMethod(param);
                        XposedBridge.log("MEID ：" + param.getResult());
                        param.setResult("8888888");
                    }
                });
    }

    public static long ipToLong(String strIp) {
        long[] ip = new long[4];
        // 先找到IP地址字符串中.的位置
        int position1 = strIp.indexOf(".");
        int position2 = strIp.indexOf(".", position1 + 1);
        int position3 = strIp.indexOf(".", position2 + 1);
        // 将每个.之间的字符串转换成整形
        ip[0] = Long.parseLong(strIp.substring(0, position1));
        ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(strIp.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] >> 8) + ip[3];
    }
}
