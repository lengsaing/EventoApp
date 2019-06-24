package com.app.sangleng.evento;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {


    public SearchFragment() {
        // Required empty public constructor
    }

    EditText edit_name;
    EditText edit_location;
    EditText edit_date;
    EditText edit_category;
    Button btn_search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        edit_name = view.findViewById(R.id.edit_name);
        edit_location = view.findViewById(R.id.edit_location);
        edit_date = view.findViewById(R.id.edit_date);
        edit_category = view.findViewById(R.id.edit_category);
        btn_search = view.findViewById(R.id.btn_search);

        btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String eventName = edit_name.getText().toString().trim();
                    final String eventLoc = edit_location.getText().toString().trim();
                    final String eventDate = edit_date.getText().toString().trim();
                    final String eventCat = edit_category.getText().toString().trim();

                    if(!eventName.isEmpty() || !eventLoc.isEmpty() || !eventDate.isEmpty() || !eventCat.isEmpty()) {
                        Intent intent = new Intent(getActivity(), SearchQueryActivity.class);
                        intent.putExtra("eventName", eventName);
                        intent.putExtra("eventLoc", eventLoc);
                        intent.putExtra("eventDate", eventDate);
                        intent.putExtra("eventCat", eventCat);
                        startActivity(intent);
                    }
                }});
        return view;
    }

}
