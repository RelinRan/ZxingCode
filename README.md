# ZxingCode
以core-3.4.jar为基础构建，具有如下功能：
1.创建条形码、二维码。
2.相机扫描条形码、二维码。
3.读取照片条形码、二维码。
## Dependencies
1.build.grade
```
allprojects {
    repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
2./app/build.grade
```
dependencies {
	implementation 'com.github.RelinRan:ZxingCode:1.0.1'
}
```

## 权限
注意：系统6.0以上进行动态权限申请。
```
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.VIBRATE" />
```

## ScanCodeView
1.扫码控件
```
<com.android.zxing.view.ScanCodeView
    android:id="@+id/scan_code"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```
2.属性值
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
3.扫码监听
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
4.创建二维码
```
Bitmap qrCode = ZXWriter.createQRCode("xxxx")
```
5.创建条形码
```
Bitmap barCode = ZXWriter.createCode(BarcodeFormat.CODE_128,"xxxx",300,150);
```
6.读取照片条形码/二维码
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
