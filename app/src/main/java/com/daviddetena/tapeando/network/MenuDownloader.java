package com.daviddetena.tapeando.network;

import android.os.AsyncTask;

import com.daviddetena.tapeando.model.Allergen;
import com.daviddetena.tapeando.model.Course;
import com.daviddetena.tapeando.model.Courses;
import com.daviddetena.tapeando.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;


// AsyncTask para descargar en segundo plano del menu
public class MenuDownloader extends AsyncTask<String, Integer, Courses> {

    protected WeakReference<OnCoursesReceivedListener> mOnCoursesReceivedListener;

    public MenuDownloader(OnCoursesReceivedListener onCoursesReceivedListener) {
        mOnCoursesReceivedListener = new WeakReference<>(onCoursesReceivedListener);
    }

    @Override
    protected Courses doInBackground(String... params) {
        Courses courses = Courses.getInstance();
        URL url;
        InputStream input = null;
        try {
            url = new URL(Constants.JSON_DROPBOX_URL);
            HttpURLConnection con = (HttpURLConnection) (url.openConnection());
            con.setConnectTimeout(5000);
            con.connect();
            int responseLength = con.getContentLength();
            byte data[] = new byte[1024];
            long currentBytes  = 0;
            int downloadedBytes;
            input = con.getInputStream();
            StringBuilder sb = new StringBuilder();
            while ((downloadedBytes = input.read(data)) != -1) {
                if (isCancelled()) {
                    input.close();
                    return null;
                }

                sb.append(new String(data, 0, downloadedBytes));

                // Si tuviéramos una longitud de respuesta podríamos incluso actualizar la barra de progreso
                if (responseLength > 0) {
                    currentBytes += downloadedBytes;
                    publishProgress((int) ((currentBytes * 100) / responseLength));
                }
                else {
                    currentBytes++;
                    publishProgress((int)currentBytes * 10);
                }
            }

            JSONObject jsonRoot = new JSONObject(sb.toString());
            JSONArray platesArray = jsonRoot.getJSONArray("courses");

            if (platesArray.length() > 0){

                courses.clearCourses();

                for (int i = 0; i < platesArray.length(); i++) {
                    JSONObject plate = platesArray.getJSONObject(i);

                    int identifier = plate.getInt("identifier");
                    String name = plate.getString("name");
                    String type = plate.getString("type");
                    String image = plate.getString("image");
                    String imageUrl = plate.getString("image_url");
                    String description = plate.getString("description");
                    float price = (float)plate.getDouble("price");

                    JSONArray allergensArray = plate.getJSONArray("allergens");
                    LinkedList<Allergen> allergens = new LinkedList<>();
                    for (int j = 0; j < allergensArray.length(); j++) {
                        JSONObject allergen = allergensArray.getJSONObject(j);
                        String aName = allergen.getString("name");
                        String anIcon = allergen.getString("icon");
                        allergens.add(new Allergen(aName, anIcon));
                    }

                    courses.addCourse(new Course(0, identifier, name, type, image, imageUrl, description, allergens, price, ""));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return courses;
    }

    @Override
    protected void onPostExecute(Courses courses) {
        super.onPostExecute(courses);
        if (mOnCoursesReceivedListener != null && mOnCoursesReceivedListener.get() != null) {
            if (courses.getCourses().size()>0){
                mOnCoursesReceivedListener.get().onCoursesReceivedListener();
            }
        }
    }

    public interface OnCoursesReceivedListener {
        void onCoursesReceivedListener();
    }
}
