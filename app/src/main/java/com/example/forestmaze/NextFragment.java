package com.example.forestmaze;

import android.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class NextFragment extends Fragment {

    ImageView next_inst;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_next_fragment, container, false);
        next_inst = view.findViewById(R.id.next_inst);

        // set image based on page number
        if(CurrentUser.page_number == 1){
            next_inst.setImageResource(R.drawable.inst1);
        }
        else if(CurrentUser.page_number == 2){
            next_inst.setImageResource(R.drawable.inst2);
        }
        else if(CurrentUser.page_number == 3){
            next_inst.setImageResource(R.drawable.inst3);
        }
        else if(CurrentUser.page_number == 4){
            next_inst.setImageResource(R.drawable.inst4);
        }
        else if(CurrentUser.page_number == 5){
            next_inst.setImageResource(R.drawable.inst5);
        }
        return view;
    }
}