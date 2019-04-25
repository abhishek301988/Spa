package abhishek.dev.spa.Adapter;

import java.util.List;

import abhishek.dev.spa.Model.Banner;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class HomeSliderAdapter extends SliderAdapter {

    List<Banner> bannerList;

    public HomeSliderAdapter(List<Banner> bannerslist) {
        this.bannerList = bannerslist;
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        imageSlideViewHolder.bindImageSlide(bannerList.get(position).getImage());
    }
}
