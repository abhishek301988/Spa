package abhishek.dev.spa;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import abhishek.dev.spa.Common.Common;
import abhishek.dev.spa.Fragments.Home;
import abhishek.dev.spa.Fragments.Shop;
import abhishek.dev.spa.Model.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.bottom_nav)
    BottomNavigationView bottomNavigationView;

    BottomSheetDialog bottomSheetDialog;

    CollectionReference userRef;

     AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(HomeActivity.this);

        //initialise
        userRef = FirebaseFirestore.getInstance().collection("User");
        alertDialog = new SpotsDialog.Builder().setContext(this).build();

        //checking intent login is true enable full access
        //if login is false then shop to view services
        if(getIntent() != null){
            boolean isLogin = getIntent().getBooleanExtra(Common.IS_LOGIN,false);
            if(isLogin)
            {
           //      alertDialog.show();
                //to check user exist
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(final Account account) {
                        if(account != null){
                            DocumentReference currentUser = userRef.document(account.getPhoneNumber().toString());
                            currentUser.get()
                                    .addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful())
                                            {
                                                DocumentSnapshot userSnapShot = task.getResult();

                                                if(!userSnapShot.exists()){

                                                    showUpdateDialog(account.getPhoneNumber().toString());

                                                }
                                                else{
                                                    //if user already available
                                                    Common.currentUser = userSnapShot.toObject(User.class);
                                                    bottomNavigationView.setSelectedItemId(R.id.home);
                                                }
                                                if(alertDialog.isShowing())
                                                    alertDialog.dismiss();
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {
                        Toast.makeText(HomeActivity.this,""+accountKitError.getErrorType().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }

        //view
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            Fragment fragment = null;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            if(menuItem.getItemId() == R.id.home)
                fragment = new Home();
            else if(menuItem.getItemId() == R.id.shop)
                fragment = new Shop();

             return loadFragment(fragment);
            }
        });


    }

    private boolean loadFragment(Fragment fragment) {
        if(fragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment)
                    .commit();
            return true;

        }
        return false;

    }

    private void showUpdateDialog(final String phoneNumber) {

        if(alertDialog.isShowing())
            alertDialog.dismiss();


        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setTitle("One more step..");
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_update_information,null);

        Button btn_update = (Button)sheetView.findViewById(R.id.btn_update);
        final TextInputEditText et_name = (TextInputEditText)sheetView.findViewById(R.id.et_name);
        final TextInputEditText et_address = (TextInputEditText)sheetView.findViewById(R.id.et_address);


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User user = new User(et_name.getText().toString(),et_address.getText().toString(),phoneNumber);
                userRef.document(phoneNumber)
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                bottomSheetDialog.dismiss();

                                if(alertDialog.isShowing())
                                    alertDialog.dismiss();

                                Common.currentUser = user;
                                bottomNavigationView.setSelectedItemId(R.id.home);

                                Toast.makeText(HomeActivity.this,"Thank You",Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        bottomSheetDialog.dismiss();
                        Toast.makeText(HomeActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }
}
