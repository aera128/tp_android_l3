package fr.unicaen.aera128.immobilier.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private List<String> imgUrls;

    public ViewPagerAdapter(Context context, List<String> imgUrls) {
        this.context = context;
        this.imgUrls = imgUrls;
    }

    @Override
    public int getCount() {
        return imgUrls.size();

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        /**
         * Vérification si l'image est en locale ou non
         */
        if (imgUrls.get(position).contains(context.getCacheDir().toString())) {
            Picasso.get().load(new File(imgUrls.get(position))).resize(1000, 800).centerCrop().into(imageView);
        } else {
            Picasso.get().load(imgUrls.get(position)).resize(1000, 800).centerCrop().into(imageView);
        }
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
