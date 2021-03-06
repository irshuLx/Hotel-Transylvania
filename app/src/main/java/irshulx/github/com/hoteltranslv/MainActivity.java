package irshulx.github.com.hoteltranslv;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView txtAdults, txtChildren, txtInfants, lblresult;
    List<TextView> lblRooms = new ArrayList<>();
    int[] roomBgs = {R.drawable.oval_room_1, R.drawable.oval_room_2, R.drawable.oval_room_3};
    private final int MAX_ROOMS_PER_BOOKKING = 3;
    private final int MAX_ADULTS = 3;
    private final int MAX_CHILDREN = 3;
    private final int MAX_INFANTS = 3;
    private final int MAX_HEADCOUNT = MAX_ROOMS_PER_BOOKKING * (MAX_ADULTS + MAX_CHILDREN + MAX_INFANTS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        initialize();
    }




    private void calculateRoomsRequired(int adults, int children, int infants) {
        int counter = 0;

        if(adults + children + infants == 0) {
            showError("we need atleast a pax to proceed");
            return;
        }

        if(adults + children + infants > MAX_HEADCOUNT) {
            showError("Booking exceeds maximum guests");
            return;
        }

        if(adults + children > 7){
            showError("Booking exceeds maximum adult + children");
            return;
        }

        if(children > MAX_ROOMS_PER_BOOKKING * MAX_CHILDREN){
            showError("Sorry, booking exceeds maximum  children");
            return;
        }

        if(infants > MAX_ROOMS_PER_BOOKKING * MAX_INFANTS){
            showError("Sorry, booking exceeds maximum  infants");
            return;
        }




        if(infants + children > 1 && adults ==0){
            showError("Sorry, we need atleast an adult for supervision");
            return;
        }

        List<Integer> roomDistribution = new ArrayList<>();




        float roomsNeeded = children<=3 ? 1 : children/(float) MAX_CHILDREN;
        if(roomsNeeded % 1 != 0){
            roomsNeeded++;
        }

        for(int i = 0; i < (int) roomsNeeded ; i++){
            roomDistribution.add(0);
        }


        while (children > 0){
            if(counter>=roomDistribution.size()){
                counter=0;
            }
            roomDistribution.set(counter, roomDistribution.get(counter)+1);
            counter++;
            children--;
        }


        roomsNeeded = infants<=3 ? 1 : infants/(float) MAX_INFANTS;
        if(roomsNeeded % 1 != 0){
            roomsNeeded++;
        }


        if(roomDistribution.size() < (int) roomsNeeded){
            int diff = (int)roomsNeeded - roomDistribution.size();
            for(int i = 0; i < diff ; i++){
                roomDistribution.add(0);
            }
        }


        counter=0;
        while (infants > 0){
            if(counter>=roomDistribution.size()){
                counter=0;
            }
            roomDistribution.set(counter, roomDistribution.get(counter)+1);
            counter++;
            infants--;
        }


        if(roomDistribution.size() > adults){
            showError("Sorry, we need more adults as we can't let the kids alone in rooms");
            return;
        }


        if(roomDistribution.size() < MAX_ROOMS_PER_BOOKKING){
            float adultPerRooms = adults/(float) MAX_ADULTS;
                if(adultPerRooms % 1 != 0){
                    adultPerRooms++;
                }
                int diff =  (int) adultPerRooms - roomDistribution.size();
                for(int i = 0; i< diff ;i++){
                    roomDistribution.add(0);
                }
        }



        counter = 0;
        while (adults>0){
            if(counter>=roomDistribution.size()){
                counter = 0;
            }
            roomDistribution.set(counter,roomDistribution.get(counter) + 1);
            adults--;
            counter++;
        }


        for(int i = 0; i<roomDistribution.size(); i++){
            lblRooms.get(i).setBackground(getResources().getDrawable(roomBgs[i]));
            lblRooms.get(i).setText(""+roomDistribution.get(i));
        }
    }





    /**
     * View/UI operations
     */

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        exit();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void exit() {
        super.onBackPressed();
    }

    private void initialize() {
        txtAdults = findViewById(R.id.txt_adults);
        txtChildren = findViewById(R.id.txt_children);
        txtInfants = findViewById(R.id.txt_infants);
        lblRooms.add((TextView) findViewById(R.id.lbl_room_1));
        lblRooms.add((TextView) findViewById(R.id.lbl_room_2));
        lblRooms.add((TextView) findViewById(R.id.lbl_room_3));

        setDefaults();
        addFocusListener(txtAdults);
        addFocusListener(txtChildren);
        addFocusListener(txtInfants);

        findViewById(R.id.btn_proceed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
                setDefaults();
                calculateRoomsRequired(getText(txtAdults), getText(txtChildren), getText(txtInfants));
            }
        });
    }
    private void addFocusListener(final TextView textView){
        textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    textView.setText("");
                }
            }
        });
    }

    private void setDefaults() {
        for(TextView textView: lblRooms){
            textView.setText("0");
        }
    }

    private void showError(String message){
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, 3000);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });

        snackbar.show();
    }

    private int getText(TextView textView){
        if(!TextUtils.isEmpty(textView.getText())){
            return Integer.parseInt(textView.getText().toString());
        }
        return 0;
    }

}

