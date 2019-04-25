package abhishek.dev.spa.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.accountkit.AccountKit;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import abhishek.dev.spa.Adapter.HomeSliderAdapter;
import abhishek.dev.spa.Adapter.LookBookAdapter;
import abhishek.dev.spa.Common.Common;
import abhishek.dev.spa.Interface.IBannerLoadListener;
import abhishek.dev.spa.Interface.ILookBookLoadListener;
import abhishek.dev.spa.Model.Banner;
import abhishek.dev.spa.R;
import abhishek.dev.spa.Service.PicassoImageLoadingService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ss.com.bannerslider.Slider;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment implements IBannerLoadListener, ILookBookLoadListener {

    private Unbinder unbinder;

    @BindView(R.id.layout_user_info)
    LinearLayout layout_user_information;

    @BindView(R.id.txt_uname)
    TextView txt_uname;

    @BindView(R.id.bannerslider)
    Slider banner_Slider;

    @BindView(R.id.recycler_lb)
    RecyclerView lookbook_rv;

    //firestore db
    CollectionReference bannerRef,lookbookRef;

    //interface
    IBannerLoadListener iBannerLoadListener;
    ILookBookLoadListener iLookBookLoadListener;



    public Home() {
        bannerRef = FirebaseFirestore.getInstance().collection("Banner");
        lookbookRef = FirebaseFirestore.getInstance().collection("Lookbook");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this,view);

        Slider.init(new PicassoImageLoadingService());
        iBannerLoadListener = this;
        iLookBookLoadListener = this;

        if( AccountKit.getCurrentAccessToken()!= null){
            setUserInformation();
            loadBanner();
            loadLookbook();
        }
        return view;
    }

    private void loadLookbook() {
        lookbookRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Banner> lookbooks = new ArrayList<>();

                if(task.isSuccessful()){

                    for(QueryDocumentSnapshot bannerSnapshot: task.getResult())
                    {
                        Banner banner = bannerSnapshot.toObject(Banner.class);
                        lookbooks.add(banner);

                    }
                    iLookBookLoadListener.OnLookBookLoadSuccess(lookbooks);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iLookBookLoadListener.OnLookBookLoadFailed(e.getMessage());
            }
        });
    }

    private void loadBanner() {
        bannerRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        List<Banner> banners = new ArrayList<>();

                        if(task.isSuccessful()){

                            for(QueryDocumentSnapshot bannerSnapshot: task.getResult())
                            {
                                Banner banner = bannerSnapshot.toObject(Banner.class);
                                banners.add(banner);

                            }
                            iBannerLoadListener.OnBannerLoadSuccess(banners);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBannerLoadListener.OnBannerLoadFailed(e.getMessage());
            }
        });
    }

    private void setUserInformation() {
        layout_user_information.setVisibility(View.VISIBLE);
        txt_uname.setText(Common.currentUser.getName());
    }

    @Override
    public void OnBannerLoadSuccess(List<Banner> banners) {
        banner_Slider.setAdapter(new HomeSliderAdapter(banners));
    }

    @Override
    public void OnBannerLoadFailed(String message) {

        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnLookBookLoadSuccess(List<Banner> lookbook) {
        lookbook_rv.setHasFixedSize(true);
        lookbook_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        lookbook_rv.setAdapter(new LookBookAdapter(getActivity(),lookbook));
    }

    @Override
    public void OnLookBookLoadFailed(String message) {
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }
}
