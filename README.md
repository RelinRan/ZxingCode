# ZxingCode
[国外GitHub](https://github.com/RelinRan/ZxingCode)、[国内Gitee](https://gitee.com/relin/ZxingCode)  
以core-3.4.jar为基础构建，具有如下功能：   
1.创建条形码、二维码;    
2.相机扫描条形码、二维码;    
3.读取照片条形码、二维码;     
4.识别率算法修改提升;    
## FIX-2022.5.12.1
1.新增手电筒开关调用方法;     
2.新增暂停解码方法;    
3.新增恢复解码方法;      
4.虚拟机扫码崩溃;    
5.OnScanCodeListener统一为一个;  
6.图形变形问题处理;  
## Dependencies
./build.grade | setting.grade
```
allprojects {
    repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
./app/build.grade
```
dependencies {
	implementation 'com.github.RelinRan:ZxingCode:2022.5.12.1'
}
```
## ARR
GitHub下载：[zxing_code_2022.5.12.1.aar](https://github.com/RelinRan/ZxingCode/blob/master/zxing_code_2022.5.12.1.aar)  
Gitee下载：[zxing_code_2022.5.12.1.aar](https://gitee.com/relin/ZxingCode/blob/master/zxing_code_2022.5.12.1.aar)  
下载之后放入libs文件夹里面，然后./app/build.gradle配置如下
```
android {
    ....
    repositories {
        flatDir {
            dirs 'libs'
        }
    }
}
dependencies {
    implementation(name: 'zxing_code_2022.5.12.1', ext: 'aar')
}
```
## 权限
注意：系统6.0以上进行动态权限申请。
```
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.VIBRATE" />
```
## ScanCodeView
```
<com.android.zxing.view.ScanCodeView
    android:id="@+id/scan_code"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```
## 属性
```
<!--扫描中心X-->
<attr name="areaCenterX" format="dimension|reference"></attr>
<!--扫描中心Y-->
<attr name="areaCenterY" format="dimension|reference"></attr>
<!--扫描宽度-->
<attr name="areaWidth" format="dimension|reference"></attr>
<!--扫描高度-->
<attr name="areaHeight" format="dimension|reference"></attr>
<!--扫描背景-->
<attr name="backgroundColor" format="color|reference"></attr>
<!--角落可见性-->
<attr name="cornerVisible" format="boolean|reference"></attr>
<!--角落线条颜色-->
<attr name="cornerLineColor" format="color|reference"></attr>
<!--角落线条间距-->
<attr name="cornerLineMargin" format="dimension|reference"></attr>
<!--角落线条长度-->
<attr name="cornerLineLength" format="dimension|reference"></attr>
<!--角落线条宽度-->
<attr name="cornerLineWidth" format="dimension|reference"></attr>
<!--动画时长-->
<attr name="duration" format="integer|reference"></attr>
<!--扫描线资源-->
<attr name="lineDrawable" format="integer|reference"></attr>
<!--扫描震动-->
<attr name="vibrator" format="boolean|reference"></attr>
```
## 手电筒开关
```
ScanCodeView scan_code = findViewById(R.id.scan_code);
scan_code.toggleTorch();
```
## 暂停解码
```
ScanCodeView scan_code = findViewById(R.id.scan_code);
scan_code.setPause(true);
```
## 恢复解码
```
ScanCodeView scan_code = findViewById(R.id.scan_code);
scan_code.setResume(true);
```
## 扫码监听
```
ScanCodeView scan_code = findViewById(R.id.scan_code);
scan_code.setOnScanCodeListener(new ScanResultCallback());
//扫码监听
private class ScanResultCallback implements OnScanCodeListener{
@Override
public void onScanCodeSucceed(Result result) {
    String code = result.getText();
}

@Override
public void onScanCodeFailed(ReaderException exception) {

   }
}
```
## 创建二维码
```
Bitmap qrCode = ZXWriter.createQRCode("xxxx")
```
## 创建条形码
```
Bitmap barCode = ZXWriter.createCode(BarcodeFormat.CODE_128,"xxxx",300,150);
```
## 读取照片条形码/二维码
```
File file = new File("/storage/emulated/0/tencent/TIMfile_recv/0001.png");
ZXReader.fromFile(file, new OnScanCodeListener() {
     @Override
     public void onScanCodeSucceed(Result result) {

     }

     @Override
     public void onScanCodeFailed(ReaderException exception) {

     }
});
```
