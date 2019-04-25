package abhishek.dev.spa.Interface;

import java.util.List;

import abhishek.dev.spa.Model.Banner;

public interface ILookBookLoadListener {
    void OnLookBookLoadSuccess(List<Banner> banners);
    void OnLookBookLoadFailed(String message);
}
