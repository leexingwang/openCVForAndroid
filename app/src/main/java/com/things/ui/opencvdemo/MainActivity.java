package com.things.ui.opencvdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toolbar;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.LINE_8;
import static org.opencv.imgproc.Imgproc.LINE_AA;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.but_01).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = OpenCVLoader.initDebug();
                if (b) {
                    Log.e(MainActivity.this.getClass().getSimpleName(), "Success");
                } else {
                    Log.e(MainActivity.this.getClass().getSimpleName(), "Fail");
                }
//                Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.drawable.ic_launcher);
//                Mat mat = new Mat();
//                Utils.bitmapToMat(bitmap, mat);
//                Mat dst = new Mat();
//                Imgproc.cvtColor(mat, dst, Imgproc.COLOR_BGRA2BGR);
//                Utils.matToBitmap(dst, bitmap);
////                findViewById(R.id.but_01).setBackground(new BitmapDrawable(bitmap));
//                mat.release();
//                dst.release();
//
//                Mat mat1 = Mat.zeros(500, 500, CvType.CV_8UC3);
//                mat1.setTo(new Scalar(255, 255, 255));
//                Imgproc.circle(mat1, new Point(200, 200), 500, new Scalar(0, 255, 255), 2, LINE_AA, 1);
//                Bitmap bitmap1 = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
//                Utils.matToBitmap(mat1, bitmap1);
//                findViewById(R.id.but_01).setBackground(new BitmapDrawable(bitmap1));

                Mat mat = null;
                try {
                    mat = Utils.loadResource(MainActivity.this, R.drawable.picvv);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (mat == null) {
                    return;
                }

//                Mat mat1 = null;
//                try {
//                    mat1 = Utils.loadResource(MainActivity.this, R.drawable.ic_launcher);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (mat1 == null) {
//                    return;
//                }
                int channels = mat.channels();
                int width = mat.width();
                int height = mat.height();
//                oneByOne(mat, channels, width, height);
//                allOnce(mat, channels, width, height);
//                matSplit(mat, channels, width, height);
                matBinarization(mat, channels, width, height);
//                MatAddSubMultDiv(mat, width, height);
//                matLight(mat, width, height);

            }

        });
    }

    private void MatAddSubMultDiv(Mat mat, int width, int height) {
        Mat dst=new Mat(width,height,mat.type());
        Scalar scalar=new Scalar(25,125,125);
//        Core.add(mat,scalar,dst);
        Core.subtract(mat,scalar,dst);
//        Core.multiply(mat,scalar,dst);
//        Core.divide(mat,scalar,dst);
        Bitmap bitmap1 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Imgproc.cvtColor(dst,dst,Imgproc.COLOR_BGR2RGBA);
        Utils.matToBitmap(dst, bitmap1);
        findViewById(R.id.but_01).setBackground(new BitmapDrawable(bitmap1));
    }
    private void matLight(Mat mat, int width, int height) {
        Mat dst=new Mat(width,height,mat.type());
        Scalar scalar=new Scalar(25,25,25);
        Core.add(mat,scalar,dst);
//        Core.subtract(mat,scalar,dst);
//        Core.multiply(mat,scalar,dst);
//        Core.divide(mat,scalar,dst);
        Bitmap bitmap1 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Imgproc.cvtColor(dst,dst,Imgproc.COLOR_BGR2RGBA);
        Utils.matToBitmap(dst, bitmap1);
        findViewById(R.id.but_01).setBackground(new BitmapDrawable(bitmap1));
    }
    /**
     * 图像二值化（ Image Binarization）就是将图像上的像素点的灰度值设置为0或255，也就是将整个图像呈现出明显的黑白效果的过程。
     * 在数字图像处理中，二值图像占有非常重要的地位，图像的二值化使图像中数据量大为减少，从而能凸显出目标的轮廓。
     *
     * @param mat
     * @param channels
     * @param width
     * @param height
     */
    private void matBinarization(Mat mat, int channels, int width, int height) {
        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
        mat = gray;
        MatOfDouble matOfDouble = new MatOfDouble();
        MatOfDouble stddev = new MatOfDouble();
        Core.meanStdDev(mat, matOfDouble, stddev);
        Log.e(MainActivity.class.getSimpleName(), "mean: " + matOfDouble.toArray()[0]);
        Log.e(MainActivity.class.getSimpleName(), "stddev: " + stddev.toArray()[0]);
        byte[] data = new byte[channels * width * height];
        int pv = 0;
        mat.get(0, 0, data);
        for (int i = 0; i < data.length; i++) {
            pv = (data[i] & 0xff);
            data[i] = (byte) (pv >= matOfDouble.toArray()[0] ? 255 : 0);
        }
        mat.put(0, 0, data);
        Bitmap bitmap1 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap1);
        findViewById(R.id.but_01).setBackground(new BitmapDrawable(bitmap1));
    }

    /**
     * 通道拆分
     *
     * @param mat
     * @param channels
     * @param width
     * @param height
     */
    private void matSplit(Mat mat, int channels, int width, int height) {
        List<Mat> mats = new ArrayList<>();
        Core.split(mat, mats);
        Bitmap bitmap1 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mats.get(1), bitmap1);
        findViewById(R.id.but_01).setBackground(new BitmapDrawable(bitmap1));
    }

    private void allOnce(Mat mat, int channels, int width, int height) {
        byte[] data = new byte[channels * width * height];
        int pv = 0;
        mat.get(0, 0, data);
        for (int i = 0; i < data.length; i++) {
            pv = (data[i] & 0xff);
            data[i] = (byte) (155 - pv);
        }
        mat.put(0, 0, data);
        Bitmap bitmap1 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap1);
        findViewById(R.id.but_01).setBackground(new BitmapDrawable(bitmap1));
    }

    private void oneByOne(Mat mat, int channels, int width, int height) {
        byte[] data = new byte[channels];
        int b1 = 0, g = 0, r = 0;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                mat.get(row, col, data);
                b1 = data[0] & 0xff;
                g = data[1] & 0xff;
                r = data[2] & 0xff;
                b1 = 255 - b1;
                g = 255 - g;
                r = 255 - r;
                data[0] = (byte) b1;
                data[1] = (byte) g;
                data[2] = (byte) r;
                mat.put(row, col, data);
            }
        }
        Bitmap bitmap1 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap1);
        findViewById(R.id.but_01).setBackground(new BitmapDrawable(bitmap1));
    }
}
