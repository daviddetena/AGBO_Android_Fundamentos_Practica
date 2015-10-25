package com.daviddetena.tapeando.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class ImageDownloader extends AsyncTask<ImageDownloader.ImageDownloaderParams, Integer, Bitmap>{

    private  final WeakReference<ImageView> mImageViewWeakReference;
    private int mDefaultImageResId;
    private Context mContext;

    public ImageDownloader(Context context, ImageView imageViewReference, int defaultImageResId) {
        mContext = context;
        mImageViewWeakReference = new WeakReference<>(imageViewReference);
        mDefaultImageResId = defaultImageResId;

        if (mImageViewWeakReference != null && mImageViewWeakReference.get() != null){
            mImageViewWeakReference.get().setImageResource(mDefaultImageResId);
        }
    }


    @Override
    protected Bitmap doInBackground(ImageDownloaderParams... params) {

        File imageFile = new File(mContext.getCacheDir(), params[0].getCachedImageName());
        if (imageFile.exists()) {
            // La imagen existe
            return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        }

        // La imagen no existe, la descargamos
        InputStream inputStream = null;
        try {
            inputStream = new java.net.URL(params[0].getImageUrl()).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Guardamos la imagen en caché
            FileOutputStream fileOutputStrea = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStrea);

            return bitmap;
        } catch (Exception ex) {

            Log.e(ImageDownloader.class.getSimpleName(), "Error downloading image", ex);
            // Devolmenos la imagen por defecto
            return BitmapFactory.decodeResource(mContext.getResources(), mDefaultImageResId);

        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception ex) {
                Log.e(ImageDownloader.class.getSimpleName(), "Error finalizing image download", ex);
            }
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (mImageViewWeakReference != null && mImageViewWeakReference.get() != null){
            mImageViewWeakReference.get().setImageBitmap(bitmap);
        }
    }

    public static class ImageDownloaderParams {

        public String getImageUrl() {
            return mImageUrl;
        }

        public String getCachedImageName() {
            return mCachedImageName;
        }

        String mImageUrl;
        String mCachedImageName;

        public ImageDownloaderParams(String imageUrl, String cachedImageName) {
            this.mImageUrl = imageUrl;
            this.mCachedImageName = cachedImageName;
        }
    }
}
