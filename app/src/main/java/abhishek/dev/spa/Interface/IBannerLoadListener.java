package abhishek.dev.spa.Interface;

import java.util.List;

import abhishek.dev.spa.Model.Banner;

public interface IBannerLoadListener {
    void OnBannerLoadSuccess(List<Banner> banners);
    void OnBannerLoadFailed(String message);
}
