package com.wangsc.lovehome.fragment;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.wangsc.lovehome.DataContext;
import com.wangsc.lovehome.IfragmentInit;
import com.wangsc.lovehome.R;
import com.wangsc.lovehome._Utils;
import com.wangsc.lovehome.model.RimetClock;
import com.wangsc.lovehome.model.Setting;

import java.text.DecimalFormat;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClockFragment extends Fragment implements IfragmentInit {

    private DataContext mDataContext;
    private Switch aSwitchIsClockRandom;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataContext = new DataContext(getContext());
        adapter = new RimetClockListdAdapter();
        rimetClockList = mDataContext.getRimetClocks();
        if (rimetClockList.size() <= 0) {
            rimetClockList.add(new RimetClock(8, 0, "上班"));
            rimetClockList.add(new RimetClock(12, 5, "下班"));
            rimetClockList.add(new RimetClock(13, 10, "上班"));
            rimetClockList.add(new RimetClock(18, 5, "下班"));
            mDataContext.addRimetClocks(rimetClockList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clock, container, false);

        listViewRimetClock = view.findViewById(R.id.listView_clock);

        listViewRimetClock.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                RimetClock rimetClock = rimetClockList.get(position);
                editClockDialog(rimetClock);
                return true;
            }
        });
        listViewRimetClock.setAdapter(adapter);

        aSwitchIsClockRandom = view.findViewById(R.id.switch_isClockRandom);
        aSwitchIsClockRandom.setChecked(mDataContext.getSetting(Setting.KEYS.is_clock_random, true).getBoolean());
        aSwitchIsClockRandom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDataContext.editSetting(Setting.KEYS.is_clock_random, isChecked);
            }
        });

        return view;
    }

    @Override
    public void init() {
        Log.e("wangsc", "RunlogsFragment.init()");
    }

    public void editClockDialog(final RimetClock rimetClock) {
        View view = View.inflate(getContext(), R.layout.inflate_dialog_eidt_clock, null);
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(getContext()).create();
        dialog.setView(view);
        dialog.setTitle("编辑闹钟");

        String[] hourNumbers = new String[24];
        for (int i = 0; i < 24; i++) {
            hourNumbers[i] = i + "时";
        }
        String[] minNumbers = new String[60];
        for (int i = 0; i < 60; i++) {
            minNumbers[i] = i + "分";
        }
        final NumberPicker number_hour = (NumberPicker) view.findViewById(R.id.number_hour);
        final NumberPicker number_min = (NumberPicker) view.findViewById(R.id.number_min);
        number_hour.setMinValue(0);
        number_hour.setDisplayedValues(hourNumbers);
        number_hour.setMaxValue(23);
        number_hour.setValue(rimetClock.getHour());
        number_hour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中
        number_min.setMinValue(0);
        number_min.setDisplayedValues(minNumbers);
        number_min.setMaxValue(59);
        number_min.setValue(rimetClock.getMinite());
        number_min.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // 禁止对话框打开后数字选择框被选中

        final EditText editTextSummary = view.findViewById(R.id.editText_summary);
        editTextSummary.setText(rimetClock.getSummery());

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                rimetClock.setHour(number_hour.getValue());
                rimetClock.setMinite(number_min.getValue());
                rimetClock.setSummery(editTextSummary.getText().toString());
                mDataContext.updateRimetClock(rimetClock);
                rimetClockList = mDataContext.getRimetClocks();
                adapter.notifyDataSetChanged();
                if (mDataContext.getSetting(Setting.KEYS.listener, false).getBoolean() == true) {
                    OprateFragment.setAlarmRimet(getContext());
                }

            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private ListView listViewRimetClock;
    private List<RimetClock> rimetClockList;
    private RimetClockListdAdapter adapter;


    protected class RimetClockListdAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return rimetClockList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int index = position;
            try {
                convertView = View.inflate(getContext(), R.layout.inflate_clock, null);
                final RimetClock rimetClock = rimetClockList.get(position);
                TextView textView1 = convertView.findViewById(R.id.textView_Time);
                TextView textView2 = convertView.findViewById(R.id.textView_summary);
                textView1.setText(new DecimalFormat("00").format(rimetClock.getHour()) + ":" + new DecimalFormat("00").format(rimetClock.getMinite()));
                textView2.setText(rimetClock.getSummery());
                if (rimetClock.getSummery().equals("上班")) {
                    textView2.setTextColor(Color.RED);
                } else if (rimetClock.getSummery().equals("下班")) {
                    textView2.setTextColor(Color.BLUE);
                }
            } catch (Exception e) {
                _Utils.printException(getContext(), e);
            }
            return convertView;
        }
    }
}
