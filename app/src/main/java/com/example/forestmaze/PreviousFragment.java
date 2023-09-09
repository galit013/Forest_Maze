package com.example.forestmaze;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PreviousFragment extends Fragment {

    ImageView pre_inst;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_previous_fragment, container, false);
        pre_inst = view.findViewById(R.id.pre_inst);

        // set image based on page number
        if(CurrentUser.page_number == 1){
        pre_inst.setImageResource(R.drawable.inst1);
        }
        else if(CurrentUser.page_number == 2){
            pre_inst.setImageResource(R.drawable.inst2);
        }
        else if(CurrentUser.page_number == 3){
            pre_inst.setImageResource(R.drawable.inst3);
        }
        else if(CurrentUser.page_number == 4){
            pre_inst.setImageResource(R.drawable.inst4);
        }
        else if(CurrentUser.page_number == 5){
            pre_inst.setImageResource(R.drawable.inst5);
        }
        return view;
    }
}