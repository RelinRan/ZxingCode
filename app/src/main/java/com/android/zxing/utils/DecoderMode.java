/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.zxing.utils;

public final class DecoderMode {

    /**
     * 默认情况下，发送此邮件会解码我们了解的所有条形码。 但是它
     * 可能对将扫描限制为某些格式很有用。 使用
     * {@link android.content.Intent＃putExtra（String，String）}，具有以下值之一。
     * <p>
     * 设置此属性实际上是使用{@link #FORMATS}设置显式格式的快捷方式。
     * 被该设置覆盖。
     */
    public static final String MODE = "SCAN_MODE";

    /**
     * 仅解码UPC和EAN条形码。 这是购物应用程序的正确选择，
     * 产品的价格，评论等。
     */
    public static final String PRODUCT_MODE = "PRODUCT_MODE";

    /**
     * 仅解码一维条形码。
     */
    public static final String ONE_D_MODE = "ONE_D_MODE";

    /**
     * 仅解码QR码。
     */
    public static final String QR_CODE_MODE = "QR_CODE_MODE";

    /**
     * 仅解码数据矩阵代码。
     */
    public static final String DATA_MATRIX_MODE = "DATA_MATRIX_MODE";

    /**
     * 仅解码Aztec。
     */
    public static final String AZTEC_MODE = "AZTEC_MODE";

    /**
     * 仅解码PDF417。
     */
    public static final String PDF417_MODE = "PDF417_MODE";

    /**
     * 以逗号分隔的要扫描格式的列表。 值必须与的名称匹配
     * {@link com.google.zxing.BarcodeFormat} s，例如 {@link com.google.zxing.BarcodeFormat＃EAN_13}。
     * 示例：“ EAN_13，EAN_8，QR_CODE”。 这会覆盖{@link #MODE}。
     */
    public static final String FORMATS = "SCAN_FORMATS";
}
