package com.wangsc.lovehome.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.wangsc.lovehome.DataContext;
import com.wangsc.lovehome.IfragmentInit;
import com.wangsc.lovehome.R;
import com.wangsc.lovehome.model.RunLog;
import com.wangsc.lovehome._Utils;

import java.util.List;

public class RunlogsFragment extends Fragment implements IfragmentInit {

    private DataContext mDataContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("wangsc","onCreate()");
        super.onCreate(savedInstanceState);

        mDataContext = new DataContext(getContext());
    }


    @Override
    public void init() {
        Log.e("wangsc","RunlogsFragment.init()");
        try {
            runLogs = mDataContext.getRunLogs();
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("wangsc","onCreateView()");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_runlogs, container, false);
        //

        runLogs = mDataContext.getRunLogs();
        adapter = new RunlogListdAdapter();

        listViewRunLog = view.findViewById(R.id.listView_runlog);
        listViewRunLog.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(getContext()).setMessage("确认要清空日志吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDataContext.clearRunLog();
                        runLogs = mDataContext.getRunLogs();
                        adapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("取消", null).show();
                return true;
            }
        });
        listViewRunLog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(getContext()).setMessage("确认要删除当前日志吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDataContext.deleteRunLog(runLogs.get(position).getId());
                        runLogs = mDataContext.getRunLogs();
                        adapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("取消", null).show();
            }
        });

        listViewRunLog.setAdapter(adapter);

        return view;
    }


    private ListView listViewRunLog;
    private List<RunLog> runLogs;
    private RunlogListdAdapter adapter;


    protected class RunlogListdAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return runLogs.size();
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
                convertView = View.inflate(getContext(), R.layout.inflate_runlog, null);
                final RunLog log = runLogs.get(position);
                TextView textView1 =  convertView.findViewById(R.id.textView_dateTime);
                TextView textView11 =  convertView.findViewById(R.id.textView11);
                TextView textView2 =  convertView.findViewById(R.id.textView_body);
                textView1.setText(log.getItem());
                textView11.setText(log.getTag());
                textView11.setVisibility(View.GONE);
                textView2.setText(log.getMessage());
                if (log.getItem() != null && log.getItem().isEmpty())
                    textView1.setVisibility(View.GONE);
                if (log.getMessage() != null && log.getMessage().isEmpty())
                    textView2.setVisibility(View.GONE);
            } catch (Exception e) {
                _Utils.printException(getContext(), e);
            }
            return convertView;
        }
    }

}
