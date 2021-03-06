package com.example.hzkto.ball.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.hzkto.ball.R;

import static com.example.hzkto.ball.Constants.SETTINGS_LIGHT;
import static com.example.hzkto.ball.Constants.SETTINGS_ROTATE;
import static com.example.hzkto.ball.MainActivity.closeKeyboard;
import static com.example.hzkto.ball.MainActivity.setToolbarTitle;
import static com.example.hzkto.ball.R.id.container;

/**
 * A simple {@link Fragment} subclass.
 */
public class RotateFragment extends MyFragment {
    Button btnOk, btnClose;
    TextView tvX, tvY, tvZ, tvStandart;
    View focusView;

    public RotateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_rotate, container, false);
        setToolbarTitle(getActivity(), R.string.rotate);
        initViews(view);
        setListeners();
        return view;
    }

    private void setListeners() {
        tvStandart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    tvX.setText("0");
                    tvY.setText("0");
                    tvZ.setText("90");
                }
            }
        } );
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    setToolbarTitle(getActivity(), R.string.sphere);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        } );
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    focusView = null;
                    if (tvX.getText().toString().equals("")) {
                        focusView = tvX;
                        focusView.requestFocus();
                        return;
                    }
                    if (tvY.getText().toString().equals("")) {
                        focusView = tvY;
                        focusView.requestFocus();
                        return;
                    }
                    if (tvZ.getText().toString().equals("")) {
                        focusView = tvZ;
                        focusView.requestFocus();
                        return;
                    }
                    closeKeyboard(getContext());
                    Bundle args = new Bundle();
                    args.putInt("label", SETTINGS_ROTATE);
                    args.putDouble("rotateX", Double.valueOf(tvX.getText().toString()));
                    args.putDouble("rotateY", Double.valueOf(tvY.getText().toString()));
                    args.putDouble("rotateZ", Double.valueOf(tvZ.getText().toString()));
                    SphereFragment sphereFragment = new SphereFragment();
                    sphereFragment.setArguments(args);
                    getFragmentManager()
                            .beginTransaction()
                            .replace(container, sphereFragment)
                            .commit();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
    }

    private void initViews(View v) {
        btnOk = (Button) v.findViewById(R.id.f_rotate_btnOk);
        btnClose = (Button) v.findViewById(R.id.f_rotate_btnClose);
        tvStandart = (TextView) v.findViewById(R.id.f_rotate_tvStandart);
        tvX = (TextView) v.findViewById(R.id.f_rotate_X);
        tvY = (TextView) v.findViewById(R.id.f_rotate_Y);
        tvZ = (TextView) v.findViewById(R.id.f_rotate_Z);
    }
}
