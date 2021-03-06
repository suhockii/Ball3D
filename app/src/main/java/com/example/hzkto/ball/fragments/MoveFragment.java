package com.example.hzkto.ball.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.example.hzkto.ball.R;
import com.example.hzkto.ball.system.DrawThread;

import static com.example.hzkto.ball.Constants.SETTINGS_LIGHT;
import static com.example.hzkto.ball.Constants.SETTINGS_MOVE;
import static com.example.hzkto.ball.MainActivity.closeKeyboard;
import static com.example.hzkto.ball.MainActivity.setToolbarTitle;
import static com.example.hzkto.ball.R.id.container;

/**
 * Created by hzkto on 11/23/2016.
 */

public class MoveFragment extends MyFragment {
    Button btnOk, btnClose;
    TextView tvX, tvY, tvZ, tvRadius, tvStandart;
    View focusView;

    public MoveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_move, container, false);
        setToolbarTitle(getActivity(), R.string.move);
        initViews(view);
        setListeners();
        return view;
    }

    private void setListeners() {
        tvStandart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvRadius.setText(String.valueOf(DrawThread.radius));
                tvX.setText(String.valueOf(DrawThread.center.x));
                tvY.setText(String.valueOf(DrawThread.center.y));
                tvZ.setText(String.valueOf(DrawThread.center.z));
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    setToolbarTitle(getActivity(), R.string.sphere);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
        tvRadius.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        btnOk.callOnClick();
                        return true;
                    }
                    return false;
                }
            }
        });
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
                    if (tvRadius.getText().toString().equals("")) {
                        focusView = tvRadius;
                        focusView.requestFocus();
                        return;
                    }
                    closeKeyboard(getContext());
                    Bundle args = new Bundle();
                    args.putInt("label", SETTINGS_MOVE);
                    args.putDouble("centerX", Double.valueOf(tvX.getText().toString()));
                    args.putDouble("centerY", Double.valueOf(tvY.getText().toString()));
                    args.putDouble("centerZ", Double.valueOf(tvZ.getText().toString()));
                    args.putDouble("radius", Double.valueOf(tvRadius.getText().toString()));

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
        btnOk = (Button) v.findViewById(R.id.f_config_btnOk);
        btnClose = (Button) v.findViewById(R.id.f_config_btnClose);
        tvX = (TextView) v.findViewById(R.id.f_config_centerX);
        tvY = (TextView) v.findViewById(R.id.f_config_centerY);
        tvZ = (TextView) v.findViewById(R.id.f_config_centerZ);
        tvStandart = (TextView) v.findViewById(R.id.f_config_tvStandart);
        tvRadius = (TextView) v.findViewById(R.id.f_config_radius);
    }
}
